package io.system.counter.reader.rule.type;

import io.system.counter.model.RuleCounter;
import io.system.counter.syntax.rule.SimpleSyntax;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.util.List;

import static io.system.counter.TestFilePath.TEST_RULE_READER;

@DisplayName("Читатель текстового файла (TXT) с правилами")
public class TextRuleReaderTest {

    private File testRuleFile;

    @BeforeEach
    public void setUp(){
        testRuleFile = TEST_RULE_READER.loadFile();
    }

    @Test
    @DisplayName("Загрузка и парсинг правил из файла")
    public void load_shouldLoadRuleReaderFile_thenCheckValidLoad(){
        final TextRuleReader testLoad = new TextRuleReader(testRuleFile.getAbsolutePath(), new SimpleSyntax());
        testLoad.load();
        final List<RuleCounter> ruleCounters = testLoad.getRules();
        Assertions.assertEquals(2, ruleCounters.size());
        final RuleCounter allAnimal = ruleCounters.get(0);
        Assertions.assertEquals("Все животные которые всеядные", allAnimal.getNameRule());
        Assertions.assertTrue(allAnimal.getPredicate().test("ЛЕГКОЕ,МАЛЕНЬКОЕ,ВСЕЯДНОЕ"));
        Assertions.assertFalse(allAnimal.getPredicate().test("ЛЕГКОЕ,МАЛЕНЬКОЕ,ПЛОТОЯДНОЕ"));

        final RuleCounter animalLightOrSmallAndOmnivorous = ruleCounters.get(1);
        Assertions.assertEquals("Животное легкое или оно маленьки и всеядное", animalLightOrSmallAndOmnivorous.getNameRule());
        Assertions.assertTrue(animalLightOrSmallAndOmnivorous.getPredicate().test("ЛЕГКОЕ,НЕВЫСОКОЕ,ТРАВОЯДНОЕ"));
        Assertions.assertTrue(animalLightOrSmallAndOmnivorous.getPredicate().test("СРЕДНЕЕ,МАЛЕНЬКОЕ,ВСЕЯДНОЕ"));
        Assertions.assertFalse(animalLightOrSmallAndOmnivorous.getPredicate().test("СРЕДНЕЕ,МАЛЕНЬКОЕ,ТРАВОЯДНОЕ"));
    }

    @Test
    @DisplayName("Добавления правила в список")
    public void action_shouldGetConsumerReadLine_thenCheckAddRule(){
        final TextRuleReader testAction = new TextRuleReader(testRuleFile.getAbsolutePath(), new SimpleSyntax());
        Assertions.assertEquals(0, testAction.getRules().size());
        testAction.action().accept("Все животные которые всеядные;ВСЕЯДНОЕ");
        Assertions.assertEquals(1, testAction.getRules().size());
    }
}
