"use client";

import { useEffect, useState, useMemo } from "react";
import { get } from "@/lib/api";
import { ApiResponse, Movimentacao, TipoMovimentacao } from "@/types/api";
import { toast } from "sonner";

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
  ArchiveIcon,
} from "@radix-ui/react-icons";

// Helpers de formatação (assumindo que estão no mesmo arquivo ou importados)
const parseJavaDateTimeArray = (dateTimeArray: number[]): Date => {
  if (!dateTimeArray || dateTimeArray.length < 6) {
    throw new Error("Array de data/hora inválido");
  }
  const [year, month, day, hour, minute, second, nanosecond = 0] =
    dateTimeArray;
  const millisecond = nanosecond / 1_000_000;
  return new Date(year, month - 1, day, hour, minute, second, millisecond);
};

const formatDateTime = (dateTimeArray: number[]): string => {
  try {
    const date = parseJavaDateTimeArray(dateTimeArray);
    return date.toLocaleString("pt-BR", {
      day: "2-digit",
      month: "2-digit",
      year: "numeric",
      hour: "2-digit",
      minute: "2-digit",
    });
  } catch (error) {
    return "Data inválida";
  }
};

const formatTipoMovimentacao = (tipo: TipoMovimentacao) => {
  return tipo === TipoMovimentacao.ENTRADA ? "ENTRADA" : "SAÍDA";
};

export default function HistoricoPage() {
  const [movimentacoes, setMovimentacoes] = useState<Movimentacao[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);

  // Estados para filtros
  const [filterCodigoBarras, setFilterCodigoBarras] = useState("");
  const [filterMotivo, setFilterMotivo] = useState("");
  const [filterTipo, setFilterTipo] = useState("");
  const [filterDataInicio, setFilterDataInicio] = useState("");
  const [filterDataFim, setFilterDataFim] = useState("");

  // Estados da Tabela
  const [sorting, setSorting] = useState<SortingState>([
    { id: "dataHora", desc: true },
  ]);
  const [columnFilters, setColumnFilters] = useState<ColumnFiltersState>([]);

  const fetchAllMovimentacoes = async () => {
    setLoading(true);
    setError(null);
    try {
      const response: ApiResponse<Movimentacao[]> = await get("/historico");
      if (response.success && response.data) {
        setMovimentacoes(response.data);
        toast.success("Histórico carregado com sucesso!");
      } else {
        throw new Error(response.message || "Falha ao carregar histórico.");
      }
    } catch (err: any) {
      setMovimentacoes([]);
      setError(err.message);
      toast.error("Erro ao carregar histórico", { description: err.message });
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchAllMovimentacoes();
  }, []);

  useEffect(() => {
    const filters: ColumnFiltersState = [];
    if (filterCodigoBarras)
      filters.push({ id: "codigoBarrasProduto", value: filterCodigoBarras });
    if (filterMotivo) filters.push({ id: "motivo", value: filterMotivo });
    // CORREÇÃO: Ignorar o filtro se o valor for "all" ou vazio
    if (filterTipo && filterTipo !== "all") {
      filters.push({ id: "tipo", value: filterTipo });
    }
    if (filterDataInicio || filterDataFim) {
      filters.push({
        id: "dataHora",
        value: {
          start: filterDataInicio
            ? new Date(filterDataInicio).getTime()
            : undefined,
          end: filterDataFim ? new Date(filterDataFim).getTime() : undefined,
        },
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

  const handleClearFilters = () => {
    setFilterCodigoBarras("");
    setFilterMotivo("");
    setFilterTipo("");
    setFilterDataInicio("");
    setFilterDataFim("");
    setSorting([{ id: "dataHora", desc: true }]);
  };

  const handleExport = () => {
    toast.info("Função de exportação será implementada em breve!");
  };

  const columns: ColumnDef<Movimentacao>[] = useMemo(
    () => [
      {
        accessorKey: "codigoBarrasProduto",
        header: ({ column }) => (
          <Button
            variant="ghost"
            onClick={() => column.toggleSorting(column.getIsSorted() === "asc")}
            className="w-full justify-center px-0 text-center"
          >
            Código do Produto
            <CaretSortIcon className="ml-2 h-4 w-4" />
          </Button>
        ),
        cell: (info) => (
          <div className="text-center">{info.getValue<string>()}</div>
        ),
      },
      {
        accessorKey: "quantidade",
        header: ({ column }) => (
          <Button
            variant="ghost"
            onClick={() => column.toggleSorting(column.getIsSorted() === "asc")}
            className="w-full justify-center px-0 text-center"
          >
            Quantidade
            <CaretSortIcon className="ml-2 h-4 w-4" />
          </Button>
        ),
        cell: (info) => (
          <div className="text-center">{info.getValue<number>()}</div>
        ),
      },
      {
        accessorKey: "tipo",
        header: ({ column }) => (
          <Button
            variant="ghost"
            onClick={() => column.toggleSorting(column.getIsSorted() === "asc")}
            className="w-full justify-center px-0 text-center"
          >
            Tipo
            <CaretSortIcon className="ml-2 h-4 w-4" />
          </Button>
        ),
        cell: (info) => (
          <div
            className={`text-center font-medium ${
              info.getValue<TipoMovimentacao>() === TipoMovimentacao.ENTRADA
                ? "text-green-600"
                : "text-red-600"
            }`}
          >
            {info.getValue<TipoMovimentacao>() === TipoMovimentacao.ENTRADA
              ? "ENTRADA"
              : "SAÍDA"}
          </div>
        ),
      },
      {
        accessorKey: "motivo",
        header: ({ column }) => (
          <Button
            variant="ghost"
            onClick={() => column.toggleSorting(column.getIsSorted() === "asc")}
            className="w-full justify-center px-0 text-center"
          >
            Motivo
            <CaretSortIcon className="ml-2 h-4 w-4" />
          </Button>
        ),
        cell: (info) => (
          <div className="text-center">{info.getValue<string>()}</div>
        ),
      },
      {
        accessorKey: "dataHora",
        header: ({ column }) => (
          <Button
            variant="ghost"
            onClick={() => column.toggleSorting(column.getIsSorted() === "asc")}
            className="w-full justify-center px-0 text-center"
          >
            Data/Hora
            <CaretSortIcon className="ml-2 h-4 w-4" />
          </Button>
        ),
        cell: (info) => {
          const dateTimeArray = info.getValue<number[]>();
          const dateObject = parseJavaDateTimeArray(dateTimeArray);

          return (
            <div className="text-center">
              {dateObject.toLocaleString("pt-BR", {
                dateStyle: "short",
                timeStyle: "short",
              })}
            </div>
          );
        },

        filterFn: (
          row,
          columnId,
          filterValue: { start?: number; end?: number }
        ) => {
          const dateTimeArray = row.getValue<number[]>(columnId);
          if (!dateTimeArray) return false;

          const rowDateTime = parseJavaDateTimeArray(dateTimeArray).getTime();

          if (filterValue.start && rowDateTime < filterValue.start)
            return false;
          if (filterValue.end && rowDateTime > filterValue.end) return false;

          return true;
        },
      },
    ],
    []
  );

  const table = useReactTable({
    data: movimentacoes,
    columns,
    state: { sorting, columnFilters },
    onSortingChange: setSorting,
    onColumnFiltersChange: setColumnFilters,
    getCoreRowModel: getCoreRowModel(),
    getPaginationRowModel: getPaginationRowModel(),
    getSortedRowModel: getSortedRowModel(),
    getFilteredRowModel: getFilteredRowModel(),
    initialState: {
      pagination: {
        pageSize: 15,
      },
    },
  });

  if (loading) {
    return (
      <div className="flex h-screen items-center justify-center">
        <ReloadIcon className="mr-2 h-8 w-8 animate-spin text-primary" />
        <span className="text-lg text-muted-foreground">
          Carregando histórico...
        </span>
      </div>
    );
  }

  if (error) {
    return (
      <div className="flex h-screen flex-col items-center justify-center gap-4">
        <p className="text-xl font-semibold text-destructive">Erro: {error}</p>
        <Button onClick={fetchAllMovimentacoes}>
          <ReloadIcon className="mr-2 h-4 w-4" /> Tentar Novamente
        </Button>
      </div>
    );
  }

  return (
    <div className="container mx-auto p-4 md:p-6 lg:p-8">
      <Card>
        <CardHeader>
          <div className="flex flex-col md:flex-row md:items-start md:justify-between gap-4">
            <div className="flex items-center gap-4">
              <ArchiveIcon className="h-8 w-8 text-primary" />
              <div>
                <CardTitle className="text-2xl">
                  Histórico de Movimentações
                </CardTitle>
                <CardDescription>
                  Consulte, filtre e analise todas as entradas e saídas do
                  estoque.
                </CardDescription>
              </div>
            </div>
            <div className="flex flex-shrink-0 gap-2">
              <Button variant="outline" onClick={handleExport}>
                <DownloadIcon className="mr-2 h-4 w-4" />
                Exportar
              </Button>
              <Button onClick={fetchAllMovimentacoes}>
                <ReloadIcon className="mr-2 h-4 w-4" />
                Atualizar
              </Button>
            </div>
          </div>
        </CardHeader>
        <CardContent>
          {/* Seção de Filtros */}
          <div className="p-4 mb-6 border rounded-lg bg-muted/50">
            <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-5 gap-4">
              <div className="space-y-2 lg:col-span-1">
                <Label htmlFor="filter-codigo">Código do Produto</Label>
                <Input
                  id="filter-codigo"
                  placeholder="Filtrar por código..."
                  value={filterCodigoBarras}
                  onChange={(e) => setFilterCodigoBarras(e.target.value)}
                />
              </div>
              <div className="space-y-2 lg:col-span-1">
                <Label htmlFor="filter-motivo">Motivo</Label>
                <Input
                  id="filter-motivo"
                  placeholder="Filtrar por motivo..."
                  value={filterMotivo}
                  onChange={(e) => setFilterMotivo(e.target.value)}
                />
              </div>
              <div className="space-y-2 lg:col-span-1">
                <Label htmlFor="filter-tipo">Tipo</Label>
                <Select value={filterTipo} onValueChange={setFilterTipo}>
                  <SelectTrigger id="filter-tipo">
                    <SelectValue placeholder="Todos os Tipos" />
                  </SelectTrigger>
                  <SelectContent>
                    {/* CORREÇÃO: O valor não pode ser uma string vazia */}
                    <SelectItem value="all">Todos</SelectItem>
                    <SelectItem value={TipoMovimentacao.ENTRADA}>
                      Entrada
                    </SelectItem>
                    <SelectItem value={TipoMovimentacao.SAIDA}>
                      Saída
                    </SelectItem>
                  </SelectContent>
                </Select>
              </div>
              <div className="space-y-2 lg:col-span-2 grid grid-cols-2 gap-4">
                <div>
                  <Label htmlFor="filter-data-inicio">Data Início</Label>
                  <Input
                    id="filter-data-inicio"
                    type="datetime-local"
                    value={filterDataInicio}
                    onChange={(e) => setFilterDataInicio(e.target.value)}
                  />
                </div>
                <div>
                  <Label htmlFor="filter-data-fim">Data Fim</Label>
                  <Input
                    id="filter-data-fim"
                    type="datetime-local"
                    value={filterDataFim}
                    onChange={(e) => setFilterDataFim(e.target.value)}
                  />
                </div>
              </div>
            </div>
            <div className="flex justify-end mt-4">
              <Button
                variant="ghost"
                onClick={handleClearFilters}
                disabled={loading}
              >
                <Cross2Icon className="mr-2 h-4 w-4" />
                Limpar Filtros
              </Button>
            </div>
          </div>

          {/* Tabela */}
          <div className="rounded-md border">
            <Table>
              <TableHeader>
                {table.getHeaderGroups().map((headerGroup) => (
                  <TableRow key={headerGroup.id}>
                    {headerGroup.headers.map((header) => (
                      <TableHead key={header.id}>
                        {flexRender(
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
                          Tente ajustar os filtros ou aguarde novos registros.
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
            registro(s) exibido(s).
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
    </div>
  );
}
