// Tipos base da sua ApiResponse Java
export interface ApiResponse<T> {
  success: boolean;
  message: string;
  data: T | null;
  timestamp: string; // LocalDateTime será serializado como string ISO 8601
}

// Tipos para Produto
export interface Produto {
  codigoBarras: string;
  nome: string;
  categoria: string;
  quantidade: number;
  precoCusto: number;
  precoVenda: number;
  fornecedor: string;
}

// Tipos para Requisições de Estoque (Adicionar/Remover)
export interface EstoqueRequest {
  quantidade: number;
  motivo: string;
}

// Tipos para Resposta de Estoque Atualizado (se você optou por retornar)
export interface EstoqueAtualizadoResponse {
  codigoBarras: string;
  novaQuantidade: number;
}

// Tipos para Movimentação
export enum TipoMovimentacao {
  ENTRADA = "ENTRADA",
  SAIDA = "SAIDA",
}

export interface Movimentacao {
  id: string;
  produto: Produto; // Ou o tipo correspondente se for diferente
  quantidade: number;
  tipo: TipoMovimentacao;
  dataHora: number[]; // <-- GARANTA QUE ESTEJA COMO number[]
  motivo: string;
  codigoBarrasProduto: string;
}

// Você pode adicionar outros tipos conforme a necessidade (e.g., ProdutoRequest se for diferente do Produto)
