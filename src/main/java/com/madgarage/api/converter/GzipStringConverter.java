package com.madgarage.api.converter;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

/**
 * JPA AttributeConverter that transparently GZip-compresses message text
 * before writing to the DB and decompresses on read.
 *
 * Typical savings: 50–80% for natural-language chat text.
 * Storage type: MEDIUMBLOB (supports up to 16MB of compressed data).
 */
@Converter
public class GzipStringConverter implements AttributeConverter<String, byte[]> {

    @Override
    public byte[] convertToDatabaseColumn(String attribute) {
        if (attribute == null || attribute.isBlank()) return null;
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream();
             GZIPOutputStream gzos = new GZIPOutputStream(baos)) {
            gzos.write(attribute.getBytes(StandardCharsets.UTF_8));
            gzos.finish();
            return baos.toByteArray();
        } catch (IOException e) {
            throw new RuntimeException("Failed to compress chat message for storage", e);
        }
    }

    @Override
    public String convertToEntityAttribute(byte[] dbData) {
        if (dbData == null || dbData.length == 0) return null;
        try (GZIPInputStream gzis = new GZIPInputStream(new ByteArrayInputStream(dbData))) {
            return new String(gzis.readAllBytes(), StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new RuntimeException("Failed to decompress chat message from storage", e);
        }
    }
}
