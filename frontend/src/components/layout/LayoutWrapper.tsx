// src/components/layout/LayoutWrapper.tsx
"use client"; // Necessário para usar hooks do React e outros componentes client-side

import Sidebar, { MobileSidebar } from "@/components/layout/Sidebar"; // Importa a Sidebar principal e mobile
import Image from "next/image"; // Importa o componente Image do Next.js para otimização de imagens

export default function LayoutWrapper({
  children,
}: {
  children: React.ReactNode;
}) {
  return (
    // Grid para layout de duas colunas (sidebar + conteúdo)
    <div className="grid min-h-screen w-full md:grid-cols-[200px_1fr] lg:grid-cols-[230px_1fr]">
      <Sidebar /> {/* Renderiza a Sidebar (visível em desktop) */}
      <div className="flex flex-col">
        {/* Header Superior (com menu mobile e, opcionalmente, barra de pesquisa/usuário) */}
        <header className="flex h-14 items-center gap-4 border-b bg-muted/40 px-4 lg:h-[60px] lg:px-6">
          <MobileSidebar /> {/* Renderiza o botão do menu mobile */}
          <div className="w-full flex-1">
            {/* <Image
              src="https://picsum.photos/200"
              alt="Descrição da imagem"
              fill
              sizes="100vw"
              style={{ objectFit: "cover" }}
            /> */}
          </div>
          {/* Adicione aqui qualquer componente de usuário, dropdown, etc., no canto superior direito */}
        </header>
        {/* Conteúdo Principal da Página */}
        <main className="flex flex-1 flex-col gap-4 p-4 lg:gap-6 lg:p-6">
          {children}{" "}
          {/* O conteúdo da página atual (e.g., ProdutosPage, HistoricoPage) será renderizado aqui */}
        </main>
      </div>
    </div>
  );
}
