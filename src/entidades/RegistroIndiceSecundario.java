package entidades;

import java.io.*;

public class RegistroIndiceSecundario implements RegistroArvoreBMais<RegistroIndiceSecundario> {

  private int chaveSecundaria;
  private int chavePrimaria;

  public RegistroIndiceSecundario() {
    this(-1, -1);
  }

  public RegistroIndiceSecundario(int chaveSecundaria, int chavePrimaria) {
    this.chaveSecundaria = chaveSecundaria;
    this.chavePrimaria = chavePrimaria;
  }

  public int getChavePrimaria() {
    return this.chavePrimaria;
  }

  public int getChaveSecundaria() {
    return this.chaveSecundaria;
  }

  @Override
  public int compareTo(RegistroIndiceSecundario other) {
    int diff = Integer.compare(this.chaveSecundaria, other.chaveSecundaria);
    if (diff != 0) {
      return diff;
    }
    return Integer.compare(this.chavePrimaria, other.chavePrimaria);
  }

  // --- Implementation of RegistroArvoreBMais ---

  @Override
  public short size() {
    return 8; // int (4 bytes) + int (4 bytes)
  }

  @Override
  public byte[] toByteArray() throws IOException {
    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    DataOutputStream dos = new DataOutputStream(baos);
    dos.writeInt(chaveSecundaria);
    dos.writeInt(chavePrimaria);
    return baos.toByteArray();
  }

  @Override
  public void fromByteArray(byte[] ba) throws IOException {
    ByteArrayInputStream bais = new ByteArrayInputStream(ba);
    DataInputStream dis = new DataInputStream(bais);
    this.chaveSecundaria = dis.readInt();
    this.chavePrimaria = dis.readInt();
  }

  @Override
  public RegistroIndiceSecundario clone() {
    return new RegistroIndiceSecundario(this.chaveSecundaria, this.chavePrimaria);
  }
}
