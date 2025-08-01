"use client";

import { useState, useEffect, useRef } from "react";
import { get } from "@/lib/api";
import { ApiResponse, Produto } from "@/types/api";
import { Button } from "@/components/ui/button";
import { Input } from "@/components/ui/input";
import {
  Card,
  CardContent,
  CardDescription,
  CardFooter,
  CardHeader,
  CardTitle,
} from "@/components/ui/card";
import { Badge } from "@/components/ui/badge";
import { Separator } from "@/components/ui/separator";
import { Alert, AlertDescription, AlertTitle } from "@/components/ui/alert";
import {
  ReloadIcon,
  MagnifyingGlassIcon,
  Cross2Icon,
  // PackageIcon,
  InfoCircledIcon,
} from "@radix-ui/react-icons";
import { toast } from "sonner";
import EditarProduto from "./EditarProduto";

interface ProdutoDetalhesProps {
  onClose?: () => void;
  onClear?: () => void;
  codigoInicial?: string;
  onSuccess?: () => void;
}

export default function ProdutoDetalhes({
  onClose,
  onClear,
  codigoInicial = "",
  onSuccess,
}: ProdutoDetalhesProps) {
  const [codigoBarras, setCodigoBarras] = useState("");
  const [produto, setProduto] = useState<Produto | null>(null);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);
  const [pesquisado, setPesquisado] = useState(false);
  const inputRef = useRef<HTMLInputElement>(null);

  // Efeito para buscar automaticamente quando receber código inicial
  useEffect(() => {
    if (codigoInicial && codigoInicial !== codigoBarras) {
      setCodigoBarras(codigoInicial);
      buscarProdutoAutomaticamente(codigoInicial);
    }
  }, [codigoInicial]);

  const buscarProdutoAutomaticamente = async (codigo: string) => {
    setLoading(true);
    setError(null);
    setProduto(null);
    setPesquisado(false);

    try {
      const response: ApiResponse<Produto> = await get(
        `/produtos/${codigo.trim()}`
      );

      if (response.success && response.data) {
        setProduto(response.data);
        console.log("Produto encontrado automaticamente:", response.data);
      } else {
        setError(response.message || "Produto não encontrado");
      }
    } catch (err: any) {
      setError(err.message || "Erro ao buscar produto");
      console.error("Erro ao buscar produto:", err);
    } finally {
      setLoading(false);
      setPesquisado(true);
    }
  };

  const buscarProduto = async () => {
    if (!codigoBarras.trim()) {
      setError("Por favor, digite um código de barras");
      return;
    }

    setLoading(true);
    setError(null);
    setProduto(null);
    setPesquisado(false);

    try {
      const response: ApiResponse<Produto> = await get(
        `/produtos/${codigoBarras.trim()}`
      );

      if (response.success && response.data) {
        setProduto(response.data);
        console.log("Produto encontrado:", response.data);
      } else {
        setError(response.message || "Produto não encontrado");
      }
    } catch (err: any) {
      setError(err.message || "Erro ao buscar produto");
      console.error("Erro ao buscar produto:", err);
    } finally {
      setLoading(false);
      setPesquisado(true);
    }
  };

  const handleKeyPress = (e: React.KeyboardEvent) => {
    if (e.key === "Enter") {
      buscarProduto();
    }
  };

  const limpar = () => {
    setCodigoBarras("");
    setProduto(null);
    setError(null);
    setPesquisado(false);

    // Notificar o componente pai que foi limpo
    if (onClear) {
      onClear();
    }
  };

  const getEstoqueStatus = (
    quantidade: number
  ): { variant: "default" | "destructive" | "secondary"; text: string } => {
    if (quantidade <= 0) return { variant: "destructive", text: "Sem Estoque" };
    if (quantidade <= 10)
      return { variant: "secondary", text: "Estoque Baixo" };
    return { variant: "default", text: "Em Estoque" };
  };

  return (
    <Card className="w-full max-w-2xl mx-auto">
      <CardHeader>
        <div className="flex items-center justify-between">
          <div className="flex items-center gap-3">
            <MagnifyingGlassIcon className="h-6 w-6" />
            <CardTitle className="text-xl">Consultar Produto</CardTitle>
          </div>
          {onClose && (
            <Button variant="ghost" size="icon" onClick={onClose}>
              <Cross2Icon className="h-5 w-5" />
            </Button>
          )}
        </div>
        <CardDescription>
          Use o leitor de código de barras ou digite manualmente para ver os
          detalhes de um produto.
        </CardDescription>
      </CardHeader>

      <CardContent className="space-y-4">
        <div className="flex space-x-2">
          <Input
            ref={inputRef}
            placeholder="Digite ou escaneie o código de barras..."
            value={codigoBarras}
            onChange={(e) => setCodigoBarras(e.target.value)}
            onKeyDown={handleKeyPress}
            autoFocus={!codigoInicial}
          />
          <Button
            onClick={buscarProduto}
            disabled={loading || !codigoBarras.trim()}
          >
            {loading ? (
              <ReloadIcon className="h-4 w-4 animate-spin" />
            ) : (
              "Buscar"
            )}
          </Button>
        </div>

        {loading && (
          <div className="flex items-center justify-center py-8 text-muted-foreground">
            <ReloadIcon className="mr-2 h-4 w-4 animate-spin" />
            Buscando produto...
          </div>
        )}

        {error && (
          <Alert variant="destructive">
            <InfoCircledIcon className="h-4 w-4" />
            <AlertTitle>Produto não encontrado</AlertTitle>
            <AlertDescription>
              {error}. Verifique o código ou cadastre um novo produto.
            </AlertDescription>
          </Alert>
        )}

        {produto && (
          <div className="space-y-4 rounded-lg border p-4">
            <div className="flex flex-col sm:flex-row sm:items-start gap-4">
              {/* <PackageIcon className="h-12 w-12 text-muted-foreground flex-shrink-0 mt-1" /> */}
              <div className="flex-grow">
                <div className="flex justify-between items-baseline">
                  <h3 className="text-2xl font-bold text-primary">
                    {produto.nome}
                  </h3>
                  <Badge variant={getEstoqueStatus(produto.quantidade).variant}>
                    {getEstoqueStatus(produto.quantidade).text}
                  </Badge>
                </div>
                <p className="font-mono text-muted-foreground">
                  {produto.codigoBarras}
                </p>
              </div>
            </div>

            <Separator />

            <div className="grid grid-cols-2 md:grid-cols-3 gap-4 text-sm">
              <div className="space-y-1">
                <p className="text-muted-foreground">Estoque Atual</p>
                <p className="font-semibold text-lg">
                  {produto.quantidade} unidades
                </p>
              </div>
              <div className="space-y-1">
                <p className="text-muted-foreground">Preço de Custo</p>
                <p className="font-semibold">
                  R$ {produto.precoCusto?.toFixed(2) ?? "N/A"}
                </p>
              </div>
              <div className="space-y-1">
                <p className="text-muted-foreground">Preço de Venda</p>
                <p className="font-semibold text-lg text-green-600">
                  R$ {produto.precoVenda?.toFixed(2) ?? "N/A"}
                </p>
              </div>
              <div className="space-y-1 col-span-2 md:col-span-1">
                <p className="text-muted-foreground">Categoria</p>
                <p className="font-semibold">{produto.categoria || "N/A"}</p>
              </div>
              <div className="space-y-1 col-span-2">
                <p className="text-muted-foreground">Fornecedor</p>
                <p className="font-semibold">{produto.fornecedor || "N/A"}</p>
              </div>
            </div>
          </div>
        )}
      </CardContent>

      <CardFooter className="flex justify-between">
        <div>
          {produto && (
            <EditarProduto
              produto={produto}
              onSuccess={() => {
                if (onSuccess) onSuccess();
                // Recarregar os dados do produto após edição
                buscarProdutoAutomaticamente(produto.codigoBarras);
              }}
            />
          )}
        </div>
        <Button variant="outline" onClick={limpar}>
          <Cross2Icon className="mr-2 h-4 w-4" />
          Limpar Consulta
        </Button>
      </CardFooter>
    </Card>
  );
}
