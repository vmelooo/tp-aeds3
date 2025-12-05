package controllers;

import dao.ArquivoCategoria;
import models.Categoria;
import views.MenuView;

import java.util.List;

public class CategoriaController {

    private final ArquivoCategoria arquivoCategoria;
    private final MenuView view;

    public CategoriaController(ArquivoCategoria arquivoCategoria, MenuView view) {
        this.arquivoCategoria = arquivoCategoria;
        this.view = view;
    }

    // Getter para ArquivoCategoria
    public ArquivoCategoria getArquivoCategoria() {
        return arquivoCategoria;
    }

    /**
     * CREATE - Cria uma nova categoria
     */
    public void criarCategoria() {
        try {
            String nome = view.lerTexto("Nome da Categoria: ");

            Categoria c = new Categoria(nome);
            int id = arquivoCategoria.create(c);
            view.exibirSucesso("Categoria criada com ID " + id);

        } catch (Exception e) {
            view.exibirErro("Falha ao criar categoria: " + e.getMessage());
        }
    }

    /**
     * LIST - Lista todas as categorias ativas
     */
    public void listarCategorias() {
        try {
            List<Categoria> lista = arquivoCategoria.listarTodosAtivos();
            if (lista.isEmpty()) {
                view.exibirMensagem("Nenhuma categoria ativa cadastrada.");
                return;
            }
            view.exibirMensagem("Categorias Ativas:");
            for (Categoria c : lista) {
                view.exibirMensagem(c.toString());
            }
        } catch (Exception e) {
            view.exibirErro("Falha ao listar categorias: " + e.getMessage());
        }
    }
}
