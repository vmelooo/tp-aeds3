package dados;

import entidades.TarefaCategoria;
import java.io.IOException;
import java.util.List;

public class ArquivoTarefaCategoria extends ArquivoGenerico<TarefaCategoria> {

    public ArquivoTarefaCategoria(String arquivo) {
        super(arquivo);
    }

    // CREATE
    public boolean create(TarefaCategoria tc) throws IOException, ClassNotFoundException {
        // Validação: Não pode existir a mesma combinação Tarefa-Categoria (PK duplicada)
        if (read(tc.getIdTarefa(), tc.getIdCategoria()) != null) {
            // Este registro já existe e está ativo
            return false; 
        }
        
        List<TarefaCategoria> lista = readAll();
        lista.add(tc);
        salvar(lista);
        return true;
    }

    // READ (por Chave Composta)
    public TarefaCategoria read(int idTarefa, int idCategoria) throws IOException, ClassNotFoundException {
        List<TarefaCategoria> lista = readAll();
        for (TarefaCategoria tc : lista) {
            if (tc.getIdTarefa() == idTarefa && tc.getIdCategoria() == idCategoria && tc.isAtivo()) {
                return tc;
            }
        }
        return null; 
    }

    // READ ALL ATIVOS de um lado do relacionamento (Ex: todas as Categorias de uma Tarefa)
    public List<TarefaCategoria> listarPorTarefa(int idTarefa) throws IOException, ClassNotFoundException {
        List<TarefaCategoria> todos = super.readAll();
        todos.removeIf(tc -> tc.getIdTarefa() != idTarefa || !tc.isAtivo());
        return todos;
    }

    // UPDATE
    public boolean update(TarefaCategoria tc) throws IOException, ClassNotFoundException {
        List<TarefaCategoria> lista = readAll();
        for (int i = 0; i < lista.size(); i++) {
            TarefaCategoria existente = lista.get(i);
            if (existente.getIdTarefa() == tc.getIdTarefa() && existente.getIdCategoria() == tc.getIdCategoria()) {
                tc.setAtivo(existente.isAtivo()); 
                lista.set(i, tc);
                salvar(lista);
                return true;
            }
        }
        return false; 
    }

    // DELETE (Exclusão Lógica)
    public boolean delete(int idTarefa, int idCategoria) throws IOException, ClassNotFoundException {
        List<TarefaCategoria> lista = readAll();
        for (TarefaCategoria tc : lista) {
            if (tc.getIdTarefa() == idTarefa && tc.getIdCategoria() == idCategoria && tc.isAtivo()) {
                tc.setAtivo(false); 
                salvar(lista);
                return true;
            }
        }
        return false; 
    }
}