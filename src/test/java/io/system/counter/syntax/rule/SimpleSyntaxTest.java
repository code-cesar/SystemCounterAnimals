package io.system.counter.syntax.rule;
import io.system.counter.syntax.SyntaxRule;
import io.system.counter.syntax.SyntaxRuleTest;
import org.junit.jupiter.api.DisplayName;

@DisplayName("Простой синтаксис")
public class SimpleSyntaxTest extends SyntaxRuleTest {
    @Override
    protected SyntaxRule getSyntaxRule() {
        return new SimpleSyntax();
    }
}
