package com.bupt.travelsystem_v1_backend.service;

import org.springframework.stereotype.Service;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.zip.DataFormatException;
import java.util.zip.Deflater;
import java.util.zip.Inflater;

@Service
public class CompressionService {
    private static final int COMPRESSION_LEVEL = Deflater.BEST_COMPRESSION;
    private static final int BUFFER_SIZE = 1024;

    /**
     * 压缩文本内容
     * @param content 原始文本内容
     * @return 压缩后的字节数组，如果压缩后大小大于原始大小则返回原始数据
     * @throws IOException 如果压缩过程中发生IO错误
     */
    public byte[] compressContent(String content) throws IOException {
        if (content == null || content.isEmpty()) {
            return null;
        }

        byte[] input = content.getBytes(StandardCharsets.UTF_8);
        Deflater deflater = new Deflater(COMPRESSION_LEVEL);
        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            deflater.setInput(input);
            deflater.finish();

            byte[] buffer = new byte[BUFFER_SIZE];
            while (!deflater.finished()) {
                int count = deflater.deflate(buffer);
                outputStream.write(buffer, 0, count);
            }

            byte[] compressed = outputStream.toByteArray();
            return compressed.length < input.length ? compressed : input;
        } finally {
            deflater.end();
        }
    }

    /**
     * 解压文本内容
     * @param compressedData 压缩后的字节数组
     * @return 解压后的文本内容
     * @throws DataFormatException 如果压缩数据格式错误
     * @throws IOException 如果解压过程中发生IO错误
     */
    public String decompressContent(byte[] compressedData) throws DataFormatException, IOException {
        if (compressedData == null || compressedData.length == 0) {
            return null;
        }

        Inflater inflater = new Inflater();
        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            inflater.setInput(compressedData);

            byte[] buffer = new byte[BUFFER_SIZE];
            while (!inflater.finished()) {
                int count = inflater.inflate(buffer);
                outputStream.write(buffer, 0, count);
            }
            return outputStream.toString(StandardCharsets.UTF_8.name());
        } finally {
            inflater.end();
        }
    }
} 