package com.cda.utils.functional;

@FunctionalInterface
public interface ThrowableSupplier<T> {
    public T get() throws Exception;
}
