package com.weehong.checkstyle.checks;

import java.util.List;

@SuppressWarnings("unchecked")
public class InputNoSuppressWarningsViolation {

    @SuppressWarnings("unused")
    private String unused;

    @SuppressWarnings({"rawtypes", "unchecked"})
    public List getList() {
        return null;
    }
}
