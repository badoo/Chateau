package com.badoo.chateau.example.data.util;

import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.VisibleForTesting;
import android.util.Log;

import com.parse.ParseCloud;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.util.List;
import java.util.Map;

import rx.Observable;
import rx.Subscriber;

public class ParseHelper {

    public static final ParseHelper INSTANCE = new ParseHelper();

    private static final String TAG = ParseHelper.class.getSimpleName();
    private static final boolean DEBUG = true;

    @VisibleForTesting
    ParseHelper() {
    }

    public ParseUser getCurrentUser() {
        return ParseUser.getCurrentUser();
    }

    public <T> Observable<T> callFunction(@NonNull String functionName, @NonNull Map<String, ?> params) {
        Log.d(TAG, "Calling function: " + functionName + ", with params: " + params);
        return Observable.create(new SimpleParseRequest<T>("Exception while calling method " + functionName) {

            @NonNull
            @Override
            protected T networkCall() throws ParseException {
                return ParseCloud.callFunction(functionName, params);
            }
        });
    }

    public <T> Observable<T> callFunctionForList(@NonNull String functionName, @NonNull Map<String, ?> params) {
        Log.d(TAG, "Calling function: " + functionName + ", with params: " + params);
        return Observable.create(new ParseRequest<List<T>, T>("Exception while calling method " + functionName) {

            @NonNull
            @Override
            protected List<T> networkCall() throws ParseException {
                return ParseCloud.callFunction(functionName, params);
            }

            @Override
            protected void publishResult(@NonNull Subscriber<? super T> subscriber, @NonNull List<T> results) {
                for (T result : results) {
                    subscriber.onNext(result);
                }
            }
        });
    }

    public <T extends ParseObject> Observable<List<T>> find(@NonNull ParseQuery<T> query) {
        Log.d(TAG, "Running query for: " + query.getClassName());
        return Observable.create(new SimpleParseRequest<List<T>>("Exception while executing find() for class " + query.getClassName()) {
            @NonNull
            @Override
            protected List<T> networkCall() throws ParseException {
                return query.find();
            }
        });
    }

    public <T extends ParseObject> Observable<T> get(@NonNull ParseQuery<T> query, @NonNull String objectId) {
        Log.d(TAG, "Running query for: " + query.getClassName());
        return Observable.create(new SimpleParseRequest<T>("Exception while executing get() for class " + query.getClassName()) {

            @NonNull
            @Override
            protected T networkCall() throws ParseException {
                return query.get(objectId);
            }
        });
    }

    public Observable<ParseUser> signIn(@NonNull String userName, @NonNull String password) {
        Log.d(TAG, "Running sign in");
        return Observable.create(new SimpleParseRequest<ParseUser>("Exception while calling signIn") {

            @NonNull
            @Override
            protected ParseUser networkCall() throws ParseException {
                return ParseUser.logIn(userName, password);
            }
        });
    }

    public Observable<Void> signOut() {
        Log.d(TAG, "Running sign out");
        return Observable.create(new SimpleParseRequest<Void>("Exception while calling signOut") {

            @NonNull
            @Override
            protected Void networkCall() throws ParseException {
                ParseUser.logOut();
                return null;
            }
        });
    }

    public Observable<ParseUser> signUp(@NonNull String userName, @NonNull String password, @NonNull Map<String, ?> params) {
        Log.d(TAG, "Running sign up");
        return Observable.create(new SimpleParseRequest<ParseUser>("Exception while calling signUp") {

            @NonNull
            @Override
            protected ParseUser networkCall() throws ParseException {
                final ParseUser user = new ParseUser();
                user.setUsername(userName);
                user.setPassword(password);
                for (Map.Entry<String, ?> param : params.entrySet()) {
                    user.put(param.getKey(), param.getValue());
                }
                user.signUp();
                return user;
            }
        });
    }

    public void saveInBackground(@NonNull ParseObject msg, @Nullable SaveCallback callback) {
        msg.saveInBackground(callback);
    }

    public void save(@NonNull ParseObject msg) {
        try {
            msg.save();
        }
        catch (ParseException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Simple implementation of ParseRequest where the result of the network call is published to the subscriber directly.
     */
    private abstract class SimpleParseRequest<T> extends ParseRequest<T, T> {

        SimpleParseRequest(@Nullable String traceMessage) {
            super(traceMessage);
        }

        @Override
        protected void publishResult(@NonNull Subscriber<? super T> subscriber, @NonNull T result) {
            subscriber.onNext(result);
        }
    }

    /**
     * Abstract implementation of {@link OnSubscribe} to ensure that for a given request no matter the number of subscriptions made the
     * network call will only be executed once.  Subsequent subscriptions will either wait for an existing network call to complete or
     * just receive the result.
     */
    private abstract class ParseRequest<R, T> implements Observable.OnSubscribe<T> {

        private final RuntimeException mTraceException;
        private volatile boolean mCallCompleted = false;
        private R mResult;
        private ParseException mParseException;

        /**
         * If using in debug mode, a suppressed exception will be added to the error published via the subscriber to assist with finding the
         * cause.
         */
        ParseRequest(@Nullable String traceMessage) {
            //noinspection PointlessBooleanExpression
            mTraceException = DEBUG && traceMessage != null ? new RuntimeException(traceMessage) : null;
        }

        @Override
        public void call(Subscriber<? super T> subscriber) {
            if (!mCallCompleted) {
                synchronized (this) {
                    if (!mCallCompleted) {
                        try {
                            mResult = networkCall();
                        }
                        catch (ParseException e) {
                            mParseException = e;
                        }
                        mCallCompleted = true;
                    }
                }
            }
            if (mResult != null) {
                publishResult(subscriber, mResult);
            }
            else if (mParseException != null) {
                publishError(subscriber, mParseException);
                return;
            }
            // As we might not have a result for example where the return type is void, onCompleted needs to occur outside of the result
            // if statement.
            subscriber.onCompleted();
        }

        @NonNull
        protected abstract R networkCall() throws ParseException;

        protected abstract void publishResult(@NonNull Subscriber<? super T> subscriber, @NonNull R result);

        protected void publishError(@NonNull Subscriber<? super T> subscriber, @NonNull ParseException exception) {
            if (mTraceException != null && Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                exception.addSuppressed(mTraceException);
            }
            subscriber.onError(mParseException);
        }
    }
}
