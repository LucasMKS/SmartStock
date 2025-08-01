// src/components/layout/Sidebar.tsx
"use client"; // Necessário para usar hooks do React e Next.js

import Link from "next/link";
import { usePathname } from "next/navigation";
import {
  Package2,
  Home,
  Package,
  History,
  LogIn,
  LogOut,
  Menu,
} from "lucide-react";
import {
  Card,
  CardContent,
  CardDescription,
  CardHeader,
  CardTitle,
} from "@/components/ui/card";
import { Sheet, SheetContent, SheetTrigger } from "@/components/ui/sheet";
import { Button } from "@/components/ui/button";

// Definição dos itens de navegação
const navItems = [
  { href: "/", icon: Home, label: "Painel" },
  { href: "/produtos", icon: Package, label: "Produtos" },
  { href: "/entrada", icon: LogIn, label: "Entrada" },
  { href: "/saida", icon: LogOut, label: "Saída" },
  { href: "/historico", icon: History, label: "Histórico" },
];

// Componente unificado que contém a lógica da navegação
export function NavigationLinks() {
  const pathname = usePathname();

  return (
    <nav className="grid items-start gap-1 px-2 text-sm font-medium lg:px-4">
      {navItems.map((item) => (
        <Link
          key={item.label}
          href={item.href}
          className={`flex items-center gap-3 rounded-lg px-3 py-2 transition-all hover:text-primary ${
            // Lógica para destacar o link ativo
            pathname === item.href
              ? "bg-muted text-primary"
              : "text-muted-foreground"
          }`}
        >
          <item.icon className="h-4 w-4" />
          {item.label}
        </Link>
      ))}
    </nav>
  );
}

// Componente principal da Sidebar para Desktop
export default function Sidebar() {
  return (
    <aside className="hidden border-r bg-muted/40 md:block">
      <div className="flex h-full max-h-screen flex-col gap-2">
        <div className="flex h-14 items-center border-b px-4 lg:h-[60px] lg:px-6">
          <Link href="/" className="flex items-center gap-2 font-semibold">
            <Package2 className="h-6 w-6 text-primary" />
            <span className="">Controle de Estoque</span>
          </Link>
        </div>
        <div className="flex-1 py-2">
          <NavigationLinks />
        </div>
        <div className="mt-auto p-4">
          <Card>
            <CardHeader className="p-2 pt-0 md:p-4">
              <CardTitle>Ajuda & Suporte</CardTitle>
              <CardDescription>
                Precisa de ajuda? Acesse nossa central de suporte ou entre em
                contato.
              </CardDescription>
            </CardHeader>
            <CardContent className="p-2 pt-0 md:p-4 md:pt-0">
              <Button size="sm" className="w-full">
                Acessar Suporte
              </Button>
            </CardContent>
          </Card>
        </div>
      </div>
    </aside>
  );
}

// Componente para a Sidebar em telas menores (mobile)
// Usado em conjunto com o Header principal para ser um menu expansível
export function MobileSidebar() {
  const pathname = usePathname();

  return (
    <Sheet>
      <SheetTrigger asChild>
        <Button
          variant="outline"
          size="icon"
          className="shrink-0 md:hidden" // Mostra apenas em telas menores
        >
          <Menu className="h-5 w-5" /> {/* Ícone de hambúrguer */}
          <span className="sr-only">Abrir menu de navegação</span>
        </Button>
      </SheetTrigger>
      <SheetContent side="left" className="flex flex-col">
        {" "}
        {/* Conteúdo do menu que desliza */}
        <nav className="grid gap-2 text-lg font-medium">
          <Link
            href="#" // Pode ser o link para a página inicial
            className="flex items-center gap-2 text-lg font-semibold"
          >
            <Package2 className="h-6 w-6" />
            <span className="sr-only">Estoque App</span>
          </Link>
          {navItems.map((item) => (
            <Link
              key={item.href}
              href={item.href}
              className={`mx-[-0.65rem] flex items-center gap-4 rounded-xl px-3 py-2 text-muted-foreground hover:text-foreground ${
                pathname === item.href ? "bg-muted" : "" // Estilo para link ativo
              }`}
            >
              <item.icon className="h-5 w-5" />
              {item.label}
            </Link>
          ))}
        </nav>
      </SheetContent>
    </Sheet>
  );
}
