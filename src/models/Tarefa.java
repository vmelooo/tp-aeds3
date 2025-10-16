package models;

import java.io.Serializable;

public class Tarefa implements Serializable {
    private int id;
    private String titulo;
    private String descricao;
    private String prazo; // Simples string para manter compatibilidade, idealmente Date ou LocalDate
    private int idUsuario; // FK para Usuário (1:N)
    private int idStatus;  // FK para StatusTarefa (N:1)
    private boolean ativo; // Adicionado para Exclusão Lógica

    public Tarefa() {
        this.ativo = true;
    }

    // Getters e Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getTitulo() { return titulo; }
    public void setTitulo(String titulo) { this.titulo = titulo; }

    public String getDescricao() { return descricao; }
    public void setDescricao(String descricao) { this.descricao = descricao; }

    public String getPrazo() { return prazo; }
    public void setPrazo(String prazo) { this.prazo = prazo; }

    public int getIdUsuario() { return idUsuario; }
    public void setIdUsuario(int idUsuario) { this.idUsuario = idUsuario; }

    public int getIdStatus() { return idStatus; }
    public void setIdStatus(int idStatus) { this.idStatus = idStatus; }

    // Getter e Setter para o campo ativo
    public boolean isAtivo() { return ativo; }
    public void setAtivo(boolean ativo) { this.ativo = ativo; }

    @Override
    public String toString() {
        String status = this.ativo ? "ATIVO" : "EXCLUÍDO (LÓGICO)";
        return String.format("ID: %d | Título: %s | Usuário: %d | Status: %d | Status: %s", 
                             id, titulo, idUsuario, idStatus, status);
    }
}