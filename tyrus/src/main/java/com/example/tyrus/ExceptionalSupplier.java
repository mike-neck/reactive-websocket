package com.example.tyrus;

import java.util.function.Supplier;

interface ExceptionalSupplier<T> {

    static  <T> Supplier<T> supplier(final ExceptionalSupplier<? extends T> supplier) {
        return () -> {
            try {
                return supplier.get();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        };
    }

    T get() throws Exception;
}
