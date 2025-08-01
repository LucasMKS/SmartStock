"use client";

import { useEffect, useCallback, useRef } from "react";

interface UseBarcodeScanner {
  onBarcodeDetected: (barcode: string) => void;
  enabled?: boolean;
  minLength?: number;
  maxTypingSpeed?: number; // ms entre teclas para considerar como digitação humana
  endTimeout?: number; // ms para finalizar leitura após última tecla
}

export function useGlobalBarcodeScanner({
  onBarcodeDetected,
  enabled = true,
  minLength = 8,
  maxTypingSpeed = 50, // Humanos normalmente não digitam mais rápido que 50ms entre teclas
  endTimeout = 100,
}: UseBarcodeScanner) {
  const barcodeRef = useRef("");
  const timeoutRef = useRef<NodeJS.Timeout | null>(null);
  const keystrokeTimestamps = useRef<number[]>([]);

  const processBarcodeBuffer = useCallback(() => {
    if (barcodeRef.current.length >= minLength) {
      // Verificar se foi digitação rápida (scanner) ou lenta (humano)
      const timestamps = keystrokeTimestamps.current;
      if (timestamps.length >= 2) {
        const intervals = timestamps
          .slice(1)
          .map((time, index) => time - timestamps[index]);
        const avgInterval =
          intervals.reduce((a, b) => a + b, 0) / intervals.length;

        // Se a velocidade média for muito alta, considerar como scanner
        if (avgInterval <= maxTypingSpeed) {
          const cleanCode = barcodeRef.current
            .replace(/[^a-zA-Z0-9]/g, "")
            .trim();

          if (cleanCode.length >= minLength) {
            console.log(
              "Scanner detectado - Código:",
              cleanCode,
              "Velocidade média:",
              avgInterval + "ms"
            );
            onBarcodeDetected(cleanCode);
          }
        } else {
          console.log(
            "Digitação humana detectada - ignorando",
            avgInterval + "ms"
          );
        }
      }
    }

    // Reset
    barcodeRef.current = "";
    keystrokeTimestamps.current = [];
  }, [minLength, maxTypingSpeed, onBarcodeDetected]);

  const handleKeyPress = useCallback(
    (event: KeyboardEvent) => {
      if (!enabled) return;

      // Ignorar se estiver digitando em um input/textarea
      const target = event.target as HTMLElement;
      if (
        target.tagName === "INPUT" ||
        target.tagName === "TEXTAREA" ||
        target.isContentEditable
      ) {
        return;
      }

      const currentTime = Date.now();

      // Enter indica fim da leitura
      if (event.key === "Enter") {
        event.preventDefault();

        if (timeoutRef.current) {
          clearTimeout(timeoutRef.current);
        }

        processBarcodeBuffer();
        return;
      }

      // Reset se passou muito tempo desde a última tecla
      if (keystrokeTimestamps.current.length > 0) {
        const lastTime =
          keystrokeTimestamps.current[keystrokeTimestamps.current.length - 1];
        if (currentTime - lastTime > endTimeout * 3) {
          barcodeRef.current = "";
          keystrokeTimestamps.current = [];
        }
      }

      // Adicionar apenas caracteres alfanuméricos
      if (event.key.match(/[a-zA-Z0-9]/)) {
        barcodeRef.current += event.key;
        keystrokeTimestamps.current.push(currentTime);

        // Limitar histórico de timestamps
        if (keystrokeTimestamps.current.length > 20) {
          keystrokeTimestamps.current = keystrokeTimestamps.current.slice(-20);
        }

        // Limpar timeout anterior
        if (timeoutRef.current) {
          clearTimeout(timeoutRef.current);
        }

        // Timeout para processar caso não venha Enter
        timeoutRef.current = setTimeout(() => {
          processBarcodeBuffer();
        }, endTimeout);
      }
    },
    [enabled, endTimeout, processBarcodeBuffer]
  );

  useEffect(() => {
    if (enabled) {
      document.addEventListener("keydown", handleKeyPress);
      return () => {
        document.removeEventListener("keydown", handleKeyPress);
        if (timeoutRef.current) {
          clearTimeout(timeoutRef.current);
        }
      };
    }
  }, [handleKeyPress, enabled]);

  return {
    clearBuffer: () => {
      barcodeRef.current = "";
      keystrokeTimestamps.current = [];
      if (timeoutRef.current) {
        clearTimeout(timeoutRef.current);
      }
    },
  };
}
