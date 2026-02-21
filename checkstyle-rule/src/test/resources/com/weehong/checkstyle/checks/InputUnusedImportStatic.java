package com.weehong.checkstyle.checks;

import java.util.List;

import static java.util.Collections.emptyList;
import static java.util.Collections.emptyMap;
import static java.util.Collections.singletonList;

public class InputUnusedImportStatic {

    private List<String> items = singletonList("test");

    public void process() {
        var map = emptyMap();
    }

}
