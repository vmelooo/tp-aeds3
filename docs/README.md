# TP AEDS 03 - Gerenciador de Tarefas

## Índice

- [TP AEDS 03 - Gerenciador de Tarefas](#tp-aeds-03---gerenciador-de-tarefas)
  - [Índice](#índice)
  - [Pré-requisitos](#pré-requisitos)
  - [Instalação e Uso](#instalação-e-uso)
  - [Estrutura](#estrutura)

---

## Pré-requisitos

* [Git](https://git-scm.com/downloads)
* [Maven](https://maven.apache.org/)

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
├── data                        # Registros e arquivos de índices 
├── docs                        # Arquivos de documentação
└── src                         # Código-fonte do projeto
    ├── Main.java               # Função principal a ser executada (CLI)
    ├── MainJavaFX.java         # Função principal a ser executada (GUI)
    ├── controllers             # Controlador das operações do CRUD
    ├── dao                     # Data Access Objects
    ├── models                  # Entidades do projeto
    │   └── structures          # Entidades de estruturas para chaves de pesquisa
    ├── utils                   # Funções utilitárias e auxiliaries
    └── views                   # Interface do menu
```

---