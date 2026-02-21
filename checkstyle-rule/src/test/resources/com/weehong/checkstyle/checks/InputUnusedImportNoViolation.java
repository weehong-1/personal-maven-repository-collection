package com.weehong.checkstyle.checks;

import java.util.ArrayList;
import java.util.List;

public class InputUnusedImportNoViolation {

    private List<String> items = new ArrayList<>();

    public void addItem(String item) {
        items.add(item);
    }

}
