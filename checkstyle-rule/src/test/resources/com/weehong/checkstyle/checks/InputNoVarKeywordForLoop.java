package com.weehong.checkstyle.checks;

import java.util.List;

public class InputNoVarKeywordForLoop {

    public void processItems(List<String> items) {
        for (var item : items) {
            System.out.println(item);
        }
    }
}
