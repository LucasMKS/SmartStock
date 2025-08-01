"use client";

import Link from "next/link";
import { Menu, Package2 } from "lucide-react";
import { Sheet, SheetContent, SheetTrigger } from "@/components/ui/sheet";
import { Button } from "@/components/ui/button";
import { NavigationLinks } from "@/components/layout/Sidebar"; // Reutiliza os links

export default function Header() {
  return (
    <header className="flex h-14 items-center gap-4 border-b bg-muted/40 px-4 lg:h-[60px] lg:px-6">
      {/* Menu Mobile */}
      <Sheet>
        <SheetTrigger asChild>
          <Button variant="outline" size="icon" className="shrink-0 md:hidden">
            <Menu className="h-5 w-5" />
            <span className="sr-only">Abrir menu de navegação</span>
          </Button>
        </SheetTrigger>
        <SheetContent side="left" className="flex flex-col">
          <div className="mt-6">
            <NavigationLinks />
          </div>
        </SheetContent>
      </Sheet>

      {/* Espaço para outros elementos do Header, como busca ou perfil */}
      <div className="w-full flex-1">
        {/* Ex: <Input type="search" placeholder="Buscar produtos..." /> */}
      </div>

      {/* Ex: Botão de Perfil do Usuário */}
      {/* <Button variant="secondary" size="icon" className="rounded-full">
        <CircleUser className="h-5 w-5" />
        <span className="sr-only">Toggle user menu</span>
      </Button> */}
    </header>
  );
}
