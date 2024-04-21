package io.system.counter.writer;

import io.system.counter.model.RuleCounter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.function.Consumer;

public abstract class Writer implements AutoCloseable{
    protected static final Logger log = LoggerFactory.getLogger(Writer.class);

    private final List<RuleCounter> ruleCounter;

    protected Writer(final String fileName,
                     final List<RuleCounter> ruleCounter) {
        this.ruleCounter = ruleCounter;
    }

    /**
     * Выполнения действия для каждого правила в списке
     */
    public void output(){
        ruleCounter.forEach(write());
    }

    /**
     * @return Действия для каждого правила
     */
    protected abstract Consumer<RuleCounter> write();

    @Override
    public abstract void close() throws Exception;
}
