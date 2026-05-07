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

        // Check for GZIP magic header (0x1f, 0x8b) before attempting decompression
        if (dbData.length >= 2 && dbData[0] == (byte) 0x1f && dbData[1] == (byte) 0x8b) {
            try (GZIPInputStream gzis = new GZIPInputStream(new ByteArrayInputStream(dbData))) {
                return new String(gzis.readAllBytes(), StandardCharsets.UTF_8);
            } catch (IOException e) {
                // If decompression fails despite magic header, fallback to raw string to prevent app crash
                return new String(dbData, StandardCharsets.UTF_8);
            }
        }

        // Data is not GZIP (likely legacy text), return as raw UTF-8 string
        return new String(dbData, StandardCharsets.UTF_8);
    }
}
