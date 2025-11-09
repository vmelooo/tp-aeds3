package models.structures;

import java.io.*;
import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.List;

/**
 * Implementação de Hash Extensível Dinâmico
 * Estrutura de indexação que cresce dinamicamente conforme necessário
 *
 * @param <T> Tipo do registro que implementa RegistroHash
 */
public class HashExtensivel<T extends RegistroHash<T>> {

    private int profundidadeGlobal;
    private int capacidadeBucket;
    private List<Bucket<T>> diretorio;
    private String nomeArquivo;
    private Constructor<T> construtor;

    /**
     * Bucket que armazena registros
     */
    private static class Bucket<T extends RegistroHash<T>> implements Serializable {
        private int profundidadeLocal;
        private List<T> registros;
        private int capacidade;

        public Bucket(int capacidade, int profundidadeLocal) {
            this.capacidade = capacidade;
            this.profundidadeLocal = profundidadeLocal;
            this.registros = new ArrayList<>();
        }

        public boolean inserir(T registro) {
            // Verifica se já existe
            for (T r : registros) {
                if (r.equals(registro)) {
                    return false; // Já existe
                }
            }

            if (registros.size() < capacidade) {
                registros.add(registro);
                return true;
            }
            return false; // Bucket cheio
        }

        public boolean remover(T registro) {
            return registros.removeIf(r -> r.equals(registro));
        }

        public T buscar(T chave) {
            for (T r : registros) {
                if (r.equals(chave)) {
                    return r;
                }
            }
            return null;
        }

        public List<T> listar() {
            return new ArrayList<>(registros);
        }

        public boolean estaVazio() {
            return registros.isEmpty();
        }

        public int getProfundidadeLocal() {
            return profundidadeLocal;
        }

        public void setProfundidadeLocal(int profundidadeLocal) {
            this.profundidadeLocal = profundidadeLocal;
        }

        public int getTamanho() {
            return registros.size();
        }
    }

    /**
     * Construtor do Hash Extensível
     *
     * @param construtor Construtor da classe T para instanciar novos objetos
     * @param capacidadeBucket Número máximo de registros por bucket
     * @param nomeArquivo Nome do arquivo para persistência
     */
    public HashExtensivel(Constructor<T> construtor, int capacidadeBucket, String nomeArquivo) throws Exception {
        this.construtor = construtor;
        this.capacidadeBucket = capacidadeBucket;
        this.nomeArquivo = nomeArquivo;

        File arquivo = new File(nomeArquivo);
        if (arquivo.exists()) {
            carregar();
        } else {
            this.profundidadeGlobal = 0;
            this.diretorio = new ArrayList<>();
            // Inicializa com um bucket
            Bucket<T> bucketInicial = new Bucket<>(capacidadeBucket, 0);
            diretorio.add(bucketInicial);
            salvar();
        }
    }

    /**
     * Calcula o índice no diretório baseado no hash do registro
     */
    private int hash(int chave) {
        // Usa os primeiros 'profundidadeGlobal' bits do hash
        int mascara = (1 << profundidadeGlobal) - 1;
        return Math.abs(chave) & mascara;
    }

    /**
     * Insere um registro no hash
     */
    public boolean create(T registro) throws Exception {
        int indice = hash(registro.hashCode());
        Bucket<T> bucket = diretorio.get(indice);

        if (bucket.inserir(registro)) {
            salvar();
            return true;
        }

        // Bucket cheio - precisa dividir
        if (bucket.getProfundidadeLocal() < profundidadeGlobal) {
            // Divisão simples do bucket
            dividirBucket(indice);
        } else {
            // Precisa duplicar o diretório
            duplicarDiretorio();
            dividirBucket(indice);
        }

        // Tenta inserir novamente após divisão
        return create(registro);
    }

    /**
     * Duplica o diretório quando necessário
     */
    private void duplicarDiretorio() {
        int tamanhoAtual = diretorio.size();
        profundidadeGlobal++;

        // Duplica as referências
        for (int i = 0; i < tamanhoAtual; i++) {
            diretorio.add(diretorio.get(i));
        }
    }

    /**
     * Divide um bucket quando está cheio
     */
    private void dividirBucket(int indice) {
        Bucket<T> bucketAntigo = diretorio.get(indice);
        int novaProfundidade = bucketAntigo.getProfundidadeLocal() + 1;

        // Cria dois novos buckets
        Bucket<T> bucket0 = new Bucket<>(capacidadeBucket, novaProfundidade);
        Bucket<T> bucket1 = new Bucket<>(capacidadeBucket, novaProfundidade);

        // Redistribui os registros
        List<T> registrosAntigos = bucketAntigo.listar();
        for (T registro : registrosAntigos) {
            int novoIndice = hash(registro.hashCode());
            int bit = (novoIndice >> (novaProfundidade - 1)) & 1;

            if (bit == 0) {
                bucket0.inserir(registro);
            } else {
                bucket1.inserir(registro);
            }
        }

        // Atualiza o diretório
        int incremento = 1 << novaProfundidade;
        for (int i = 0; i < diretorio.size(); i++) {
            if (diretorio.get(i) == bucketAntigo) {
                int bit = (i >> (novaProfundidade - 1)) & 1;
                if (bit == 0) {
                    diretorio.set(i, bucket0);
                } else {
                    diretorio.set(i, bucket1);
                }
            }
        }
    }

    /**
     * Busca um registro pela chave
     */
    public T read(T chave) {
        int indice = hash(chave.hashCode());
        Bucket<T> bucket = diretorio.get(indice);
        return bucket.buscar(chave);
    }

    /**
     * Remove um registro
     */
    public boolean delete(T registro) throws Exception {
        int indice = hash(registro.hashCode());
        Bucket<T> bucket = diretorio.get(indice);

        boolean removido = bucket.remover(registro);
        if (removido) {
            salvar();
        }
        return removido;
    }

    /**
     * Lista todos os registros
     */
    public List<T> listarTodos() {
        List<T> todos = new ArrayList<>();
        List<Bucket<T>> bucketsProcessados = new ArrayList<>();

        for (Bucket<T> bucket : diretorio) {
            // Evita processar o mesmo bucket múltiplas vezes
            if (!bucketsProcessados.contains(bucket)) {
                todos.addAll(bucket.listar());
                bucketsProcessados.add(bucket);
            }
        }
        return todos;
    }

    /**
     * Salva a estrutura em arquivo
     */
    private void salvar() throws IOException {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(nomeArquivo))) {
            oos.writeInt(profundidadeGlobal);
            oos.writeInt(capacidadeBucket);
            oos.writeInt(diretorio.size());

            // Salva buckets únicos
            List<Bucket<T>> bucketsUnicos = new ArrayList<>();
            List<Integer> mapeamento = new ArrayList<>();

            for (Bucket<T> bucket : diretorio) {
                int idx = bucketsUnicos.indexOf(bucket);
                if (idx == -1) {
                    idx = bucketsUnicos.size();
                    bucketsUnicos.add(bucket);
                }
                mapeamento.add(idx);
            }

            oos.writeInt(bucketsUnicos.size());

            // Salva cada bucket único
            for (Bucket<T> bucket : bucketsUnicos) {
                oos.writeInt(bucket.getProfundidadeLocal());
                oos.writeInt(bucket.getTamanho());

                for (T registro : bucket.listar()) {
                    byte[] dados = registro.toByteArray();
                    oos.writeInt(dados.length);
                    oos.write(dados);
                }
            }

            // Salva mapeamento
            for (Integer idx : mapeamento) {
                oos.writeInt(idx);
            }
        }
    }

    /**
     * Carrega a estrutura do arquivo
     */
    private void carregar() throws Exception {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(nomeArquivo))) {
            profundidadeGlobal = ois.readInt();
            capacidadeBucket = ois.readInt();
            int tamanhoDiretorio = ois.readInt();
            int numBucketsUnicos = ois.readInt();

            // Carrega buckets únicos
            List<Bucket<T>> bucketsUnicos = new ArrayList<>();
            for (int i = 0; i < numBucketsUnicos; i++) {
                int profundidadeLocal = ois.readInt();
                int numRegistros = ois.readInt();

                Bucket<T> bucket = new Bucket<>(capacidadeBucket, profundidadeLocal);

                for (int j = 0; j < numRegistros; j++) {
                    int tamanho = ois.readInt();
                    byte[] dados = new byte[tamanho];
                    ois.readFully(dados);

                    T registro = construtor.newInstance();
                    registro.fromByteArray(dados);
                    bucket.inserir(registro);
                }

                bucketsUnicos.add(bucket);
            }

            // Reconstrói diretório
            diretorio = new ArrayList<>();
            for (int i = 0; i < tamanhoDiretorio; i++) {
                int idx = ois.readInt();
                diretorio.add(bucketsUnicos.get(idx));
            }
        }
    }

    /**
     * Retorna estatísticas do hash
     */
    public String getEstatisticas() {
        List<Bucket<T>> bucketsUnicos = new ArrayList<>();
        for (Bucket<T> bucket : diretorio) {
            if (!bucketsUnicos.contains(bucket)) {
                bucketsUnicos.add(bucket);
            }
        }

        int totalRegistros = 0;
        for (Bucket<T> bucket : bucketsUnicos) {
            totalRegistros += bucket.getTamanho();
        }

        return String.format(
            "Profundidade Global: %d | Buckets Únicos: %d | Tamanho Diretório: %d | Total Registros: %d",
            profundidadeGlobal, bucketsUnicos.size(), diretorio.size(), totalRegistros
        );
    }
}
