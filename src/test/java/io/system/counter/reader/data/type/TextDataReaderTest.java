package io.system.counter.reader.data.type;

import io.system.counter.model.RuleCounter;
import io.system.counter.syntax.rule.SimpleSyntax;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

import static io.system.counter.TestFilePath.TEST_DATA_READER;

@DisplayName("Читатель текстового файла (TXT) с данными")
public class TextDataReaderTest {

    private enum RULE{
        SMALL(() -> new RuleCounter("МАЛЕНЬКОЕ", SimpleSyntax.FUNCTION_CHECK_WORD.apply("МАЛЕНЬКОЕ"))),
        SHORT_AND_CARNIVOROUS(() -> new RuleCounter("НЕВЫСОКОЕ И ПЛОТОЯДНОЕ", SimpleSyntax.FUNCTION_CHECK_WORD.apply("НЕВЫСОКОЕ")
                .and(SimpleSyntax.FUNCTION_CHECK_WORD.apply("ПЛОТОЯДНОЕ"))))
        ;
        private final Supplier<RuleCounter> ruleCounter;

        RULE(Supplier<RuleCounter> ruleCounter) {
            this.ruleCounter = ruleCounter;
        }
    }

    private File testDataFile;

    @BeforeEach
    public void setUp(){
        testDataFile = TEST_DATA_READER.loadFile();
    }

    private List<RuleCounter> initRules(){
        final List<RuleCounter> rules = new ArrayList<>();
        for(RULE rule : RULE.values()) {
            rules.add(rule.ruleCounter.get());
        }
        return rules;
    }

   @Test
    @DisplayName("Загрузка данных из файла и подсчёт совпадений по правилам")
    public void load_shouldLoadDataReaderFile_thenCheckValidLoad(){
        final TextDataReader textDataReader = new TextDataReader(testDataFile.getAbsolutePath(), initRules());
        textDataReader.load();
        final List<RuleCounter> ruleCounters = textDataReader.getRules();
        Assertions.assertEquals(2, ruleCounters.get(RULE.SMALL.ordinal()).getCount());
        Assertions.assertEquals(1, ruleCounters.get(RULE.SHORT_AND_CARNIVOROUS.ordinal()).getCount());
    }

    @Test
    @DisplayName("Увелечения счётчика через action при обнаружении совпадении с правилом")
    public void action_shouldGetConsumerReadLine_thenCheckAddRule(){
        final TextDataReader textDataReader = new TextDataReader(testDataFile.getAbsolutePath(), initRules());
        textDataReader.action().accept("ЛЕГКОЕ,МАЛЕНЬКОЕ,ВСЕЯДНОЕ");

        final List<RuleCounter> ruleCounters = textDataReader.getRules();

        Assertions.assertEquals(1, ruleCounters.get(RULE.SMALL.ordinal()).getCount());

        textDataReader.action().accept("ЛЕГКОЕ,НЕВЫСОКОЕ,ВСЕЯДНОЕ");

        Assertions.assertEquals(1, ruleCounters.get(RULE.SMALL.ordinal()).getCount());
        Assertions.assertEquals(0, ruleCounters.get(RULE.SHORT_AND_CARNIVOROUS.ordinal()).getCount());

        textDataReader.action().accept("ЛЕГКОЕ,НЕВЫСОКОЕ,ПЛОТОЯДНОЕ");

        Assertions.assertEquals(1, ruleCounters.get(RULE.SMALL.ordinal()).getCount());
        Assertions.assertEquals(1, ruleCounters.get(RULE.SHORT_AND_CARNIVOROUS.ordinal()).getCount());
    }
}
