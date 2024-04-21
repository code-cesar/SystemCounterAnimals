package io.system.counter.reader.rule.type;

import io.system.counter.exception.SyntaxRuleException;
import io.system.counter.model.RuleCounter;
import io.system.counter.reader.rule.RuleReader;
import io.system.counter.reader.type.TextFile;
import io.system.counter.syntax.SyntaxRule;

import java.util.Optional;
import java.util.function.Consumer;

public final class TextRuleReader extends RuleReader<String> {
    private final SyntaxRule syntaxRule;

    /**
     * Текстовый читатель правил
     * @param fileName   - Наименования файла
     * @param syntaxRule - Тип синтаксиса правил
     */
    public TextRuleReader(final String fileName,
                          final SyntaxRule syntaxRule) {
        super(fileName, new TextFile());
        this.syntaxRule = syntaxRule;
    }


    /**
     * @return - Возвращает действие по парсу правила и добавления в список
     */
    @Override
    protected Consumer<String> action() {
        return (line -> {
            try {
                final String[] nameAndRule = line.split(";");

                final Optional<RuleCounter> rule = nameAndRule.length == 2
                        ? syntaxRule.get(nameAndRule[0], nameAndRule[1])
                        : syntaxRule.get(line);
                if(rule.isPresent()){
                    rules.add(rule.get());
                }else {
                    log.warn("Not parse line {}", line);
                }
            }catch (final SyntaxRuleException e){
                log.warn("Not parse line ", e);
            }
        });
    }
}
