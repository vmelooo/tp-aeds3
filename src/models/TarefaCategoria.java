package models;

import java.io.*;

public class TarefaCategoria implements ArquivoBinario {
  // heaps need internal id 
  private int id;
  // Chave Composta / Chaves Estrangeiras
  private int idTarefa;
  private int idCategoria;

  private int prioridade; // Atributo do relacionamento
  private boolean ativo; // Adicionado para Exclusão Lógica

  public TarefaCategoria() {
    this.ativo = true;
  }

  // Construtor principal para o relacionamento
  public TarefaCategoria(int idTarefa, int idCategoria, int prioridade) {
    this.idTarefa = idTarefa;
    this.idCategoria = idCategoria;
    this.prioridade = prioridade;
    this.ativo = true;
  }

  // Getters e Setters
  public int getIdTarefa() {
    return idTarefa;
  }

  public void setIdTarefa(int idTarefa) {
    this.idTarefa = idTarefa;
  }
  public int getId() {
    return id;
  }

  public void setId(int id) {
    this.id = id;
  }

  public int getIdCategoria() {
    return idCategoria;
  }

  public void setIdCategoria(int idCategoria) {
    this.idCategoria = idCategoria;
  }

  public int getPrioridade() {
    return prioridade;
  }

  public void setPrioridade(int prioridade) {
    this.prioridade = prioridade;
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
    dos.writeInt(idTarefa);
    dos.writeInt(idCategoria);
    dos.writeInt(prioridade);
    dos.writeBoolean(ativo);

    return baos.toByteArray();
  }

  @Override
  public void fromByteArray(byte[] ba) throws IOException {
    ByteArrayInputStream bais = new ByteArrayInputStream(ba);
    DataInputStream dis = new DataInputStream(bais);

    this.id = dis.readInt();
    this.idTarefa = dis.readInt();
    this.idCategoria = dis.readInt();
    this.prioridade = dis.readInt();
    this.ativo = dis.readBoolean();
  }

  @Override
  public String toString() {
    String status = this.ativo ? "ATIVO" : "EXCLUÍDO (LÓGICO)";
    return String.format("Tarefa ID: %d | Categoria ID: %d | Prioridade: %d | Status: %s",
        idTarefa, idCategoria, prioridade, status);
  }
}
