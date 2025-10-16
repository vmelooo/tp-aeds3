package models;

import java.io.*;

public class ApontamentoDeHoras implements ArquivoBinario {
  private int id;
  private String dataInicio; // Representação simples de data/hora
  private String dataFim; // Representação simples de data/hora
  private double duracao; // Duração em horas (ou minutos)
  private String descricao;

  // Chaves Estrangeiras (FKs)
  private int idTarefa; // FK para Tarefa (N:1)
  private int idUsuario; // FK para Usuário (1:N)

  private boolean ativo; // Adicionado para Exclusão Lógica

  public ApontamentoDeHoras() {
    this.ativo = true;
    this.dataInicio = "";
    this.dataFim = "";
    this.descricao = "";
  }

  // Getters e Setters
  public int getId() {
    return id;
  }

  public void setId(int id) {
    this.id = id;
  }

  public String getDataInicio() {
    return dataInicio;
  }

  public void setDataInicio(String dataInicio) {
    this.dataInicio = dataInicio;
  }

  public String getDataFim() {
    return dataFim;
  }

  public void setDataFim(String dataFim) {
    this.dataFim = dataFim;
  }

  public double getDuracao() {
    return duracao;
  }

  public void setDuracao(double duracao) {
    this.duracao = duracao;
  }

  public String getDescricao() {
    return descricao;
  }

  public void setDescricao(String descricao) {
    this.descricao = descricao;
  }

  public int getIdTarefa() {
    return idTarefa;
  }

  public void setIdTarefa(int idTarefa) {
    this.idTarefa = idTarefa;
  }

  public int getIdUsuario() {
    return idUsuario;
  }

  public void setIdUsuario(int idUsuario) {
    this.idUsuario = idUsuario;
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

    dos.writeInt(this.id);
    dos.writeInt(this.idTarefa);
    dos.writeInt(this.idUsuario);
    dos.writeBoolean(this.ativo);
    dos.writeDouble(this.duracao);

    dos.writeUTF(this.dataInicio != null ? this.dataInicio : "");
    dos.writeUTF(this.dataFim != null ? this.dataFim : "");
    dos.writeUTF(this.descricao != null ? this.descricao : "");

    return baos.toByteArray();
  }

  @Override
  public void fromByteArray(byte[] ba) throws IOException {
    ByteArrayInputStream bais = new ByteArrayInputStream(ba);
    DataInputStream dis = new DataInputStream(bais);

    this.id = dis.readInt();
    this.idTarefa = dis.readInt();
    this.idUsuario = dis.readInt();
    this.ativo = dis.readBoolean();
    this.duracao = dis.readDouble();
    this.dataInicio = dis.readUTF();
    this.dataFim = dis.readUTF();
    this.descricao = dis.readUTF();
  }

  @Override
  public String toString() {
    String status = this.ativo ? "ATIVO" : "EXCLUÍDO (LÓGICO)";
    return String.format("ID: %d | Tarefa: %d | Usuário: %d | Duração: %.2f h | Status: %s",
        id, idTarefa, idUsuario, duracao, status);
  }
}
