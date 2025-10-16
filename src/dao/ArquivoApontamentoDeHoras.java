package dao;

import models.ApontamentoDeHoras;
import models.structures.ArvoreBMais;
import models.structures.RegistroIndice;
import models.structures.RegistroIndiceSecundario;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.List;

public class ArquivoApontamentoDeHoras {

  private final Arquivo<ApontamentoDeHoras> arquivo;
  private final ArvoreBMais<RegistroIndice> index;
  private final ArvoreBMais<RegistroIndiceSecundario> indexUsuario;
  private final ArvoreBMais<RegistroIndiceSecundario> indexTarefa;
  private int proximoId;

  public ArquivoApontamentoDeHoras() throws Exception {
    Constructor<ApontamentoDeHoras> construtorHeap = ApontamentoDeHoras.class.getConstructor();
    this.arquivo = new Arquivo<>("apontamentos.db", construtorHeap);

    Constructor<RegistroIndice> construtorIndex = RegistroIndice.class.getConstructor();
    this.index = new ArvoreBMais<>(construtorIndex, 4, "data/apontamentos.idx");

    Constructor<RegistroIndiceSecundario> construtorIndexUsuario = RegistroIndiceSecundario.class.getConstructor();
    this.indexUsuario = new ArvoreBMais<>(construtorIndexUsuario, 4, "data/apontamentos_usuario.idx");

    Constructor<RegistroIndiceSecundario> construtorIndexTarefa = RegistroIndiceSecundario.class.getConstructor();
    this.indexTarefa = new ArvoreBMais<>(construtorIndexTarefa, 4, "data/apontamentos_tarefa.idx");

    this.proximoId = getLastId() + 1;
  }

  // --- CRUD Methods ---

  // CREATE
  public int create(ApontamentoDeHoras a) throws Exception {
    a.setId(proximoId++);

    long pos = arquivo.create(a);

    index.create(new RegistroIndice(a.getId(), pos));
    indexUsuario.create(new RegistroIndiceSecundario(a.getIdUsuario(), a.getId()));
    indexTarefa.create(new RegistroIndiceSecundario(a.getIdTarefa(), a.getId()));

    return a.getId();
  }

  // READ
  public ApontamentoDeHoras read(int id) throws Exception {
    RegistroIndice indice = new RegistroIndice(id, -1);
    ArrayList<RegistroIndice> resultados = index.read(indice);

    if (resultados.isEmpty()) {
      return null;
    }

    long pos = resultados.get(0).getPonteiro();
    return arquivo.read(pos);
  }

  // UPDATE
  public boolean update(ApontamentoDeHoras apontamentoAtualizado) throws Exception {
    ApontamentoDeHoras apontamentoAntigo = read(apontamentoAtualizado.getId());
    if (apontamentoAntigo == null)
      return false;

    // Update secondary index on change
    if (apontamentoAntigo.getIdUsuario() != apontamentoAtualizado.getIdUsuario()) {
      indexUsuario.delete(new RegistroIndiceSecundario(apontamentoAntigo.getIdUsuario(), apontamentoAntigo.getId()));
      indexUsuario.create(new RegistroIndiceSecundario(apontamentoAtualizado.getIdUsuario(), apontamentoAtualizado.getId()));
    }

    if (apontamentoAntigo.getIdTarefa() != apontamentoAtualizado.getIdTarefa()) {
      indexTarefa.delete(new RegistroIndiceSecundario(apontamentoAntigo.getIdTarefa(), apontamentoAntigo.getId()));
      indexTarefa.create(new RegistroIndiceSecundario(apontamentoAtualizado.getIdTarefa(), apontamentoAtualizado.getId()));
    }

    RegistroIndice indicePrimario = new RegistroIndice(apontamentoAtualizado.getId(), -1);
    long pos = index.read(indicePrimario).get(0).getPonteiro();
    long newPos = arquivo.update(pos, apontamentoAtualizado);

    if (newPos != pos) {
      index.delete(indicePrimario);
      index.create(new RegistroIndice(apontamentoAtualizado.getId(), newPos));
    }
    return true;
  }

  // DELETE (Logical)
  public boolean delete(int id) throws Exception {
    ApontamentoDeHoras a = read(id);
    if (a == null)
      return false;

    // need to remove from both secondary indexes
    indexUsuario.delete(new RegistroIndiceSecundario(a.getIdUsuario(), a.getId()));
    indexTarefa.delete(new RegistroIndiceSecundario(a.getIdTarefa(), a.getId()));

    RegistroIndice indicePrimario = new RegistroIndice(id, -1);
    long pos = index.read(indicePrimario).get(0).getPonteiro();
    return arquivo.delete(pos);
  }

  // --- List Methods ---

  public List<ApontamentoDeHoras> listarPorUsuario(int idUsuario) throws Exception {
    List<ApontamentoDeHoras> filtrada = new ArrayList<>();

    // first search on secondary index
    ArrayList<RegistroIndiceSecundario> resultados = indexUsuario.read(new RegistroIndiceSecundario(idUsuario, -1));

    // then get from those
    for (RegistroIndiceSecundario res : resultados) {
      ApontamentoDeHoras a = read(res.getChavePrimaria());
      if (a != null && a.isAtivo()) {
        filtrada.add(a);
      }
    }
    return filtrada;
  }

  public List<ApontamentoDeHoras> listarPorTarefa(int idTarefa) throws Exception {
    List<ApontamentoDeHoras> filtrada = new ArrayList<>();

    // first search on secondary index
    ArrayList<RegistroIndiceSecundario> resultados = indexTarefa.read(new RegistroIndiceSecundario(idTarefa, -1));

    // then get from those
    for (RegistroIndiceSecundario res : resultados) {
      ApontamentoDeHoras a = read(res.getChavePrimaria());
      if (a != null && a.isAtivo()) {
        filtrada.add(a);
      }
    }
    return filtrada;
  }

  public List<ApontamentoDeHoras> listarTodosAtivos() throws IOException {
    List<ApontamentoDeHoras> lista = new ArrayList<>();
    RandomAccessFile raf = arquivo.getFile();
    raf.seek(0);

    while (raf.getFilePointer() < raf.length()) {
      long pos = raf.getFilePointer();
      ApontamentoDeHoras a = arquivo.read(pos);
      if (a != null && a.isAtivo()) {
        lista.add(a);
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
      ApontamentoDeHoras a = arquivo.read(pos);
      if (a != null && a.getId() > maxId) {
        maxId = a.getId();
      }
    }
    return maxId;
  }
}
