package dados;

import entidades.Categoria;
import java.io.IOException;
import java.util.List;

public class ArquivoCategoria extends ArquivoGenerico<Categoria> {

    public ArquivoCategoria(String arquivo) {
        super(arquivo);
    }

    // CREATE
    public int create(Categoria c) throws IOException, ClassNotFoundException {
        List<Categoria> lista = readAll();
        int id = lista.size() + 1; 
        c.setId(id);
        lista.add(c);
        salvar(lista);
        return id;
    }

    // READ (por ID)
    public Categoria read(int id) throws IOException, ClassNotFoundException {
        List<Categoria> lista = readAll();
        for (Categoria c : lista) {
            if (c.getId() == id && c.isAtivo()) {
                return c;
            }
        }
        return null; 
    }

    // READ ALL ATIVOS
    public List<Categoria> listarTodosAtivos() throws IOException, ClassNotFoundException {
        List<Categoria> todos = super.readAll();
        todos.removeIf(c -> !c.isAtivo());
        return todos;
    }

    // UPDATE
    public boolean update(Categoria c) throws IOException, ClassNotFoundException {
        List<Categoria> lista = readAll();
        for (int i = 0; i < lista.size(); i++) {
            if (lista.get(i).getId() == c.getId()) {
                c.setAtivo(lista.get(i).isAtivo()); 
                lista.set(i, c);
                salvar(lista);
                return true;
            }
        }
        return false; 
    }

    // DELETE (Exclusão Lógica)
    public boolean delete(int id) throws IOException, ClassNotFoundException {
        List<Categoria> lista = readAll();
        for (Categoria c : lista) {
            if (c.getId() == id && c.isAtivo()) {
                c.setAtivo(false); 
                salvar(lista);
                return true;
            }
        }
        return false; 
    }
}