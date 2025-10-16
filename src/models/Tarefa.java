package models;

import java.io.*;

public class Tarefa implements ArquivoBinario {
  private int id;
  private String titulo;
  private String descricao;
  private String prazo; // Simples string para manter compatibilidade, idealmente Date ou LocalDate
  private int idUsuario; // FK para Usuário (1:N)
  private int idStatus; // FK para StatusTarefa (N:1)
  private boolean ativo; // Adicionado para Exclusão Lógica

  public Tarefa() {
    this.ativo = true;
    this.titulo = "";
    this.descricao = "";
    this.prazo = "";
  }

  // Getters e Setters
  public int getId() {
    return id;
  }

  public void setId(int id) {
    this.id = id;
  }

  public String getTitulo() {
    return titulo;
  }

  public void setTitulo(String titulo) {
    this.titulo = titulo;
  }

  public String getDescricao() {
    return descricao;
  }

  public void setDescricao(String descricao) {
    this.descricao = descricao;
  }

  public String getPrazo() {
    return prazo;
  }

  public void setPrazo(String prazo) {
    this.prazo = prazo;
  }

  public int getIdUsuario() {
    return idUsuario;
  }

  public void setIdUsuario(int idUsuario) {
    this.idUsuario = idUsuario;
  }

  public int getIdStatus() {
    return idStatus;
  }

  public void setIdStatus(int idStatus) {
    this.idStatus = idStatus;
  }

  // Getter e Setter para o campo ativo
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
    dos.writeUTF(titulo);
    dos.writeUTF(descricao);
    dos.writeUTF(prazo != null ? prazo : "");
    dos.writeInt(idUsuario);
    dos.writeInt(idStatus);
    dos.writeBoolean(ativo);

    return baos.toByteArray();
  }

  @Override
  public void fromByteArray(byte[] ba) throws IOException {
    ByteArrayInputStream bais = new ByteArrayInputStream(ba);
    DataInputStream dis = new DataInputStream(bais);

    this.id = dis.readInt();
    this.titulo = dis.readUTF();
    this.descricao = dis.readUTF();
    this.prazo = dis.readUTF();
    this.idUsuario = dis.readInt();
    this.idStatus = dis.readInt();
    this.ativo = dis.readBoolean();
  }

  @Override
  public String toString() {
    String status = this.ativo ? "ATIVO" : "EXCLUÍDO (LÓGICO)";
    return String.format("ID: %d | Título: %s | Usuário: %d | Status: %d | Status: %s",
        id, titulo, idUsuario, idStatus, status);
  }
}
