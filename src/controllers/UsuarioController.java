package controllers;

import dao.ArquivoUsuario;
import models.Usuario;
import views.MenuView;

import java.util.List;

public class UsuarioController {

    private final ArquivoUsuario arquivoUsuario;
    private final MenuView view;

    public UsuarioController(ArquivoUsuario arquivoUsuario, MenuView view) {
        this.arquivoUsuario = arquivoUsuario;
        this.view = view;
    }

    /**
     * CREATE - Cria um novo usuário
     */
    public void criarUsuario() {
        try {
            String nome = view.lerTexto("Nome: ");
            String login = view.lerTexto("Login (Email): ");
            String senha = view.lerTexto("Senha: ");

            Usuario u = new Usuario(nome, login, senha);
            int id = arquivoUsuario.create(u);
            view.exibirSucesso("Usuário criado com ID " + id);
        } catch (Exception e) {
            view.exibirErro("Falha ao criar usuário: " + e.getMessage());
        }
    }

    /**
     * READ - Busca um usuário por ID
     */
    public void buscarUsuario() {
        try {
            int id = view.lerInteiro("ID do Usuário para busca: ");
            Usuario u = arquivoUsuario.read(id);

            if (u != null) {
                view.exibirMensagem(u.toString());
            } else {
                view.exibirMensagem("Usuário não encontrado ou está logicamente excluído.");
            }
        } catch (NumberFormatException e) {
            view.exibirErro("ID inválido.");
        } catch (Exception e) {
            view.exibirErro("Falha ao buscar usuário: " + e.getMessage());
        }
    }

    /**
     * UPDATE - Atualiza um usuário existente
     */
    public void atualizarUsuario() {
        try {
            int id = view.lerInteiro("ID do Usuário para atualizar: ");
            Usuario u = arquivoUsuario.read(id);

            if (u == null) {
                view.exibirMensagem("Usuário não encontrado ou está logicamente excluído.");
                return;
            }

            view.exibirMensagem("--- Atualizando Usuário " + id + " ---");
            view.exibirMensagem("Nome atual: " + u.getNome());

            String novoNome = view.lerTexto("Novo nome (Enter para manter): ");
            if (!novoNome.isEmpty()) {
                u.setNome(novoNome);
            }

            if (arquivoUsuario.update(u)) {
                view.exibirSucesso("Usuário atualizado com sucesso.");
            } else {
                view.exibirErro("Falha ao atualizar. (Registro inexistente)");
            }
        } catch (NumberFormatException e) {
            view.exibirErro("ID inválido.");
        } catch (Exception e) {
            view.exibirErro("Falha ao atualizar usuário: " + e.getMessage());
        }
    }

    /**
     * DELETE - Exclusão lógica de um usuário
     */
    public void deletarUsuario() {
        try {
            int id = view.lerInteiro("ID do Usuário para exclusão LÓGICA: ");

            if (arquivoUsuario.delete(id)) {
                view.exibirSucesso("Usuário ID " + id + " removido logicamente.");
            } else {
                view.exibirErro("Falha ao remover Usuário (ID não encontrado/já inativo).");
            }
        } catch (NumberFormatException e) {
            view.exibirErro("ID inválido.");
        } catch (Exception e) {
            view.exibirErro("Falha ao deletar usuário: " + e.getMessage());
        }
    }

    /**
     * LIST - Lista todos os usuários ativos
     */
    public void listarUsuarios() {
        try {
            List<Usuario> lista = arquivoUsuario.listarAtivos();

            if (lista.isEmpty()) {
                view.exibirMensagem("Nenhum usuário ativo cadastrado.");
                return;
            }

            view.exibirMensagem("Usuários Ativos:");
            for (Usuario u : lista) {
                view.exibirMensagem(u.toString());
            }
        } catch (Exception e) {
            view.exibirErro("Falha ao listar usuários: " + e.getMessage());
        }
    }

    /**
     * Gerencia telefones de um usuário (atributo multivalorado)
     */
    public void gerenciarTelefones() {
        try {
            int id = view.lerInteiro("ID do Usuário para gerenciar telefones: ");
            Usuario u = arquivoUsuario.read(id);

            if (u == null) {
                view.exibirMensagem("Usuário não encontrado ou está inativo.");
                return;
            }

            view.exibirMensagem("\n--- Gerenciando Telefones de " + u.getNome() + " (ID " + id + ") ---");

            // Exibe telefones atuais
            List<String> telefones = u.getTelefones();
            if (telefones.isEmpty()) {
                view.exibirMensagem("Nenhum telefone cadastrado.");
            } else {
                view.exibirMensagem("Telefones atuais:");
                for (int i = 0; i < telefones.size(); i++) {
                    view.exibirMensagem((i + 1) + ". " + telefones.get(i));
                }
            }

            view.exibirMensagem("\nAções:");
            view.exibirMensagem("1 - Adicionar Telefone");
            view.exibirMensagem("2 - Remover Telefone");
            String opcao = view.lerTexto("Escolha uma opção (ou Enter para sair): ");

            if (opcao.equals("1")) {
                String telefone = view.lerTexto("Número de telefone: ");

                if (telefone.isEmpty()) {
                    view.exibirErro("Telefone não pode ser vazio.");
                    return;
                }

                u.adicionarTelefone(telefone);

                if (arquivoUsuario.update(u)) {
                    view.exibirSucesso("Telefone adicionado com sucesso.");
                } else {
                    view.exibirErro("Falha ao atualizar usuário.");
                }

            } else if (opcao.equals("2")) {
                if (telefones.isEmpty()) {
                    view.exibirMensagem("Não há telefones para remover.");
                    return;
                }

                String telefone = view.lerTexto("Número de telefone a remover: ");

                if (u.removerTelefone(telefone)) {
                    if (arquivoUsuario.update(u)) {
                        view.exibirSucesso("Telefone removido com sucesso.");
                    } else {
                        view.exibirErro("Falha ao atualizar usuário.");
                    }
                } else {
                    view.exibirErro("Telefone não encontrado.");
                }
            }

        } catch (NumberFormatException e) {
            view.exibirErro("ID inválido.");
        } catch (Exception e) {
            view.exibirErro("Falha ao gerenciar telefones: " + e.getMessage());
        }
    }
}
