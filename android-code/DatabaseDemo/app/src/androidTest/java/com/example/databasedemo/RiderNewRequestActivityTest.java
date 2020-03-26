package com.example.databasedemo;

import android.app.Activity;
import android.app.Instrumentation;
import android.content.Intent;
import android.view.KeyEvent;

import androidx.test.espresso.Root;
import androidx.test.espresso.action.ViewActions;
import androidx.test.espresso.intent.ActivityResultFunction;
import androidx.test.espresso.intent.rule.IntentsTestRule;
import androidx.test.filters.LargeTest;
import androidx.test.rule.ActivityTestRule;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.pressImeActionButton;
import static androidx.test.espresso.action.ViewActions.pressKey;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.intent.Intents.intending;
import static androidx.test.espresso.intent.matcher.IntentMatchers.anyIntent;
import static androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent;
import static androidx.test.espresso.intent.matcher.IntentMatchers.hasExtras;
import static androidx.test.espresso.intent.matcher.IntentMatchers.toPackage;
import static androidx.test.espresso.matcher.ViewMatchers.assertThat;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static org.hamcrest.core.AllOf.allOf;

@RunWith(JUnit4.class)
@LargeTest
public class RiderNewRequestActivityTest {
    private String location2 = "New York";
    private String tipAmount = "10";
    private String username = "LocalNatives";
    private String email = "LocalNatives@gmail.com";
    private String package_name = "com.example.databasedemo";


    @Rule
    public IntentsTestRule<RiderNewRequestActivity> RiderNewReqIntentsTestRule =
            new IntentsTestRule<>(RiderNewRequestActivity.class);

    @Test
    public void EnterTwoLocations(){

        //enter destination
        onView(withId(R.id.sv2_location))
                .perform(typeText(location2));

        // press enter key and close keyboard
        onView(withId(R.id.sv2_location))
                .perform(pressKey(KeyEvent.KEYCODE_ENTER), closeSoftKeyboard());


        //get fare
        onView(withId(R.id.btnGetFare))
                .perform(click());

         //enter tip amount
        onView(withId(R.id.tipAmount))
                .perform(typeText(String.valueOf(tipAmount)), closeSoftKeyboard());

         //add tip to fare
        onView(withId(R.id.addTipButton))
                .perform(click());

        // get fare
        onView(withId(R.id.btnGetFare))
                .perform(click());



    }

    @Test
    public void newRequestIntentTest(){

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