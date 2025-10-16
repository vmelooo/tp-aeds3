package controllers;

import dao.*;
import models.*;
import views.MenuView;

import java.util.List;

public class ApontamentoController {

    private final ArquivoApontamentoDeHoras arquivoApontamento;
    private final ArquivoUsuario arquivoUsuario;
    private final ArquivoTarefa arquivoTarefa;
    private final MenuView view;

    public ApontamentoController(ArquivoApontamentoDeHoras arquivoApontamento,
                                 ArquivoUsuario arquivoUsuario,
                                 ArquivoTarefa arquivoTarefa,
                                 MenuView view) {
        this.arquivoApontamento = arquivoApontamento;
        this.arquivoUsuario = arquivoUsuario;
        this.arquivoTarefa = arquivoTarefa;
        this.view = view;
    }

    /**
     * CREATE - Cria um novo apontamento
     */
    public void criarApontamento() {
        try {
            int idUsuario = view.lerInteiro("ID do Usuário que está apontando as horas: ");
            if (arquivoUsuario.read(idUsuario) == null) {
                view.exibirErro("Usuário não encontrado ou inativo. Apontamento não criado.");
                return;
            }

            int idTarefa = view.lerInteiro("ID da Tarefa: ");
            if (arquivoTarefa.read(idTarefa) == null) {
                view.exibirErro("Tarefa não encontrada ou inativa. Apontamento não criado.");
                return;
            }

            String descricao = view.lerTexto("Descrição do Apontamento: ");
            double duracao = view.lerDouble("Duração em horas (ex: 1.5): ");

            ApontamentoDeHoras a = new ApontamentoDeHoras();
            a.setIdUsuario(idUsuario);
            a.setIdTarefa(idTarefa);
            a.setDescricao(descricao);
            a.setDuracao(duracao);

            int id = arquivoApontamento.create(a);
            view.exibirSucesso("Apontamento criado com ID " + id);

        } catch (NumberFormatException e) {
            view.exibirErro("Entrada inválida. Apontamento não criado.");
        } catch (Exception e) {
            view.exibirErro("Falha ao criar apontamento: " + e.getMessage());
        }
    }

    /**
     * LIST - Lista todos os apontamentos ativos
     */
    public void listarApontamentos() {
        try {
            List<ApontamentoDeHoras> lista = arquivoApontamento.listarTodosAtivos();
            if (lista.isEmpty()) {
                view.exibirMensagem("Nenhum apontamento ativo cadastrado.");
                return;
            }
            view.exibirMensagem("Apontamentos Ativos:");
            for (ApontamentoDeHoras a : lista) {
                view.exibirMensagem(a.toString());
            }
        } catch (Exception e) {
            view.exibirErro("Falha ao listar apontamentos: " + e.getMessage());
        }
    }

    /**
     * DELETE - Exclusão lógica de um apontamento
     */
    public void deletarApontamento() {
        try {
            int id = view.lerInteiro("ID do Apontamento para exclusão LÓGICA: ");

            if (arquivoApontamento.delete(id)) {
                view.exibirSucesso("Apontamento ID " + id + " removido logicamente.");
            } else {
                view.exibirErro("Falha ao remover Apontamento (ID não encontrado/já inativo).");
            }

        } catch (NumberFormatException e) {
            view.exibirErro("ID inválido.");
        } catch (Exception e) {
            view.exibirErro("Falha ao deletar apontamento: " + e.getMessage());
        }
    }
}
