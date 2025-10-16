package models;

import java.io.*;

public class Categoria implements ArquivoBinario {
  private int id;
  private String nome;
  private boolean ativo; // Adicionado para Exclusão Lógica

  public Categoria() {
    this.ativo = true;
    this.nome = "";
  }

  public Categoria(String nome) {
    this.nome = nome;
    this.ativo = true;
  }

  public int getId() {
    return id;
  }

  public void setId(int id) {
    this.id = id;
  }

  public String getNome() {
    return nome;
  }

  public void setNome(String nome) {
    this.nome = nome;
  }

  public boolean isAtivo() {
    return ativo;
  }

  public void setAtivo(boolean ativo) {
    this.ativo = ativo;
  }

  @Override
  public byte[] toByteArray() throws IOException {
    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    DataOutputStream dos = new DataOutputStream(baos);

    dos.writeInt(id);
    dos.writeUTF(nome);
    dos.writeBoolean(ativo);

    return baos.toByteArray();
  }

  @Override
  public void fromByteArray(byte[] ba) throws IOException {
    ByteArrayInputStream bais = new ByteArrayInputStream(ba);
    DataInputStream dis = new DataInputStream(bais);

    this.id = dis.readInt();
    this.nome = dis.readUTF();
    this.ativo = dis.readBoolean();

  }

  @Override
  public String toString() {
    String status = this.ativo ? "ATIVO" : "EXCLUÍDO (LÓGICO)";
    return String.format("ID: %d | Nome: %s | Status: %s", id, nome, status);
  }
}
