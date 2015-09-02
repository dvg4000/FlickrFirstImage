package ru.triton265.flickrfirstimage.app;

import android.util.Log;
import junit.framework.TestCase;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class RxJavaTest extends TestCase {
    private static final String TAG_DEBUG = RxJavaTest.class.getSimpleName();

    public void test1() {
        Observable.from(new String[] {"one", "two", "three", "four", "five"})
                .subscribeOn(Schedulers.io())
                .map(s -> {
                    Log.d(TAG_DEBUG, "map1 : " + Long.toString(Thread.currentThread().getId()) + " : " + s);
                    return s;
                })
                .map(s -> {
                    Log.d(TAG_DEBUG, "map2 : " + Long.toString(Thread.currentThread().getId()) + " : " + s);
                    return s;
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(s -> Log.d(TAG_DEBUG, Long.toString(Thread.currentThread().getId()) + " : " + s));
    }

    public void test2() {
        Observable.from(new String[] {"one", "two", "three", "four", "five"})
                .subscribeOn(Schedulers.io())
                .flatMap(s -> {
                    Log.d(TAG_DEBUG, "flatmap1 : " + Long.toString(Thread.currentThread().getId()) + " : " + s);
                    return Observable.just(s);
                })
                .map(s -> {
                    Log.d(TAG_DEBUG, "map1 : " + Long.toString(Thread.currentThread().getId()) + " : " + s);
                    return s;
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(s -> Log.d(TAG_DEBUG, Long.toString(Thread.currentThread().getId()) + " : " + s));
    }
}
