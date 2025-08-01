"use client";

import { useEffect, useState } from "react";
import { useRouter } from "next/navigation";
import Link from "next/link";
import { get } from "@/lib/api";
import { ApiResponse, Produto } from "@/types/api";
import {
  Card,
  CardContent,
  CardHeader,
  CardTitle,
} from "@/components/ui/card";
import { Separator } from "@/components/ui/separator";
import { Button } from "@/components/ui/button";
import {
  ArrowRight,
  Box,
  LayoutDashboard,
  LogIn,
  LogOut,
  PlusCircle,
  TrendingDown,
  TrendingUp,
  Warehouse,
  AlertTriangle,
} from "lucide-react";
import { Skeleton } from "@/components/ui/skeleton";
import { toast } from "sonner";
import { useGlobalBarcodeScanner } from "@/hooks/useGlobalBarcodeScanner";

// Interface para os dados do dashboard
interface DashboardData {
  totalProdutos: number;
  estoqueBaixo: number;
  valorEstoqueCusto: number;
  ultimas24h: {
    entradas: number;
    saidas: number;
  };
}

// Componente para o esqueleto de carregamento dos cards
const DashboardSkeleton = () => (
  <div className="grid gap-4 md:grid-cols-2 lg:grid-cols-4">
    <Skeleton className="h-[120px]" />
    <Skeleton className="h-[120px]" />
    <Skeleton className="h-[120px]" />
    <Skeleton className="h-[120px]" />
  </div>
);

export default function HomePage() {
  const router = useRouter();
  const [data, setData] = useState<DashboardData | null>(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);

  // Função para verificar se o produto existe e redirecionar adequadamente
  const handleBarcodeDetection = async (barcode: string) => {
    toast.info(`Código detectado: ${barcode}`, {
      description: "Verificando se o produto existe...",
    });

    try {
      // Fazer a consulta para verificar se o produto existe
      const response: ApiResponse<Produto> = await get(`/produtos/${barcode}`);

      if (response.success && response.data) {
        // Produto encontrado - redirecionar para consulta
        toast.success(`Produto "${response.data.nome}" encontrado!`, {
          description: "Redirecionando para os detalhes...",
        });
        router.push(`/produtos?tab=consultar&codigo=${barcode}`);
      } else {
        // Produto não encontrado - redirecionar para adicionar
        toast.warning("Produto não encontrado", {
          description: "Redirecionando para cadastro...",
        });
        router.push(`/produtos?tab=adicionar&codigo=${barcode}`);
      }
    } catch (error: any) {
      // Em caso de erro na API, assumir que o produto não existe
      toast.warning("Produto não encontrado", {
        description: "Redirecionando para cadastro...",
      });
      router.push(`/produtos?tab=adicionar&codigo=${barcode}`);
    }
  };

  // Hook do Scanner Global para a página inicial
  useGlobalBarcodeScanner({
    onBarcodeDetected: handleBarcodeDetection,
    enabled: true,
    minLength: 8,
  });

  useEffect(() => {
    const fetchDashboardData = async () => {
      setLoading(true);
      setError(null);
      try {
        const response: ApiResponse<DashboardData> = await get(
          "/dashboard/stats"
        );
        if (response.success && response.data) {
          setData(response.data);
        } else {
          throw new Error(response.message || "Falha ao carregar dados.");
        }
      } catch (err: any) {
        setError(err.message);
      } finally {
        setLoading(false);
      }
    };

    fetchDashboardData();
  }, []);

  return (
    <div className="container mx-auto p-4 md:p-6 lg:p-8 space-y-12">
      {/* Cabeçalho */}
      <header className="flex items-center gap-4">
        <LayoutDashboard className="h-10 w-10 text-primary" />
        <div>
          <h1 className="text-3xl md:text-4xl font-bold tracking-tight">
            Painel de Controle
          </h1>
          <p className="text-lg text-muted-foreground">
            Bem-vindo ao seu sistema de gerenciamento de estoque.
          </p>
        </div>
      </header>

      {/* Seção de Ações Rápidas */}
      <section className="space-y-4">
        <h2 className="text-2xl font-semibold tracking-tight">Ações Rápidas</h2>
        <div className="grid gap-4 md:grid-cols-2 lg:grid-cols-4">
          <Link href="/entrada" passHref>
            <Card className="hover:border-primary hover:shadow-lg transition-all cursor-pointer h-full">
              <CardHeader className="flex-row items-center justify-between pb-2">
                <CardTitle className="text-lg font-medium">
                  Registrar Entrada
                </CardTitle>
                <LogIn className="h-6 w-6 text-green-500" />
              </CardHeader>
              <CardContent>
                <p className="text-sm text-muted-foreground">
                  Adicione novos itens ao estoque.
                </p>
              </CardContent>
            </Card>
          </Link>

          <Link href="/saida" passHref>
            <Card className="hover:border-primary hover:shadow-lg transition-all cursor-pointer h-full">
              <CardHeader className="flex-row items-center justify-between pb-2">
                <CardTitle className="text-lg font-medium">
                  Registrar Saída
                </CardTitle>
                <LogOut className="h-6 w-6 text-red-500" />
              </CardHeader>
              <CardContent>
                <p className="text-sm text-muted-foreground">
                  Dê baixa em itens por venda ou perda.
                </p>
              </CardContent>
            </Card>
          </Link>

          <Link href="/produtos" passHref>
            <Card className="hover:border-primary hover:shadow-lg transition-all cursor-pointer h-full">
              <CardHeader className="flex-row items-center justify-between pb-2">
                <CardTitle className="text-lg font-medium">Produtos</CardTitle>
                <PlusCircle className="h-6 w-6 text-blue-500" />
              </CardHeader>
              <CardContent>
                <p className="text-sm text-muted-foreground">
                  Página para gerenciar produtos.
                </p>
              </CardContent>
            </Card>
          </Link>

          <Link href="/historico" passHref>
            <Card className="hover:border-primary hover:shadow-lg transition-all cursor-pointer h-full">
              <CardHeader className="flex-row items-center justify-between pb-2">
                <CardTitle className="text-lg font-medium">
                  Histórico de Movimentações
                </CardTitle>
                <Warehouse className="h-6 w-6 text-muted-foreground" />
              </CardHeader>
              <CardContent>
                <p className="text-sm text-muted-foreground">
                  Visualize o histórico de movimentações e estoque.
                </p>
              </CardContent>
            </Card>
          </Link>
        </div>
      </section>

      <Separator />

      {/* Seção de Visão Geral */}
      <section className="space-y-4">
        <h2 className="text-2xl font-semibold tracking-tight">Visão Geral</h2>
        {loading && <DashboardSkeleton />}
        {error && (
          <div className="flex flex-col items-center justify-center text-destructive bg-destructive/10 p-6 rounded-lg">
            <AlertTriangle className="h-8 w-8 mb-2" />
            <p className="font-semibold">Erro ao carregar dados</p>
            <p className="text-sm">{error}</p>
          </div>
        )}
        {!loading && !error && data && (
          <div className="grid gap-4 md:grid-cols-2 lg:grid-cols-4">
            <Card>
              <CardHeader className="flex-row items-center justify-between pb-2">
                <CardTitle className="text-sm font-medium">
                  Total de Produtos
                </CardTitle>
                <Box className="h-4 w-4 text-muted-foreground" />
              </CardHeader>
              <CardContent>
                <div className="text-2xl font-bold">{data.totalProdutos}</div>
                <p className="text-xs text-muted-foreground">
                  Itens únicos cadastrados
                </p>
              </CardContent>
            </Card>

            <Card>
              <CardHeader className="flex-row items-center justify-between pb-2">
                <CardTitle className="text-sm font-medium">
                  Itens com Estoque Baixo
                </CardTitle>
                <TrendingDown className="h-4 w-4 text-orange-500" />
              </CardHeader>
              <CardContent>
                <div className="text-2xl font-bold text-orange-500">
                  {data.estoqueBaixo}
                </div>
                <p className="text-xs text-muted-foreground">
                  Produtos que precisam de reposição
                </p>
              </CardContent>
            </Card>

            <Card>
              <CardHeader className="flex-row items-center justify-between pb-2">
                <CardTitle className="text-sm font-medium">
                  Valor do Estoque (Custo)
                </CardTitle>
                <TrendingUp className="h-4 w-4 text-muted-foreground" />
              </CardHeader>
              <CardContent>
                <div className="text-2xl font-bold">
                  {data.valorEstoqueCusto.toLocaleString("pt-BR", {
                    style: "currency",
                    currency: "BRL",
                  })}
                </div>
                <p className="text-xs text-muted-foreground">
                  Valor total de custo dos itens
                </p>
              </CardContent>
            </Card>

            <Card>
              <CardHeader className="flex-row items-center justify-between pb-2">
                <CardTitle className="text-sm font-medium">
                  Movimentações (24h)
                </CardTitle>
                <div className="flex gap-2">
                  <LogIn className="h-4 w-4 text-green-500" />
                  <LogOut className="h-4 w-4 text-red-500" />
                </div>
              </CardHeader>
              <CardContent>
                <div className="text-2xl font-bold flex items-center gap-4">
                  <span className="text-green-500">
                    +{data.ultimas24h.entradas}
                  </span>
                  <span className="text-red-500">
                    -{data.ultimas24h.saidas}
                  </span>
                </div>
                <p className="text-xs text-muted-foreground">
                  Entradas e saídas no último dia
                </p>
              </CardContent>
            </Card>
          </div>
        )}
      </section>

      {/* Links Principais */}
      <footer className="flex justify-center gap-4 pt-8">
        <Link href="/produtos" passHref>
          <Button size="lg">
            Gerenciar Catálogo Completo <ArrowRight className="ml-2 h-4 w-4" />
          </Button>
        </Link>
        <Link href="/historico" passHref>
          <Button size="lg" variant="secondary">
            Ver Histórico Detalhado
          </Button>
        </Link>
      </footer>
    </div>
  );
}
