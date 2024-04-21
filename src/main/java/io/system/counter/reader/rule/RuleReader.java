package io.system.counter.reader.rule;

import io.system.counter.reader.Reader;
import io.system.counter.reader.TypeFile;

public abstract class RuleReader<T> extends Reader<T> {

    /**
     * Читатель правил
     * @param fileName - Наименования файла
     */
    protected RuleReader(final String fileName,
                         final TypeFile<T> typeFile) {
        super(fileName, typeFile);
    }
}
