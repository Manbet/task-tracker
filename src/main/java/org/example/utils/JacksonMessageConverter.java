package org.example.utils;

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;
import org.springframework.messaging.converter.AbstractJsonMessageConverter;
import org.springframework.messaging.converter.MessageConversionException;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.lang.reflect.Type;

@Slf4j
@RequiredArgsConstructor
public class JacksonMessageConverter extends AbstractJsonMessageConverter {

    private final ObjectMapper objectMapper;

    @Override
    protected Object fromJson(Reader reader, Type resolvedType) {
        try {
            JavaType javaType = objectMapper.constructType(resolvedType);
            return objectMapper.readValue(reader, javaType);
        } catch (IOException e) {
            log.error("Ошибка десериализации сообщений", e);
            return null;
        }
    }

    @Override
    protected Object fromJson(String payload, Type resolvedType) {
        try {
            JavaType javaType = objectMapper.constructType(resolvedType);
            return objectMapper.readValue(payload, javaType);
        } catch (IOException e) {
            throw new MessageConversionException("Failed to convert JSON from String", e);
        }
    }

    @Override
    protected void toJson(Object payload, Type resolvedType, Writer writer) {
        try {
            writer.write(objectMapper.writeValueAsString(payload));
        } catch (IOException e) {
            log.error("Ошибка сериализации сообщений", e);
        }
    }

    @Override
    protected String toJson(Object payload, Type resolvedType) {
        try {
            return objectMapper.writeValueAsString(payload);
        } catch (IOException e) {
            log.error("Ошибка сериализации сообщений", e);
            return null;
        }
    }
}
