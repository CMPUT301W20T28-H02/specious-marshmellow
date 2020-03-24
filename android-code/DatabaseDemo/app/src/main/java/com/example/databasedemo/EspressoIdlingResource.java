package com.example.databasedemo;

import androidx.test.espresso.IdlingResource;


/**
 *
 *  Author: Ihor Klimov
 *  github: https://github.com/IhorKlimov/AndroidTests/tree/master/app/src/main/java/com/myhexaville/androidtests/util
 *  Youtube: https://www.youtube.com/watch?v=_V3N4YHRbkA&t=273s
 *
 */

/* Idling resource is essentially a counter that increments when a asynchronous task, such as a
   Firestore query, is called and decrements when that process when that process is finished
 */
public class EspressoIdlingResource {
    private static final String RESOURCE = "GLOBAL";

    private static SimpleCountingIdlingResource mCountingIdlingResource =
            new SimpleCountingIdlingResource(RESOURCE);

    public static void increment() {
        mCountingIdlingResource.increment();
    }

    public static void decrement() {
        mCountingIdlingResource.decrement();
    }

    public static IdlingResource getIdlingResource() {
        return mCountingIdlingResource;
    }
}
