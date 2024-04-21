package io.system.counter.reader.data;

import io.system.counter.model.RuleCounter;
import io.system.counter.reader.Reader;
import io.system.counter.reader.TypeFile;
import java.util.List;

public abstract class DataReader<T> extends Reader<T> {

    protected DataReader(final String fileName,
                         final List<RuleCounter> ruleCounters,
                         final TypeFile<T> typeFile) {
        super(fileName, typeFile, ruleCounters);
    }

}
