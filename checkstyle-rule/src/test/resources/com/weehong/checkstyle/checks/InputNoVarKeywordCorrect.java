package com.weehong.checkstyle.checks;

import java.util.ArrayList;
import java.util.List;

public class InputNoVarKeywordCorrect {

    public void correctExplicitTypes() {
        String name = "John";
        int age = 25;
        List<String> items = new ArrayList<>();
        Double price = 99.99;

        for (int i = 0; i < 10; i++) {
            System.out.println(i);
        }

        for (String item : items) {
            System.out.println(item);
        }
    }

    public List<String> getList() {
        List<String> result = new ArrayList<>();
        result.add("test");
        return result;
    }
}
