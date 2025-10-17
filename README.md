# TP AEDS 03 - Gerenciador de Tarefas

## Índice

- [Pré-requisitos](#pré_requisitos)
- [Instalação e Uso](#instalação_e_uso)
- [Estrutura](#estrutura)

---

## Pré-requisitos

* [Git](https://git-scm.com/downloads)

---

## Instalação e Uso

1. Clone o repositório:
    ```bash
    git clone https://github.com/vmelooo/tp-aeds3.git
    cd tp-aeds3/src
    ```
2. Execute `javac Main.java` para compilar o projeto.
3. Execute `java Main` para iniciar o projeto.
4. Use o menu no terminal para executar as operações de CRUD.

---

## Estrutura
```
├── data                       # Registros e arquivos de índices
└── src                        # Código-fonte do projeto
    ├── Main.java              # Função principal a ser executada
    ├── controllers            # Controlador das operações do CRUD
    ├── dao                    # Data Access Objects
    ├── data
    ├── models                 # Entidades do projeto
    │   └── structures         # Entidades de estruturas para chaves de pesquisa
    └── views                  # Interface do menu
```

---