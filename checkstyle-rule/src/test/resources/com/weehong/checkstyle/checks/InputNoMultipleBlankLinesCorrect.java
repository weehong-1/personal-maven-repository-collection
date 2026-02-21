package com.weehong.checkstyle.checks;

public class InputNoMultipleBlankLinesCorrect {

    private String field1;

    private String field2;

    public void method1() {
        int x = 1;

        int y = 2;

        System.out.println(x + y);
    }

    public void method2() {
        String msg = "test";
    }
}
