import type { Metadata } from "next";
import { Inter } from "next/font/google";
import "./globals.css";
import { Toaster } from "@/components/ui/sonner";
import ClientLayout from "@/components/layout/ClientLayout"; // Importe o novo componente

const inter = Inter({ subsets: ["latin"] });

export const metadata: Metadata = {
  title: "Gerenciador de Estoque",
  description: "Gerenciador de Estoque - Aplicação para controle de estoque",
};

export default function RootLayout({
  children,
}: Readonly<{
  children: React.ReactNode;
}>) {
  return (
    <html lang="pt-br">
      <body className={inter.className}>
        <ClientLayout>{children}</ClientLayout>
        <Toaster richColors position="top-right" />
      </body>
    </html>
  );
}
