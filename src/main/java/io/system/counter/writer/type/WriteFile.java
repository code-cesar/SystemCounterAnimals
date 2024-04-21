package io.system.counter.writer.type;

import io.system.counter.model.RuleCounter;
import io.system.counter.writer.Writer;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.function.Consumer;

public class WriteFile extends Writer {
    private final BufferedWriter writer;

    public WriteFile(final String fileName, List<RuleCounter> ruleCounter) {
        super(fileName, ruleCounter);
        try{
            writer = Files.newBufferedWriter(Paths.get(fileName));
        }catch (IOException e){
            log.error("Not init WriteFile ", e);
            throw new RuntimeException(e);
        }
    }

    @Override
    protected Consumer<RuleCounter> write() {
        return ruleCounter -> {
            try {
                writer.write(ruleCounter.toString());
                writer.write("\n");
            } catch (IOException e) {
                log.error("WriteFile not write {} {}", ruleCounter, e);
            }
        };
    }

    @Override
    public void close() throws Exception {
        writer.close();
    }
}
