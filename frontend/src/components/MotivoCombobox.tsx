"use client";

import * as React from "react";
import { Check, ChevronsUpDown } from "lucide-react";

import { cn } from "@/lib/utils";
import { Button } from "@/components/ui/button";
import {
  Command,
  CommandEmpty,
  CommandGroup,
  CommandInput,
  CommandItem,
} from "@/components/ui/command";
import {
  Popover,
  PopoverContent,
  PopoverTrigger,
} from "@/components/ui/popover";
import { Input } from "./ui/input";

interface MotivoComboboxProps {
  value: string;
  onChange: (value: string) => void;
  motivosSugeridos: string[];
  placeholder?: string;
  disabled?: boolean;
}

export function MotivoCombobox({
  value,
  onChange,
  motivosSugeridos,
  placeholder = "Selecione ou digite um motivo...",
  disabled = false,
}: MotivoComboboxProps) {
  const [open, setOpen] = React.useState(false);

  // Permite que o usuário digite diretamente no input principal
  const handleInputChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    onChange(e.target.value);
  };

  return (
    <div className="flex items-center space-x-2">
      <Input
        value={value}
        onChange={handleInputChange}
        placeholder={placeholder}
        disabled={disabled}
        className="flex-grow"
      />
      <Popover open={open} onOpenChange={setOpen}>
        <PopoverTrigger asChild>
          <Button
            variant="outline"
            role="combobox"
            aria-expanded={open}
            className="w-[40px] justify-center p-0"
            disabled={disabled}
          >
            <ChevronsUpDown className="h-4 w-4 shrink-0 opacity-50" />
          </Button>
        </PopoverTrigger>
        <PopoverContent className="w-[--radix-popover-trigger-width] p-0">
          <Command>
            <CommandInput placeholder="Buscar motivo..." />
            <CommandEmpty>Nenhum motivo encontrado.</CommandEmpty>
            <CommandGroup>
              {motivosSugeridos.map((motivo) => (
                <CommandItem
                  key={motivo}
                  value={motivo}
                  onSelect={(currentValue) => {
                    // Define o valor selecionado e fecha o popover
                    onChange(currentValue === value ? "" : currentValue);
                    setOpen(false);
                  }}
                >
                  <Check
                    className={cn(
                      "mr-2 h-4 w-4",
                      value.toLowerCase() === motivo.toLowerCase()
                        ? "opacity-100"
                        : "opacity-0"
                    )}
                  />
                  {motivo}
                </CommandItem>
              ))}
            </CommandGroup>
          </Command>
        </PopoverContent>
      </Popover>
    </div>
  );
}
