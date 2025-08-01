// Conectar como admin e criar banco estoque_db
db = db.getSiblingDB("estoque_db");

// Criar usuário específico para o banco estoque_db
db.createUser({
  user: "estoque_user",
  pwd: "estoque_password",
  roles: [
    {
      role: "readWrite",
      db: "estoque_db",
    },
  ],
});

// Criar coleção de produtos com índices
db.createCollection("produtos");
db.produtos.createIndex({ codigoBarras: 1 }, { unique: true });
db.produtos.createIndex({ nome: "text" });

// Inserir um produto de exemplo para testar
db.produtos.insertOne({
  codigoBarras: "7891234567890",
  nome: "Produto Teste",
  categoria: "Teste",
  quantidade: 10,
  precoCusto: 5.0,
  precoVenda: 10.0,
  fornecedor: "Fornecedor Teste",
});

print("✅ Banco de dados estoque_db inicializado com sucesso!");
print("✅ Usuário estoque_user criado!");
print("✅ Produto de teste inserido!");
