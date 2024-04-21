package io.system.counter.util;

import io.system.counter.syntax.Operation;

/**
 * Класс для построения правил
 */
public final class BuilderRule {
    private final StringBuilder build = new StringBuilder();

    /**
     * Добавляет слово к последовательности
     * @param word Добавляемое слово
     * @return Текущий объект
     */
    public BuilderRule word(final String word){
        build.append(word);
        return this;
    }

    /**
     * Добавляет оператор к последовательности, если оператор логический, то добавляет разделители
     * @param operation - Добавляемый оператор
     * @return Текущий объект
     */
    public BuilderRule operation(final Operation operation){
        if(Operation.isLogicOperation(operation)) {
            if (operation == Operation.OR || operation == Operation.AND) {
                build.append(Operation.SPLIT.getOperationName());
            }
            build.append(operation.getOperationName())
                    .append(Operation.SPLIT.getOperationName());
        }
        else build.append(operation.getOperationName());
        return this;
    }

    /**
     * Переводит текущую последовательность в String и очищает Builder
     * @return Строка
     */
    public String build(){
        final String result = build.toString();
        build.setLength(0);
        return result;
    }
}
