package dao;

import models.Usuario;
import java.io.IOException;
import java.util.List;

public class ArquivoUsuario extends ArquivoGenerico<Usuario> {

    public ArquivoUsuario(String arquivo) {
        super(arquivo);
    }

    // CREATE
    public int create(Usuario u) throws IOException, ClassNotFoundException {
        List<Usuario> lista = readAll();
        // Gerar ID sequencial (muito simples e não escalável, mas ok para esta fase)
        int id = lista.size() + 1; 
        u.setId(id);
        lista.add(u);
        salvar(lista);
        return id;
    }

    // READ (por ID)
    public Usuario read(int id) throws IOException, ClassNotFoundException {
        List<Usuario> lista = readAll();
        for (Usuario u : lista) {
            if (u.getId() == id && u.isAtivo()) {
                return u;
            }
        }
        return null; // Retorna null se não encontrar ou se estiver logicamente excluído
    }

    // READ ALL (Todos, incluindo logicamente excluídos)
    public List<Usuario> readAll() throws IOException, ClassNotFoundException {
        // O readAll do ArquivoGenerico já faz isso, mas podemos filtrar se necessário
        return super.readAll(); 
    }
    
    // READ ALL ATIVOS (O mais usado na prática)
    public List<Usuario> listarAtivos() throws IOException, ClassNotFoundException {
        List<Usuario> todos = super.readAll();
        // Filtra e retorna apenas os ativos
        todos.removeIf(u -> !u.isAtivo());
        return todos;
    }


    // UPDATE
    public boolean update(Usuario u) throws IOException, ClassNotFoundException {
        List<Usuario> lista = readAll();
        for (int i = 0; i < lista.size(); i++) {
            if (lista.get(i).getId() == u.getId()) {
                // Preserva o status ativo/inativo que já estava no registro salvo
                u.setAtivo(lista.get(i).isAtivo()); 
                lista.set(i, u);
                salvar(lista);
                return true;
            }
        }
        return false; // Usuário não encontrado para atualização
    }

    // DELETE (Exclusão Lógica)
    public boolean delete(int id) throws IOException, ClassNotFoundException {
        List<Usuario> lista = readAll();
        for (Usuario u : lista) {
            if (u.getId() == id && u.isAtivo()) {
                u.setAtivo(false); // Marca como excluído
                salvar(lista);
                return true;
            }
        }
        return false; // Usuário não encontrado ou já inativo
    }
}