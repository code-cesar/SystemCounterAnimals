package io.system.counter.reader;

import java.nio.file.Path;
import java.util.function.Consumer;

public interface TypeFile<T> {
    /**
     * Загрузить файл и при загрузке выполнить действия
     * @param path Путь до файла
     * @param consumer - Действие
     */
    void load(final Path path, final Consumer<T> consumer);
}
