package com.projetointegrador.petshop.infrastructure.web;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class JsonUtil {
    
    // Code Smell: Reinventing the Wheel resolvido. Usando Gson.
    private static final Gson gson = new GsonBuilder()
            .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
            .setPrettyPrinting()
            .create();

    public static String toJson(Object object) {
        return gson.toJson(object);
    }

    public static <T> T fromJson(String json, Class<T> classOfT) {
        try {
            return gson.fromJson(json, classOfT);
        } catch (JsonSyntaxException e) {
            throw new IllegalArgumentException("JSON inválido: " + e.getMessage());
        }
    }

    // Adapter simples para LocalDateTime (Gson padrão não lida bem com Java 8 Time)
    private static class LocalDateTimeAdapter extends com.google.gson.TypeAdapter<LocalDateTime> {
        private final DateTimeFormatter formatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME;
        
        @Override
        public void write(com.google.gson.stream.JsonWriter out, LocalDateTime value) throws java.io.IOException {
            if (value == null) out.nullValue();
            else out.value(value.format(formatter));
        }

        @Override
        public LocalDateTime read(com.google.gson.stream.JsonReader in) throws java.io.IOException {
            if (in.peek() == com.google.gson.stream.JsonToken.NULL) {
                in.nextNull();
                return null;
            }
            return LocalDateTime.parse(in.nextString(), formatter);
        }
    }
}
