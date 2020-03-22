package com.example.databasedemo;

import android.util.Log;
import android.widget.EditText;

import androidx.annotation.NonNull;
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
    FirebaseFirestore db = FirebaseFirestore.getInstance();

    private String username = "foo";
    private User testUser;
    private EditText userText;

    @Rule
    public ActivityTestRule<CreateAccount> createAccountActivityTestRule
            = new ActivityTestRule<>(CreateAccount.class);

    @Before
    public void addUsername(){
        testUser = new User();
        testUser.setUsername(username);
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
    }

    @Test
    public void enterUsernameText(){
        onView(withId(R.id.username))
                .perform(typeText(username), closeSoftKeyboard());
        onView(withId(R.id.password))
                .perform(typeText("123456"), closeSoftKeyboard());
        onView(withId(R.id.email))
                .perform(typeText("hkwarsam@ualberta.ca"), closeSoftKeyboard());
        onView(withId(R.id.address))
                .perform(typeText("Hogwarts"), closeSoftKeyboard());
        onView(withId(R.id.phone))
                .perform(typeText(String.valueOf("780-434-4323")), closeSoftKeyboard());
        onView(withId(R.id.driver_or_rider))
                .perform(click());
        onData(anything()).atPosition(1).perform(click());


        onView(withId(R.id.finish_create)).perform(click());

        onView(withId(R.id.username)).check(matches(hasErrorText("Username is not unique.")));
    }

}