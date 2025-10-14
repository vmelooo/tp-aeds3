package dados;

import entidades.StatusTarefa;
import java.io.IOException;
import java.util.List;

public class ArquivoStatusTarefa extends ArquivoGenerico<StatusTarefa> {

    public ArquivoStatusTarefa(String arquivo) {
        super(arquivo);
    }

    // CREATE
    public int create(StatusTarefa s) throws IOException, ClassNotFoundException {
        List<StatusTarefa> lista = readAll();
        int id = lista.size() + 1;
        s.setId(id);
        lista.add(s);
        salvar(lista);
        return id;
    }

    // READ (por ID)
    public StatusTarefa read(int id) throws IOException, ClassNotFoundException {
        List<StatusTarefa> lista = readAll();
        for (StatusTarefa s : lista) {
            if (s.getId() == id && s.isAtivo()) {
                return s;
            }
        }
        return null;
    }

    // READ ALL ATIVOS (Seu antigo listarTodos, que usarei no Main)
    public List<StatusTarefa> listarTodos() throws IOException, ClassNotFoundException {
        List<StatusTarefa> todos = super.readAll();
        todos.removeIf(s -> !s.isAtivo());
        return todos;
    }

    // UPDATE
    public boolean update(StatusTarefa s) throws IOException, ClassNotFoundException {
        List<StatusTarefa> lista = readAll();
        for (int i = 0; i < lista.size(); i++) {
            if (lista.get(i).getId() == s.getId()) {
                s.setAtivo(lista.get(i).isAtivo());
                lista.set(i, s);
                salvar(lista);
                return true;
            }
        }
        return false;
    }

    // DELETE (Exclusão Lógica)
    public boolean delete(int id) throws IOException, ClassNotFoundException {
        List<StatusTarefa> lista = readAll();
        for (StatusTarefa s : lista) {
            // Um status que já tem tarefas ligadas NÃO deveria ser excluído para
            // manter a Integridade Referencial, mas para Exclusão Lógica simples...
            if (s.getId() == id && s.isAtivo()) {
                s.setAtivo(false);
                salvar(lista);
                return true;
            }
        }
        return false;
    }
}