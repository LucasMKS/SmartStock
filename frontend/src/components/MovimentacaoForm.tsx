// src/components/MovimentacaoForm.tsx
"use client";

import { useState, useRef, useEffect } from "react";
import { toast } from "sonner";
import { Button } from "@/components/ui/button";
import { Input } from "@/components/ui/input";
import { Label } from "@/components/ui/label";
import { Separator } from "@/components/ui/separator";
import { Alert, AlertDescription, AlertTitle } from "@/components/ui/alert";

// Ícones
import {
  ReloadIcon,
  PlusCircledIcon,
  MinusCircledIcon,
  Cross2Icon,
  // PackageIcon,
} from "@radix-ui/react-icons";
import { ScanBarcodeIcon } from "lucide-react";

// API e Tipos
import { get, put } from "@/lib/api";
import { ApiResponse, Produto, EstoqueRequest } from "@/types/api";

// Hooks
import { useGlobalBarcodeScanner } from "@/hooks/useGlobalBarcodeScanner";
import { MotivoCombobox } from "./MotivoCombobox"; // <-- Importar o novo componente

type FormMode = "entrada" | "saida";

interface MovimentacaoFormProps {
  mode: FormMode;
  onSuccess?: (produtoAtualizado: Produto) => void;
}

export default function MovimentacaoForm({
  mode,
  onSuccess,
}: MovimentacaoFormProps) {
  const [codigoBarras, setCodigoBarras] = useState<string>("");
  const [quantidade, setQuantidade] = useState<number>(0);
  const [motivo, setMotivo] = useState<string>("");
  const [produtoEncontrado, setProdutoEncontrado] = useState<Produto | null>(
    null
  );
  const [loading, setLoading] = useState(false);

  const codigoBarrasRef = useRef<HTMLInputElement>(null);

  useEffect(() => {
    codigoBarrasRef.current?.focus();
  }, []);

  const handleClearForm = () => {
    setCodigoBarras("");
    setQuantidade(0);
    setMotivo("");
    setProdutoEncontrado(null);
    setLoading(false);
    toast.info("Formulário limpo.");
    codigoBarrasRef.current?.focus();
  };

  const buscarDetalhesProduto = async (codigo: string) => {
    if (!codigo.trim()) {
      toast.warning("Digite um código para buscar.");
      return;
    }
    setLoading(true);
    setProdutoEncontrado(null);
    try {
      const response: ApiResponse<Produto> = await get(`/produtos/${codigo}`);
      if (response.success && response.data) {
        setProdutoEncontrado(response.data);
        toast.success("Produto encontrado!", {
          description: `"${response.data.nome}" carregado.`,
        });
      } else {
        toast.error("Produto não encontrado", {
          description: `Código ${codigo} não cadastrado.`,
        });
      }
    } catch (err: any) {
      toast.error("Erro ao buscar produto", {
        description: err.message || "Ocorreu um erro inesperado.",
      });
    } finally {
      setLoading(false);
    }
  };

  useGlobalBarcodeScanner({
    onBarcodeDetected: async (scannedBarcode) => {
      setCodigoBarras(scannedBarcode);
      await buscarDetalhesProduto(scannedBarcode);
    },
    enabled: true,
  });

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    setLoading(true);

    if (!produtoEncontrado) {
      toast.error("Nenhum produto selecionado.");
      setLoading(false);
      return;
    }
    if (quantidade <= 0) {
      toast.error("A quantidade deve ser maior que zero.");
      setLoading(false);
      return;
    }
    if (!motivo.trim()) {
      toast.error("O motivo é obrigatório.");
      setLoading(false);
      return;
    }

    try {
      const estoqueRequest: EstoqueRequest = { quantidade, motivo };
      const endpoint =
        mode === "entrada"
          ? `/produtos/${codigoBarras}/adicionarEstoque`
          : `/produtos/${codigoBarras}/removerEstoque`;

      const response: ApiResponse<Produto> = await put(
        endpoint,
        estoqueRequest
      );

      // --- LÓGICA CORRIGIDA ---
      // Verifica se a operação foi um sucesso, mesmo que o backend não retorne success: true.
      // Condição 1: O backend retorna o objeto do produto atualizado.
      // Condição 2 (Fallback): O backend retorna uma mensagem de sucesso.
      const isSuccess =
        response.data ||
        (response.message &&
          (response.message.toLowerCase().includes("sucesso") ||
            response.message.toLowerCase().includes("adicionado") ||
            response.message.toLowerCase().includes("removido")));

      if (isSuccess) {
        let produtoAtualizado: Produto;

        if (response.data) {
          // Se a resposta inicial já continha os dados, usamos eles.
          produtoAtualizado = response.data;
        } else {
          // Se não, fazemos uma nova busca para garantir os dados atualizados.
          const novaResposta: ApiResponse<Produto> = await get(
            `/produtos/${codigoBarras}`
          );
          if (novaResposta.success && novaResposta.data) {
            produtoAtualizado = novaResposta.data;
          } else {
            // Se a busca falhar, lançamos um erro.
            throw new Error(
              "Não foi possível obter os dados atualizados do produto."
            );
          }
        }

        toast.success(
          `${mode === "entrada" ? "Entrada" : "Saída"} registrada!`,
          {
            description: `Produto: ${produtoAtualizado.nome}, Estoque Atual: ${produtoAtualizado.quantidade}`,
          }
        );
        onSuccess?.(produtoAtualizado);
        handleClearForm();
      } else {
        // Se não for sucesso, lança o erro como antes.
        throw new Error(
          response.message || "Falha na movimentação de estoque."
        );
      }
    } catch (err: any) {
      toast.error("Erro na movimentação", {
        description: err.message || "Ocorreu um erro inesperado.",
      });
    } finally {
      setLoading(false);
    }
  };

  const isEntrada = mode === "entrada";
  const themeColor = isEntrada ? "green" : "red";
  const themeAccent = isEntrada ? "text-green-600" : "text-red-600";
  const themeBorder = isEntrada ? "border-green-200" : "border-red-200";
  const themeBg = isEntrada ? "bg-green-50" : "bg-red-50";

  // Listas de motivos sugeridos baseadas no modo
  const motivosSugeridos = isEntrada
    ? [
        "Compra de fornecedor",
        "Devolução de cliente",
        "Ajuste de inventário (positivo)",
        "Entrada inicial",
      ]
    : [
        "Venda",
        "Ajuste de perda",
        "Uso interno",
        "Transferência",
        "Devolução ao fornecedor",
      ];

  return (
    <div className="w-full">
      <Alert className={`mb-6 ${themeBorder} ${themeBg}`}>
        <ScanBarcodeIcon className={`h-5 w-5 ${themeAccent}`} />
        <AlertTitle className={themeAccent}>
          Leitor de Código de Barras Ativo
        </AlertTitle>
        <AlertDescription>
          Escaneie o código em qualquer lugar da página para buscar um produto.
        </AlertDescription>
      </Alert>

      <form onSubmit={handleSubmit} className="space-y-6">
        {/* Seção 1: Busca do Produto */}
        <div className="space-y-2">
          <Label htmlFor="codigoBarras" className="text-base font-semibold">
            Localizar Produto
          </Label>
          <div className="flex items-center space-x-2">
            <Input
              id="codigoBarras"
              type="text"
              placeholder="Digite ou escaneie o código de barras"
              value={codigoBarras}
              onChange={(e) => setCodigoBarras(e.target.value)}
              onKeyDown={(e) => {
                if (e.key === "Enter") {
                  e.preventDefault();
                  buscarDetalhesProduto(codigoBarras);
                }
              }}
              ref={codigoBarrasRef}
              disabled={loading}
              required
            />
            <Button
              type="button"
              onClick={() => buscarDetalhesProduto(codigoBarras)}
              disabled={loading || !codigoBarras.trim()}
              variant="outline"
              className="px-14"
            >
              {loading ? (
                <ReloadIcon className="h-4 w-4 animate-spin" />
              ) : (
                "Buscar"
              )}
            </Button>
          </div>
        </div>

        {/* Seção 2: Detalhes do Produto e Movimentação */}
        {produtoEncontrado && (
          <div className="space-y-6 rounded-lg border bg-background p-4 md:p-6">
            {/* Detalhes do Produto */}
            <div className="flex flex-col md:flex-row md:items-start gap-4">
              {/* <PackageIcon className="h-12 w-12 text-muted-foreground flex-shrink-0 mt-1" /> */}
              <div className="flex-grow">
                <p className="text-sm text-muted-foreground">
                  Produto Localizado
                </p>
                <h3 className="text-xl font-bold text-primary">
                  {produtoEncontrado.nome}
                </h3>
                <p className="text-sm font-mono text-muted-foreground">
                  {produtoEncontrado.codigoBarras}
                </p>
                <div className="mt-2 text-lg">
                  <span>Estoque Atual: </span>
                  <span className="font-bold">
                    {produtoEncontrado.quantidade}
                  </span>
                </div>
              </div>
            </div>

            <Separator />

            {/* Campos de Quantidade e Motivo */}
            <div>
              <Label className="text-base font-semibold">
                2. Informar Movimentação
              </Label>
              <div className="mt-2 grid grid-cols-1 md:grid-cols-2 gap-4">
                <div className="space-y-2">
                  <Label htmlFor="quantidade">Quantidade</Label>
                  <Input
                    id="quantidade"
                    type="number"
                    min="1"
                    placeholder="Ex: 5"
                    value={quantidade === 0 ? "" : quantidade}
                    onChange={(e) =>
                      setQuantidade(parseInt(e.target.value) || 0)
                    }
                    disabled={loading}
                    required
                    className={`border-2 focus:border-${themeColor}-500`}
                  />
                </div>
                <div className="space-y-2">
                  <Label htmlFor="motivo">Motivo</Label>
                  {/* Substituir o Input pelo Combobox */}
                  <MotivoCombobox
                    value={motivo}
                    onChange={setMotivo}
                    motivosSugeridos={motivosSugeridos}
                    placeholder={
                      isEntrada
                        ? "Ex: Compra de fornecedor"
                        : "Ex: Venda, Ajuste de perda"
                    }
                    disabled={loading}
                  />
                </div>
              </div>
            </div>
          </div>
        )}

        {/* Seção 3: Ações */}
        <div className="flex flex-col sm:flex-row justify-end space-y-2 sm:space-y-0 sm:space-x-2 pt-4">
          <Button
            type="button"
            variant="outline"
            onClick={handleClearForm}
            disabled={loading}
          >
            <Cross2Icon className="mr-2 h-4 w-4" />
            Limpar
          </Button>
          <Button
            type="submit"
            disabled={
              loading || !produtoEncontrado || quantidade <= 0 || !motivo.trim()
            }
            className={
              isEntrada
                ? "bg-green-600 hover:bg-green-700"
                : "bg-red-600 hover:bg-red-700"
            }
          >
            {loading ? (
              <>
                <ReloadIcon className="mr-2 h-4 w-4 animate-spin" />
                Processando...
              </>
            ) : (
              <>
                {isEntrada ? (
                  <PlusCircledIcon className="mr-2 h-4 w-4" />
                ) : (
                  <MinusCircledIcon className="mr-2 h-4 w-4" />
                )}
                Confirmar {isEntrada ? "Entrada" : "Saída"}
              </>
            )}
          </Button>
        </div>
      </form>
    </div>
  );
}
