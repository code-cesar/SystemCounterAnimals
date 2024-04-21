package io.system.counter.writer.type;

import io.system.counter.model.RuleCounter;
import io.system.counter.writer.Writer;

import java.util.List;
import java.util.function.Consumer;

public class Console extends Writer {
    public Console(final String fileName, final List<RuleCounter> ruleCounter) {
        super(fileName, ruleCounter);
    }

    @Override
    protected Consumer<RuleCounter> write() {
        return (System.out::println);
    }

    @Override
    public void close() {}
}
