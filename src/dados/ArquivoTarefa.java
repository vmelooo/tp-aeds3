package dados;

import entidades.Tarefa;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ArquivoTarefa extends ArquivoGenerico<Tarefa> {

    public ArquivoTarefa(String arquivo) {
        super(arquivo);
    }

    // CREATE
    public int create(Tarefa t) throws IOException, ClassNotFoundException {
        List<Tarefa> lista = readAll();
        int id = lista.size() + 1;
        t.setId(id);
        lista.add(t);
        salvar(lista);
        return id;
    }

    // READ (por ID)
    public Tarefa read(int id) throws IOException, ClassNotFoundException {
        List<Tarefa> lista = readAll();
        for (Tarefa t : lista) {
            if (t.getId() == id && t.isAtivo()) {
                return t;
            }
        }
        return null;
    }
    
    // READ ALL ATIVOS
    public List<Tarefa> listarTodosAtivos() throws IOException, ClassNotFoundException {
        List<Tarefa> todos = super.readAll();
        todos.removeIf(t -> !t.isAtivo());
        return todos;
    }

    // READ (Busca por Usuário - Relacionamento 1:N)
    public List<Tarefa> listarPorUsuario(int idUsuario) throws IOException, ClassNotFoundException {
        List<Tarefa> todas = listarTodosAtivos(); // Só lista tarefas ativas
        List<Tarefa> filtrada = new ArrayList<>();
        
        // Simulação da busca usando o índice/relacionamento (Neste caso, lista sequencial)
        for (Tarefa t : todas) {
            if (t.getIdUsuario() == idUsuario) {
                filtrada.add(t);
            }
        }
        return filtrada;
    }

    // UPDATE
    public boolean update(Tarefa t) throws IOException, ClassNotFoundException {
        List<Tarefa> lista = readAll();
        for (int i = 0; i < lista.size(); i++) {
            if (lista.get(i).getId() == t.getId()) {
                // Preserva o status ativo/inativo
                t.setAtivo(lista.get(i).isAtivo());
                lista.set(i, t);
                salvar(lista);
                return true;
            }
        }
        return false;
    }

    // DELETE (Exclusão Lógica)
    public boolean delete(int id) throws IOException, ClassNotFoundException {
        List<Tarefa> lista = readAll();
        for (Tarefa t : lista) {
            if (t.getId() == id && t.isAtivo()) {
                t.setAtivo(false);
                salvar(lista);
                return true;
            }
        }
        return false;
    }
}