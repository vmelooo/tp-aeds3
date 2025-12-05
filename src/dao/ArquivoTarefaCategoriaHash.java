package dao;

import models.TarefaCategoria;
import models.structures.HashExtensivel;
import models.structures.RegistroHashTarefaCategoria;

import java.io.IOException;
import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO para TarefaCategoria usando Hash Extensível
 * Substitui a implementação anterior que usava B+ Tree
 * O Hash Extensível é mais eficiente para buscas por chave composta (idTarefa, idCategoria)
 */
public class ArquivoTarefaCategoriaHash {

    private final Arquivo<TarefaCategoria> arquivo;
    private final HashExtensivel<RegistroHashTarefaCategoria> hashIndex;
    private int proximoId;

    public ArquivoTarefaCategoriaHash() throws Exception {
        // Arquivo heap para armazenar os dados completos
        Constructor<TarefaCategoria> construtorHeap = TarefaCategoria.class.getConstructor();
        this.arquivo = new Arquivo<>("tarefas_categorias.db", construtorHeap);

        // Hash Extensível para indexação por chave composta
        Constructor<RegistroHashTarefaCategoria> construtorHash = RegistroHashTarefaCategoria.class.getConstructor();
        this.hashIndex = new HashExtensivel<>(construtorHash, 4, "data/tarefas_categorias.idx");

        this.proximoId = getLastId() + 1;
    }

    // --- CRUD Methods ---

    /**
     * CREATE - Insere um novo relacionamento Tarefa-Categoria
     * Retorna o ID atribuído ou -1 se já existir
     */
    public int create(TarefaCategoria tc) throws Exception {
        // Verifica se já existe este relacionamento
        if (read(tc.getIdTarefa(), tc.getIdCategoria()) != null) {
            return -1; // Relacionamento já existe
        }

        // Atribui ID e salva no arquivo heap
        tc.setId(proximoId++);
        long pos = arquivo.create(tc);

        // Cria entrada no hash com a chave composta
        RegistroHashTarefaCategoria registroHash = new RegistroHashTarefaCategoria(
            tc.getIdTarefa(),
            tc.getIdCategoria(),
            pos
        );

        hashIndex.create(registroHash);

        return tc.getId();
    }

    /**
     * READ - Busca por chave composta (idTarefa, idCategoria)
     * Esta operação é O(1) com Hash Extensível vs O(n) com file scan
     */
    public TarefaCategoria read(int idTarefa, int idCategoria) throws Exception {
        // Cria uma chave de busca
        RegistroHashTarefaCategoria chave = new RegistroHashTarefaCategoria(idTarefa, idCategoria, -1);

        // Busca no hash
        RegistroHashTarefaCategoria resultado = hashIndex.read(chave);

        if (resultado == null) {
            return null;
        }

        // Lê o registro completo do arquivo heap
        return arquivo.read(resultado.getPonteiro());
    }

    /**
     * READ por ID surrogate (menos eficiente, requer varredura)
     */
    public TarefaCategoria read(int id) throws IOException {
        // Para busca por ID, ainda precisamos fazer scan
        // pois o hash está indexado pela chave composta
        return arquivo.read(findPositionById(id));
    }

    /**
     * UPDATE - Atualiza um relacionamento existente
     */
    public boolean update(TarefaCategoria tcAtualizada) throws Exception {
        // Busca o registro atual
        TarefaCategoria atual = read(tcAtualizada.getIdTarefa(), tcAtualizada.getIdCategoria());

        if (atual == null) {
            return false;
        }

        // Mantém o ID original
        tcAtualizada.setId(atual.getId());

        // Atualiza no arquivo heap
        RegistroHashTarefaCategoria chave = new RegistroHashTarefaCategoria(
            atual.getIdTarefa(),
            atual.getIdCategoria(),
            -1
        );
        RegistroHashTarefaCategoria registroHash = hashIndex.read(chave);

        long posAntiga = registroHash.getPonteiro();
        long posNova = arquivo.update(posAntiga, tcAtualizada);

        // Se mudou de posição, atualiza o hash
        if (posNova != posAntiga) {
            hashIndex.delete(registroHash);
            RegistroHashTarefaCategoria novoRegistro = new RegistroHashTarefaCategoria(
                tcAtualizada.getIdTarefa(),
                tcAtualizada.getIdCategoria(),
                posNova
            );
            hashIndex.create(novoRegistro);
        }

        return true;
    }

    /**
     * DELETE - Remove logicamente um relacionamento
     */
    public boolean delete(int idTarefa, int idCategoria) throws Exception {
        // Busca no hash
        RegistroHashTarefaCategoria chave = new RegistroHashTarefaCategoria(idTarefa, idCategoria, -1);
        RegistroHashTarefaCategoria resultado = hashIndex.read(chave);

        if (resultado == null) {
            return false;
        }

        // Remove do arquivo heap (exclusão lógica)
        boolean removido = arquivo.delete(resultado.getPonteiro());

        // Remove do hash
        if (removido) {
            hashIndex.delete(resultado);
        }

        return removido;
    }

    /**
     * DELETE por ID surrogate
     */
    public boolean delete(int id) throws Exception {
        TarefaCategoria tc = read(id);
        if (tc == null) {
            return false;
        }
        return delete(tc.getIdTarefa(), tc.getIdCategoria());
    }

    // --- Helper and List Methods ---

    /**
     * Lista todos os relacionamentos de uma tarefa específica
     * Ainda requer varredura, mas agora podemos otimizar com índice secundário se necessário
     */
    public List<TarefaCategoria> listarPorTarefa(int idTarefa) throws IOException {
        List<TarefaCategoria> lista = new ArrayList<>();

        // Obtém todos os registros do hash
        List<RegistroHashTarefaCategoria> registrosHash = hashIndex.listarTodos();

        for (RegistroHashTarefaCategoria reg : registrosHash) {
            if (reg.getIdTarefa() == idTarefa) {
                TarefaCategoria tc = arquivo.read(reg.getPonteiro());
                if (tc != null && tc.isAtivo()) {
                    lista.add(tc);
                }
            }
        }

        return lista;
    }

    /**
     * Lista todos os relacionamentos de uma categoria específica
     */
    public List<TarefaCategoria> listarPorCategoria(int idCategoria) throws IOException {
        List<TarefaCategoria> lista = new ArrayList<>();

        List<RegistroHashTarefaCategoria> registrosHash = hashIndex.listarTodos();

        for (RegistroHashTarefaCategoria reg : registrosHash) {
            if (reg.getIdCategoria() == idCategoria) {
                TarefaCategoria tc = arquivo.read(reg.getPonteiro());
                if (tc != null && tc.isAtivo()) {
                    lista.add(tc);
                }
            }
        }

        return lista;
    }

    /**
     * Lista todos os relacionamentos ativos
     */
    public List<TarefaCategoria> listarTodos() throws IOException {
        List<TarefaCategoria> lista = new ArrayList<>();
        List<RegistroHashTarefaCategoria> registrosHash = hashIndex.listarTodos();

        for (RegistroHashTarefaCategoria reg : registrosHash) {
            TarefaCategoria tc = arquivo.read(reg.getPonteiro());
            if (tc != null && tc.isAtivo()) {
                lista.add(tc);
            }
        }

        return lista;
    }

    /**
     * Encontra a posição de um registro pelo ID
     */
    private long findPositionById(int id) throws IOException {
        List<RegistroHashTarefaCategoria> registros = hashIndex.listarTodos();

        for (RegistroHashTarefaCategoria reg : registros) {
            TarefaCategoria tc = arquivo.read(reg.getPonteiro());
            if (tc != null && tc.getId() == id) {
                return reg.getPonteiro();
            }
        }

        return -1;
    }

    /**
     * Encontra o maior ID no arquivo
     */
    private int getLastId() throws IOException {
        int maxId = 0;
        List<RegistroHashTarefaCategoria> registros = hashIndex.listarTodos();

        for (RegistroHashTarefaCategoria reg : registros) {
            TarefaCategoria tc = arquivo.read(reg.getPonteiro());
            if (tc != null && tc.getId() > maxId) {
                maxId = tc.getId();
            }
        }

        return maxId;
    }

    /**
     * Retorna estatísticas do hash
     */
    public String getEstatisticas() {
        return hashIndex.getEstatisticas();
    }
}
