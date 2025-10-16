package dao;

import models.TarefaCategoria;
import models.structures.ArvoreBMais;
import models.structures.RegistroIndice;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.List;

public class ArquivoTarefaCategoria {

  private final Arquivo<TarefaCategoria> arquivo;
  private final ArvoreBMais<RegistroIndice> index;
  private int proximoId;

  public ArquivoTarefaCategoria() throws Exception {
    Constructor<TarefaCategoria> construtorHeap = TarefaCategoria.class.getConstructor();
    this.arquivo = new Arquivo<>("tarefas_categorias.db", construtorHeap);

    Constructor<RegistroIndice> construtorIndex = RegistroIndice.class.getConstructor();
    this.index = new ArvoreBMais<>(construtorIndex, 4, "data/tarefas_categorias.idx");

    this.proximoId = getLastId() + 1;
  }

  // --- CRUD Methods ---

  // CREATE
  public int create(TarefaCategoria tc) throws Exception {
    if (read(tc.getIdTarefa(), tc.getIdCategoria()) != null) {
      // TODO: maybe throw a proper error?
      return -1;
    }

    tc.setId(proximoId++);

    long pos = arquivo.create(tc);

    RegistroIndice indice = new RegistroIndice(tc.getId(), pos);
    index.create(indice);

    return tc.getId();
  }

  // READ
  public TarefaCategoria read(int id) throws Exception {
    RegistroIndice indice = new RegistroIndice(id, -1);
    ArrayList<RegistroIndice> resultados = index.read(indice);

    if (resultados.isEmpty()) {
      return null;
    }

    long pos = resultados.get(0).getPonteiro();
    return arquivo.read(pos);
  }

  // UPDATE
  public boolean update(TarefaCategoria tcAtualizada) throws Exception {
    RegistroIndice indice = new RegistroIndice(tcAtualizada.getId(), -1);
    ArrayList<RegistroIndice> resultados = index.read(indice);

    if (resultados.isEmpty()) {
      return false;
    }

    long pos = resultados.get(0).getPonteiro();
    long newPos = arquivo.update(pos, tcAtualizada);

    if (newPos != pos) {
      RegistroIndice newIndice = new RegistroIndice(tcAtualizada.getId(), newPos);
      index.delete(indice);
      index.create(newIndice);
    }

    return true;
  }

  // DELETE (by surrogate ID)
  public boolean delete(int id) throws Exception {
    RegistroIndice indice = new RegistroIndice(id, -1);
    ArrayList<RegistroIndice> resultados = index.read(indice);

    if (resultados.isEmpty()) {
      return false;
    }

    long pos = resultados.get(0).getPonteiro();
    return arquivo.delete(pos);
  }

  // --- Helper and List Methods ---

  /**
   * Finds a record by its natural composite key. This still requires a file scan.
   */
  public TarefaCategoria read(int idTarefa, int idCategoria) throws IOException {
    RandomAccessFile raf = arquivo.getFile();
    raf.seek(0);

    while (raf.getFilePointer() < raf.length()) {
      long pos = raf.getFilePointer();
      TarefaCategoria tc = arquivo.read(pos);
      if (tc != null && tc.getIdTarefa() == idTarefa && tc.getIdCategoria() == idCategoria) {
        return tc;
      }
    }
    return null;
  }

  /**
   * Lists all relationships for a given task. This also requires a file scan.
   */
  public List<TarefaCategoria> listarPorTarefa(int idTarefa) throws IOException {
    List<TarefaCategoria> lista = new ArrayList<>();
    RandomAccessFile raf = arquivo.getFile();
    raf.seek(0);

    while (raf.getFilePointer() < raf.length()) {
      long pos = raf.getFilePointer();
      TarefaCategoria tc = arquivo.read(pos);
      if (tc != null && tc.getIdTarefa() == idTarefa && tc.isAtivo()) {
        lista.add(tc);
      }
    }
    return lista;
  }

  // Helper method to find the highest ID in the file on startup.
  private int getLastId() throws IOException {
    int maxId = 0;
    RandomAccessFile raf = arquivo.getFile();
    raf.seek(0);

    while (raf.getFilePointer() < raf.length()) {
      long pos = raf.getFilePointer();
      TarefaCategoria tc = arquivo.read(pos);
      if (tc != null && tc.getId() > maxId) {
        maxId = tc.getId();
      }
    }
    return maxId;
  }
}
