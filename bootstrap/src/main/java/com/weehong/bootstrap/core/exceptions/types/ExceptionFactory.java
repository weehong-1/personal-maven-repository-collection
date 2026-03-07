package com.weehong.bootstrap.core.exceptions.types;

@FunctionalInterface
public interface ExceptionFactory {

    RuntimeException create(String message, Throwable cause);

}
