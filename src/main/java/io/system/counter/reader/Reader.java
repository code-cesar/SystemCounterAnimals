package io.system.counter.reader;

import io.system.counter.model.RuleCounter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public abstract class Reader<T> {
    protected static final Logger log = LoggerFactory.getLogger(Reader.class);

    protected final List<RuleCounter> rules;
    protected final Path path;

    private final TypeFile<T> typeFile;

    /**
     * Создания читателя с правилами
     * @param fileName - Наименования
     * @param typeFile - Тип файла
     * @param ruleCounters - Список правил
     */
    protected Reader(final String fileName,
                     final TypeFile<T> typeFile,
                     final List<RuleCounter> ruleCounters) {
        path = Paths.get(fileName);
        rules = ruleCounters;
        this.typeFile = typeFile;
    }

    /**
     * Создания читателя без правил
     * @param fileName - Наименования
     * @param typeFile - Тип файла
     */
    protected Reader(final String fileName,
                     final TypeFile<T> typeFile) {
        this(fileName, typeFile, new ArrayList<>());
    }

    /**
     * Загрузка файла
     */
    public void load() {
        typeFile.load(path, action());
    }

    /**
     * @return Действия при загрузке файла
     */
    protected abstract Consumer<T> action();

    /**
     * @return Получения списка правил
     */
    public List<RuleCounter> getRules(){
        return rules;
    }
}
