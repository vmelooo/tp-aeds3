package models;

import java.io.*;

public class StatusTarefa implements ArquivoBinario {
  private int id;
  private String cor;
  private int ordem;
  private String nome;  // único nome, não mais uma lista
  private boolean ativo; // Exclusão lógica

  public StatusTarefa() {
    this.ativo = true;
    this.cor = "";
    this.nome = "";
  }

  public StatusTarefa(String cor, int ordem, String nome) {
    this.cor = cor;
    this.ordem = ordem;
    this.nome = nome;
    this.ativo = true;
  }

  public int getId() {
    return id;
  }

  public void setId(int id) {
    this.id = id;
  }

  public String getCor() {
    return cor;
  }

  public void setCor(String cor) {
    this.cor = cor;
  }

  public int getOrdem() {
    return ordem;
  }

  public void setOrdem(int ordem) {
    this.ordem = ordem;
  }

  public String getNome() {
    return nome;
  }

  public void setNome(String nome) {
    this.nome = nome;
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
    dos.writeUTF(cor);
    dos.writeInt(ordem);
    dos.writeBoolean(ativo);
    dos.writeUTF(nome); // grava nome único

    return baos.toByteArray();
  }

  @Override
  public void fromByteArray(byte[] ba) throws IOException {
    ByteArrayInputStream bais = new ByteArrayInputStream(ba);
    DataInputStream dis = new DataInputStream(bais);

    this.id = dis.readInt();
    this.cor = dis.readUTF();
    this.ordem = dis.readInt();
    this.ativo = dis.readBoolean();
    this.nome = dis.readUTF(); // lê nome único
  }

  @Override
  public String toString() {
    String status = this.ativo ? "ATIVO" : "EXCLUÍDO (LÓGICO)";
    return String.format("ID: %d | Nome: %s | Cor: %s | Ordem: %d | Status: %s",
        id, nome, cor, ordem, status);
  }
}
