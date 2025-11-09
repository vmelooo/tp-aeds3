package models;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class Usuario implements ArquivoBinario {
  private int id;
  private String login; // Usado como atributo, não mais para "login"
  private String senha; // Usado como atributo, não mais para "login"
  private String nome;
  private List<String> telefones; // Atributo multivalorado
  private boolean ativo; // Adicionado para Exclusão Lógica

  public Usuario() {
    this.ativo = true;
    this.telefones = new ArrayList<>();
  }

  // Construtor usado no Main para criar novo
  public Usuario(String nome, String login, String senha) {
    this.nome = nome;
    this.login = login;
    this.senha = senha;
    this.ativo = true;
    this.telefones = new ArrayList<>();
  }

  public int getId() {
    return id;
  }

  public void setId(int id) {
    this.id = id;
  }

  public String getLogin() {
    return login;
  }

  public void setLogin(String login) {
    this.login = login;
  }

  public String getSenha() {
    return senha;
  }

  public void setSenha(String senha) {
    this.senha = senha;
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

  // Getters e Setters para telefones
  public List<String> getTelefones() {
    return telefones;
  }

  public void setTelefones(List<String> telefones) {
    this.telefones = telefones;
  }

  public void adicionarTelefone(String telefone) {
    if (!this.telefones.contains(telefone)) {
      this.telefones.add(telefone);
    }
  }

  public boolean removerTelefone(String telefone) {
    return this.telefones.remove(telefone);
  }

  @Override
  public byte[] toByteArray() throws IOException {
    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    DataOutputStream dos = new DataOutputStream(baos);

    dos.writeInt(id);
    dos.writeUTF(login);
    dos.writeUTF(senha);
    dos.writeUTF(nome);
    dos.writeBoolean(ativo);

    // Serializa lista de telefones
    dos.writeInt(telefones.size());
    for (String telefone : telefones) {
      dos.writeUTF(telefone);
    }

    return baos.toByteArray();
  }

  @Override
  public void fromByteArray(byte[] ba) throws IOException {
    ByteArrayInputStream bais = new ByteArrayInputStream(ba);
    DataInputStream dis = new DataInputStream(bais);

    this.id = dis.readInt();
    this.login = dis.readUTF();
    this.senha = dis.readUTF();
    this.nome = dis.readUTF();
    this.ativo = dis.readBoolean();

    // Deserializa lista de telefones
    int numTelefones = dis.readInt();
    this.telefones = new ArrayList<>();
    for (int i = 0; i < numTelefones; i++) {
      this.telefones.add(dis.readUTF());
    }
  }

  @Override
  public String toString() {
    String status = this.ativo ? "ATIVO" : "EXCLUÍDO (LÓGICO)";
    String telefonesStr = telefones.isEmpty() ? "Nenhum" : String.join(", ", telefones);
    return String.format("ID: %d | Nome: %s | Login: %s | Telefones: %s | Status: %s",
        id, nome, login, telefonesStr, status);
  }
}
