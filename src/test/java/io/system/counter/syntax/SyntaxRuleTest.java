package io.system.counter.syntax;

import io.system.counter.exception.SyntaxRuleException;
import io.system.counter.model.RuleCounter;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvFileSource;
import io.system.counter.util.BuilderRule;

import java.util.Optional;
import java.util.function.Predicate;

@Nested
@DisplayName("Логика синтаксиса")
public abstract class SyntaxRuleTest {
    private static final String FILE_NAME_RESOURCES = "/syntaxRule.csv";
    private static final char DELIMITER = '|';
    private static final int NUM_LINES_TO_SKIP = 1;

    private final BuilderRule ruleBuilder = new BuilderRule();

    private SyntaxRule syntaxRule;

    @BeforeEach
    public void setUp(){
        syntaxRule = getSyntaxRule();
    }

    protected abstract SyntaxRule getSyntaxRule();

    @ParameterizedTest
    @DisplayName("Проверка правил и текста из файла")
    @CsvFileSource(resources = FILE_NAME_RESOURCES, delimiter = DELIMITER, numLinesToSkip = NUM_LINES_TO_SKIP)
    public void get_parameterFromFile(final String ruleText,
                                      final String text,
                                      final String success){
        final Predicate<String> rule = parseRule(ruleText);
        final boolean isSuccess = Boolean.parseBoolean(success);
        if(isSuccess) {
            Assertions.assertTrue(rule.test(text));
        }else {
            Assertions.assertFalse(rule.test(text));
        }
    }

    private Predicate<String> parseRule(final String ruleText){
        try {
            final Optional<RuleCounter> rule = syntaxRule.get(ruleText);
            Assertions.assertTrue(rule.isPresent());
            return rule.get().getPredicate();
        }catch (SyntaxRuleException e){
            Assertions.fail(e);
        }
        return (s) -> true;
    }

    @Nested
    @DisplayName("Стандартные правила")
    class TestValidateDefault {
        @Test
        @DisplayName("ВСЕЯДНОЕ")
        public void get_shouldParseRuleWithOneWord_thenApplyRuleToText(){
            final String testRule = "ВСЕЯДНОЕ";

            final String isWord = "ЛЕГКОЕ,МАЛЕНЬКОЕ,ВСЕЯДНОЕ";
            final String noWord = "ЛЕГКОЕ,МАЛЕНЬКОЕ,ПЛОТОЯДНОЕ";
            final Predicate<String> rule = parseRule(testRule);
            Assertions.assertTrue(rule.test(isWord));
            Assertions.assertFalse(rule.test(noWord));
        }

        @Test
        @DisplayName("ВСЕЯДНОЕ ИЛИ МАЛЕНЬКОЕ")
        public void get_shouldParseRuleWithOperation_OR_thenApplyRuleToText(){
            final String testRule = ruleBuilder.word("ВСЕЯДНОЕ")
                    .operation(Operation.OR)
                    .word("МАЛЕНЬКОЕ")
                    .build();

            final String isWordIsWord = "ЛЕГКОЕ,МАЛЕНЬКОЕ,ВСЕЯДНОЕ";
            final String noWordIsWord = "ЛЕГКОЕ,МАЛЕНЬКОЕ,ПЛОТОЯДНОЕ";
            final String isWordNoWord = "ЛЕГКОЕ,НЕВЫСОКОЕ,ВСЕЯДНОЕ";

            final String noWordNoWord = "ЛЕГКОЕ,НЕВЫСОКОЕ,ПЛОТОЯДНОЕ";

            final Predicate<String> rule = parseRule(testRule);

            Assertions.assertTrue(rule.test(isWordIsWord));
            Assertions.assertTrue(rule.test(noWordIsWord));
            Assertions.assertTrue(rule.test(isWordNoWord));

            Assertions.assertFalse(rule.test(noWordNoWord));
        }

        @Test
        @DisplayName("ВСЕЯДНОЕ И МАЛЕНЬКОЕ")
        public void get_shouldParseRuleWithOperation_AND_thenApplyRuleToText(){
            final String testRule = ruleBuilder.word("ВСЕЯДНОЕ")
                    .operation(Operation.AND)
                    .word("МАЛЕНЬКОЕ")
                    .build();

            final String isWordIsWord = "ЛЕГКОЕ,МАЛЕНЬКОЕ,ВСЕЯДНОЕ";
            final String noWordIsWord = "ЛЕГКОЕ,НЕВЫСОКОЕ,ВСЕЯДНОЕ";
            final String noWordNoWord = "ЛЕГКОЕ,НЕВЫСОКОЕ,ПЛОТОЯДНОЕ";

            final Predicate<String> rule = parseRule(testRule);

            Assertions.assertTrue(rule.test(isWordIsWord));

            Assertions.assertFalse(rule.test(noWordIsWord));
            Assertions.assertFalse(rule.test(noWordNoWord));
        }

        @Test
        @DisplayName("НЕ ВСЕЯДНОЕ")
        public void get_shouldParseRuleWithOperation_NOT_thenApplyRuleToText(){
            final String testRule = ruleBuilder
                    .operation(Operation.NOT)
                    .word("ВСЕЯДНОЕ")
                    .build();

            final String isWord = "ЛЕГКОЕ,МАЛЕНЬКОЕ,ВСЕЯДНОЕ";

            final String noWord = "ЛЕГКОЕ,МАЛЕНЬКОЕ,ПЛОТОЯДНОЕ";

            final Predicate<String> rule = parseRule(testRule);

            Assertions.assertFalse(rule.test(isWord));

            Assertions.assertTrue(rule.test(noWord));
        }

        @Test
        @DisplayName("НЕ ВСЕЯДНОЕ ИЛИ МАЛЕНЬКОЕ")
        public void get_shouldParseRuleWithOperation_NOT_OR_thenApplyRuleToText(){
            final String testRule = ruleBuilder
                    .operation(Operation.NOT)
                    .word("ВСЕЯДНОЕ")
                    .operation(Operation.OR)
                    .word("МАЛЕНЬКОЕ")
                    .build();


            final String isWordNoWord = "ЛЕГКОЕ,НЕВЫСОКОЕ,ВСЕЯДНОЕ";

            final String isWordIsWord = "ЛЕГКОЕ,МАЛЕНЬКОЕ,ВСЕЯДНОЕ";
            final String noWordNoWord = "ЛЕГКОЕ,НЕВЫСОКОЕ,ПЛОТОЯДНОЕ";

            final Predicate<String> rule = parseRule(testRule);

            Assertions.assertFalse(rule.test(isWordNoWord));

            Assertions.assertTrue(rule.test(isWordIsWord));
            Assertions.assertTrue(rule.test(noWordNoWord));
        }

        @Test
        @DisplayName("НЕ ВСЕЯДНОЕ И МАЛЕНЬКОЕ")
        public void get_shouldParseRuleWithOperation_NOT_AND_thenApplyRuleToText(){
            final String testRule = ruleBuilder
                    .operation(Operation.NOT)
                    .word("ВСЕЯДНОЕ")
                    .operation(Operation.AND)
                    .word("МАЛЕНЬКОЕ")
                    .build();

            final String isWordNoWord = "ЛЕГКОЕ,НЕВЫСОКОЕ,ВСЕЯДНОЕ";
            final String isWordIsWord = "ЛЕГКОЕ,МАЛЕНЬКОЕ,ВСЕЯДНОЕ";
            final String noWordNoWord = "ЛЕГКОЕ,НЕВЫСОКОЕ,ПЛОТОЯДНОЕ";
            final String noWordIsWord = "ЛЕГКОЕ,МАЛЕНЬКОЕ,ПЛОТОЯДНОЕ";

            final Predicate<String> rule = parseRule(testRule);

            Assertions.assertFalse(rule.test(isWordNoWord));
            Assertions.assertFalse(rule.test(isWordIsWord));
            Assertions.assertFalse(rule.test(noWordNoWord));

            Assertions.assertTrue(rule.test(noWordIsWord));
        }

        @Test
        @DisplayName("НЕ ВСЕЯДНОЕ ИЛИ НЕ МАЛЕНЬКОЕ")
        public void get_shouldParseRuleWithOperation_NOT_OR_NOT_thenApplyRuleToText(){
            final String testRule = ruleBuilder
                    .operation(Operation.NOT)
                    .word("ВСЕЯДНОЕ")
                    .operation(Operation.OR)
                    .operation(Operation.NOT)
                    .word("МАЛЕНЬКОЕ")
                    .build();


            final String isWordIsWord = "ЛЕГКОЕ,МАЛЕНЬКОЕ,ВСЕЯДНОЕ";

            final String isWordNoWord = "ЛЕГКОЕ,НЕВЫСОКОЕ,ВСЕЯДНОЕ";
            final String noWordIsWord = "ЛЕГКОЕ,МАЛЕНЬКОЕ,ПЛОТОЯДНОЕ";
            final String noWordNoWord = "ЛЕГКОЕ,НЕВЫСОКОЕ,ПЛОТОЯДНОЕ";

            final Predicate<String> rule = parseRule(testRule);

            Assertions.assertFalse(rule.test(isWordIsWord));

            Assertions.assertTrue(rule.test(isWordNoWord));
            Assertions.assertTrue(rule.test(noWordIsWord));
            Assertions.assertTrue(rule.test(noWordNoWord));
        }

        @Test
        @DisplayName("НЕ ВСЕЯДНОЕ И НЕ МАЛЕНЬКОЕ")
        public void get_shouldParseRuleWithOperation_NOT_AND_NOT_thenApplyRuleToText(){
            final String testRule = ruleBuilder
                    .operation(Operation.NOT)
                    .word("ВСЕЯДНОЕ")
                    .operation(Operation.AND)
                    .operation(Operation.NOT)
                    .word("МАЛЕНЬКОЕ")
                    .build();

            final String isWordNoWord = "ЛЕГКОЕ,НЕВЫСОКОЕ,ВСЕЯДНОЕ";
            final String isWordIsWord = "ЛЕГКОЕ,МАЛЕНЬКОЕ,ВСЕЯДНОЕ";
            final String noWordIsWord = "ЛЕГКОЕ,МАЛЕНЬКОЕ,ПЛОТОЯДНОЕ";

            final String noWordNoWord = "ЛЕГКОЕ,НЕВЫСОКОЕ,ПЛОТОЯДНОЕ";

            final Predicate<String> rule = parseRule(testRule);

            Assertions.assertFalse(rule.test(isWordNoWord));
            Assertions.assertFalse(rule.test(isWordIsWord));
            Assertions.assertFalse(rule.test(noWordIsWord));

            Assertions.assertTrue(rule.test(noWordNoWord));
        }

        @Test
        @DisplayName("ЛЕГКОЕ ИЛИ МАЛЕНЬКОЕ И ВСЕЯДНОЕ")
        public void get_shouldParseRuleWithOperation_OR_AND_thenApplyRuleToText(){
            final String testRule = ruleBuilder
                    .word("ЛЕГКОЕ")
                    .operation(Operation.OR)
                    .word("МАЛЕНЬКОЕ")
                    .operation(Operation.AND)
                    .word("ВСЕЯДНОЕ")
                    .build();

            final String isWordNoWordNoWord = "ЛЕГКОЕ,НЕВЫСОКОЕ,ПЛОТОЯДНОЕ";
            final String isWordIsWordNoWord = "ЛЕГКОЕ,МАЛЕНЬКОЕ,ПЛОТОЯДНОЕ";
            final String noWordNoWordIsWord = "СРЕДНЕЕ,НЕВЫСОКОЕ,ПЛОТОЯДНОЕ";

            final String isWordNoWordIsWord = "ЛЕГКОЕ,НЕВЫСОКОЕ,ВСЕЯДНОЕ";
            final String noWordIsWordIsWord = "СРЕДНЕЕ,МАЛЕНЬКОЕ,ВСЕЯДНОЕ";

            final String isWordIsWordIsWord = "ЛЕГКОЕ,МАЛЕНЬКОЕ,ВСЕЯДНОЕ";

            final Predicate<String> rule = parseRule(testRule);

            Assertions.assertFalse(rule.test(isWordNoWordNoWord));
            Assertions.assertFalse(rule.test(isWordIsWordNoWord));
            Assertions.assertFalse(rule.test(noWordNoWordIsWord));

            Assertions.assertTrue(rule.test(isWordNoWordIsWord));
            Assertions.assertTrue(rule.test(noWordIsWordIsWord));
            Assertions.assertTrue(rule.test(isWordIsWordIsWord));
        }

        @Test
        @DisplayName("ЛЕГКОЕ ИЛИ (МАЛЕНЬКОЕ И ВСЕЯДНОЕ)")
        public void get_shouldParseRuleWithOperation_OR_BRACKET_AND_thenApplyRuleToText(){
            final String testRule = ruleBuilder
                    .word("ЛЕГКОЕ")
                    .operation(Operation.OR)
                    .operation(Operation.OPEN_BRACKET)
                    .word("МАЛЕНЬКОЕ")
                    .operation(Operation.AND)
                    .word("ВСЕЯДНОЕ")
                    .operation(Operation.CLOSED_BRACKET)
                    .build();

            final String noWordNoWordIsWord = "СРЕДНЕЕ,НЕВЫСОКОЕ,ПЛОТОЯДНОЕ";

            final String isWordNoWordIsWord = "ЛЕГКОЕ,НЕВЫСОКОЕ,ВСЕЯДНОЕ";
            final String isWordNoWordNoWord = "ЛЕГКОЕ,НЕВЫСОКОЕ,ПЛОТОЯДНОЕ";
            final String isWordIsWordNoWord = "ЛЕГКОЕ,МАЛЕНЬКОЕ,ПЛОТОЯДНОЕ";
            final String noWordIsWordIsWord = "СРЕДНЕЕ,МАЛЕНЬКОЕ,ВСЕЯДНОЕ";
            final String isWordIsWordIsWord = "ЛЕГКОЕ,МАЛЕНЬКОЕ,ВСЕЯДНОЕ";

            final Predicate<String> rule = parseRule(testRule);

            Assertions.assertFalse(rule.test(noWordNoWordIsWord));

            Assertions.assertTrue(rule.test(isWordNoWordIsWord));
            Assertions.assertTrue(rule.test(isWordNoWordNoWord));
            Assertions.assertTrue(rule.test(isWordIsWordNoWord));
            Assertions.assertTrue(rule.test(noWordIsWordIsWord));
            Assertions.assertTrue(rule.test(isWordIsWordIsWord));
        }

        @Test
        @DisplayName("ЛЕГКОЕ И (МАЛЕНЬКОЕ ИЛИ ВСЕЯДНОЕ)")
        public void get_shouldParseRuleWithOperation_AND_BRACKET_OR_thenApplyRuleToText(){
            final String testRule = ruleBuilder
                    .word("ЛЕГКОЕ")
                    .operation(Operation.AND)
                    .operation(Operation.OPEN_BRACKET)
                    .word("МАЛЕНЬКОЕ")
                    .operation(Operation.OR)
                    .word("ВСЕЯДНОЕ")
                    .operation(Operation.CLOSED_BRACKET)
                    .build();

            final String noWordNoWordIsWord = "СРЕДНЕЕ,НЕВЫСОКОЕ,ВСЕЯДНОЕ";
            final String isWordNoWordNoWord = "ЛЕГКОЕ,НЕВЫСОКОЕ,ПЛОТОЯДНОЕ";
            final String noWordIsWordIsWord = "СРЕДНЕЕ,МАЛЕНЬКОЕ,ВСЕЯДНОЕ";

            final String isWordNoWordIsWord = "ЛЕГКОЕ,НЕВЫСОКОЕ,ВСЕЯДНОЕ";
            final String isWordIsWordNoWord = "ЛЕГКОЕ,МАЛЕНЬКОЕ,ПЛОТОЯДНОЕ";
            final String isWordIsWordIsWord = "ЛЕГКОЕ,МАЛЕНЬКОЕ,ВСЕЯДНОЕ";

            final Predicate<String> rule = parseRule(testRule);

            Assertions.assertFalse(rule.test(noWordNoWordIsWord));
            Assertions.assertFalse(rule.test(isWordNoWordNoWord));
            Assertions.assertFalse(rule.test(noWordIsWordIsWord));

            Assertions.assertTrue(rule.test(isWordNoWordIsWord));
            Assertions.assertTrue(rule.test(isWordIsWordNoWord));
            Assertions.assertTrue(rule.test(isWordIsWordIsWord));
        }

        @Test
        @DisplayName("ЛЕГКОЕ И НЕ (МАЛЕНЬКОЕ ИЛИ ВСЕЯДНОЕ)")
        public void get_shouldParseRuleWithOperation_AND_NOT_BRACKET_OR_thenApplyRuleToText(){
            final String testRule = ruleBuilder
                    .word("ЛЕГКОЕ")
                    .operation(Operation.AND)
                    .operation(Operation.NOT)
                    .operation(Operation.OPEN_BRACKET)
                    .word("МАЛЕНЬКОЕ")
                    .operation(Operation.OR)
                    .word("ВСЕЯДНОЕ")
                    .operation(Operation.CLOSED_BRACKET)
                    .build();
            final String noWordNoWordIsWord = "СРЕДНЕЕ,НЕВЫСОКОЕ,ПЛОТОЯДНОЕ";
            final String noWordIsWordIsWord = "СРЕДНЕЕ,МАЛЕНЬКОЕ,ВСЕЯДНОЕ";
            final String isWordNoWordIsWord = "ЛЕГКОЕ,НЕВЫСОКОЕ,ВСЕЯДНОЕ";
            final String isWordIsWordNoWord = "ЛЕГКОЕ,МАЛЕНЬКОЕ,ПЛОТОЯДНОЕ";
            final String isWordIsWordIsWord = "ЛЕГКОЕ,МАЛЕНЬКОЕ,ВСЕЯДНОЕ";

            final String isWordNoWordNoWord = "ЛЕГКОЕ,НЕВЫСОКОЕ,ПЛОТОЯДНОЕ";

            final Predicate<String> rule = parseRule(testRule);

            Assertions.assertFalse(rule.test(noWordNoWordIsWord));
            Assertions.assertFalse(rule.test(noWordIsWordIsWord));
            Assertions.assertFalse(rule.test(isWordNoWordIsWord));
            Assertions.assertFalse(rule.test(isWordIsWordNoWord));
            Assertions.assertFalse(rule.test(isWordIsWordIsWord));

            Assertions.assertTrue(rule.test(isWordNoWordNoWord));
        }

        @Test
        @DisplayName("(ЛЕГКОЕ ИЛИ ТЯЖЕЛОЕ) И (НЕВЫСОКОЕ ИЛИ ВЫСОКОЕ)")
        public void get_shouldParseRuleWithOperation_BRACKET_OR_AND_BRACKET_OR_thenApplyRuleToText(){
            final String testRule = ruleBuilder
                    .operation(Operation.OPEN_BRACKET)
                    .word("ЛЕГКОЕ")
                    .operation(Operation.OR)
                    .word("ТЯЖЕЛОЕ")
                    .operation(Operation.CLOSED_BRACKET)
                    .operation(Operation.AND)
                    .operation(Operation.OPEN_BRACKET)
                    .word("НЕВЫСОКОЕ")
                    .operation(Operation.OR)
                    .word("ВЫСОКОЕ")
                    .operation(Operation.CLOSED_BRACKET)
                    .build();

            final String noWordsIsWords = "СРЕДНЕЕ,НЕВЫСОКОЕ,ПЛОТОЯДНОЕ";
            final String noWordsNoWords = "СРЕДНЕЕ,МАЛЕНЬКОЕ,ВСЕЯДНОЕ";
            final String isWordsNoWords = "ЛЕГКОЕ,МАЛЕНЬКОЕ,ВСЕЯДНОЕ";

            final String isWordsIsWords = "ТЯЖЕЛОЕ,ВЫСОКОЕ,ВСЕЯДНОЕ";

            final Predicate<String> rule = parseRule(testRule);

            Assertions.assertFalse(rule.test(noWordsIsWords));
            Assertions.assertFalse(rule.test(noWordsNoWords));
            Assertions.assertFalse(rule.test(isWordsNoWords));

            Assertions.assertTrue(rule.test(isWordsIsWords));
        }

        @Test
        @DisplayName("(ЛЕГКОЕ ИЛИ ТЯЖЕЛОЕ) И (НЕВЫСОКОЕ ИЛИ (ВЫСОКОЕ И ВСЕЯДНОЕ))")
        public void get_shouldParseRuleWithOperation_BRACKET_OR_AND_BRACKET_OR_BRACKET_AND_thenApplyRuleToText(){
            final String testRule = ruleBuilder
                    .operation(Operation.OPEN_BRACKET)
                    .word("ЛЕГКОЕ")
                    .operation(Operation.OR)
                    .word("ТЯЖЕЛОЕ")
                    .operation(Operation.CLOSED_BRACKET)
                    .operation(Operation.AND)
                    .operation(Operation.OPEN_BRACKET)
                    .word("НЕВЫСОКОЕ")
                    .operation(Operation.OR)
                    .operation(Operation.OPEN_BRACKET)
                    .word("ВЫСОКОЕ")
                    .operation(Operation.AND)
                    .word("ВСЕЯДНОЕ")
                    .operation(Operation.CLOSED_BRACKET)
                    .operation(Operation.CLOSED_BRACKET)
                    .build();

            final String isWordIsWordNoWord = "ЛЕГКОЕ,ВЫСОКОЕ,ПЛОТОЯДНОЕ";
            final String noWordIsLeftWordNoWord = "СРЕДНЕЕ,НЕВЫСОКОЕ,ПЛОТОЯДНОЕ";

            final String isWordIsLeftWordNoWord = "ЛЕГКОЕ,НЕВЫСОКОЕ,ПЛОТОЯДНОЕ";
            final String isWordIsWordIsWord = "ЛЕГКОЕ,ВЫСОКОЕ,ВСЕЯДНОЕ";
            final String isRightWordIsWordIsWord = "ТЯЖЕЛОЕ,ВЫСОКОЕ,ВСЕЯДНОЕ";

            final Predicate<String> rule = parseRule(testRule);

            Assertions.assertFalse(rule.test(isWordIsWordNoWord));
            Assertions.assertFalse(rule.test(noWordIsLeftWordNoWord));

            Assertions.assertTrue(rule.test(isWordIsLeftWordNoWord));
            Assertions.assertTrue(rule.test(isWordIsWordIsWord));
            Assertions.assertTrue(rule.test(isRightWordIsWordIsWord));

        }

        @Test
        @DisplayName("НЕ (ЛЕГКОЕ ИЛИ НЕ (ВСЕЯДНОЕ И НЕВЫСОКОЕ))") // НЕ ЛЕГКОЕ И НЕВЫСОКОЕ И ВСЕЯДНОЕ
        public void get_shouldParseRuleWithOperation_NOT_BRACKET_OR_NOT_BRACKET_AND_thenApplyRuleToText(){
            final String testRule = ruleBuilder
                    .operation(Operation.NOT)
                    .operation(Operation.OPEN_BRACKET)
                    .word("ЛЕГКОЕ")
                    .operation(Operation.OR)
                    .operation(Operation.NOT)
                    .operation(Operation.OPEN_BRACKET)
                    .word("ВСЕЯДНОЕ")
                    .operation(Operation.AND)
                    .word("НЕВЫСОКОЕ")
                    .operation(Operation.CLOSED_BRACKET)
                    .operation(Operation.CLOSED_BRACKET)
                    .build();

            final String isWordIsWordIsWord = "ТЯЖЕЛОЕ,НЕВЫСОКОЕ,ВСЕЯДНОЕ";

            final String noWordIsWordIsWord = "ЛЕГКОЕ,НЕВЫСОКОЕ,ВСЕЯДНОЕ";
            final String noWordNoWordIsWord = "ЛЕГКОЕ,ВЫСОКОЕ,ВСЕЯДНОЕ";
            final String noWordIsWordNoWord = "ЛЕГКОЕ,НЕВЫСОКОЕ,ПЛОТОЯДНОЕ";

            final Predicate<String> rule = parseRule(testRule);
            Assertions.assertTrue(rule.test(isWordIsWordIsWord));

            Assertions.assertFalse(rule.test(noWordIsWordIsWord));
            Assertions.assertFalse(rule.test(noWordNoWordIsWord));
            Assertions.assertFalse(rule.test(noWordIsWordNoWord));
        }
    }

    @Nested
    @DisplayName("Ошибки в правилах")
    class TestInvalid{
        @Test
        @DisplayName("Пустое правило")
        public void get_shouldParseInvalidEmptyRule_thenThrowEmptyOptional(){
            final String testRule = "";
            try {
                final Optional<RuleCounter> rule = syntaxRule.get(testRule);
                Assertions.assertFalse(rule.isPresent());
            }catch (SyntaxRuleException e){
                Assertions.fail(e);
            }
        }


        @Test
        @DisplayName("Пропущена открытая или закрытая скобка")
        public void get_shouldParseInvalidRuleWithOutOpenAndClosedBracket(){
            final String notOpenBracket = ruleBuilder.word("ЛЕГКОЕ")
                    .operation(Operation.AND)
                    .operation(Operation.OPEN_BRACKET)
                    .word("НЕВЫСОКОЕ").operation(Operation.OR).word("ВСЕЯДНОЕ").build();

            Assertions.assertThrows(SyntaxRuleException.class, () -> syntaxRule.get(notOpenBracket));

            final String notClosedBracket = ruleBuilder.word("ЛЕГКОЕ")
                    .operation(Operation.AND)
                    .word("НЕВЫСОКОЕ").operation(Operation.OR).word("ВСЕЯДНОЕ")
                    .operation(Operation.CLOSED_BRACKET).build();

            Assertions.assertThrows(SyntaxRuleException.class, () -> syntaxRule.get(notClosedBracket));
        }

        @Test
        @DisplayName("Нет логического оператора перед скобкой и оператором НЕ")
        public void get_shouldParseInvalidRuleNotLogicOperation(){
            final String notLogicBeforeOpenBracket = ruleBuilder.word("ЛЕГКОЕ")
                    .operation(Operation.SPLIT)
                    .operation(Operation.OPEN_BRACKET)
                    .word("НЕВЫСОКОЕ").operation(Operation.OR).word("ВСЕЯДНОЕ")
                    .operation(Operation.CLOSED_BRACKET).build();

            Assertions.assertThrows(SyntaxRuleException.class, () -> syntaxRule.get(notLogicBeforeOpenBracket));

            final String notLogicBeforeNot = ruleBuilder.word("ЛЕГКОЕ")
                    .operation(Operation.NOT)
                    .operation(Operation.OPEN_BRACKET)
                    .word("НЕВЫСОКОЕ").operation(Operation.OR).word("ВСЕЯДНОЕ")
                    .operation(Operation.CLOSED_BRACKET).build();


            Assertions.assertThrows(SyntaxRuleException.class, () -> syntaxRule.get(notLogicBeforeNot));
        }

        @Test
        @DisplayName("Нет слова после и перед логическим оператором")
        public void get_shouldParseInvalidRuleNotWordAfterOperation(){
            final String notWordAfterLogicOR = ruleBuilder.word("ЛЕГКОЕ")
                    .operation(Operation.OR).build();
            Assertions.assertThrows(SyntaxRuleException.class, () -> syntaxRule.get(notWordAfterLogicOR));

            final String notWordBeforeLogicOR = ruleBuilder.
                    operation(Operation.OR).word("ЛЕГКОЕ").build();

            Assertions.assertThrows(SyntaxRuleException.class, () -> syntaxRule.get(notWordBeforeLogicOR));
        }

    }

}
