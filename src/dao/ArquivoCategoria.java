package dao;

import models.Categoria;
import models.structures.ArvoreBMais;
import models.structures.RegistroIndice;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.List;

public class ArquivoCategoria {

  private final Arquivo<Categoria> arquivo;
  private final ArvoreBMais<RegistroIndice> index;
  private int proximoId;

  public ArquivoCategoria() throws Exception {
    Constructor<Categoria> construtorHeap = Categoria.class.getConstructor();
    this.arquivo = new Arquivo<>("categorias.db", construtorHeap);

    Constructor<RegistroIndice> construtorIndex = RegistroIndice.class.getConstructor();
    this.index = new ArvoreBMais<>(construtorIndex, 4, "data/categorias.idx");

    this.proximoId = getLastId() + 1;
  }

  // --- CRUD Methods ---

  // CREATE
  public int create(Categoria c) throws Exception {
    c.setId(proximoId++);

    long pos = arquivo.create(c);
    RegistroIndice indice = new RegistroIndice(c.getId(), pos);

    index.create(indice);

    return c.getId();
  }

  // READ
  public Categoria read(int id) throws Exception {
    RegistroIndice indice = new RegistroIndice(id, -1);
    ArrayList<RegistroIndice> resultados = index.read(indice);

    if (resultados.isEmpty()) {
      return null;
    }

    long pos = resultados.get(0).getPonteiro();
    return arquivo.read(pos);
  }

  // UPDATE
  public boolean update(Categoria categoriaAtualizada) throws Exception {
    RegistroIndice indice = new RegistroIndice(categoriaAtualizada.getId(), -1);
    ArrayList<RegistroIndice> resultados = index.read(indice);

    if (resultados.isEmpty()) {
      return false;
    }

    long pos = resultados.get(0).getPonteiro();
    long newPos = arquivo.update(pos, categoriaAtualizada);

    if (newPos != pos) {
      RegistroIndice newIndice = new RegistroIndice(categoriaAtualizada.getId(), newPos);
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

  // --- List Methods---
  public List<Categoria> listarTodosAtivos() throws IOException {
    List<Categoria> lista = new ArrayList<>();
    RandomAccessFile raf = arquivo.getFile();
    raf.seek(0);

    while (raf.getFilePointer() < raf.length()) {
      long pos = raf.getFilePointer();
      Categoria c = arquivo.read(pos);
      if (c != null && c.isAtivo()) {
        lista.add(c);
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
      Categoria c = arquivo.read(pos);
      if (c != null && c.getId() > maxId) {
        maxId = c.getId();
      }
    }
    return maxId;
  }
}
