package io.system.counter.exception;

public class SyntaxRuleException extends Exception{

    public SyntaxRuleException(String text) {
        super(text);
    }

    public SyntaxRuleException(String text, Throwable throwable) {
        super(text, throwable);
    }

    public static class ValidBracket extends SyntaxRuleException{
        public ValidBracket(String text) {
            super(text);
        }
    }

    public static class ValidSyntax extends SyntaxRuleException{
        public ValidSyntax(String text){
            super(text);
        }
    }
}
