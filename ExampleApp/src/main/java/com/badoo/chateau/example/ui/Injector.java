package com.badoo.chateau.example.ui;

import android.support.annotation.NonNull;

import java.util.HashMap;
import java.util.Map;

public class Injector {

    private static Map<Class<?>, Configuration<?>> sConfigurator = new HashMap<>();

    public static <T> void register(@NonNull Class<T> cls, @NonNull Configuration<T> config) {
        sConfigurator.put(cls, config);
    }

    public static <T> void inject(T target) {
        //noinspection unchecked
        Configuration<T> config = (Configuration<T>) sConfigurator.get(target.getClass());
        if (config == null) {
            throw new RuntimeException("No configuration specified for " + target.getClass().getSimpleName());
        }
        config.inject(target);
    }

    public interface Configuration<T> {

        void inject(T target);
    }
}
