# Simulador de Substituição de Páginas

## Descrição
Este projeto implementa um simulador de algoritmos de substituição de páginas de memória. Dada uma sequência de referências de páginas e diversos parâmetros de configuração, o simulador executa uma série de algoritmos (FIFO, Segunda-Chance/CLOCK, LRU, Ótimo, NRU e WS-Clock), calcula o número de faltas e acertos de página, mede o tempo de execução de cada um e gera gráficos comparativos.

## Algoritmos Implementados
- **FIFO** (First-In-First-Out): remove a página que entrou primeiro na memória.
- **Segunda-Chance (CLOCK)**: aprimora o FIFO usando um bit de referência para adiar a substituição de páginas recentemente usadas.
- **LRU** (Least-Recently-Used): substitui a página menos recentemente acessada.
- **Ótimo** (Belady): remove a página cujo próximo uso está mais distante — só aplicável em simuladores.
- **NRU** (Not-Recently-Used): classifica páginas em quatro categorias pelos bits R/M e remove aleatoriamente de uma classe baixa.
- **WS-Clock**: combina working-set com CLOCK, usando bit R, bit M e parâmetro τ.

## Parâmetros de Entrada
| Parâmetro           | Descrição                                                                                             |
|---------------------|-------------------------------------------------------------------------------------------------------|
| `memorySize`        | Número de quadros (frames) disponíveis na memória física.                                             |
| `pageQueueSize`     | Tamanho máximo da fila de referências (para validação; não altera a lógica interna do algoritmo).     |
| `pageCount`         | Número total de páginas distintas no espaço de endereçamento.                                         |
| `refs`              | Lista completa de páginas possíveis (ex.: `A|B|C|D|E`).                                               |
| `pageQueue`         | Sequência real de referências (ex.: `G|D|G|C|...`).                                                    |
| `actionQueue`       | Fila de operações (L = load, E = eject), usada por alguns algoritmos que diferenciam acesso.          |
| `initialState`      | Estado inicial dos quadros; use `0` para quadros vazios (ex.: `0|0|A|B|0`).                            |
| `clockInterruption` | Intervalo de interrupção do relógio em número de referências (0 desabilita; afeta WS-Clock e NRU).   |
| `tau`               | Parâmetro τ para WS-Clock e NRU (idade mínima de página para working-set).                             |
| `algs`              | Lista de algoritmos a executar (valores aceitos: `FIFO`, `CLOCK`, `LRU`, `OPTIMAL`, `NRU`, `WS_CLOCK`).|

Referência: https://sdpm-simulator.netlify.app/simulator

## Organização do projeto
### Camadas
1. API / Controle (SimulatorResource.java):
   Responsável por receber requisições HTTP, delegar ao serviço e retornar as views HTML.
2. Serviço / Negócio (SimulationService.java):
   Implementa a lógica de orquestração das simulações, invocando cada algoritmo e registrando histórico.
3. Algoritmos (algorithm/):
   Cada classe implementa PageReplacementAlgorithm e encapsula a lógica de um método de substituição de páginas.
4. Templates Qute (src/main/resources/templates):
   Arquivos HTML com marcações para exibir formulário, resultados e histórico de simulações.
5. Recursos estáticos (assets/TestValues.json):
   Contém casos de teste para preencher rapidamente o formulário na interface.

---

## Como baixar e executar (Windows)

## Pré-requisitos
- **Java 21** (ou superior)
- **Maven 3.6+**
- **Git**


### 1. Clonar o repositório

Abra o PowerShell (ou terminal de sua preferência) e execute:

```bash
  git clone https://github.com/pedrolucas802/sim-sub-pages
```
```bash
  cd sim-sub-pages
```

### 2. Configurar o Java 21 caso seja preciso:
1. link para o executavel do jdk21(https://download.oracle.com/java/21/latest/jdk-21_windows-x64_bin.exe) 
2. Após instalar o JDK21 configure a variável JAVA_HOME (temporário — só vale para a sessão atual):
   1. powershell:
      ```bash
      $env:JAVA_HOME = "C:\Program Files\Java\jdk-21"
      ```
   2. cmd
      ```bash
      set JAVA_HOME=C:\Program Files\Java\jdk-21
    ```
### 3. Instalar o Maven caso seja preciso:
* obs: é possivel que funcione sem baixar o maven diretamente, mas ao inicializar o projeto irá inicar a instalação das bibliotecas.

1. Baixe o ZIP em https://maven.apache.org/download.cgi
2. Descompacte em uma pasta de sua escolha (por exemplo `C:\apache-maven`)
3. Adicione `C:\apache-maven\bin` à variável de ambiente `PATH`


### 4. Inicializar o projeto

```shell script
  ./mvnw quarkus:dev
```

   A aplicação ficará disponível em `http://localhost:8085`.


## Endpoints

| Método | Caminho    | Descrição                                                                                   |
| ------ | ---------- | ------------------------------------------------------------------------------------------- |
| GET    | `/`        | Carrega a interface HTML para configurar e executar a simulação.                            |
| POST   | `/`        | Recebe parâmetros via formulário, executa a simulação e retorna resultados e gráfico (PNG Base64). |
| GET    | `/history` | Exibe o histórico de todas as simulações já executadas, com dados e gráficos.               |

---

## Exemplo de uso

1. Acesse `http://localhost:8085` no navegador.
2. Preencha os campos do formulário (referências, número de frames, sequência de referências etc.).
3. Clique em **Executar**.
4. Veja o resultado numérico e o gráfico de utilização de frames.
5. Para revisar simulações anteriores, acesse **Histórico**.


---

Pedro L. Barreto (matrícula: 2220318, Universidade de Fortaleza)
