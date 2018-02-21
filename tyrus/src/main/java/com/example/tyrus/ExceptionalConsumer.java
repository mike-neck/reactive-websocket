package com.example.tyrus;

import java.util.function.Consumer;

interface ExceptionalConsumer<T> {

    static <T> Consumer<T> consumer(final ExceptionalConsumer<? super T> consumer) {
        return t -> {
            try {
                consumer.accept(t);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        };
    }

    void accept(final T object) throws Exception;
}
