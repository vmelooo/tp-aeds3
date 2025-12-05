package controllers;

import dao.ArquivoStatusTarefa;
import java.util.List;
import models.StatusTarefa;
import views.MenuView;

public class StatusController {

  private final ArquivoStatusTarefa arquivoStatus;
  private final MenuView view;

  public StatusController(ArquivoStatusTarefa arquivoStatus, MenuView view) {
    this.arquivoStatus = arquivoStatus;
    this.view = view;
  }

  // Getter para ArquivoStatus
  public ArquivoStatusTarefa getArquivoStatus() {
    return arquivoStatus;
  }

  // CREATE
  public void criarStatus() {
    try {
      String cor = view.lerTexto("Cor: ");
      int ordem = view.lerInteiro("Ordem: ");
      String nome = view.lerTexto("Nome: ");

      if (nome.isEmpty()) {
        view.exibirErro("O nome do status não pode estar vazio. Criação cancelada.");
        return;
      }

      StatusTarefa s = new StatusTarefa(cor, ordem, nome);
      int id = arquivoStatus.create(s);
      view.exibirSucesso("Status criado com id " + id);

    } catch (NumberFormatException e) {
      view.exibirErro("Ordem inválida. Status não criado.");
    } catch (Exception e) {
      view.exibirErro("Falha ao criar status: " + e.getMessage());
    }
  }

  // LIST
  public void listarStatus() {
    try {
      List<StatusTarefa> lista = arquivoStatus.listarTodosAtivos();
      if (lista.isEmpty()) {
        view.exibirMensagem("Nenhum status ativo cadastrado.");
        return;
      }
      view.exibirMensagem("Status ativos disponíveis:");
      for (StatusTarefa s : lista) {
        view.exibirMensagem(String.format("ID: %d -> Nome: %s | Cor: %s | Ordem: %d",
            s.getId(), s.getNome(), s.getCor(), s.getOrdem()));
      }
    } catch (Exception e) {
      view.exibirErro("Falha ao listar status: " + e.getMessage());
    }
  }

  // UPDATE
  public void atualizarStatus() {
    try {
      int id = view.lerInteiro("ID do Status para atualizar: ");
      StatusTarefa s = arquivoStatus.read(id);

      if (s == null) {
        view.exibirMensagem("Status não encontrado ou está logicamente excluído.");
        return;
      }

      view.exibirMensagem("--- Atualizando Status " + id + " ---");

      String novaCor = view.lerTexto("Nova Cor (" + s.getCor() + ", Enter para manter): ");
      if (!novaCor.isEmpty())
        s.setCor(novaCor);

      String novaOrdemStr = view.lerTexto("Nova Ordem (" + s.getOrdem() + ", Enter para manter): ");
      if (!novaOrdemStr.isEmpty()) {
        try {
          s.setOrdem(Integer.parseInt(novaOrdemStr));
        } catch (NumberFormatException e) {
          view.exibirErro("Ordem inválida. Ordem não alterada.");
        }
      }

      String novoNome = view.lerTexto("Novo Nome (" + s.getNome() + ", Enter para manter): ");
      if (!novoNome.isEmpty())
        s.setNome(novoNome);

      if (arquivoStatus.update(s)) {
        view.exibirSucesso("Status atualizado com sucesso.");
      } else {
        view.exibirErro("Falha ao atualizar.");
      }

    } catch (NumberFormatException e) {
      view.exibirErro("ID inválido.");
    } catch (Exception e) {
      view.exibirErro("Falha ao atualizar status: " + e.getMessage());
    }
  }

  // DELETE
  public void deletarStatus() {
    try {
      int id = view.lerInteiro("ID do Status para exclusão LÓGICA: ");

      if (arquivoStatus.delete(id)) {
        view.exibirSucesso("Status ID " + id + " removido logicamente.");
      } else {
        view.exibirErro("Falha ao remover Status (ID não encontrado/já inativo).");
      }

    } catch (NumberFormatException e) {
      view.exibirErro("ID inválido.");
    } catch (Exception e) {
      view.exibirErro("Falha ao deletar status: " + e.getMessage());
    }
  }
}
