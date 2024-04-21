package io.system.counter;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Nested;

import java.io.File;
import java.net.URL;

@Nested
public enum TestFilePath {
    RULE("data/ruleTest.txt"),
    DATA("data/dataTest.txt"),
    TEST_RULE_READER("data/reader/ruleReaderTest.txt"),
    TEST_DATA_READER("data/reader/dataReaderTest.txt")
    ;
    private final String fileName;

    TestFilePath(String fileName) {
        this.fileName = fileName;
    }

    public File loadFile(){
        final ClassLoader classLoader = getClass().getClassLoader();
        final URL url = classLoader.getResource(fileName);
        if(url != null) {
            return new File(url.getFile());
        }else Assertions.fail("Not find int resource " + fileName);
        return null;
    }
}
