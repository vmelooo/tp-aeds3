package models;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class StatusTarefa implements ArquivoBinario {
  private int id;
  private String cor;
  private int ordem;
  private List<String> nomes; // Atributo multivalorado
  private boolean ativo; // Adicionado para Exclusão Lógica

  public StatusTarefa() {
    this.ativo = true;
    this.cor = "";
    this.nomes = new ArrayList<>();
  }

  public StatusTarefa(String cor, int ordem, List<String> nomes) {
    this.cor = cor;
    this.ordem = ordem;
    this.nomes = nomes;
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

  public List<String> getNomes() {
    return nomes;
  }

  public void setNomes(List<String> nomes) {
    this.nomes = nomes;
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

    // make sure list exists before iterating or populating records
    if (nomes == null) {
      dos.writeInt(0);
    } else {
      dos.writeInt(nomes.size());
      for (String nome : nomes) {
        dos.writeUTF(nome);
      }
    }

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

    // get entry count | read list
    int numNomes = dis.readInt();
    this.nomes = new ArrayList<>();
    for (int i = 0; i < numNomes; i++) {
      this.nomes.add(dis.readUTF());
    }
  }

  @Override
  public String toString() {
    String status = this.ativo ? "ATIVO" : "EXCLUÍDO (LÓGICO)";
    return String.format("ID: %d | Nomes: %s | Cor: %s | Ordem: %d | Status: %s", id, nomes, cor, ordem, status);
  }
}
