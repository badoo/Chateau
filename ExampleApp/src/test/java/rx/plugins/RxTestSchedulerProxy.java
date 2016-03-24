package rx.plugins;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.badoo.unittest.rx.SchedulerFactory;

import rx.Scheduler;
import rx.android.plugins.RxAndroidPlugins;
import rx.android.plugins.RxAndroidSchedulersHook;
import rx.schedulers.Schedulers;

/**
 * Class to change the schedulers used by rxJava during testing.
 */
public class RxTestSchedulerProxy {

    private static final RxTestSchedulerProxy INSTANCE = new RxTestSchedulerProxy();

    public static RxTestSchedulerProxy getInstance() {
        return INSTANCE;
    }

    @Nullable
    private SchedulerFactory mFactory;

    private RxTestSchedulerProxy() {
        RxJavaPlugins.getInstance().registerSchedulersHook(new RxJavaSchedulersHook() {
            @Override
            public Scheduler getIOScheduler() {
                if (mFactory == null) {
                    return Schedulers.immediate();
                }
                return mFactory.createIOScheduler();
            }

            @Override
            public Scheduler getComputationScheduler() {
                if (mFactory == null) {
                    return Schedulers.immediate();
                }
                return mFactory.createComputationScheduler();
            }

            @Override
            public Scheduler getNewThreadScheduler() {
                if (mFactory == null) {
                    return Schedulers.immediate();
                }
                return mFactory.createNewThreadScheduler();
            }
        });

        RxAndroidPlugins.getInstance().registerSchedulersHook(new RxAndroidSchedulersHook() {

            @Override
            public Scheduler getMainThreadScheduler() {
                if (mFactory == null) {
                    return Schedulers.immediate();
                }
                return mFactory.createMainThreadScheduler();
            }
        });
    }

    public void setSchedulerFactory(@NonNull SchedulerFactory factory) {
        mFactory = factory;
    }

    public void reset() {
        mFactory = null;
    }
}
