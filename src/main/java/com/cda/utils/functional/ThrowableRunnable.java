package com.cda.utils.functional;

@FunctionalInterface
public interface ThrowableRunnable {
    void run() throws Exception;

}
