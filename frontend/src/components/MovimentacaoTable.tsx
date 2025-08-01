// src/components/MovimentacaoTable.tsx
"use client";

import { useEffect, useState, useMemo } from "react";
import { get } from "@/lib/api";
import { ApiResponse, Movimentacao, TipoMovimentacao } from "@/types/api";

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
} from "@tanstack/react-table";

// Importar componentes Shadcn/ui
import {
  Table,
  TableBody,
  TableCell,
  TableHead,
  TableHeader,
  TableRow,
} from "@/components/ui/table";
import { Button } from "@/components/ui/button";
import { Input } from "@/components/ui/input";
import { Label } from "@/components/ui/label";
import {
  Select,
  SelectContent,
  SelectItem,
  SelectTrigger,
  SelectValue,
} from "@/components/ui/select";
import { Badge } from "@/components/ui/badge";
import {
  Card,
  CardContent,
  CardDescription,
  CardFooter,
  CardHeader,
  CardTitle,
} from "@/components/ui/card";
import {
  Tooltip,
  TooltipContent,
  TooltipProvider,
  TooltipTrigger,
} from "@/components/ui/tooltip";

// Ícones
import {
  ReloadIcon,
  CaretSortIcon,
  MagnifyingGlassIcon,
  Cross2Icon,
  DownloadIcon,
  ChevronLeftIcon,
  ChevronRightIcon,
  DoubleArrowLeftIcon,
  DoubleArrowRightIcon,
} from "@radix-ui/react-icons";
import { toast } from "sonner";

interface MovimentacaoTableProps {
  title?: string;
  initialFilterTipo?: TipoMovimentacao;
  codigoProduto?: string; // Para filtrar por produto específico
  refreshTrigger?: number;
  showFilters?: boolean;
  pageSize?: number;
  className?: string;
}

// Helper para converter array de data/hora Java para Date
const parseJavaDateTimeArray = (dateTimeArray: number[]): Date => {
  if (!dateTimeArray || dateTimeArray.length < 6) {
    return new Date();
  }

  const [year, month, day, hour, minute, second, nanosecond = 0] =
    dateTimeArray;
  const millisecond = nanosecond / 1_000_000;

  return new Date(year, month - 1, day, hour, minute, second, millisecond);
};

// Helper para formatar data/hora
const formatDateTime = (dateTimeArray: number[]): string => {
  try {
    const date = parseJavaDateTimeArray(dateTimeArray);
    return date.toLocaleString("pt-BR", {
      day: "2-digit",
      month: "2-digit",
      year: "numeric",
      hour: "2-digit",
      minute: "2-digit",
      second: "2-digit",
    });
  } catch (error) {
    return "Data inválida";
  }
};

// Helper para formatar tipo de movimentação
const formatTipoMovimentacao = (tipo: TipoMovimentacao) => {
  return tipo === TipoMovimentacao.ENTRADA ? "ENTRADA" : "SAÍDA";
};

export default function MovimentacaoTable({
  title,
  initialFilterTipo,
  codigoProduto,
  refreshTrigger = 0,
  showFilters = true,
  pageSize = 10,
  className = "",
}: MovimentacaoTableProps) {
  // Estados principais
  const [movimentacoes, setMovimentacoes] = useState<Movimentacao[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);

  // Estados para filtros
  const [filterCodigoBarras, setFilterCodigoBarras] = useState<string>(
    codigoProduto || ""
  );
  const [filterMotivo, setFilterMotivo] = useState<string>("");
  const [filterTipo, setFilterTipo] = useState<string>(initialFilterTipo || "");
  const [filterDataInicio, setFilterDataInicio] = useState<string>("");
  const [filterDataFim, setFilterDataFim] = useState<string>("");

  // Estados do TanStack Table
  const [sorting, setSorting] = useState<SortingState>([
    { id: "dataHora", desc: true },
  ]);
  const [columnFilters, setColumnFilters] = useState<ColumnFiltersState>([]);

  // Função para buscar movimentações
  const fetchMovimentacoes = async () => {
    setLoading(true);
    setError(null);

    try {
      let endpoint = "/historico";

      // Se há código do produto específico, buscar só desse produto
      if (codigoProduto) {
        endpoint = `/historico/produto/${codigoProduto}`;
      }

      const response: ApiResponse<Movimentacao[]> = await get(endpoint);

      if (response.success && response.data) {
        let filteredData = response.data;

        // Aplicar filtro inicial por tipo se especificado
        if (initialFilterTipo) {
          filteredData = filteredData.filter(
            (m) => m.tipo === initialFilterTipo
          );
        }

        setMovimentacoes(filteredData);

        toast.success(
          `Histórico carregado! ${filteredData.length} movimentações encontradas.`,
          { description: "Dados atualizados do servidor." }
        );
      } else {
        setMovimentacoes([]);
        setError(response.message || "Falha ao carregar movimentações.");
        toast.error("Falha ao carregar histórico", {
          description: response.message || "Verifique a API e tente novamente.",
        });
      }
    } catch (err: any) {
      setMovimentacoes([]);
      setError(err.message || "Erro de rede ao carregar movimentações.");
      toast.error("Erro de rede", {
        description: err.message || "Não foi possível conectar à API.",
      });
      console.error("Erro ao buscar movimentações:", err);
    } finally {
      setLoading(false);
    }
  };

  // Effect para carregar dados
  useEffect(() => {
    fetchMovimentacoes();
  }, [refreshTrigger, codigoProduto, initialFilterTipo]);

  // Effect para aplicar filtros locais
  useEffect(() => {
    const filters: any[] = [];

    if (filterCodigoBarras) {
      filters.push({ id: "codigoBarrasProduto", value: filterCodigoBarras });
    }

    if (filterMotivo) {
      filters.push({ id: "motivo", value: filterMotivo });
    }

    if (filterTipo) {
      filters.push({ id: "tipo", value: filterTipo });
    }

    if (filterDataInicio || filterDataFim) {
      const startTime = filterDataInicio
        ? new Date(filterDataInicio).getTime()
        : undefined;
      const endTime = filterDataFim
        ? new Date(filterDataFim).getTime()
        : undefined;

      filters.push({
        id: "dataHora",
        value: { start: startTime, end: endTime },
      });
    }

    setColumnFilters(filters);
  }, [
    filterCodigoBarras,
    filterMotivo,
    filterTipo,
    filterDataInicio,
    filterDataFim,
  ]);

  // Função para limpar filtros
  const handleClearFilters = () => {
    setFilterCodigoBarras(codigoProduto || "");
    setFilterMotivo("");
    setFilterTipo(initialFilterTipo || "");
    setFilterDataInicio("");
    setFilterDataFim("");
    setSorting([{ id: "dataHora", desc: true }]);
  };

  // Função para exportar dados (futuro)
  const handleExport = () => {
    // Usar table após ser declarado
    if (table) {
      const visibleData = table
        .getFilteredRowModel()
        .rows.map((row) => row.original);
      console.log("Exportando dados:", visibleData);
    }
    toast.info("Função de exportação será implementada em breve!");
  };

  // Definição das colunas com melhorias visuais
  const columns: ColumnDef<Movimentacao>[] = useMemo(
    () => [
      {
        accessorKey: "codigoBarrasProduto",
        header: ({ column }) => (
          <Button
            variant="ghost"
            onClick={() => column.toggleSorting(column.getIsSorted() === "asc")}
          >
            Produto
            <CaretSortIcon className="ml-2 h-4 w-4" />
          </Button>
        ),
        cell: ({ row }) => (
          <div className="font-mono text-center">
            {row.original.codigoBarrasProduto}
          </div>
        ),
        filterFn: "includesString",
      },
      {
        accessorKey: "tipo",
        header: "Tipo",
        cell: ({ row }) => {
          const tipo = row.original.tipo;
          const isEntrada = tipo === TipoMovimentacao.ENTRADA;
          return (
            <div className="text-center">
              <Badge
                variant={isEntrada ? "default" : "destructive"}
                className={
                  isEntrada
                    ? "bg-green-100 text-green-800 border-green-200"
                    : "bg-red-100 text-red-800 border-red-200"
                }
              >
                {formatTipoMovimentacao(tipo)}
              </Badge>
            </div>
          );
        },
        filterFn: (row, columnId, filterValue: string) => {
          if (!filterValue) return true;
          return row.getValue(columnId) === filterValue;
        },
      },
      {
        accessorKey: "quantidade",
        header: ({ column }) => (
          <div className="text-center">
            <Button
              variant="ghost"
              onClick={() =>
                column.toggleSorting(column.getIsSorted() === "asc")
              }
            >
              Qtd.
              <CaretSortIcon className="ml-2 h-4 w-4" />
            </Button>
          </div>
        ),
        cell: ({ row }) => (
          <div className="text-center font-bold text-lg">
            {row.original.quantidade}
          </div>
        ),
      },
      {
        accessorKey: "motivo",
        header: "Motivo",
        cell: ({ row }) => (
          <TooltipProvider>
            <Tooltip>
              <TooltipTrigger asChild>
                <div className="truncate max-w-[200px] mx-auto text-center">
                  {row.original.motivo}
                </div>
              </TooltipTrigger>
              <TooltipContent>
                <p>{row.original.motivo}</p>
              </TooltipContent>
            </Tooltip>
          </TooltipProvider>
        ),
        filterFn: "includesString",
      },
      {
        accessorKey: "dataHora",
        header: ({ column }) => (
          <div className="text-right">
            <Button
              variant="ghost"
              onClick={() =>
                column.toggleSorting(column.getIsSorted() === "asc")
              }
            >
              Data/Hora
              <CaretSortIcon className="ml-2 h-4 w-4" />
            </Button>
          </div>
        ),
        cell: ({ row }) => (
          <div className="text-right font-mono text-sm text-muted-foreground">
            {formatDateTime(row.original.dataHora)}
          </div>
        ),
        // --- LÓGICA DO FILTRO CORRIGIDA ---
        filterFn: (
          row,
          columnId,
          filterValue: { start?: number; end?: number }
        ) => {
          // Se não houver filtro de data, mostra todas as linhas
          if (!filterValue || (!filterValue.start && !filterValue.end)) {
            return true;
          }

          // Converte a data da linha (que é um array de números) para um timestamp
          const dateTimeArray = row.getValue(columnId) as number[];
          const rowDateTime = parseJavaDateTimeArray(dateTimeArray).getTime();

          const { start, end } = filterValue;

          // Verifica se a data da linha está dentro do intervalo
          if (start && rowDateTime < start) {
            return false; // A data é anterior ao início do filtro
          }
          if (end && rowDateTime > end) {
            return false; // A data é posterior ao fim do filtro
          }

          // Se passou por todas as verificações, a linha está no intervalo
          return true;
        },
      },
    ],
    []
  );

  const table = useReactTable({
    data: movimentacoes,
    columns,
    getCoreRowModel: getCoreRowModel(),
    getPaginationRowModel: getPaginationRowModel(),
    getSortedRowModel: getSortedRowModel(),
    getFilteredRowModel: getFilteredRowModel(),
    onSortingChange: setSorting,
    onColumnFiltersChange: setColumnFilters,
    state: {
      sorting,
      columnFilters,
    },
    initialState: {
      pagination: {
        pageSize: pageSize,
      },
    },
  });

  // Renderização condicional para loading
  if (loading) {
    return (
      <Card className={className}>
        <CardContent className="flex items-center justify-center p-8">
          <ReloadIcon className="mr-2 h-4 w-4 animate-spin" />
          Carregando histórico...
        </CardContent>
      </Card>
    );
  }

  // Renderização condicional para erro
  if (error) {
    return (
      <Card className={className}>
        <CardContent className="flex flex-col items-center justify-center p-8 text-red-500">
          <p className="mb-4">Erro: {error}</p>
          <Button onClick={fetchMovimentacoes} variant="outline">
            <ReloadIcon className="mr-2 h-4 w-4" />
            Tentar Novamente
          </Button>
        </CardContent>
      </Card>
    );
  }

  return (
    <Card className={className}>
      <CardHeader>
        <div className="flex flex-col md:flex-row md:items-center md:justify-between gap-4">
          <div>
            <CardTitle className="text-xl">{title}</CardTitle>
            <CardDescription>
              Visualize as movimentações de estoque registradas.
            </CardDescription>
          </div>
          <div className="flex gap-2">
            <Button variant="outline" size="sm" onClick={handleExport}>
              <DownloadIcon className="mr-2 h-4 w-4" />
              Exportar
            </Button>
            <Button variant="outline" size="sm" onClick={fetchMovimentacoes}>
              <ReloadIcon className="mr-2 h-4 w-4" />
              Atualizar
            </Button>
          </div>
        </div>
      </CardHeader>

      <CardContent>
        {/* Filtros */}
        {showFilters && (
          <div className="p-4 mb-6 border rounded-lg bg-muted/50">
            <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-4 gap-4">
              {/* Filtro por código (se não for fixo) */}
              {!codigoProduto && (
                <div className="space-y-2">
                  <Label htmlFor="filter-codigo">Código do Produto</Label>
                  <Input
                    id="filter-codigo"
                    placeholder="Filtrar por código..."
                    value={filterCodigoBarras}
                    onChange={(e) => setFilterCodigoBarras(e.target.value)}
                  />
                </div>
              )}

              {/* Filtro por motivo */}
              <div className="space-y-2">
                <Label htmlFor="filter-motivo">Motivo</Label>
                <Input
                  id="filter-motivo"
                  placeholder="Filtrar por motivo..."
                  value={filterMotivo}
                  onChange={(e) => setFilterMotivo(e.target.value)}
                />
              </div>

              {/* Filtro por tipo (se não for fixo) */}
              {!initialFilterTipo && (
                <div className="space-y-2">
                  <Label htmlFor="filter-tipo">Tipo</Label>
                  <Select value={filterTipo} onValueChange={setFilterTipo}>
                    <SelectTrigger id="filter-tipo">
                      <SelectValue placeholder="Filtrar por tipo..." />
                    </SelectTrigger>
                    <SelectContent>
                      <SelectItem value="">Todos</SelectItem>
                      <SelectItem value={TipoMovimentacao.ENTRADA}>
                        Entrada
                      </SelectItem>
                      <SelectItem value={TipoMovimentacao.SAIDA}>
                        Saída
                      </SelectItem>
                    </SelectContent>
                  </Select>
                </div>
              )}

              {/* Filtros de data */}
              <div className="space-y-2">
                <Label htmlFor="filter-data-inicio">Data Início</Label>
                <Input
                  id="filter-data-inicio"
                  type="datetime-local"
                  value={filterDataInicio}
                  onChange={(e) => setFilterDataInicio(e.target.value)}
                />
              </div>
              <div className="space-y-2">
                <Label htmlFor="filter-data-fim">Data Fim</Label>
                <Input
                  id="filter-data-fim"
                  type="datetime-local"
                  value={filterDataFim}
                  onChange={(e) => setFilterDataFim(e.target.value)}
                />
              </div>
            </div>
            <div className="flex justify-end mt-4">
              <Button variant="ghost" onClick={handleClearFilters}>
                <Cross2Icon className="mr-2 h-4 w-4" />
                Limpar Filtros
              </Button>
            </div>
          </div>
        )}

        {/* Tabela */}
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
                    className="h-32 text-center"
                  >
                    <div className="flex flex-col items-center justify-center gap-2 text-muted-foreground">
                      <MagnifyingGlassIcon className="h-10 w-10" />
                      <p className="font-semibold">
                        Nenhuma movimentação encontrada
                      </p>
                      <p className="text-sm">
                        Tente ajustar os filtros ou registre uma nova
                        movimentação.
                      </p>
                    </div>
                  </TableCell>
                </TableRow>
              )}
            </TableBody>
          </Table>
        </div>
      </CardContent>

      <CardFooter className="flex items-center justify-between flex-wrap gap-4">
        <div className="text-sm text-muted-foreground">
          {table.getFilteredRowModel().rows.length} de {movimentacoes.length}{" "}
          registro(s).
        </div>
        <div className="flex items-center space-x-2">
          <span className="text-sm">
            Página {table.getState().pagination.pageIndex + 1} de{" "}
            {table.getPageCount()}
          </span>
          <Button
            variant="outline"
            className="h-8 w-8 p-0"
            onClick={() => table.setPageIndex(0)}
            disabled={!table.getCanPreviousPage()}
          >
            <DoubleArrowLeftIcon className="h-4 w-4" />
          </Button>
          <Button
            variant="outline"
            className="h-8 w-8 p-0"
            onClick={() => table.previousPage()}
            disabled={!table.getCanPreviousPage()}
          >
            <ChevronLeftIcon className="h-4 w-4" />
          </Button>
          <Button
            variant="outline"
            className="h-8 w-8 p-0"
            onClick={() => table.nextPage()}
            disabled={!table.getCanNextPage()}
          >
            <ChevronRightIcon className="h-4 w-4" />
          </Button>
          <Button
            variant="outline"
            className="h-8 w-8 p-0"
            onClick={() => table.setPageIndex(table.getPageCount() - 1)}
            disabled={!table.getCanNextPage()}
          >
            <DoubleArrowRightIcon className="h-4 w-4" />
          </Button>
        </div>
      </CardFooter>
    </Card>
  );
}
