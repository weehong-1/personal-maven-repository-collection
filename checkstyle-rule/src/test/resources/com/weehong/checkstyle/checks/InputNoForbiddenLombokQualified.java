package com.weehong.checkstyle.checks;

@lombok.Data
public class InputNoForbiddenLombokQualified {

    private String field1;

    @lombok.Builder
    public static class NestedClass {
        private String field2;
    }

    @lombok.Getter
    @lombok.Setter
    private String field3;
}
