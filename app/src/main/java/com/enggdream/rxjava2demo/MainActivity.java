package com.enggdream.rxjava2demo;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import java.util.Arrays;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.BiFunction;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = MainActivity.class.getCanonicalName();
    List<String> words = Arrays.asList(
            "the",
            "quick",
            "brown",
            "fox",
            "jumped",
            "over",
            "the",
            "lazy",
            "dogs",
            "the"
    );
    CompositeDisposable compositeDisposable = new CompositeDisposable();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        observableWithData();
    }

    private void observableWithData() {
        final Observable<String> stringObservable = new Observable<String>() {
            @Override
            protected void subscribeActual(Observer<? super String> observer) {
                for (String word : words) {
                    observer.onNext(word);
                }
                observer.onComplete();
            }
        }.flatMap(new Function<String, ObservableSource<String>>() {
            @Override
            public ObservableSource<String> apply(@NonNull String s) throws Exception {
                Log.d(TAG, "apply: " + s);
                return Observable.fromArray(s.split(""));
            }
        })
                .sorted()
                .distinct()
                .zipWith(Observable.range(1, Integer.MAX_VALUE), new BiFunction<String, Integer, String>() {
                    @Override
                    public String apply(@NonNull String s, @NonNull Integer integer) throws Exception {
                        return integer + "." + s;
                    }
                })
                .observeOn(Schedulers.io())
                .subscribeOn(AndroidSchedulers.mainThread())
                /*.map(new Function<String, Integer>() {
                    @Override
                    public Integer apply(@NonNull String s) throws Exception {
                        Log.d(TAG, "apply: " + s);
                        return s.length();
                    }
                })
                .filter(new Predicate<Integer>() {
                    @Override
                    public boolean test(@NonNull Integer integer) throws Exception {
                        return integer>5;
                    }
                })*/;
        Observer<String> stringObserver = new Observer<String>() {
            @Override
            public void onSubscribe(Disposable d) {
                Log.d(TAG, "onSubscribe: called");
                compositeDisposable.add(d);
            }

            @Override
            public void onNext(String s) {
                Log.d(TAG, "onNext: " + s);
            }

            @Override
            public void onError(Throwable e) {
                Log.d(TAG, "onError: called");
            }

            @Override
            public void onComplete() {
                Log.d(TAG, "onComplete: called");
            }
        };

        stringObservable.subscribeWith(stringObserver);
        /*
        final Observer<String> string2Observer = new Observer<String>() {
            @Override
            public void onSubscribe(Disposable d) {
                Log.d(TAG, "onSubscribe2: called");
                compositeDisposable.add(d);
            }

            @Override
            public void onNext(String s) {
                Log.d(TAG, "onNext2: " + s);
            }

            @Override
            public void onError(Throwable e) {
                Log.d(TAG, "onError2: called");
            }

            @Override
            public void onComplete() {
                Log.d(TAG, "onComplete2: called");
            }
        };
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                stringObservable.subscribeWith(string2Observer);
            }
        }, 2500);*/
    }

    @Override
    protected void onStop() {
        super.onStop();
        compositeDisposable.dispose();
    }
}
