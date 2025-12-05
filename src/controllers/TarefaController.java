package controllers;

import dao.*;
import java.util.List;
import models.*;
import views.MenuView;

public class TarefaController {

  private final ArquivoTarefa arquivoTarefa;
  private final ArquivoUsuario arquivoUsuario;
  private final ArquivoStatusTarefa arquivoStatus;
  private final ArquivoCategoria arquivoCategoria;
  private final ArquivoTarefaCategoriaHash arquivoTarefaCategoria;
  private final MenuView view;

  public TarefaController(ArquivoTarefa arquivoTarefa, ArquivoUsuario arquivoUsuario,
      ArquivoStatusTarefa arquivoStatus, ArquivoCategoria arquivoCategoria,
      ArquivoTarefaCategoriaHash arquivoTarefaCategoria, MenuView view) {
    this.arquivoTarefa = arquivoTarefa;
    this.arquivoUsuario = arquivoUsuario;
    this.arquivoStatus = arquivoStatus;
    this.arquivoCategoria = arquivoCategoria;
    this.arquivoTarefaCategoria = arquivoTarefaCategoria;
    this.view = view;
  }

  // Getter para ArquivoTarefa
  public ArquivoTarefa getArquivoTarefa() {
    return arquivoTarefa;
  }

  // CREATE
  public void criarTarefa() {
    try {
      int idUsuario = view.lerInteiro("ID do Usuário responsável: ");
      Usuario u = arquivoUsuario.read(idUsuario);

      if (u == null) {
        view.exibirErro("Usuário ID " + idUsuario + " não encontrado. Tarefa não criada.");
        return;
      }

      String titulo = view.lerTexto("Título: ");
      String descricao = view.lerTexto("Descrição: ");

      listarStatus();
      int idStatus = view.lerInteiro("Escolha o ID do status: ");
      StatusTarefa s = arquivoStatus.read(idStatus);

      if (s == null) {
        view.exibirErro("Status ID " + idStatus + " não encontrado. Tarefa não criada.");
        return;
      }

      Tarefa t = new Tarefa();
      t.setTitulo(titulo);
      t.setDescricao(descricao);
      t.setIdUsuario(idUsuario);
      t.setIdStatus(idStatus);
      t.setPrazo("");

      int id = arquivoTarefa.create(t);
      view.exibirSucesso("Tarefa criada com ID " + id);

    } catch (NumberFormatException e) {
      view.exibirErro("ID inválido. Tarefa não criada.");
    } catch (Exception e) {
      view.exibirErro("Falha ao criar tarefa: " + e.getMessage());
    }
  }

  // READ
  public void listarTarefasPorUsuario() {
    try {
      int idUsuario = view.lerInteiro("ID do Usuário para listar tarefas: ");
      Usuario u = arquivoUsuario.read(idUsuario);

      if (u == null) {
        view.exibirMensagem("Usuário ID " + idUsuario + " não encontrado ou está inativo.");
        return;
      }

      List<Tarefa> lista = arquivoTarefa.listarPorUsuario(idUsuario);

      if (lista.isEmpty()) {
        view.exibirMensagem(
            "Nenhuma tarefa ativa encontrada para o Usuário " + u.getNome() + " (ID " + idUsuario + ").");
        return;
      }

      view.exibirMensagem("Tarefas de " + u.getNome() + ":");
      for (Tarefa t : lista) {
        StatusTarefa s = arquivoStatus.read(t.getIdStatus());
        String statusNome = (s != null && s.isAtivo()) ? s.getNome() : "Status Inativo/Inexistente";
        view.exibirMensagem(String.format("ID: %d | Título: %s | Status: %s", t.getId(), t.getTitulo(), statusNome));
      }

    } catch (NumberFormatException e) {
      view.exibirErro("ID inválido.");
    } catch (Exception e) {
      view.exibirErro("Falha ao listar tarefas: " + e.getMessage());
    }
  }

  // UPDATE
  public void atualizarTarefa() {
    try {
      int id = view.lerInteiro("ID da tarefa para atualizar: ");
      Tarefa t = arquivoTarefa.read(id);

      if (t == null) {
        view.exibirMensagem("Tarefa não encontrada ou está logicamente excluída.");
        return;
      }

      view.exibirMensagem("--- Atualizando Tarefa " + id + " ---");

      String novoTitulo = view.lerTexto("Novo título (" + t.getTitulo() + "): ");
      if (!novoTitulo.isEmpty())
        t.setTitulo(novoTitulo);

      String novaDesc = view.lerTexto("Nova descrição (" + t.getDescricao() + "): ");
      if (!novaDesc.isEmpty())
        t.setDescricao(novaDesc);

      String novoIdUsuarioStr = view
          .lerTexto("Novo ID de Usuário (Atual: " + t.getIdUsuario() + ". Enter para manter): ");
      if (!novoIdUsuarioStr.isEmpty()) {
        try {
          int novoIdUsuario = Integer.parseInt(novoIdUsuarioStr);
          Usuario novoU = arquivoUsuario.read(novoIdUsuario);
          if (novoU == null) {
            view.exibirErro("Novo Usuário ID " + novoIdUsuario + " não encontrado. Usuário não alterado.");
          } else {
            t.setIdUsuario(novoIdUsuario);
          }
        } catch (NumberFormatException e) {
          view.exibirErro("ID de Usuário inválido. Usuário não alterado.");
        }
      }

      listarStatus();
      String novoIdStatusStr = view.lerTexto("Novo ID de Status (Atual: " + t.getIdStatus() + ". Enter para manter): ");
      if (!novoIdStatusStr.isEmpty()) {
        try {
          int novoIdStatus = Integer.parseInt(novoIdStatusStr);
          StatusTarefa novoS = arquivoStatus.read(novoIdStatus);
          if (novoS == null) {
            view.exibirErro("Novo Status ID " + novoIdStatus + " não encontrado. Status não alterado.");
          } else {
            t.setIdStatus(novoIdStatus);
          }
        } catch (NumberFormatException e) {
          view.exibirErro("ID de Status inválido. Status não alterado.");
        }
      }

      if (arquivoTarefa.update(t)) {
        view.exibirSucesso("Tarefa atualizada com sucesso.");
      } else {
        view.exibirErro("Falha ao atualizar.");
      }

    } catch (NumberFormatException e) {
      view.exibirErro("ID inválido.");
    } catch (Exception e) {
      view.exibirErro("Falha ao atualizar tarefa: " + e.getMessage());
    }
  }

  // DELETE
  public void deletarTarefa() {
    try {
      int id = view.lerInteiro("ID da tarefa para exclusão LÓGICA: ");

      if (arquivoTarefa.delete(id)) {
        view.exibirSucesso("Tarefa ID " + id + " removida logicamente.");
      } else {
        view.exibirErro("Falha ao remover Tarefa (ID não encontrado/já inativo).");
      }

    } catch (NumberFormatException e) {
      view.exibirErro("ID inválido.");
    } catch (Exception e) {
      view.exibirErro("Falha ao deletar tarefa: " + e.getMessage());
    }
  }

  // Manage N:N relationship
  public void gerenciarCategoriasTarefa() {
    try {
      int idTarefa = view.lerInteiro("ID da Tarefa para gerenciar categorias: ");

      if (arquivoTarefa.read(idTarefa) == null) {
        view.exibirMensagem("Tarefa não encontrada ou está inativa.");
        return;
      }

      view.exibirMensagem("\n--- Gerenciando Categorias para Tarefa ID " + idTarefa + " ---");

      List<TarefaCategoria> relacoes = arquivoTarefaCategoria.listarPorTarefa(idTarefa);
      if (relacoes.isEmpty()) {
        view.exibirMensagem("Nenhuma categoria associada a esta tarefa.");
      } else {
        view.exibirMensagem("Categorias Atuais:");
        for (TarefaCategoria tc : relacoes) {
          Categoria c = arquivoCategoria.read(tc.getIdCategoria());
          String nome = (c != null) ? c.getNome() : "INEXISTENTE";
          view.exibirMensagem(String.format("ID Categoria: %d | Nome: %s | Prioridade: %d",
              tc.getIdCategoria(), nome, tc.getPrioridade()));
        }
      }

      view.exibirMensagem("\nAções:");
      view.exibirMensagem("1 - Adicionar Categoria");
      view.exibirMensagem("2 - Remover Categoria (Lógico)");
      String opcao = view.lerTexto("Escolha uma opção (ou Enter para sair): ");

      if (opcao.equals("1")) {
        listarCategorias();
        int idCategoria = view.lerInteiro("ID da Categoria a adicionar: ");

        if (arquivoCategoria.read(idCategoria) == null) {
          view.exibirMensagem("Categoria não encontrada.");
          return;
        }

        int prioridade = view.lerInteiro("Prioridade (1-10): ");

        if (arquivoTarefaCategoria.create(new TarefaCategoria(idTarefa, idCategoria, prioridade)) > -1) {
          view.exibirSucesso("Relacionamento adicionado com sucesso.");
        } else {
          view.exibirErro("Relacionamento já existe ou falha na criação.");
        }

      } else if (opcao.equals("2")) {
        int idCategoria = view.lerInteiro("ID da Categoria a remover: ");

        // Com Hash Extensível, podemos deletar diretamente pela chave composta
        if (arquivoTarefaCategoria.delete(idTarefa, idCategoria)) {
          view.exibirSucesso("Relacionamento removido logicamente.");
        } else {
          view.exibirErro("Relacionamento não encontrado.");
        }
      }

    } catch (Exception e) {
      view.exibirErro("Falha ao gerenciar categorias: " + e.getMessage());
    }
  }

  // Helper to list statuses
  private void listarStatus() {
    try {
      List<StatusTarefa> lista = arquivoStatus.listarTodosAtivos();
      if (lista.isEmpty()) {
        view.exibirMensagem("Nenhum status ativo cadastrado.");
        return;
      }
      view.exibirMensagem("Status ativos disponíveis:");
      for (StatusTarefa s : lista) {
        view.exibirMensagem(String.format("ID: %d -> Nomes: %s | Cor: %s | Ordem: %d",
            s.getId(), s.getNome(), s.getCor(), s.getOrdem()));
      }
    } catch (Exception e) {
      view.exibirErro("Falha ao listar status: " + e.getMessage());
    }
  }

  // Helper to list categories
  private void listarCategorias() {
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
