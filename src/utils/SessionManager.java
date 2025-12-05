package utils;

import models.Usuario;

/**
 * Gerenciador de sessão do usuário logado
 * Implementa padrão Singleton
 */
public class SessionManager {
    private static SessionManager instance;
    private Usuario usuarioLogado;

    private SessionManager() {
        // Construtor privado para Singleton
    }

    public static SessionManager getInstance() {
        if (instance == null) {
            instance = new SessionManager();
        }
        return instance;
    }

    public void login(Usuario usuario) {
        this.usuarioLogado = usuario;
    }

    public void logout() {
        this.usuarioLogado = null;
    }

    public Usuario getUsuarioLogado() {
        return usuarioLogado;
    }

    public boolean isLogado() {
        return usuarioLogado != null;
    }

    public int getIdUsuarioLogado() {
        return usuarioLogado != null ? usuarioLogado.getId() : -1;
    }

    public String getNomeUsuarioLogado() {
        return usuarioLogado != null ? usuarioLogado.getNome() : "Não logado";
    }
}
