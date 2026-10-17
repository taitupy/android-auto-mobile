package com.tu.core;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Properties;

/**
 * Đọc cấu hình từ src/test/resources/config/{env}.properties
 *
 * Thứ tự ưu tiên khi lấy 1 giá trị:
 *   1. Tham số dòng lệnh / CI:   mvn test -Dappium.url=...
 *   2. File properties:          config/dev.properties (mặc định env=dev)
 *   3. Giá trị mặc định truyền vào hàm get(...)
 *
 * Đổi môi trường:  mvn test -Denv=staging
 */
public final class ConfigReader {

    private static final String ENV = System.getProperty("env", "dev");
    private static final Properties PROPS = new Properties();

    static {
        String file = "config/" + ENV + ".properties";
        try (InputStream in = ConfigReader.class.getClassLoader().getResourceAsStream(file)) {
            if (in != null) {
                PROPS.load(new InputStreamReader(in, StandardCharsets.UTF_8));
                System.out.println("[ConfigReader] Da load " + file);
            } else {
                System.out.println("[ConfigReader] Khong tim thay " + file + " -> dung gia tri mac dinh");
            }
        } catch (Exception e) {
            throw new RuntimeException("Loi doc file cau hinh " + file, e);
        }
    }

    private ConfigReader() { }

    public static String get(String key, String defaultValue) {
        String fromCommandLine = System.getProperty(key);
        if (fromCommandLine != null && !fromCommandLine.isBlank()) {
            return fromCommandLine.trim();
        }
        return PROPS.getProperty(key, defaultValue).trim();
    }

    public static int getInt(String key, int defaultValue) {
        return Integer.parseInt(get(key, String.valueOf(defaultValue)));
    }
}
