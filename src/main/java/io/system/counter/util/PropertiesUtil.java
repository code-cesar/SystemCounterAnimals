package io.system.counter.util;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * Класс для загрузки и взаимодействия с характеристиками из файла
 */
public final class PropertiesUtil {
    private static final String NAME_FILE_RESOURCE = "config.properties";

    private static final Properties PROPERTIES = new Properties();

    static {
        load(null);
    }

    private PropertiesUtil(){

    }

    /**
     * Получить значение по ключу из NAME_FILE_RESOURCE
     * @param key - Ключ
     * @return - Значение
     */
    public static String get(final String key){
        return PROPERTIES.getProperty(key);
    }

    /**
     * Получить значение по ключу из NAME_FILE_RESOURCE, если ключ не найден,
     * то возвращать значение по умолчанию
     * @param key - Ключ
     * @param defaultValue - Значение по умолчанию
     * @return Значение
     */
    public static String get(final String key, final String defaultValue){
        return PROPERTIES.getProperty(key, defaultValue);
    }

    /**
     * Читает файл NAME_FILE_RESOURCE
     * @throws RuntimeException - Если произошла ошибка при чтении файла
     */
    public static void load(final String path) {
        try (final InputStream inputStream =
                     path == null ? PropertiesUtil.class.getClassLoader().getResourceAsStream(NAME_FILE_RESOURCE)
                : new FileInputStream(path)){
            if(inputStream == null){
                return;
            }
            PROPERTIES.load(inputStream);
        }catch (final IOException e){
            throw new RuntimeException(e);
        }
    }
}
