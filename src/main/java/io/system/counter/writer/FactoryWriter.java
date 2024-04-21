package io.system.counter.writer;

import io.system.counter.model.RuleCounter;
import io.system.counter.writer.type.Console;
import io.system.counter.writer.type.WriteFile;

import java.util.List;
import java.util.function.BiFunction;

public enum FactoryWriter {
    CONSOLE(Console::new),
    WRITE_FILE(WriteFile::new,".txt")
    ;

    private final BiFunction<String, List<RuleCounter>, Writer> factoryWriter;
    private final String[] prefixFormat;

    /**
     * @param factoryWriter - Фабрика для создания экземпляра на запись
     * @param prefixFormat - Окончания файлов
     */
    FactoryWriter(final BiFunction<String, List<RuleCounter>, Writer> factoryWriter,
                  final String... prefixFormat) {
        this.factoryWriter = factoryWriter;
        this.prefixFormat = prefixFormat;
    }

    /**
     * Получения экземпляра на запись
     * @param fileName - Наименования выходного файла
     * @param ruleCounters - Список правил
     * @return Экземпляр на запись
     */
    public Writer getWriter(final String fileName, final List<RuleCounter> ruleCounters){
        return factoryWriter.apply(fileName, ruleCounters);
    }

    /**
     * Получения фабрики согласно окончанию наименования файла
     * @param fileName - Наименования файла
     * @return Фабрика, по умолчанию CONSOLE
     */
    public static FactoryWriter get(final String fileName){
        for(FactoryWriter factoryWriter : values()){
            for(String prefix : factoryWriter.prefixFormat) {
                if (fileName.endsWith(prefix)) {
                    return factoryWriter;
                }
            }
        }
        return CONSOLE;
    }
}
