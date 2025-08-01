"use client";

import Link from "next/link";
import { usePathname } from "next/navigation";
import {
  Tooltip,
  TooltipContent,
  TooltipProvider,
  TooltipTrigger,
} from "@/components/ui/tooltip";
import {
  Package2,
  Home,
  Package,
  History,
  LogIn,
  LogOut,
  Settings,
} from "lucide-react";

// Definição dos itens de navegação
const navItems = [
  { href: "/", icon: Home, label: "Painel" },
  { href: "/produtos", icon: Package, label: "Produtos" },
  { href: "/entrada", icon: LogIn, label: "Entrada" },
  { href: "/saida", icon: LogOut, label: "Saída" },
  { href: "/historico", icon: History, label: "Histórico" },
];

// Componente para os links de navegação da versão mobile (com texto)
export function NavigationLinksAlt() {
  const pathname = usePathname();
  return (
    <>
      <Link
        href="/"
        className="flex items-center gap-2 text-lg font-semibold mb-4"
      >
        <Package2 className="h-6 w-6" />
        <span>Controle de Estoque</span>
      </Link>
      {navItems.map((item) => (
        <Link
          key={item.label}
          href={item.href}
          className={`flex items-center gap-4 rounded-xl px-3 py-2 transition-all hover:text-foreground ${
            pathname === item.href
              ? "bg-muted text-foreground"
              : "text-muted-foreground"
          }`}
        >
          <item.icon className="h-5 w-5" />
          {item.label}
        </Link>
      ))}
    </>
  );
}

// Componente principal da Sidebar Alternativa (Desktop)
export default function SidebarAlt() {
  return (
    <aside className="fixed inset-y-0 left-0 z-10 hidden w-14 flex-col border-r bg-zinc-900 sm:flex">
      <TooltipProvider>
        <nav className="flex flex-col items-center gap-4 px-2 sm:py-5">
          <Link
            href="/"
            className="group flex h-9 w-9 shrink-0 items-center justify-center gap-2 rounded-full bg-zinc-400 text-lg font-semibold text-zinc-900 md:h-8 md:w-8 md:text-base"
          >
            <Package2 className="h-4 w-4 transition-all group-hover:scale-110" />
            <span className="sr-only">Controle de Estoque</span>
          </Link>
          {navItems.map((item) => (
            <Tooltip key={item.label}>
              <TooltipTrigger asChild>
                <Link
                  href={item.href}
                  className={`flex h-9 w-9 items-center justify-center rounded-lg transition-color md:h-8 md:w-8 text-zinc-300 hover:text-slate-500`}
                >
                  <item.icon className="h-5 w-5" />
                  <span className="sr-only">{item.label}</span>
                </Link>
              </TooltipTrigger>
              <TooltipContent side="right">{item.label}</TooltipContent>
            </Tooltip>
          ))}
        </nav>
        <nav className="mt-auto flex flex-col items-center gap-4 px-2 sm:py-5">
          <Tooltip>
            <TooltipTrigger asChild>
              <Link
                href="#"
                className="flex h-9 w-9 items-center justify-center rounded-lg text-zinc-300 hover:text-slate-500 transition-colors md:h-8 md:w-8"
              >
                <Settings className="h-5 w-5" />
                <span className="sr-only">Configurações</span>
              </Link>
            </TooltipTrigger>
            <TooltipContent side="right">Configurações</TooltipContent>
          </Tooltip>
        </nav>
      </TooltipProvider>
    </aside>
  );
}
