package com.weehong.checkstyle.checks;

import lombok.Getter;
import lombok.Setter;
import lombok.Builder;

@Builder
@Getter
@Setter
public class InputNoForbiddenLombokMixed {

    private String name;
    private int age;
}
