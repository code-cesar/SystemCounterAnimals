package io.system.counter.syntax;

import io.system.counter.exception.SyntaxRuleException;
import io.system.counter.model.RuleCounter;

import java.util.Optional;

public interface SyntaxRule {

    /**
     * Парсинг правил из передаваемого текста
     * @param input Передаваемый текст
     * @return Опциональный объект RuleCounter
     * @throws SyntaxRuleException - Исключительная ситуация при парсинги правила
     */
    Optional<RuleCounter> get(final String input) throws SyntaxRuleException;

    /**
     * Парсинг правил из передаваемого текста
     * @param nameRule Наименования правила
     * @param input Передаваемый текст
     * @return Опциональный объект RuleCounter
     * @throws SyntaxRuleException - Исключительная ситуация при парсинги правила
     */
    Optional<RuleCounter> get(final String nameRule, final String input) throws SyntaxRuleException;
}
