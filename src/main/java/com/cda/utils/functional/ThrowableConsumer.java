package com.cda.utils.functional;

@FunctionalInterface
public interface ThrowableConsumer<T> {
    void run(T parameter) throws Exception;

}
