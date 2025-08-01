package com.lucasmks.api.adapter;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import java.io.IOException;
import java.time.LocalDateTime;

public class LocalDateTimeAdapter extends TypeAdapter<LocalDateTime> {

    @Override
    public void write(JsonWriter out, LocalDateTime value) throws IOException {
        if (value == null) {
            out.nullValue();
            return;
        }
        // Converte o LocalDateTime para o formato de array que o frontend espera
        out.beginArray();
        out.value(value.getYear());
        out.value(value.getMonthValue());
        out.value(value.getDayOfMonth());
        out.value(value.getHour());
        out.value(value.getMinute());
        out.value(value.getSecond());
        out.value(value.getNano());
        out.endArray();
    }

    @Override
    public LocalDateTime read(JsonReader in) throws IOException {
        // Implementação para ler do JSON de volta para LocalDateTime (se necessário no futuro)
        in.beginArray();
        int year = in.nextInt();
        int month = in.nextInt();
        int day = in.nextInt();
        int hour = in.nextInt();
        int minute = in.nextInt();
        int second = in.nextInt();
        int nano = in.nextInt();
        in.endArray();
        return LocalDateTime.of(year, month, day, hour, minute, second, nano);
    }
}