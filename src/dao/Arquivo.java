package dao;

import models.ArquivoBinario;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.lang.reflect.Constructor;

public class Arquivo<T extends ArquivoBinario> {

  private final RandomAccessFile file;
  private final Constructor<T> construtor;
  private static final String DB_DIRECTORY = "data/";

  public Arquivo(String nomeArquivo, Constructor<T> construtor) throws IOException {
    File dir = new File(DB_DIRECTORY);
    if (!dir.exists()) {
      dir.mkdir();
    }
    this.file = new RandomAccessFile(DB_DIRECTORY + nomeArquivo, "rw");
    this.construtor = construtor;
  }

  /**
   * CREATE: Appends a new record to the end of the file.
   * Returns the pointer (position) where the record was created.
   */
  public long create(T obj) throws IOException {
    long pos = file.length();
    file.seek(pos);

    byte[] recordBytes = obj.toByteArray();

    file.writeBoolean(false); // [exclusion] = false (active)
    file.writeInt(recordBytes.length); // [size]
    file.write(recordBytes); // [data]

    return pos;
  }

  /**
   * READ: Reads a record from a specific position in the file.
   */
  public T read(long pos) throws IOException {
    file.seek(pos);

    boolean isExcluded = file.readBoolean();
    if (isExcluded) {
      return null;
    }

    int size = file.readInt();
    byte[] recordBytes = new byte[size];
    file.readFully(recordBytes);

    try {
      T obj = construtor.newInstance();
      obj.fromByteArray(recordBytes);
      return obj;
    } catch (Exception e) {
      throw new IOException("Failed to instantiate object from file", e);
    }
  }

  /**
   * UPDATE: Replaces a record at a specific position.
   * If the new record is smaller or the same size, it overwrites the original.
   * If the new record is larger, it logically deletes the original
   * and creates the new record at the end of the file.
   * Returns the position of the updated (or new) record.
   */
  public long update(long pos, T newObj) throws IOException {
    file.seek(pos);
    boolean isExcluded = file.readBoolean();
    int oldSize = file.readInt();

    if (isExcluded) {
      throw new IOException("Cannot update a logically deleted record.");
    }

    byte[] newBytes = newObj.toByteArray();
    int newSize = newBytes.length;

    // Simple overwrite
    if (newSize <= oldSize) {
      // Seek back to the start of the record data
      file.seek(pos + 1 + 4);
      file.write(newBytes);
      return pos;
    }

    // New is bigger; mark old to delete, append at the end.
    else {
      delete(pos);
      return create(newObj);
    }
  }

  /**
   * DELETE: Logically deletes a record by setting its exclusion mark to true.
   */
  public boolean delete(long pos) throws IOException {
    file.seek(pos);
    file.writeBoolean(true);
    return true;
  }

  public void close() throws IOException {
    file.close();
  }

  public RandomAccessFile getFile() {
    return file;
  }
}
