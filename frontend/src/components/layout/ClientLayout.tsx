"use client";

import { usePathname } from "next/navigation";

// --- ESCOLHA SEU LAYOUT ---
// Descomente o layout que deseja usar e comente o outro.

// Opção 1: Layout Original
// import Sidebar from "@/components/layout/Sidebar";
// import Header from "@/components/layout/Header";

// Opção 2: Novo Layout Compacto (estilo shadcn)
import SidebarAlt from "@/components/layout/SidebarAlt";
import HeaderAlt from "@/components/layout/HeaderAlt";

export default function ClientLayout({
  children,
}: {
  children: React.ReactNode;
}) {
  const pathname = usePathname();
  const isHomePage = pathname === "/";

  if (isHomePage) {
    return <>{children}</>;
  }

  // --- RENDERIZAÇÃO DO LAYOUT ESCOLHIDO ---

  // Layout Original
  // return (
  //   <div className="grid min-h-screen w-full md:grid-cols-[220px_1fr] lg:grid-cols-[280px_1fr]">
  //     <Sidebar />
  //     <div className="flex flex-col">
  //       <Header />
  //       <main className="flex flex-1 flex-col gap-4 p-4 lg:gap-6 lg:p-6 bg-background">
  //         {children}
  //       </main>
  //     </div>
  //   </div>
  // );

  // Novo Layout Compacto
  return (
    <div className="flex min-h-screen w-full flex-col bg-muted/40">
      <SidebarAlt />
      <div className="flex flex-col sm:gap-4 sm:py-4 sm:pl-14">
        <HeaderAlt />
        <main className="grid flex-1 items-start gap-4 p-4 sm:px-6 sm:py-0 md:gap-8">
          {children}
        </main>
      </div>
    </div>
  );
}
