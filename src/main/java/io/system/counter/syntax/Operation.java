package io.system.counter.syntax;

import io.system.counter.util.PropertiesUtil;

import java.util.EnumSet;
import java.util.Optional;

public enum Operation {
    OR("operation.or", "ИЛИ"),
    AND("operation.and", "И"),
    NOT("operation.no", "НЕ"),

    OPEN_BRACKET("operation.open_bracket", "("),
    CLOSED_BRACKET("operation.closed_bracket", ")"),
    SPLIT("operation.split", " "),
    ;

    private static final EnumSet<Operation> logics = EnumSet.of(OR, AND, NOT);

    private final String operationName;

    /**
     * Названия операторов берется из ресурсов
     * @param operationName - Наименования переменной из файла ресурса
     * @param defaultValue - Значение по умолчанию, если оператор не найден
     */
    Operation(final String operationName, final String defaultValue) {
        final String value = PropertiesUtil.get(operationName, defaultValue);
        this.operationName = !value.isEmpty() ? value : defaultValue;
    }

    /**
     * @return Наименования оператора
     */
    public String getOperationName() {
        return operationName;
    }

    /**
     * Проверяет что передаваемый символ - открытая скобка
     * @param ch - символ
     * @return True если символ это открытая скобка
     */
    public static boolean isOpenBracket(final char ch) {
        return OPEN_BRACKET.operationName.charAt(0) == ch;
    }

    /**
     * Проверяет что передаваемый символ - закрытая скобка
     * @param ch - символ
     * @return True если символ это закрытая скобка
     */
    public static boolean isClosedBracket(final char ch) {
        return CLOSED_BRACKET.operationName.charAt(0) == ch;
    }

    /**
     * Проверяет что передаваемый символ - разделитель
     * @param ch - символ
     * @return True если символ разделитель
     */
    public static boolean isSplitChar(final char ch){
        return SPLIT.operationName.charAt(0) == ch;
    }

    /**
     * Возвращает оператор по слову
     * @param word - Слово
     * @return Возвращает Optional оператора, если оператор не найден, то Optional пуст
     */
    public static Optional<Operation> isWordOperation(final String word){
        for (Operation operation : logics) {
            if (word.equalsIgnoreCase(operation.operationName)) {
                return Optional.of(operation);
            }
        }
        return Optional.empty();
    }

    /**
     * Проверяет что оператор логический
     * @param operation - Оператор
     * @return - True если оператор логический
     */
    public static boolean isLogicOperation(final Operation operation){
        return logics.contains(operation);
    }
}
