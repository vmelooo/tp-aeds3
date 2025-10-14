package entidades;

import java.io.Serializable;

public class TarefaCategoria implements Serializable {
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
    public int getIdTarefa() { return idTarefa; }
    public void setIdTarefa(int idTarefa) { this.idTarefa = idTarefa; }

    public int getIdCategoria() { return idCategoria; }
    public void setIdCategoria(int idCategoria) { this.idCategoria = idCategoria; }

    public int getPrioridade() { return prioridade; }
    public void setPrioridade(int prioridade) { this.prioridade = prioridade; }

    public boolean isAtivo() { return ativo; }
    public void setAtivo(boolean ativo) { this.ativo = ativo; }
    
    @Override
    public String toString() {
        String status = this.ativo ? "ATIVO" : "EXCLUÍDO (LÓGICO)";
        return String.format("Tarefa ID: %d | Categoria ID: %d | Prioridade: %d | Status: %s", 
                             idTarefa, idCategoria, prioridade, status);
    }
}