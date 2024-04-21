package io.system.counter.manager;

import io.system.counter.model.RuleCounter;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.List;

import static io.system.counter.TestFilePath.DATA;
import static io.system.counter.TestFilePath.RULE;

@DisplayName("Взаимодействие файлов")
public class FileProcessorTest {
    private File testRuleFile;
    private File testDataFile;

    @BeforeEach
    public void setUp(){
        testRuleFile = RULE.loadFile();
        testDataFile = DATA.loadFile();
    }

    @Test
    @DisplayName("Подсчёт совпадений данных из файла согласно правилам из файла")
    public void checkRules_shouldGetListRules_thenCheckCount() throws FileNotFoundException {
        final List<RuleCounter> ruleCounters = FileProcessor.checkRules(testRuleFile.getAbsolutePath(), testDataFile.getAbsolutePath());
        Assertions.assertEquals(ruleCounters.get(0).getCount(), 3);
        Assertions.assertEquals(ruleCounters.get(1).getCount(), 1);
    }
}
