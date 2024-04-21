package io.system.counter.reader.data.type;

import io.system.counter.model.RuleCounter;
import io.system.counter.reader.data.DataReader;
import io.system.counter.reader.type.TextFile;

import java.util.List;
import java.util.function.Consumer;

/**
 * Текстовый представления данных
 */
public class TextDataReader extends DataReader<String> {
    public TextDataReader(final String fileName,
                          final List<RuleCounter> ruleCounters) {
        super(fileName, ruleCounters, new TextFile());
    }

    /**
     * Проверяет правила и увеличивает счётчик для входящей строчки
     * @return Действие
     */
    @Override
    protected Consumer<String> action() {
        return (input -> rules.forEach((ruleCounter ->
                ruleCounter.testAndIncrement(input))));
    }
}
