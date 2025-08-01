"use client";

import { useState, useEffect } from "react";
import { post } from "@/lib/api";
import { ApiResponse, Produto } from "@/types/api";
import { Button } from "@/components/ui/button";
import { Input } from "@/components/ui/input";
import { Label } from "@/components/ui/label";
import {
  Card,
  CardContent,
  CardDescription,
  CardFooter,
  CardHeader,
  CardTitle,
} from "@/components/ui/card";
import { Separator } from "@/components/ui/separator";
import { Alert, AlertDescription, AlertTitle } from "@/components/ui/alert";
import { Badge } from "@/components/ui/badge";
import {
  ReloadIcon,
  Cross2Icon,
  CheckIcon,
  PlusIcon,
  InfoCircledIcon,
} from "@radix-ui/react-icons";
import { toast } from "sonner";

interface AdicionarProdutoProps {
  onClose?: () => void;
  onSuccess?: (produto: Produto) => void;
  onClear?: () => void;
  codigoInicial?: string;
}

interface ProdutoForm {
  codigoBarras: string;
  nome: string;
  categoria: string;
  quantidade: number;
  precoCusto: number;
  precoVenda: number;
  fornecedor: string;
}

export default function AdicionarProduto({
  onClose,
  onSuccess,
  onClear,
  codigoInicial = "",
}: AdicionarProdutoProps) {
  const [formData, setFormData] = useState<ProdutoForm>({
    codigoBarras: "",
    nome: "",
    categoria: "",
    quantidade: 0,
    precoCusto: 0,
    precoVenda: 0,
    fornecedor: "",
  });

  const [loading, setLoading] = useState(false);
  const [success, setSuccess] = useState(false);
  const [error, setError] = useState<string | null>(null);
  const [codigoBarrasLido, setCodigoBarrasLido] = useState(false);

  // Simular leitura de código de barras (quando o campo recebe um valor completo rapidamente)
  //   useEffect(() => {
  //     if (formData.codigoBarras && formData.codigoBarras.length >= 8) {
  //       setCodigoBarrasLido(true);

  //       // Simular dados vindos da maquininha (em um cenário real, viria de uma API externa)
  //       if (formData.codigoBarras.length >= 12 && !formData.nome) {
  //         simularDadosMaquininha(formData.codigoBarras);
  //       }
  //     } else {
  //       setCodigoBarrasLido(false);
  //     }
  //   }, [formData.codigoBarras]);

  //   // Simula dados que viriam de uma maquininha/leitor
  //   const simularDadosMaquininha = (codigo: string) => {
  //     // Em um cenário real, aqui você faria uma requisição para uma API de produtos
  //     // que retornaria informações básicas baseadas no código de barras

  //     const dadosSimulados = {
  //       nome: `Produto ${codigo.slice(-4)}`, // Simula um nome baseado no código
  //       categoria: "Categoria Padrão",
  //       fornecedor: "Fornecedor Automático",
  //     };

  //     setFormData((prev) => ({
  //       ...prev,
  //       ...dadosSimulados,
  //     }));
  //   };

  // Efeito para preencher código inicial
  useEffect(() => {
    if (codigoInicial && codigoInicial !== formData.codigoBarras) {
      setFormData((prev) => ({
        ...prev,
        codigoBarras: codigoInicial,
      }));
      setCodigoBarrasLido(true);
    }
  }, [codigoInicial]);

  const handleInputChange = (
    field: keyof ProdutoForm,
    value: string | number
  ) => {
    setFormData((prev) => ({
      ...prev,
      [field]: value,
    }));

    // Limpar mensagens ao editar
    if (error) setError(null);
    if (success) setSuccess(false);
  };

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();

    // Validações
    if (!formData.codigoBarras.trim()) {
      setError("Código de barras é obrigatório");
      return;
    }

    if (!formData.nome.trim()) {
      setError("Nome do produto é obrigatório");
      return;
    }

    if (formData.quantidade < 0) {
      setError("Quantidade não pode ser negativa");
      return;
    }

    if (formData.precoCusto < 0 || formData.precoVenda < 0) {
      setError("Preços não podem ser negativos");
      return;
    }

    setLoading(true);
    setError(null);

    try {
      const response: ApiResponse<Produto> = await post("/produtos", formData);

      if (response.success && response.data) {
        toast.success("Produto cadastrado com sucesso!", {
          description: `"${response.data.nome}" foi adicionado ao catálogo.`,
        });
        onSuccess?.(response.data);
        limparFormulario();
      } else {
        setError(response.message || "Erro ao cadastrar produto");
      }
    } catch (err: any) {
      setError(err.message || "Erro ao cadastrar produto");
    } finally {
      setLoading(false);
    }
  };

  const limparFormulario = () => {
    setFormData({
      codigoBarras: "",
      nome: "",
      categoria: "",
      quantidade: 0,
      precoCusto: 0,
      precoVenda: 0,
      fornecedor: "",
    });
    setError(null);
    setSuccess(false);
    setCodigoBarrasLido(false);

    // Notificar o componente pai que o formulário foi limpo
    if (onClear) {
      onClear();
    }
  };

  return (
    <Card className="w-full max-w-3xl mx-auto">
      <CardHeader>
        <div className="flex items-center justify-between">
          <div className="flex items-center gap-3">
            <PlusIcon className="h-6 w-6" />
            <CardTitle className="text-xl">Adicionar Novo Produto</CardTitle>
          </div>
          {onClose && (
            <Button variant="ghost" size="icon" onClick={onClose}>
              <Cross2Icon className="h-5 w-5" />
            </Button>
          )}
        </div>
        <CardDescription>
          {codigoInicial
            ? `Produto com código "${codigoInicial}" não encontrado. Preencha os dados abaixo para cadastrá-lo.`
            : "Preencha as informações do novo produto para adicioná-lo ao catálogo."}
        </CardDescription>
      </CardHeader>

      <form onSubmit={handleSubmit}>
        <CardContent className="space-y-6">
          {error && (
            <Alert variant="destructive">
              <InfoCircledIcon className="h-4 w-4" />
              <AlertTitle>Erro no Cadastro</AlertTitle>
              <AlertDescription>{error}</AlertDescription>
            </Alert>
          )}

          {/* Seção 1: Informações Principais */}
          <div className="space-y-4">
            <div className="space-y-2">
              <Label htmlFor="codigoBarras">Código de Barras *</Label>
              <div className="flex items-center gap-2">
                {/* <ScanLine className="h-5 w-5 text-muted-foreground" /> */}
                <Input
                  id="codigoBarras"
                  placeholder="Digite ou escaneie o código..."
                  value={formData.codigoBarras}
                  onChange={(e) =>
                    handleInputChange("codigoBarras", e.target.value)
                  }
                  className={
                    codigoBarrasLido ? "border-blue-500 bg-blue-50/50" : ""
                  }
                  autoFocus
                  required
                />
              </div>
              {codigoBarrasLido && (
                <p className="text-sm text-blue-600">
                  Código preenchido automaticamente.
                </p>
              )}
            </div>

            <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
              <div className="space-y-2">
                <Label htmlFor="nome">Nome do Produto *</Label>
                <Input
                  id="nome"
                  placeholder="Ex: Arroz Integral 1kg"
                  value={formData.nome}
                  onChange={(e) => handleInputChange("nome", e.target.value)}
                  required
                />
              </div>
              <div className="space-y-2">
                <Label htmlFor="categoria">Categoria</Label>
                <Input
                  id="categoria"
                  placeholder="Ex: Alimentos, Bebidas"
                  value={formData.categoria}
                  onChange={(e) =>
                    handleInputChange("categoria", e.target.value)
                  }
                />
              </div>
            </div>
          </div>

          <Separator />

          {/* Seção 2: Valores e Estoque */}
          <div className="space-y-4">
            <h3 className="text-md font-semibold">Estoque e Precificação</h3>
            <div className="grid grid-cols-1 md:grid-cols-3 gap-4">
              <div className="space-y-2">
                <Label htmlFor="quantidade">Qtd. Inicial</Label>
                <Input
                  id="quantidade"
                  type="number"
                  min="0"
                  placeholder="0"
                  value={formData.quantidade}
                  onChange={(e) =>
                    handleInputChange(
                      "quantidade",
                      parseInt(e.target.value) || 0
                    )
                  }
                />
              </div>
              <div className="space-y-2">
                <Label htmlFor="precoCusto">Custo (R$)</Label>
                <Input
                  id="precoCusto"
                  type="number"
                  step="0.01"
                  min="0"
                  placeholder="0.00"
                  value={formData.precoCusto}
                  onChange={(e) =>
                    handleInputChange(
                      "precoCusto",
                      parseFloat(e.target.value) || 0
                    )
                  }
                />
              </div>
              <div className="space-y-2">
                <Label htmlFor="precoVenda">Venda (R$)</Label>
                <Input
                  id="precoVenda"
                  type="number"
                  step="0.01"
                  min="0"
                  placeholder="0.00"
                  value={formData.precoVenda}
                  onChange={(e) =>
                    handleInputChange(
                      "precoVenda",
                      parseFloat(e.target.value) || 0
                    )
                  }
                />
              </div>
            </div>
          </div>

          <Separator />

          {/* Seção 3: Fornecedor */}
          <div className="space-y-2">
            <Label htmlFor="fornecedor">Fornecedor</Label>
            <Input
              id="fornecedor"
              placeholder="Ex: Distribuidora ABC Ltda"
              value={formData.fornecedor}
              onChange={(e) => handleInputChange("fornecedor", e.target.value)}
            />
          </div>
        </CardContent>

        <CardFooter className="flex justify-end space-x-2">
          <Button
            type="button"
            variant="outline"
            onClick={limparFormulario}
            disabled={loading}
          >
            <Cross2Icon className="mr-2 h-4 w-4" />
            Limpar
          </Button>
          <Button
            type="submit"
            disabled={loading || !formData.codigoBarras || !formData.nome}
          >
            {loading ? (
              <>
                <ReloadIcon className="mr-2 h-4 w-4 animate-spin" />
                Cadastrando...
              </>
            ) : (
              <>
                <CheckIcon className="mr-2 h-4 w-4" />
                Salvar Produto
              </>
            )}
          </Button>
        </CardFooter>
      </form>
    </Card>
  );
}
