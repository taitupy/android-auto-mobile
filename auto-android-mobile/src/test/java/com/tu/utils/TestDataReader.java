package com.tu.utils;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Đọc test data từ src/test/resources/testdata/*.json
 *
 * Tách dữ liệu ra khỏi code:
 *   - Thêm/sửa bộ dữ liệu chỉ cần sửa file JSON, KHÔNG phải sửa code Java
 *   - Người không biết code (BA, QC manual) cũng đọc/bổ sung được dữ liệu
 */
public final class TestDataReader {

    private static final ObjectMapper MAPPER = new ObjectMapper();

    private TestDataReader() { }

    /** Đọc cả file JSON thành cây node (dùng cho dữ liệu dạng object) */
    public static JsonNode read(String fileName) {
        String path = "testdata/" + fileName;
        try (InputStream in = TestDataReader.class.getClassLoader().getResourceAsStream(path)) {
            if (in == null) {
                throw new IllegalArgumentException("Khong tim thay file test data: " + path);
            }
            return MAPPER.readTree(in);
        } catch (IllegalArgumentException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException("Loi doc file test data " + path, e);
        }
    }

    /** Lấy 1 giá trị chữ theo key, vd: getString("wifi.json", "wifiName") */
    public static String getString(String fileName, String key) {
        JsonNode node = read(fileName).get(key);
        if (node == null) {
            throw new IllegalArgumentException("File " + fileName + " khong co key: " + key);
        }
        return node.asText();
    }

    /**
     * Chuyển 1 mảng JSON thành Object[][] cho @DataProvider của TestNG.
     *
     * Ví dụ file:  { "messages": [ {"caseName":"Ngắn","message":"Hi"}, ... ] }
     * Gọi:         toDataProvider("sms-messages.json", "messages", "caseName", "message")
     * Kết quả:     { {"Ngắn","Hi"}, ... }  -> mỗi dòng = 1 lần chạy test
     */
    public static Object[][] toDataProvider(String fileName, String arrayKey, String... fields) {
        JsonNode array = read(fileName).get(arrayKey);
        if (array == null || !array.isArray()) {
            throw new IllegalArgumentException("File " + fileName + " khong co mang: " + arrayKey);
        }
        List<Object[]> rows = new ArrayList<>();
        for (JsonNode item : array) {
            Object[] row = new Object[fields.length];
            for (int i = 0; i < fields.length; i++) {
                JsonNode value = item.get(fields[i]);
                row[i] = value == null ? null : value.asText();
            }
            rows.add(row);
        }
        return rows.toArray(new Object[0][]);
    }
}
