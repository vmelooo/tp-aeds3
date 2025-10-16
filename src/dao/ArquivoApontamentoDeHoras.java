package dao;

import models.ApontamentoDeHoras;
import entidades.ArvoreBMais;
import entidades.RegistroIndice;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.List;

public class ArquivoApontamentoDeHoras {

  private final Arquivo<ApontamentoDeHoras> arquivo;
  private final ArvoreBMais<RegistroIndice> index;
  private int proximoId;

  public ArquivoApontamentoDeHoras() throws Exception {
    Constructor<ApontamentoDeHoras> construtorHeap = ApontamentoDeHoras.class.getConstructor();
    this.arquivo = new Arquivo<>("apontamentos.db", construtorHeap);

    Constructor<RegistroIndice> construtorIndex = RegistroIndice.class.getConstructor();
    this.index = new ArvoreBMais<>(construtorIndex, 4, "data/apontamentos.idx");

    this.proximoId = getLastId() + 1;
  }

  // --- CRUD Methods ---

  // CREATE
  public int create(ApontamentoDeHoras a) throws Exception {
    a.setId(proximoId++);

    long pos = arquivo.create(a);
    RegistroIndice indice = new RegistroIndice(a.getId(), pos);

    index.create(indice);

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
    RegistroIndice indice = new RegistroIndice(apontamentoAtualizado.getId(), -1);
    ArrayList<RegistroIndice> resultados = index.read(indice);

    if (resultados.isEmpty()) {
      return false;
    }

    long pos = resultados.get(0).getPonteiro();
    long newPos = arquivo.update(pos, apontamentoAtualizado);

    if (newPos != pos) {
      RegistroIndice newIndice = new RegistroIndice(apontamentoAtualizado.getId(), newPos);
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
