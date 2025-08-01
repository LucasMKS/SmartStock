// src/app/entrada/page.tsx
"use client";

import { useState } from "react";
import { toast } from "sonner";
import { LogIn } from "lucide-react"; // Ícone para entrada

import MovimentacaoForm from "@/components/MovimentacaoForm";
import MovimentacaoTable from "@/components/MovimentacaoTable";
import { Produto, TipoMovimentacao } from "@/types/api";
import {
  Card,
  CardContent,
  CardDescription,
  CardHeader,
  CardTitle,
} from "@/components/ui/card";

export default function EntradaEstoquePage() {
  // Estado para forçar o refresh da tabela após uma operação
  const [refreshTableTrigger, setRefreshTableTrigger] = useState(0);

  // Callback de sucesso para o formulário
  const handleSuccess = (produtoAtualizado: Produto) => {
    toast.success("Entrada de estoque registrada com sucesso!", {
      description: `Produto: ${produtoAtualizado.nome}, Nova Quantidade: ${produtoAtualizado.quantidade}`,
    });
    // Incrementa o estado para disparar o useEffect na tabela
    setRefreshTableTrigger((prev) => prev + 1);
  };

  return (
    <div className="container mx-auto p-4 md:p-6 lg:p-8">
      {/* Cabeçalho da Página */}
      <div className="flex items-center gap-4 mb-8">
        <LogIn className="h-8 w-8 text-green-600" />
        <div>
          <h1 className="text-3xl font-bold tracking-tight">
            Registrar Entrada de Estoque
          </h1>
          <p className="text-muted-foreground">
            Use o leitor de código de barras ou digite manualmente para
            adicionar produtos ao estoque.
          </p>
        </div>
      </div>

      {/* Card para o Formulário de Movimentação */}
      <Card className="mb-12">
        <CardHeader>
          <CardTitle>Registrar Nova Entrada</CardTitle>
          <CardDescription>
            Localize o produto pelo código de barras e informe a quantidade e o
            motivo da entrada.
          </CardDescription>
        </CardHeader>
        <CardContent>
          <MovimentacaoForm mode="entrada" onSuccess={handleSuccess} />
        </CardContent>
      </Card>

      {/* Tabela de Histórico de Entradas (já é um Card) */}
      <MovimentacaoTable
        title="Últimas Entradas Registradas"
        initialFilterTipo={TipoMovimentacao.ENTRADA}
        refreshTrigger={refreshTableTrigger}
        showFilters={true} // Mostrar filtros completos na tabela
        pageSize={5} // Mostrar menos itens para um resumo rápido
      />
    </div>
  );
}
