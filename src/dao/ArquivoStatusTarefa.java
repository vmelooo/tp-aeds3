package dao;

import models.StatusTarefa;
import entidades.ArvoreBMais;
import entidades.RegistroIndice;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.List;

public class ArquivoStatusTarefa {

  private final Arquivo<StatusTarefa> arquivo;
  private final ArvoreBMais<RegistroIndice> index;
  private int proximoId;

  public ArquivoStatusTarefa() throws Exception {
    Constructor<StatusTarefa> construtorHeap = StatusTarefa.class.getConstructor();
    this.arquivo = new Arquivo<>("status.db", construtorHeap);

    Constructor<RegistroIndice> construtorIndex = RegistroIndice.class.getConstructor();
    this.index = new ArvoreBMais<>(construtorIndex, 4, "data/status.idx");

    this.proximoId = getLastId() + 1;
  }

  // --- CRUD Methods ---

  // CREATE
  public int create(StatusTarefa s) throws Exception {
    s.setId(proximoId++);

    long pos = arquivo.create(s);
    RegistroIndice indice = new RegistroIndice(s.getId(), pos);

    index.create(indice);

    return s.getId();
  }

  // READ
  public StatusTarefa read(int id) throws Exception {
    RegistroIndice indice = new RegistroIndice(id, -1);
    ArrayList<RegistroIndice> resultados = index.read(indice);

    if (resultados.isEmpty()) {
      return null;
    }

    long pos = resultados.get(0).getPonteiro();
    return arquivo.read(pos);
  }

  // UPDATE
  public boolean update(StatusTarefa statusAtualizado) throws Exception {
    RegistroIndice indice = new RegistroIndice(statusAtualizado.getId(), -1);
    ArrayList<RegistroIndice> resultados = index.read(indice);

    if (resultados.isEmpty()) {
      return false;
    }

    long pos = resultados.get(0).getPonteiro();

    long newPos = arquivo.update(pos, statusAtualizado);

    if (newPos != pos) {
      RegistroIndice newIndice = new RegistroIndice(statusAtualizado.getId(), newPos);
      index.delete(indice);
      index.create(newIndice);
    }

    return true;
  }

  // DELETE (Logical)
  public boolean delete(int id) throws Exception {
    RegistroIndice indice = new RegistroIndice(id, -1);
    ArrayList<RegistroIndice> resultados = index.read(indice);

    if (resultados.isEmpty()) {
      return false;
    }

    long pos = resultados.get(0).getPonteiro();
    return arquivo.delete(pos);
  }

  // --- List Methods ---

  public List<StatusTarefa> listarTodosAtivos() throws IOException {
    List<StatusTarefa> lista = new ArrayList<>();
    RandomAccessFile raf = arquivo.getFile();
    raf.seek(0);

    while (raf.getFilePointer() < raf.length()) {
      long pos = raf.getFilePointer();
      StatusTarefa s = arquivo.read(pos);
      if (s != null && s.isAtivo()) {
        lista.add(s);
      }
    }
    return lista;
  }

  private int getLastId() throws IOException {
    int maxId = 0;
    RandomAccessFile raf = arquivo.getFile();
    raf.seek(0);

    while (raf.getFilePointer() < raf.length()) {
      long pos = raf.getFilePointer();
      StatusTarefa s = arquivo.read(pos);
      if (s != null && s.getId() > maxId) {
        maxId = s.getId();
      }
    }
    return maxId;
  }
}
