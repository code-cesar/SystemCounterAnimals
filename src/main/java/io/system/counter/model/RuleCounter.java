package io.system.counter.model;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Predicate;

public final class RuleCounter {
    private final String nameRule;
    private final Predicate<String> predicate;
    private final AtomicInteger count = new AtomicInteger();

    public RuleCounter(final String name,
                       final Predicate<String> pred) {
        this.nameRule = name;
        this.predicate = pred;
    }

    /**
     * @return Получения наименования правил
     */
    public String getNameRule() {
        return nameRule;
    }

    /**
     * @return Получения условия
     */
    public Predicate<String> getPredicate() {
        return predicate;
    }

    /**
     * Проверяет входную строчку на соответствия правил И увеличивает счётчик
     * @param input - Входная строка
     */
    public void testAndIncrement(final String input){
        if(predicate.test(input)){
            count.incrementAndGet();
        }
    }

    public int getCount() {
        return count.get();
    }

    @Override
    public String toString() {
        return nameRule + " - " + count;
    }
}
