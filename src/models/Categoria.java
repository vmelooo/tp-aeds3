package models;

import java.io.Serializable;

public class Categoria implements Serializable {
    private int id;
    private String nome;
    private boolean ativo; // Adicionado para Exclusão Lógica

    public Categoria() {
        this.ativo = true;
    }

    public Categoria(String nome) {
        this.nome = nome;
        this.ativo = true;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }

    public boolean isAtivo() { return ativo; }
    public void setAtivo(boolean ativo) { this.ativo = ativo; }

    @Override
    public String toString() {
        String status = this.ativo ? "ATIVO" : "EXCLUÍDO (LÓGICO)";
        return String.format("ID: %d | Nome: %s | Status: %s", id, nome, status);
    }
}