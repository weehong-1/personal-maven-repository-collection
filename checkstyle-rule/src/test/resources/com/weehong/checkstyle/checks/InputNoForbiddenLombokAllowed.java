package com.weehong.checkstyle.checks;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class InputNoForbiddenLombokAllowed {

    private String name;

    @Getter
    private int age;

    @Setter
    private String address;

    @Getter
    @Setter
    private double salary;
}
