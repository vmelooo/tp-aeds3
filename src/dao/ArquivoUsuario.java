package dao;

import models.Usuario;
import models.structures.ArvoreBMais;
import models.structures.RegistroIndice;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.List;

public class ArquivoUsuario {

  private final Arquivo<Usuario> arquivo;
  private final ArvoreBMais<RegistroIndice> index;
  private int proximoId;

  public ArquivoUsuario() throws Exception {
    Constructor<Usuario> construtorHeap = Usuario.class.getConstructor();
    this.arquivo = new Arquivo<>("usuarios.db", construtorHeap);

    Constructor<RegistroIndice> construtorIndex = RegistroIndice.class.getConstructor();
    this.index = new ArvoreBMais<>(construtorIndex, 4, "data/usuarios.idx");

    // TODO: maybe safeer a way of ids?
    this.proximoId = getLastId() + 1;
  }

  // --- CRUD Methods ---

  // CREATE
  public int create(Usuario u) throws Exception {
    u.setId(proximoId++);
    
    long pos = arquivo.create(u);

    RegistroIndice indice = new RegistroIndice(u.getId(), pos);
    index.create(indice);

    return u.getId();
  }

  // READ (by ID) - INDEX FILE
  public Usuario read(int id) throws Exception {
    RegistroIndice indice = new RegistroIndice(id, -1);
    ArrayList<RegistroIndice> resultados = index.read(indice);

    if (resultados.isEmpty()) {
      return null;
    }

    long pos = resultados.get(0).getPonteiro();
    return arquivo.read(pos);
  }

  // UPDATE
  public boolean update(Usuario usuarioAtualizado) throws Exception {
    RegistroIndice indice = new RegistroIndice(usuarioAtualizado.getId(), -1);
    ArrayList<RegistroIndice> resultados = index.read(indice);

    if (resultados.isEmpty()) {
      return false;
    }

    long pos = resultados.get(0).getPonteiro();

    long newPos = arquivo.update(pos, usuarioAtualizado);

    // different position means we add at the end.
    if (newPos != pos) {
      RegistroIndice newIndice = new RegistroIndice(usuarioAtualizado.getId(), newPos);
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

  public List<Usuario> listarAtivos() throws IOException {
    List<Usuario> lista = new ArrayList<>();
    RandomAccessFile raf = arquivo.getFile();
    raf.seek(0);

    while (raf.getFilePointer() < raf.length()) {
      long pos = raf.getFilePointer();
      Usuario u = arquivo.read(pos);
      if (u != null && u.isAtivo()) {
        lista.add(u);
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
      Usuario u = arquivo.read(pos);
      if (u != null && u.getId() > maxId) {
        maxId = u.getId();
      }
    }
    return maxId;
  }
}
