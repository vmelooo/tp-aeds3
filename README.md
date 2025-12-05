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
    cd tp-aeds3
    ```
2. Execute `mvn clean javafx:run` para compilar e iniciar o projeto.

---

## Estrutura
```
.
├── docs                        # Arquivos de documentação
└── src                         # Código-fonte do projeto
    ├── Main.java               # Função principal a ser executada
    ├── controllers             # Controlador das operações do CRUD
    ├── dao                     # Data Access Objects
    ├── data                    # Registros e arquivos de índices 
    ├── models                  # Entidades do projeto
    │   └── structures          # Entidades de estruturas para chaves de pesquisa
    └── views                   # Interface do menu
```

---