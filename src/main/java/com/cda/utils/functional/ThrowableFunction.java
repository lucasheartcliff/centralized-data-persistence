package com.cda.utils.functional;

@FunctionalInterface
public interface ThrowableFunction<T, R> {
    R apply(T parameter) throws Exception;
}
