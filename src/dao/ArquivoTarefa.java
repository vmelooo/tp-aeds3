package dao;

import models.Tarefa;
import models.structures.ArvoreBMais;
import models.structures.RegistroIndice;
import models.structures.RegistroIndiceSecundario;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.List;

public class ArquivoTarefa {

  private final Arquivo<Tarefa> arquivo;
  private final ArvoreBMais<RegistroIndice> index;
  private final ArvoreBMais<RegistroIndiceSecundario> indexUsuario;
  private int proximoId;

  public ArquivoTarefa() throws Exception {
    Constructor<Tarefa> construtorHeap = Tarefa.class.getConstructor();
    this.arquivo = new Arquivo<>("tarefas.db", construtorHeap);

    Constructor<RegistroIndice> construtorIndex = RegistroIndice.class.getConstructor();
    this.index = new ArvoreBMais<>(construtorIndex, 4, "data/tarefas.idx");

    Constructor<RegistroIndiceSecundario> construtorIndexUsuario = RegistroIndiceSecundario.class.getConstructor();
    this.indexUsuario = new ArvoreBMais<>(construtorIndexUsuario, 4, "data/tarefas_usuario.idx");

    this.proximoId = getLastId() + 1;
  }

  // CREATE
  public int create(Tarefa t) throws Exception {
    t.setId(proximoId++);
    long pos = arquivo.create(t);

    index.create(new RegistroIndice(t.getId(), pos));
    indexUsuario.create(new RegistroIndiceSecundario(t.getIdUsuario(), t.getId()));

    return t.getId();
  }

  // READ
  public Tarefa read(int id) throws Exception {
    ArrayList<RegistroIndice> resultados = index.read(new RegistroIndice(id, -1));
    if (resultados.isEmpty())
      return null;
    return arquivo.read(resultados.get(0).getPonteiro());
  }

  // UPDATE
  public boolean update(Tarefa tarefaAtualizada) throws Exception {
    Tarefa tarefaAntiga = read(tarefaAtualizada.getId());
    if (tarefaAntiga == null)
      return false;

    // Update secondary index on change
    if (tarefaAntiga.getIdUsuario() != tarefaAtualizada.getIdUsuario()) {
      indexUsuario.delete(new RegistroIndiceSecundario(tarefaAntiga.getIdUsuario(), tarefaAntiga.getId()));
      indexUsuario.create(new RegistroIndiceSecundario(tarefaAtualizada.getIdUsuario(), tarefaAtualizada.getId()));
    }

    RegistroIndice indicePrimario = new RegistroIndice(tarefaAtualizada.getId(), -1);
    long pos = index.read(indicePrimario).get(0).getPonteiro();
    long newPos = arquivo.update(pos, tarefaAtualizada);

    if (newPos != pos) {
      index.delete(indicePrimario);
      index.create(new RegistroIndice(tarefaAtualizada.getId(), newPos));
    }
    return true;
  }

  // DELETE
  public boolean delete(int id) throws Exception {
    Tarefa t = read(id);
    if (t == null)
      return false;

    // need to fix both
    indexUsuario.delete(new RegistroIndiceSecundario(t.getIdUsuario(), t.getId()));

    RegistroIndice indicePrimario = new RegistroIndice(id, -1);
    long pos = index.read(indicePrimario).get(0).getPonteiro();
    return arquivo.delete(pos);
  }

  // --- List Methods ---

  public List<Tarefa> listarPorUsuario(int idUsuario) throws Exception {
    List<Tarefa> filtrada = new ArrayList<>();

    // first search on secondary index
    ArrayList<RegistroIndiceSecundario> resultados = indexUsuario.read(new RegistroIndiceSecundario(idUsuario, -1));

    // then get from those
    for (RegistroIndiceSecundario res : resultados) {
      Tarefa t = read(res.getChavePrimaria());
      if (t != null && t.isAtivo()) {
        filtrada.add(t);
      }
    }
    return filtrada;
  }

  public List<Tarefa> listarTodosAtivos() throws IOException {
    List<Tarefa> lista = new ArrayList<>();
    RandomAccessFile raf = arquivo.getFile();
    raf.seek(0);
    while (raf.getFilePointer() < raf.length()) {
      Tarefa t = arquivo.read(raf.getFilePointer());
      if (t != null && t.isAtivo()) {
        lista.add(t);
      }
    }
    return lista;
  }

  private int getLastId() throws IOException {
    int maxId = 0;
    RandomAccessFile raf = arquivo.getFile();
    raf.seek(0);
    while (raf.getFilePointer() < raf.length()) {
      Tarefa t = arquivo.read(raf.getFilePointer());
      if (t != null && t.getId() > maxId) {
        maxId = t.getId();
      }
    }
    return maxId;
  }
}
