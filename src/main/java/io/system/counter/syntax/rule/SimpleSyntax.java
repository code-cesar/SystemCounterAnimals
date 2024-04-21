package io.system.counter.syntax.rule;

import io.system.counter.exception.SyntaxRuleException;
import io.system.counter.model.RuleCounter;
import io.system.counter.syntax.Operation;
import io.system.counter.syntax.SyntaxRule;

import java.util.*;
import java.util.function.Function;
import java.util.function.Predicate;

/**
 * Парсинг простого синтакса с учётом скобок
 * СЛОВО И (СЛОВО ИЛИ СЛОВО)
 */
public class SimpleSyntax implements SyntaxRule {
    public static final Function<String, Predicate<String>> FUNCTION_CHECK_WORD
            = (word) -> (inputWords) -> inputWords.contains(word);


    private static class Brackets {
        private final Operation prevOperation;
        private final boolean isNegative;
        private Predicate<String> predicate;

        public Brackets(final Operation prevOperation,
                        final boolean isNegative) {
            this.prevOperation = prevOperation;
            this.isNegative = isNegative;
        }

        public Predicate<String> getPredicate() {
            return predicate;
        }

        /**
         * Объединение условий согласно оператору
         * @param tempPredicate - Условие с которым объединяем
         * @param operation - Логический оператор
         */
        public void join(final Predicate<String> tempPredicate, final Operation operation) {
            if(predicate != null) {
                if (operation == Operation.OR) {
                    predicate = predicate.or(tempPredicate);
                } else if (operation == Operation.AND) {
                    predicate = predicate.and(tempPredicate);
                }
            }
            else {
                predicate = tempPredicate;
            }
        }

        /**
         * Применение к текущему условию отрицание
         */
        public void applyNegative(){
            predicate = isNegativePredicate(predicate, isNegative);
        }

        public Operation getPrevOperation() {
            return prevOperation;
        }
    }

    /**
     * Применяет логику отрицания к передаваемому условию
     * @param tempPredicate - Условие
     * @param isNegative - Необходимо ли применять отрицание
     * @return Возвращает условие с отрицанием (isNegative = true), или обычное условие
     */
    private static Predicate<String> isNegativePredicate(final Predicate<String> tempPredicate, final boolean isNegative){
        if(isNegative){
            return tempPredicate.negate();
        }
        return tempPredicate;
    }

    private final List<Brackets> bracketPool;

    private final StringBuilder carrage = new StringBuilder();
    private final StringBuilder words = new StringBuilder();

    public SimpleSyntax(){
        this(16);
    }

    /**
     * Создания списка с указанным размером
     * @param initCapacityPool - Размер
     */
    public SimpleSyntax(final int initCapacityPool){
        bracketPool = new ArrayList<>(initCapacityPool);
    }

    /**
     * Парсинг правил из передаваемого текста
     * @param input - Передуваемый текст
     * @return Опциональный объект RuleCounter
     * @throws SyntaxRuleException - Исключительная ситуация при парсинги правила
     */
    @Override
    public Optional<RuleCounter> get(final String input) throws SyntaxRuleException {
        return getRule(input, input);
    }

    /**
     * Парсинг правил из передаваемого текста
     * @param nameRule - Наименования правила
     * @param input    - Передуваемый текст
     * @return Опциональный объект RuleCounter
     * @throws SyntaxRuleException - Исключительная ситуация при парсинги правила
     */
    @Override
    public Optional<RuleCounter> get(String nameRule, String input) throws SyntaxRuleException {
        return getRule(nameRule, input);
    }

    /**
     * Парсинг правил из передаваемого текста
     * @param nameRule - Наименования правила
     * @param input - Передуваемый текст
     * @return Опциональный объект RuleCounter
     * @throws SyntaxRuleException - Исключительная ситуация при парсинги правила
     */
    private Optional<RuleCounter> getRule(final String nameRule, final String input) throws SyntaxRuleException{
        if(input == null || input.isEmpty()){
            return Optional.empty();
        }
        try {
            final Predicate<String> rule = parseRule(input.trim().toCharArray());
            return Optional.of(new RuleCounter(nameRule, rule));
        }catch (final SyntaxRuleException e){
            throw new SyntaxRuleException(nameRule, e);
        }finally {
            clearBuilder();
            bracketPool.clear();
        }
    }

    /**
     * Очистка builders
     */
    private void clearBuilder(){
        carrage.setLength(0);
        words.setLength(0);
    }

    /**
     * @param array - Массив символов
     * @return Окончательно условие
     * @throws SyntaxRuleException - Исключительная ситуация при парсинги правила
     */
    private Predicate<String> parseRule(final char[] array) throws SyntaxRuleException {
        int validBracket = 0;
        boolean isNegative = false;
        Operation operation = null;

        for(char ch : array){
            if(Operation.isSplitChar(ch) && carrage.length() > 0){
                final String word = carrage.toString().trim();
                carrage.setLength(0);
                final Optional<Operation> operationWord = Operation.isWordOperation(word);
                if(!operationWord.isPresent()){
                    words.append(word);
                    continue;
                }

                final Operation logic = operationWord.get();
                final boolean isNot = logic == Operation.NOT;
                if(!isNot){
                    final Predicate<String> tempPredicate = createPredicate(isNegative);

                    if(tempPredicate != null) {
                        joinPredicate(tempPredicate, operation);
                    }else if(bracketPool.isEmpty()){
                        throw new SyntaxRuleException.ValidSyntax("Not word before operation " + operation);
                    }
                    operation = logic;
                }
                isNegative = isNot;
                if(isExistWordAndNotOperation(operation)){
                    throw new SyntaxRuleException.ValidSyntax("Not operation before operation NO and saved word" + words);
                }
                words.setLength(0);
            }
            else if(Operation.isOpenBracket(ch)){
                if(isExistWordAndNotOperation(operation)){
                    throw new SyntaxRuleException.ValidSyntax("Not operation before open bracket and saved word " + words);
                }
                createBracket(operation, isNegative);
                operation = null;
                isNegative = false;
                clearBuilder();
                validBracket++;
            }
            else if(Operation.isClosedBracket(ch)){
                validBracket--;
                if(validBracket < 0){
                    throw new SyntaxRuleException.ValidBracket("Closed parenthesis comes first");
                }
                final Predicate<String> lastWordInBracket = createPredicate(isNegative);
                if(lastWordInBracket != null){
                    joinPredicate(lastWordInBracket, operation);
                }

                final int index = bracketPool.size() - 1;
                final Brackets bracket = bracketPool.get(index);
                final Operation bracketOperation = bracket.getPrevOperation();
                bracket.applyNegative();
                if(bracketOperation != null) {
                    bracketPool.remove(index);
                    joinPredicate(bracket.getPredicate(), bracketOperation);
                }

                isNegative = false;
                operation = null;
                clearBuilder();
            } else {
                carrage.append(ch);
            }
        }
        if(validBracket != 0){
            throw new SyntaxRuleException.ValidBracket(validBracket < 0
                    ? "Not open bracket"
                    : "Not closed bracket");
        }
        else if(carrage.length() > 0){
            final String word = carrage.toString().trim();
            final Optional<Operation> operationWord = Operation.isWordOperation(word);
            if(operationWord.isPresent()){
                throw new SyntaxRuleException.ValidSyntax("Not word after operation " + operation);
            }
        }
        final Predicate<String> lastWord = createPredicate(isNegative);
        if(lastWord != null){
            joinPredicate(lastWord, operation);
        }
        return bracketPool.get(0).getPredicate();
    }

    /**
     * Создаётся предикат из текста с билдеров. Функция проверки слова описана в FUNCTION_CHECK_WORD
     * @param isNegative - Если перед словом стоял оператор NOT
     * @return Созданный предикат, если в билдере нет текста, то возвращает null
     */
    private Predicate<String> createPredicate(final boolean isNegative){
        if(carrage.length() > 0 || words.length() > 0){
            final String word = carrage.length() > 0 ? carrage.toString().trim() : words.toString().trim();
            return isNegativePredicate(FUNCTION_CHECK_WORD.apply(word),
                    isNegative);
        }
        return null;
    }

    /**
     * Соединяет условия согласно оператору
     * @param tempPredicate Созданное новое условие
     * @param operation Логический оператор
     */
    private void joinPredicate(final Predicate<String> tempPredicate,
                               final Operation operation){
        if(!bracketPool.isEmpty()) {
            final int index = bracketPool.size() - 1;
            final Brackets bracket = bracketPool.get(index);
            bracket.join(tempPredicate, operation);
            bracketPool.set(index, bracket);
        } else {
            final Brackets bracket = createBracket(operation, false);
            bracket.join(tempPredicate, operation);
        }
    }

    /**
     * Инциализировать в пуле скобок скобку
     * @param operation - Логический оператор перед скобкой
     * @param isNegative - Стоит ли оператор NOT перед скобкой
     * @return - Возвращает объект скобки
     */
    private Brackets createBracket(final Operation operation, final boolean isNegative){
        final Brackets bracket = new Brackets(operation, isNegative);
        bracketPool.add(bracket);
        return bracket;
    }

    /**
     * В builder есть слово, но нет оператора
     * @param operation - Передаётся текущий оператор прохода
     * @return - True если есть слова, но нет оператора
     */
    private boolean isExistWordAndNotOperation(final Operation operation){
        return words.length() > 0 && operation == null;
    }
}
