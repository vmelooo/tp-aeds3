package entidades;

import java.io.Serializable;
import java.util.List;

public class StatusTarefa implements Serializable {
    private int id;
    private String cor;
    private int ordem;
    private List<String> nomes; // Atributo multivalorado
    private boolean ativo; // Adicionado para Exclusão Lógica

    public StatusTarefa() {
        this.ativo = true;
    }

    public StatusTarefa(String cor, int ordem, List<String> nomes) {
        this.cor = cor;
        this.ordem = ordem;
        this.nomes = nomes;
        this.ativo = true;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getCor() { return cor; }
    public void setCor(String cor) { this.cor = cor; }

    public int getOrdem() { return ordem; }
    public void setOrdem(int ordem) { this.ordem = ordem; }

    public List<String> getNomes() { return nomes; }
    public void setNomes(List<String> nomes) { this.nomes = nomes; }

    // Getter e Setter para o campo ativo
    public boolean isAtivo() { return ativo; }
    public void setAtivo(boolean ativo) { this.ativo = ativo; }

    @Override
    public String toString() {
        String status = this.ativo ? "ATIVO" : "EXCLUÍDO (LÓGICO)";
        return String.format("ID: %d | Nomes: %s | Cor: %s | Ordem: %d | Status: %s", id, nomes, cor, ordem, status);
    }
}