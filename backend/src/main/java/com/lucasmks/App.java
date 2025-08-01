package com.lucasmks;

import java.util.Scanner;

import com.lucasmks.domain.model.Produto;
import com.lucasmks.domain.usecase.BuscarProdutoUseCase;
import com.lucasmks.domain.usecase.CadastrarProdutoUseCase;
import com.lucasmks.infrastructure.database.MongoConnection;
import com.lucasmks.infrastructure.factory.ApplicationFactory;


public class App 
{
    public static void main(String[] args) {
        System.out.println("🏪 === SISTEMA DE CONTROLE DE ESTOQUE ===");
        System.out.println();
        
        try {
            // Inicializar conexão MongoDB
            initializeDatabase();
            
            // Inicializar aplicação
            initializeApplication();
            
            // Menu principal
            showMainMenu();
            
        } catch (Exception e) {
            System.err.println("❌ Erro ao inicializar aplicação: " + e.getMessage());
            e.printStackTrace();
        } finally {
            // Fechar conexões
            cleanup();
        }
    }
    
    private static void initializeDatabase() {
        System.out.println("📊 Inicializando banco de dados...");
        
        // Forçar inicialização da conexão
        MongoConnection.initializeConnection();
        
        // Verificar se está conectado
        if (MongoConnection.isConnected()) {
            System.out.println("✅ Banco de dados inicializado com sucesso!");
        } else {
            throw new RuntimeException("Falha ao conectar com o banco de dados");
        }
        System.out.println();
    }
    
    private static void initializeApplication() {
        System.out.println("🔧 Inicializando componentes da aplicação...");
        
        // Inicializar factory (lazy loading)
        ApplicationFactory.getProdutoRepository();
        
        System.out.println("✅ Aplicação inicializada com sucesso!");
        System.out.println();
    }
    
    private static void showMainMenu() {
        Scanner scanner = new Scanner(System.in);
        
        while (true) {
            System.out.println("📋 === MENU PRINCIPAL ===");
            System.out.println("1. Cadastrar Produto");
            System.out.println("2. Buscar Produto");
            System.out.println("3. Sair");
            System.out.print("Escolha uma opção: ");
            
            int opcao = scanner.nextInt();
            scanner.nextLine(); // Consumir quebra de linha
            
            switch (opcao) {
                case 1:
                    cadastrarProduto(scanner);
                    break;
                case 2:
                    buscarProduto(scanner);
                    break;
                case 3:
                    System.out.println("👋 Encerrando aplicação...");
                    return;
                default:
                    System.out.println("❌ Opção inválida!");
            }
            System.out.println();
        }
    }
    
    private static void cadastrarProduto(Scanner scanner) {
        try {
            System.out.println("📝 === CADASTRAR PRODUTO ===");
            
            System.out.print("Código de Barras: ");
            String codigoBarras = scanner.nextLine();
            
            System.out.print("Nome: ");
            String nome = scanner.nextLine();
            
            System.out.print("Categoria: ");
            String categoria = scanner.nextLine();
            
            System.out.print("Quantidade: ");
            int quantidade = scanner.nextInt();
            
            System.out.print("Preço de Custo: ");
            Double precoCusto = scanner.nextDouble();
            
            System.out.print("Preço de Venda: ");
            Double precoVenda = scanner.nextDouble();
            scanner.nextLine(); // Consumir quebra de linha
            
            System.out.print("Fornecedor: ");
            String fornecedor = scanner.nextLine();
            
            Produto produto = new Produto(codigoBarras, nome, categoria, quantidade, precoCusto, precoVenda, fornecedor);
            
            CadastrarProdutoUseCase useCase = ApplicationFactory.getCadastrarProdutoUseCase();
            useCase.executar(produto);
            
            System.out.println("✅ Produto cadastrado com sucesso!");
            
        } catch (Exception e) {
            System.err.println("❌ Erro ao cadastrar produto: " + e.getMessage());
        }
    }
    
    private static void buscarProduto(Scanner scanner) {
        try {
            System.out.println("🔍 === BUSCAR PRODUTO ===");
            
            System.out.print("Código de Barras: ");
            String codigoBarras = scanner.nextLine();
            
            BuscarProdutoUseCase useCase = ApplicationFactory.getBuscarProdutoUseCase();
            Produto produto = useCase.executar(codigoBarras);

            if (produto != null) {
                System.out.println("✅ Produto encontrado:");
                System.out.println("   Nome: " + produto.getNome());
                System.out.println("   Categoria: " + produto.getCategoria());
                System.out.println("   Quantidade: " + produto.getQuantidade());
                System.out.println("   Preço Custo: R$ " + produto.getPrecoCusto());
                System.out.println("   Preço Venda: R$ " + produto.getPrecoVenda());
                System.out.println("   Fornecedor: " + produto.getFornecedor());
            } else {
                System.out.println("❌ Produto não encontrado!");
            }
            
        } catch (Exception e) {
            System.err.println("❌ Erro ao buscar produto: " + e.getMessage());
        }
    }
    
    private static void cleanup() {
        System.out.println("🧹 Finalizando recursos...");
        MongoConnection.closeConnection();
        System.out.println("✅ Aplicação encerrada!");
    }
}