"use client";

import { useState, useEffect, useMemo } from "react";
import { useSearchParams } from "next/navigation";
import { toast } from "sonner";
import { get } from "@/lib/api";
import { Produto, ApiResponse } from "@/types/api";

// Importações do TanStack Table
import {
  ColumnDef,
  flexRender,
  getCoreRowModel,
  useReactTable,
  getPaginationRowModel,
  getSortedRowModel,
  getFilteredRowModel,
  SortingState,
  ColumnFiltersState,
  VisibilityState,
  RowSelectionState,
} from "@tanstack/react-table";

// Componentes Shadcn/ui
import {
  Table,
  TableBody,
  TableCell,
  TableHead,
  TableHeader,
  TableRow,
} from "@/components/ui/table";
import {
  Card,
  CardContent,
  CardDescription,
  CardFooter,
  CardHeader,
  CardTitle,
} from "@/components/ui/card";
import { Button } from "@/components/ui/button";
import { Input } from "@/components/ui/input";
import {
  DropdownMenu,
  DropdownMenuCheckboxItem,
  DropdownMenuContent,
  DropdownMenuTrigger,
} from "@/components/ui/dropdown-menu";
import {
  Select,
  SelectContent,
  SelectItem,
  SelectTrigger,
  SelectValue,
} from "@/components/ui/select";
import { Tabs, TabsContent, TabsList, TabsTrigger } from "@/components/ui/tabs";

// Importar Ícones
import {
  ReloadIcon,
  CaretSortIcon,
  ChevronDownIcon,
  PlusCircledIcon,
  Cross2Icon,
} from "@radix-ui/react-icons";

// Importar Componentes Filhos
import AdicionarProduto from "@/components/AdicionarProduto";
import ProdutoDetalhes from "@/components/ProdutoDetalhes";
import { useGlobalBarcodeScanner } from "@/hooks/useGlobalBarcodeScanner";

export default function ProdutosPage() {
  const searchParams = useSearchParams();

  // Estados do componente
  const [produtos, setProdutos] = useState<Produto[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);
  const [availableCategories, setAvailableCategories] = useState<string[]>([]);
  const [activeTab, setActiveTab] = useState("lista");
  const [codigoParaAdicionar, setCodigoParaAdicionar] = useState("");
  const [codigoParaConsultar, setCodigoParaConsultar] = useState("");

  // Estados da Tabela
  const [sorting, setSorting] = useState<SortingState>([]);
  const [columnFilters, setColumnFilters] = useState<ColumnFiltersState>([]);
  const [columnVisibility, setColumnVisibility] = useState<VisibilityState>({});
  const [rowSelection, setRowSelection] = useState<RowSelectionState>({});

  // Função para buscar produtos
  const fetchProdutos = async () => {
    setLoading(true);
    setError(null);
    try {
      const response: ApiResponse<Produto[]> = await get("/produtos");
      if (response.success && response.data) {
        setProdutos(response.data);
        const categories = [
          ...new Set(response.data.map((p) => p.categoria).filter(Boolean)),
        ];
        setAvailableCategories(categories);
        toast.success("Lista de produtos atualizada!");
      } else {
        setError(response.message || "Falha ao carregar produtos.");
        toast.error("Erro ao buscar produtos", {
          description: response.message,
        });
      }
    } catch (err: any) {
      setError(err.message || "Erro de rede.");
      toast.error("Erro de rede", { description: err.message });
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchProdutos();
  }, []);

  // Efeito para ler parâmetros da URL e configurar estado inicial
  useEffect(() => {
    const tabFromUrl = searchParams.get("tab");
    const codigoFromUrl = searchParams.get("codigo");

    if (
      tabFromUrl &&
      ["lista", "consultar", "adicionar"].includes(tabFromUrl)
    ) {
      setActiveTab(tabFromUrl);
    }

    if (codigoFromUrl) {
      if (tabFromUrl === "adicionar") {
        setCodigoParaAdicionar(codigoFromUrl);
      } else if (tabFromUrl === "consultar") {
        setCodigoParaConsultar(codigoFromUrl);
      }
    }
  }, [searchParams]);

  // Callbacks para os componentes filhos
  const handleProdutoAdicionado = () => {
    fetchProdutos();
    setActiveTab("lista");
  };

  const handleFormularioLimpo = () => {
    setCodigoParaAdicionar("");
  };

  const handleConsultaLimpa = () => {
    setCodigoParaConsultar("");
  };

  const handleTabChange = (value: string) => {
    setActiveTab(value);
    if (activeTab === "adicionar" && value !== "adicionar") {
      setCodigoParaAdicionar("");
    }
    if (activeTab === "consultar" && value !== "consultar") {
      setCodigoParaConsultar("");
    }
  };

  const handleAddProduct = () => {
    setCodigoParaAdicionar("");
    setActiveTab("adicionar");
  };

  // Função inteligente para lidar com códigos de barras detectados
  const handleBarcodeDetection = async (barcode: string) => {
    toast.info(`Código detectado: ${barcode}`, {
      description: "Verificando se o produto existe...",
    });

    try {
      // Fazer a consulta para verificar se o produto existe
      const response: ApiResponse<Produto> = await get(`/produtos/${barcode}`);

      if (response.success && response.data) {
        // Produto encontrado - ir para consulta
        toast.success(`Produto "${response.data.nome}" encontrado!`, {
          description: "Carregando detalhes...",
        });
        setCodigoParaConsultar(barcode);
        setActiveTab("consultar");
      } else {
        // Produto não encontrado - ir para adicionar
        toast.warning("Produto não encontrado", {
          description: "Redirecionando para cadastro...",
        });
        setCodigoParaAdicionar(barcode);
        setActiveTab("adicionar");
      }
    } catch (error: any) {
      // Em caso de erro na API, assumir que o produto não existe
      toast.warning("Produto não encontrado", {
        description: "Redirecionando para cadastro...",
      });
      setCodigoParaAdicionar(barcode);
      setActiveTab("adicionar");
    }
  };

  useGlobalBarcodeScanner({
    onBarcodeDetected: handleBarcodeDetection,
    enabled: true,
    minLength: 8,
  });

  // Definição das colunas da tabela
  const columns: ColumnDef<Produto>[] = useMemo(
    () => [
      {
        accessorKey: "codigoBarras",
        header: ({ column }) => (
          <Button
            variant="ghost"
            onClick={() => column.toggleSorting(column.getIsSorted() === "asc")}
            className="w-full justify-start px-0 text-left"
          >
            Código de Barras
            <CaretSortIcon className="ml-2 h-4 w-4" />
          </Button>
        ),
        cell: ({ row }) => {
          const codigo = row.getValue<string>("codigoBarras");
          return (
            <Button
              variant="link"
              className="h-auto p-0 font-mono text-left justify-start hover:text-blue-800 no-underline"
              onClick={() => {
                // Definir o código para consulta
                setCodigoParaConsultar(codigo);
                // Mudar para a aba de consulta
                setActiveTab("consultar");
                // Feedback visual para o usuário
                toast.info(`Consultando produto: ${codigo}`);
              }}
            >
              {codigo}
            </Button>
          );
        },
      },
      {
        accessorKey: "nome",
        header: ({ column }) => (
          <Button
            variant="ghost"
            onClick={() => column.toggleSorting(column.getIsSorted() === "asc")}
            className="w-full justify-start px-0 text-left"
          >
            Nome
            <CaretSortIcon className="ml-2 h-4 w-4" />
          </Button>
        ),
        cell: (info) => info.getValue<string>(),
      },
      {
        accessorKey: "categoria",
        header: ({ column }) => (
          <Button
            variant="ghost"
            onClick={() => column.toggleSorting(column.getIsSorted() === "asc")}
            className="w-full justify-start px-0 text-left"
          >
            Categoria
            <CaretSortIcon className="ml-2 h-4 w-4" />
          </Button>
        ),
        filterFn: "includesString",
        cell: (info) => info.getValue<string>(),
      },
      {
        accessorKey: "quantidade",
        header: ({ column }) => (
          <Button
            variant="ghost"
            onClick={() => column.toggleSorting(column.getIsSorted() === "asc")}
            className="w-full justify-start px-0 text-right"
          >
            Quantidade
            <CaretSortIcon className="ml-2 h-4 w-4" />
          </Button>
        ),
        cell: (info) => (
          <div className="text-left font-medium">{info.getValue<number>()}</div>
        ),
      },
      {
        accessorKey: "precoVenda",
        header: ({ column }) => (
          <Button
            variant="ghost"
            onClick={() => column.toggleSorting(column.getIsSorted() === "asc")}
            className="w-full justify-start px-0 text-right"
          >
            Preço de Venda
            <CaretSortIcon className="ml-2 h-4 w-4" />
          </Button>
        ),
        cell: (info) => (
          <div className="text-left font-medium">
            R$ {info.getValue<number>().toFixed(2)}
          </div>
        ),
      },
    ],
    []
  );

  const table = useReactTable({
    data: produtos,
    columns,
    getCoreRowModel: getCoreRowModel(),
    getPaginationRowModel: getPaginationRowModel(),
    getSortedRowModel: getSortedRowModel(),
    getFilteredRowModel: getFilteredRowModel(),
    onSortingChange: setSorting,
    onColumnFiltersChange: setColumnFilters,
    onColumnVisibilityChange: setColumnVisibility,
    onRowSelectionChange: setRowSelection,
    state: {
      sorting,
      columnFilters,
      columnVisibility,
      rowSelection,
    },
  });

  const handleClearFilters = () => {
    table.resetColumnFilters();
  };

  if (loading) {
    return (
      <div className="flex items-center justify-center min-h-screen">
        <ReloadIcon className="mr-2 h-4 w-4 animate-spin" /> Carregando
        produtos...
      </div>
    );
  }

  if (error) {
    return (
      <div className="flex flex-col items-center justify-center min-h-screen text-red-500">
        <p>Erro: {error}</p>
        <Button onClick={fetchProdutos} className="mt-4">
          Tentar Novamente
        </Button>
      </div>
    );
  }

  return (
    <div className="container mx-auto p-4 md:p-6 lg:p-8">
      {/* Cabeçalho da Página */}
      <div className="flex items-center justify-between mb-8">
        <div className="flex items-center gap-4">
          {/* <PackageIcon className="h-8 w-8 text-primary" /> */}
          <div>
            <h1 className="text-3xl font-bold tracking-tight">
              Gerenciamento de Produtos
            </h1>
            <p className="text-muted-foreground">
              Consulte, adicione e gerencie seu estoque de forma eficiente.
            </p>
          </div>
        </div>
        <Button onClick={handleAddProduct} size="lg">
          <PlusCircledIcon className="mr-2 h-5 w-5" />
          Adicionar Produto
        </Button>
      </div>

      {/* Sistema de Abas */}
      <Tabs
        value={activeTab}
        onValueChange={handleTabChange}
        className="w-full"
      >
        <TabsList className="grid w-full grid-cols-3">
          <TabsTrigger value="lista">Lista de Produtos</TabsTrigger>
          <TabsTrigger value="consultar">Consultar Produto</TabsTrigger>
          <TabsTrigger value="adicionar">Adicionar Produto</TabsTrigger>
        </TabsList>

        {/* Aba: Lista de Produtos */}
        <TabsContent value="lista" className="mt-6">
          <Card>
            <CardHeader>
              <CardTitle>Estoque Atual</CardTitle>
              <CardDescription>
                Filtre e gerencie os produtos cadastrados no sistema.
              </CardDescription>
              <div className="flex flex-col md:flex-row items-center gap-4 pt-4">
                <Input
                  placeholder="Filtrar por nome do produto..."
                  value={
                    (table.getColumn("nome")?.getFilterValue() as string) ?? ""
                  }
                  onChange={(event) =>
                    table.getColumn("nome")?.setFilterValue(event.target.value)
                  }
                  className="w-full md:max-w-sm"
                />
                <Select
                  value={
                    (table
                      .getColumn("categoria")
                      ?.getFilterValue() as string) ?? ""
                  }
                  onValueChange={(value) =>
                    table
                      .getColumn("categoria")
                      ?.setFilterValue(value === "all" ? "" : value)
                  }
                >
                  <SelectTrigger className="w-full md:w-[200px]">
                    <SelectValue placeholder="Filtrar por Categoria" />
                  </SelectTrigger>
                  <SelectContent>
                    <SelectItem value="all">Todas as Categorias</SelectItem>
                    {availableCategories.map((category) => (
                      <SelectItem key={category} value={category}>
                        {category}
                      </SelectItem>
                    ))}
                  </SelectContent>
                </Select>
                <Button
                  variant="outline"
                  onClick={handleClearFilters}
                  className="w-full md:w-auto"
                >
                  <Cross2Icon className="mr-2 h-4 w-4" />
                  Limpar Filtros
                </Button>
                <div className="md:ml-auto">
                  <DropdownMenu>
                    <DropdownMenuTrigger asChild>
                      <Button variant="outline">
                        Colunas <ChevronDownIcon className="ml-2 h-4 w-4" />
                      </Button>
                    </DropdownMenuTrigger>
                    <DropdownMenuContent align="end">
                      {table
                        .getAllColumns()
                        .filter((column) => column.getCanHide())
                        .map((column) => (
                          <DropdownMenuCheckboxItem
                            key={column.id}
                            className="capitalize"
                            checked={column.getIsVisible()}
                            onCheckedChange={(value) =>
                              column.toggleVisibility(!!value)
                            }
                          >
                            {column.id}
                          </DropdownMenuCheckboxItem>
                        ))}
                    </DropdownMenuContent>
                  </DropdownMenu>
                </div>
              </div>
            </CardHeader>
            <CardContent>
              {loading ? (
                <div className="flex justify-center items-center h-64 flex-col">
                  <ReloadIcon className="mb-4 h-10 w-10 animate-spin text-primary" />
                  <span className="text-muted-foreground">
                    Carregando produtos...
                  </span>
                </div>
              ) : error ? (
                <div className="text-center text-destructive py-10">
                  <p className="font-semibold">
                    Erro ao carregar produtos: {error}
                  </p>
                </div>
              ) : (
                <div className="rounded-md border">
                  <Table>
                    <TableHeader>
                      {table.getHeaderGroups().map((headerGroup) => (
                        <TableRow key={headerGroup.id}>
                          {headerGroup.headers.map((header) => (
                            <TableHead key={header.id}>
                              {header.isPlaceholder
                                ? null
                                : flexRender(
                                    header.column.columnDef.header,
                                    header.getContext()
                                  )}
                            </TableHead>
                          ))}
                        </TableRow>
                      ))}
                    </TableHeader>
                    <TableBody>
                      {table.getRowModel().rows?.length ? (
                        table.getRowModel().rows.map((row) => (
                          <TableRow
                            key={row.id}
                            data-state={row.getIsSelected() && "selected"}
                          >
                            {row.getVisibleCells().map((cell) => (
                              <TableCell key={cell.id}>
                                {flexRender(
                                  cell.column.columnDef.cell,
                                  cell.getContext()
                                )}
                              </TableCell>
                            ))}
                          </TableRow>
                        ))
                      ) : (
                        <TableRow>
                          <TableCell
                            colSpan={columns.length}
                            className="h-24 text-center"
                          >
                            Nenhum produto encontrado.
                          </TableCell>
                        </TableRow>
                      )}
                    </TableBody>
                  </Table>
                </div>
              )}
            </CardContent>
            <CardFooter className="flex items-center justify-between">
              <div className="text-sm text-muted-foreground">
                {table.getFilteredSelectedRowModel().rows.length} de{" "}
                {table.getFilteredRowModel().rows.length} linha(s) selecionadas.
              </div>
              <div className="flex items-center space-x-4">
                <Button onClick={fetchProdutos} variant="outline" size="sm">
                  <ReloadIcon className="mr-2 h-4 w-4" />
                  Atualizar
                </Button>
                <div className="flex items-center space-x-2">
                  <Button
                    variant="outline"
                    size="sm"
                    onClick={() => table.previousPage()}
                    disabled={!table.getCanPreviousPage()}
                  >
                    Anterior
                  </Button>
                  <span className="text-sm font-medium">
                    Página {table.getState().pagination.pageIndex + 1} de{" "}
                    {table.getPageCount()}
                  </span>
                  <Button
                    variant="outline"
                    size="sm"
                    onClick={() => table.nextPage()}
                    disabled={!table.getCanNextPage()}
                  >
                    Próxima
                  </Button>
                </div>
              </div>
            </CardFooter>
          </Card>
        </TabsContent>

        {/* Aba: Consultar Produto */}
        <TabsContent value="consultar" className="mt-6">
          <Card>
            <CardHeader>
              <CardTitle>Consultar Produto</CardTitle>
              <CardDescription>
                Use o código de barras para ver os detalhes de um produto
                específico.
              </CardDescription>
            </CardHeader>
            <CardContent className="pt-6">
              <ProdutoDetalhes
                codigoInicial={codigoParaConsultar}
                onClear={handleConsultaLimpa}
                onSuccess={fetchProdutos}
              />
            </CardContent>
          </Card>
        </TabsContent>

        {/* Aba: Adicionar Produto */}
        <TabsContent value="adicionar" className="mt-6">
          <Card>
            <CardHeader>
              <CardTitle>Adicionar Novo Produto</CardTitle>
              <CardDescription>
                Preencha os dados abaixo para cadastrar um novo item no estoque.
              </CardDescription>
            </CardHeader>
            <CardContent className="pt-6">
              <AdicionarProduto
                onSuccess={handleProdutoAdicionado}
                onClear={handleFormularioLimpo}
                codigoInicial={codigoParaAdicionar}
              />
            </CardContent>
          </Card>
        </TabsContent>
      </Tabs>
    </div>
  );
}
