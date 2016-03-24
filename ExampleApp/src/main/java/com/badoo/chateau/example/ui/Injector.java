package com.badoo.chateau.example.ui;

import android.support.annotation.NonNull;

import com.badoo.barf.mvp.Presenter;
import com.badoo.barf.mvp.View;

import java.util.HashMap;
import java.util.Map;

public class Injector {

    private static final String TAG = Injector.class.getSimpleName();

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

        <P extends Presenter<V, F>, V extends View<P>, F extends Presenter.FlowListener> void bind(V view, P presenter, F flowListener);
    }

    public abstract static class BaseConfiguration<T> implements Configuration<T> {

        @Override
        public <P extends Presenter<V, F>, V extends View<P>, F extends Presenter.FlowListener> void bind(V view, P presenter, F flowListener) {
            presenter.attachView(view);
            presenter.attachFlowListener(flowListener);
            view.attachPresenter(presenter);
        }
    }
}
