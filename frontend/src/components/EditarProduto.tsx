"use client";

import { useState, useEffect } from "react";
import { put, del } from "@/lib/api";
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
import {
  Dialog,
  DialogContent,
  DialogDescription,
  DialogFooter,
  DialogHeader,
  DialogTitle,
  DialogTrigger,
} from "@/components/ui/dialog";

import { Alert, AlertDescription } from "@/components/ui/alert";
import {
  ReloadIcon,
  Pencil1Icon,
  TrashIcon,
  Cross2Icon,
  ExclamationTriangleIcon,
} from "@radix-ui/react-icons";
import { toast } from "sonner";

interface EditarProdutoProps {
  produto: Produto;
  onSuccess?: () => void;
}

export default function EditarProduto({
  produto,
  onSuccess,
}: EditarProdutoProps) {
  const [isEditDialogOpen, setIsEditDialogOpen] = useState(false);
  const [isDeleteDialogOpen, setIsDeleteDialogOpen] = useState(false);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);

  // Estados do formulário
  const [formData, setFormData] = useState({
    nome: produto.nome,
    categoria: produto.categoria,
    quantidade: produto.quantidade,
    precoCusto: produto.precoCusto,
    precoVenda: produto.precoVenda,
    fornecedor: produto.fornecedor,
  });

  // Resetar formulário quando produto mudar
  useEffect(() => {
    setFormData({
      nome: produto.nome,
      categoria: produto.categoria,
      quantidade: produto.quantidade,
      precoCusto: produto.precoCusto,
      precoVenda: produto.precoVenda,
      fornecedor: produto.fornecedor,
    });
  }, [produto]);

  const handleInputChange = (field: string, value: string | number) => {
    setFormData((prev) => ({
      ...prev,
      [field]: value,
    }));
  };

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    setLoading(true);
    setError(null);

    try {
      const response: ApiResponse<Produto> = await put(
        `/produtos/${produto.codigoBarras}`,
        formData
      );

      if (response.success) {
        toast.success("Produto editado com sucesso!");
        setIsEditDialogOpen(false);
        if (onSuccess) {
          onSuccess();
        }
      } else {
        setError(response.message || "Erro ao editar produto");
        toast.error("Erro ao editar produto", {
          description: response.message,
        });
      }
    } catch (err: any) {
      setError(err.message || "Erro de rede");
      toast.error("Erro de rede", { description: err.message });
    } finally {
      setLoading(false);
    }
  };

  const handleDelete = async () => {
    setLoading(true);
    setError(null);

    try {
      const response: ApiResponse<void> = await del(
        `/produtos/${produto.codigoBarras}`
      );

      if (response.success) {
        toast.success("Produto deletado com sucesso!");
        setIsDeleteDialogOpen(false);
        if (onSuccess) {
          onSuccess();
        }
      } else {
        setError(response.message || "Erro ao deletar produto");
        toast.error("Erro ao deletar produto", {
          description: response.message,
        });
      }
    } catch (err: any) {
      setError(err.message || "Erro de rede");
      toast.error("Erro de rede", { description: err.message });
    } finally {
      setLoading(false);
    }
  };

  const resetForm = () => {
    setFormData({
      nome: produto.nome,
      categoria: produto.categoria,
      quantidade: produto.quantidade,
      precoCusto: produto.precoCusto,
      precoVenda: produto.precoVenda,
      fornecedor: produto.fornecedor,
    });
    setError(null);
  };

  return (
    <div className="flex gap-2">
      {/* Botão Editar */}
      <Dialog open={isEditDialogOpen} onOpenChange={setIsEditDialogOpen}>
        <DialogTrigger asChild>
          <Button variant="outline" size="sm">
            <Pencil1Icon className="mr-2 h-4 w-4" />
            Editar
          </Button>
        </DialogTrigger>
        <DialogContent className="sm:max-w-[500px]">
          <DialogHeader>
            <DialogTitle>Editar Produto</DialogTitle>
            <DialogDescription>
              Edite as informações do produto. O código de barras não pode ser
              alterado.
            </DialogDescription>
          </DialogHeader>

          <form onSubmit={handleSubmit} className="space-y-4">
            <div className="space-y-2">
              <Label htmlFor="codigoBarras">Código de Barras</Label>
              <Input
                id="codigoBarras"
                value={produto.codigoBarras}
                disabled
                className="bg-muted"
              />
            </div>

            <div className="space-y-2">
              <Label htmlFor="nome">Nome *</Label>
              <Input
                id="nome"
                value={formData.nome}
                onChange={(e) => handleInputChange("nome", e.target.value)}
                placeholder="Nome do produto"
                required
              />
            </div>

            <div className="grid grid-cols-2 gap-4">
              <div className="space-y-2">
                <Label htmlFor="categoria">Categoria *</Label>
                <Input
                  id="categoria"
                  value={formData.categoria}
                  onChange={(e) =>
                    handleInputChange("categoria", e.target.value)
                  }
                  placeholder="Categoria"
                  required
                />
              </div>
              <div className="space-y-2">
                <Label htmlFor="quantidade">Quantidade *</Label>
                <Input
                  id="quantidade"
                  type="number"
                  min="0"
                  value={formData.quantidade}
                  onChange={(e) =>
                    handleInputChange(
                      "quantidade",
                      parseInt(e.target.value) || 0
                    )
                  }
                  placeholder="0"
                  required
                />
              </div>
            </div>

            <div className="grid grid-cols-2 gap-4">
              <div className="space-y-2">
                <Label htmlFor="precoCusto">Preço de Custo *</Label>
                <Input
                  id="precoCusto"
                  type="number"
                  min="0"
                  step="0.01"
                  value={formData.precoCusto}
                  onChange={(e) =>
                    handleInputChange(
                      "precoCusto",
                      parseFloat(e.target.value) || 0
                    )
                  }
                  placeholder="0.00"
                  required
                />
              </div>
              <div className="space-y-2">
                <Label htmlFor="precoVenda">Preço de Venda *</Label>
                <Input
                  id="precoVenda"
                  type="number"
                  min="0"
                  step="0.01"
                  value={formData.precoVenda}
                  onChange={(e) =>
                    handleInputChange(
                      "precoVenda",
                      parseFloat(e.target.value) || 0
                    )
                  }
                  placeholder="0.00"
                  required
                />
              </div>
            </div>

            <div className="space-y-2">
              <Label htmlFor="fornecedor">Fornecedor *</Label>
              <Input
                id="fornecedor"
                value={formData.fornecedor}
                onChange={(e) =>
                  handleInputChange("fornecedor", e.target.value)
                }
                placeholder="Nome do fornecedor"
                required
              />
            </div>

            {error && (
              <Alert variant="destructive">
                <ExclamationTriangleIcon className="h-4 w-4" />
                <AlertDescription>{error}</AlertDescription>
              </Alert>
            )}

            <DialogFooter>
              <Button
                type="button"
                variant="outline"
                onClick={resetForm}
                disabled={loading}
              >
                <Cross2Icon className="mr-2 h-4 w-4" />
                Resetar
              </Button>
              <Button
                type="button"
                variant="outline"
                onClick={() => setIsEditDialogOpen(false)}
                disabled={loading}
              >
                Cancelar
              </Button>
              <Button type="submit" disabled={loading}>
                {loading ? (
                  <ReloadIcon className="mr-2 h-4 w-4 animate-spin" />
                ) : (
                  <Pencil1Icon className="mr-2 h-4 w-4" />
                )}
                Salvar Alterações
              </Button>
            </DialogFooter>
          </form>
        </DialogContent>
      </Dialog>

      {/* Botão Deletar */}
      <Dialog open={isDeleteDialogOpen} onOpenChange={setIsDeleteDialogOpen}>
        <DialogTrigger asChild>
          <Button variant="destructive" size="sm">
            <TrashIcon className="mr-2 h-4 w-4" />
            Deletar
          </Button>
        </DialogTrigger>
        <DialogContent>
          <DialogHeader>
            <DialogTitle>Confirmar Exclusão</DialogTitle>
            <DialogDescription>
              Tem certeza que deseja deletar o produto "{produto.nome}"? Esta
              ação não pode ser desfeita e removerá permanentemente o produto do
              sistema.
            </DialogDescription>
          </DialogHeader>
          <DialogFooter>
            <Button
              variant="outline"
              onClick={() => setIsDeleteDialogOpen(false)}
              disabled={loading}
            >
              Cancelar
            </Button>
            <Button
              onClick={handleDelete}
              disabled={loading}
              variant="destructive"
            >
              {loading ? (
                <ReloadIcon className="mr-2 h-4 w-4 animate-spin" />
              ) : (
                <TrashIcon className="mr-2 h-4 w-4" />
              )}
              Deletar Produto
            </Button>
          </DialogFooter>
        </DialogContent>
      </Dialog>
    </div>
  );
}
