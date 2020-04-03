package com.example.databasedemo;

import android.app.Activity;
import android.app.Instrumentation;
import android.content.Intent;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.test.espresso.intent.rule.IntentsTestRule;
import androidx.test.filters.LargeTest;
import androidx.test.rule.ActivityTestRule;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import static androidx.test.espresso.Espresso.onData;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.clearText;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.pressKey;
import static androidx.test.espresso.action.ViewActions.scrollTo;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.intent.Intents.intending;
import static androidx.test.espresso.intent.matcher.IntentMatchers.toPackage;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static org.hamcrest.Matchers.anything;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.hasToString;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.startsWith;
import static org.junit.Assert.*;
@LargeTest
@RunWith(JUnit4.class)
public class DriverStartActivityTest {
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    private String package_name = "com.example.databasedemo";
    Request request;
    Rider testRider;
    // user data
    private String username = "TestRider";
    private String email = "hkwarsam@ualberta.ca";
    private Wallet wallet = new Wallet ();
    private String phone = "780-434-4323";
    private double rating = 5.0;
    private int numOfRatings = 1;
    private boolean driver = true;
    private String password = "123456";
    private String address = "Hogwarts";

    @Rule
    public  IntentsTestRule<DriverStartActivity> DriverStartIntentsTestRule =
            new IntentsTestRule<>(DriverStartActivity.class);
    @Before
    public void setUp() throws Exception {
        // create Rider object
        testRider = new Rider(username, email, wallet, phone, rating, numOfRatings, driver, false);

        // add Rider to firestore and set data
        db.collection("users").document(username).set(testRider)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d("database", "addition successful!");
                    }

                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d("database", "addition unsuccessful" , e);
                    }
                });

        // Start and end locations
        Location loc1 = new Location( 40.366633, 74.640832);
        Location loc2 = new Location(  42.443087, 76.488707);

        request = new Request(testRider, loc1, loc2);

        // add Request to firestore and set data
        db.collection("requests").document(username).set(request)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d("database", "addition successful!");
                    }

                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d("database", "addition unsuccessful" , e);
                    }
                });


    }
    // Custom Matcher that checks if ListView contains TestRider Username
    public static Matcher withRider(final String username){
        return new TypeSafeMatcher<View>(){
            boolean contains;
            @Override
            protected boolean matchesSafely(final View view) {
                for(int i =0; i < ((ListView) view).getCount(); i++) {
                    contains = ((ListView) view).getItemAtPosition(i).toString().contains(username);
                }
                return contains;
            }

            @Override
            public void describeTo(Description description) {

            }
        };
    }

    @Test
    public void checkRequest(){

        onView(withId(R.id.driver_initial)).check(matches(isDisplayed()));
        onView(withId(R.id.global_bounds_EditText))
                .perform(clearText(), typeText(String.valueOf(100000)), pressKey(KeyEvent.KEYCODE_ENTER), closeSoftKeyboard());


        onView(withId(R.id.requestListView)).check(matches(withRider("TestRider")));



    }

    @Test
    public void sendIntent(){

        // create intent and add user data to intent
        Intent user_data = new Intent();
        user_data.putExtra("username", username);
        user_data.putExtra("email", email);

        // intent result
        Instrumentation.ActivityResult result =
                new Instrumentation.ActivityResult(Activity.RESULT_OK, user_data);

        // check result
        intending(toPackage(package_name)).respondWith(result);

    }

}