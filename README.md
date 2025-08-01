# 📦 SmartStock: Sistema de Controle de Estoque Ágil

## 📚 Descrição do Projeto

O **SmartStock** é um sistema de controle de estoque simples e moderno, projetado para otimizar o fluxo de entrada e saída de produtos utilizando a leitura de códigos de barras. O projeto foi desenvolvido com uma arquitetura **full-stack**, seguindo os princípios **SOLID** para garantir um código limpo, modular e de fácil manutenção.

### Funcionalidades principais:

- **Gerenciar Produtos**: Cadastrar, consultar e visualizar todos os produtos em estoque.
- **Movimentação de Estoque**: Realizar operações de entrada e saída de forma rápida, registrando a quantidade e o motivo de cada movimentação.
- **Histórico Completo**: Acessar um histórico detalhado de todas as movimentações, com filtros dinâmicos por produto, tipo, motivo e período.
- **Leitura Ágil**: O sistema detecta a leitura de códigos de barras em qualquer parte da aplicação para automatizar a busca e o preenchimento de dados.

---

## 🚀 Tecnologias Utilizadas

### 🔧 Backend (Java)

- **Linguagem**: Java 21  
- **Framework Web**: Spark Java (API REST)  
- **Gerenciador de Dependências**: Maven  
- **Banco de Dados**: MongoDB (driver oficial Java)  
- **Arquitetura**: Clean Architecture (Domain, Use Cases, Repositories) com testes TDD  
- **Logs**: SLF4J com Logback  

### 🎨 Frontend (Next.js)

- **Framework**: Next.js  
- **Linguagem**: TypeScript  
- **Estilização**: Tailwind CSS  
- **Componentes UI**: [shadcn/ui](https://ui.shadcn.com) (tabelas, formulários, botões, etc.)  
- **Gerenciamento de Tabela**: TanStack Table (filtros, ordenação, paginação)  
- **Notificações**: Sonner (toasts)  
- **Hook Customizado**: `useGlobalBarcodeScanner` para detecção de códigos de barras  

---

## ⚙️ Como Iniciar o Projeto

Este projeto utiliza **Docker Compose** para configurar o ambiente MongoDB de forma rápida e consistente.

### ✅ Pré-requisitos

- Docker e Docker Compose instalados  
- Java 21 JDK instalado  
- Maven instalado  
- Node.js e npm instalados  

### ▶️ Passos de Execução

1. **Clone o repositório**:

```bash
git clone https://github.com/seu-usuario/scanstock.git
cd scanstock
```

2. **Inicie o banco de dados com Docker Compose**  
Isso criará e inicializará o contêiner do MongoDB com dados de exemplo definidos em `docker/init-mongo.js`.

```bash
docker compose up -d
```

3. **Execute o Backend**  
A partir da pasta `backend/`, compile e execute a aplicação Java:

```bash
cd backend
mvn clean install
mvn exec:java
```

A API estará disponível em: [http://localhost:4567](http://localhost:4567)

4. **Execute o Frontend**  
A partir da pasta `frontend/`, instale as dependências e inicie o servidor de desenvolvimento:

```bash
cd ../frontend
npm install
npm run dev
```

O frontend estará disponível em: [http://localhost:3000](http://localhost:3000)

---

## 🎥 Demonstração em Vídeo

Assista a um breve vídeo de demonstração das funcionalidades do sistema em ação:

👉 [Clique aqui para assistir no YouTube](https://youtu.be/pfUC81NcQYs)  
[![Assista à demonstração](https://img.youtube.com/vi/pfUC81NcQYs/0.jpg)](https://youtu.be/pfUC81NcQYs)

---

## 📎 Licença

Este projeto está sob a licença MIT. Veja o arquivo `LICENSE` para mais detalhes.
