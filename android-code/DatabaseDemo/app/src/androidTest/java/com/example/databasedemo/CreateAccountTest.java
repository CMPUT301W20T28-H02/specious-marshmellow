package com.example.databasedemo;

import android.util.Log;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.test.espresso.IdlingRegistry;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;
import androidx.test.rule.ActivityTestRule;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static androidx.test.espresso.Espresso.onData;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.hasErrorText;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static org.hamcrest.CoreMatchers.anything;
import static org.junit.Assert.*;

@RunWith(AndroidJUnit4.class)
@LargeTest
public class CreateAccountTest {

    // Firestore instance
    FirebaseFirestore db = FirebaseFirestore.getInstance();

    // user data
    private User testUser;
    private String username = "foo";
    private String email = "hkwarsam@ualberta.ca";
    private Wallet wallet = new Wallet ();
    private String phone = "780-434-4323";
    private double rating = 5.0;
    private int numOfRatings = 1;
    private boolean driver = true;
    private String password = "123456";
    private String address = "Hogwarts";


    @Rule
    public ActivityTestRule<CreateAccount> createAccountActivityTestRule
            = new ActivityTestRule<>(CreateAccount.class);

    // add user to database
    @Before
    public void addUsername(){
        // create user object
        testUser = new User(username, email, wallet, phone, rating, numOfRatings, driver, false);

        // add user to firestore and set user data
        db.collection("users").document(username).set(testUser)
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

        // register Idling resource that waits for firestore processes to finish
        IdlingRegistry.getInstance().register(EspressoIdlingResource.getIdlingResource());
    }

    /* input user data and check if error text pops up
    when trying to use non-unique username */
    @Test
    public void enterUsernameText(){
        // enter username
        onView(withId(R.id.username))
                .perform(typeText(username), closeSoftKeyboard());

        // enter password
        onView(withId(R.id.password))
                .perform(typeText(password), closeSoftKeyboard());

        // enter email
        onView(withId(R.id.email))
                .perform(typeText(email), closeSoftKeyboard());

        // enter address
        onView(withId(R.id.address))
                .perform(typeText(address), closeSoftKeyboard());

        // enter phone number
        onView(withId(R.id.phone))
                .perform(typeText(String.valueOf(phone)), closeSoftKeyboard());

        // select driver option
        onView(withId(R.id.driver_or_rider))
                .perform(click());
        onData(anything()).atPosition(1).perform(click());

        // create account
        onView(withId(R.id.finish_create)).perform(click());

        // check if error text pops up when creating
        onView(withId(R.id.username)).check(matches(hasErrorText("Username is not unique.")));
    }

}