package io.system.counter.reader;

import io.system.counter.model.RuleCounter;
import io.system.counter.reader.data.DataReader;
import io.system.counter.reader.data.type.TextDataReader;
import io.system.counter.reader.rule.RuleReader;
import io.system.counter.reader.rule.type.TextRuleReader;
import io.system.counter.syntax.rule.SimpleSyntax;


import java.util.List;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.Function;

/**
 * Фабрика для создания читателя по определенному формату
 */
public enum FactoryReader {
    TXT((ruleFile) -> new TextRuleReader(ruleFile, new SimpleSyntax()),
            TextDataReader::new, "txt"),
    ;

    private final Function<String, RuleReader<?>> factoryRuleReader;
    private final BiFunction<String, List<RuleCounter>, DataReader<?>> factoryDataReader;

    private final String[] prefixFormat;

    /**
     * @param factoryRuleReader - Фабрика для создания экземпляра на чтения правил
     * @param factoryDataReader - Фабрика для создания экземпляра на чтения данных
     * @param prefixFormat - Окончания файлов
     */
    FactoryReader(final Function<String, RuleReader<?>> factoryRuleReader,
                  final BiFunction<String, List<RuleCounter>, DataReader<?>> factoryDataReader,
                  final String... prefixFormat) {
        this.factoryRuleReader = factoryRuleReader;
        this.factoryDataReader = factoryDataReader;
        this.prefixFormat = prefixFormat;
    }

    /**
     * Получения экземпляра чтения правил
     * @param ruleFileName - Наименования файла
     * @return Созданный экземпляр чтения правил
     */
    public RuleReader<?> getRuleReader(final String ruleFileName) {
        return factoryRuleReader.apply(ruleFileName);
    }

    /**
     * Получения экземпляра чтения данных
     * @param dataFileName - Наименования файла
     * @param rules - Список правил
     * @return Созданный экземпляр чтения данных
     */
    public DataReader<?> getDataReader(final String dataFileName, final List<RuleCounter> rules) {
        return factoryDataReader.apply(dataFileName, rules);
    }

    /**
     * Получения фабрику по префиксу из названия
     * @param fileName - Наименования файла
     * @return - Возвращает опционально фабрику, иначе пустоту
     */
    public static Optional<FactoryReader> get(final String fileName){
        for(FactoryReader factoryReader : values()){
            for(String prefix : factoryReader.prefixFormat) {
                if (fileName.endsWith(prefix)) {
                    return Optional.of(factoryReader);
                }
            }
        }
        return Optional.empty();
    }
}
