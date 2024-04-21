package io.system.counter.reader.type;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import io.system.counter.reader.TypeFile;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.function.Consumer;

public final class TextFile implements TypeFile<String> {
    private static final Logger log = LoggerFactory.getLogger(TextFile.class);

    /**
     * Загрузка файла
     * @param path Путь до файла
     * @param consumer - Действие
     */
    @Override
    public void load(final Path path, final Consumer<String> consumer) {
        try (final BufferedReader reader
                     = Files.newBufferedReader(path)) {
            String line;
            while ((line = reader.readLine()) != null){
                consumer.accept(line);
            }
        }catch (final IOException e){
            log.error("Not read file {}. Error {}", path.getFileName(), e);
        }
    }
}
