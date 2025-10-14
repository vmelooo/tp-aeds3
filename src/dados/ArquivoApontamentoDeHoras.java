package dados;

import entidades.ApontamentoDeHoras;
import java.io.IOException;
import java.util.List;

public class ArquivoApontamentoDeHoras extends ArquivoGenerico<ApontamentoDeHoras> {

    public ArquivoApontamentoDeHoras(String arquivo) {
        super(arquivo);
    }

    // CREATE
    public int create(ApontamentoDeHoras a) throws IOException, ClassNotFoundException {
        List<ApontamentoDeHoras> lista = readAll();
        int id = lista.size() + 1; 
        a.setId(id);
        lista.add(a);
        salvar(lista);
        return id;
    }

    // READ (por ID)
    public ApontamentoDeHoras read(int id) throws IOException, ClassNotFoundException {
        List<ApontamentoDeHoras> lista = readAll();
        for (ApontamentoDeHoras a : lista) {
            if (a.getId() == id && a.isAtivo()) {
                return a;
            }
        }
        return null; 
    }

    // READ ALL ATIVOS
    public List<ApontamentoDeHoras> listarTodosAtivos() throws IOException, ClassNotFoundException {
        List<ApontamentoDeHoras> todos = super.readAll();
        todos.removeIf(a -> !a.isAtivo());
        return todos;
    }
    
    // UPDATE
    public boolean update(ApontamentoDeHoras a) throws IOException, ClassNotFoundException {
        List<ApontamentoDeHoras> lista = readAll();
        for (int i = 0; i < lista.size(); i++) {
            if (lista.get(i).getId() == a.getId()) {
                a.setAtivo(lista.get(i).isAtivo()); 
                lista.set(i, a);
                salvar(lista);
                return true;
            }
        }
        return false; 
    }

    // DELETE (Exclusão Lógica)
    public boolean delete(int id) throws IOException, ClassNotFoundException {
        List<ApontamentoDeHoras> lista = readAll();
        for (ApontamentoDeHoras a : lista) {
            if (a.getId() == id && a.isAtivo()) {
                a.setAtivo(false); 
                salvar(lista);
                return true;
            }
        }
        return false; 
    }
}