# Sistema CRUD de Produtos

# 🛠️ Compilação e Execução

# 1. Clonar e Compilar

```bash
# Navegar para o diretório do projeto
cd PB_Tp1

# Compilar o projeto
mvn clean compile

# Executar testes
mvn test

# Verificar cobertura de código
mvn jacoco:report

# Gerar JAR executável
mvn package

```bash

# 2. Executar a Aplicação
# Antes de qualquer coisa ir no DataBaseConfig.Java e Verificar as credenciais do postgree

# Usando Maven
mvn exec:java 
#ou
mvn clean compile exec:java

# Ou usando JAR gerado
java -jar target/produtos-crud-system-1.0.0.jar
```

# 🎯 Boas Práticas Implementadas

# Clean Code
- Nomes expressivos para classes, métodos e variáveis
- Métodos pequenos e com responsabilidade única
- Comentários objetivos e não redundantes
- Organização lógica do código

# SOLID Principles
- **SRP**: Cada classe tem uma responsabilidade específica
- **OCP**: Sistema extensível sem modificar código existente
- **LSP**: Implementações respeitam contratos das interfaces
- **ISP**: Interfaces segregadas (Command/Query repositories)
- **DIP**: Dependência de abstrações, não de implementações

# Command Query Separation (CQS)
- Separação clara entre comandos que modificam estado e queries que consultam
- Interfaces distintas: `ProductCommandRepository` e `ProductQueryRepository`

# Imutabilidade
- Value objects imutáveis
- Entidades que retornam novas instâncias em atualizações
- Prevenção de efeitos colaterais

# Type Safety
- Value objects específicos em vez de tipos primitivos
- Eliminação de "valores mágicos"
- Validações centralizadas

# Tratamento de Erros
- Hierarquia de exceções bem definida
- Mensagens de erro claras e acionáveis
- Categorização de erros por tipo
- Falha rápida com validações

# 📊 Testes e Quality Assurance

# Estratégia de Testes
- **Testes unitários** para value objects e lógicas isoladas
- **Testes de integração** para fluxos completos
- **Property-based testing** com Jqwik para validar propriedades matemáticas
- **Testes de limites** para validações de entrada
- **Testes negativos** para cenários de erro

# Técnicas Aplicadas
- **Partições equivalentes** para cobertura de cenários
- **Análise de limite** para valores extremos
- **Tabelas de decisão** implementadas implicitamente nos testes
- **Testes de robustez** simulando falhas do sistema

# Métricas
- Cobertura mínima de 80% (verificada automaticamente)
- Cobertura de branches e condições
- Testes para todos os cenários de exceção


**Desenvolvido por:** [Cadu]  
**Disciplina:** Projeto de Bloco: Engenharia Disciplinada de Softwares
