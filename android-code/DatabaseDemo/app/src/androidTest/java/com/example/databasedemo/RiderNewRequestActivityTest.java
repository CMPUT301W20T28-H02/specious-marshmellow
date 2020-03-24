package com.example.databasedemo;

import android.view.KeyEvent;

import androidx.test.espresso.Root;
import androidx.test.espresso.action.ViewActions;
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
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static org.junit.Assert.*;
@RunWith(JUnit4.class)
@LargeTest
public class RiderNewRequestActivityTest {
    private String location2 = "New York";
    private String tipAmount = "10";

    @Rule
    public ActivityTestRule<RiderNewRequestActivity> RiderNewReqActivTestRule =
            new ActivityTestRule<>(RiderNewRequestActivity.class);

    @Test
    public void EnterTwoLocations(){
        // enter destination
        onView(withId(R.id.sv2_location))
                .perform(typeText(location2));


        onView(withId(R.id.sv2_location))
                .perform(pressKey(KeyEvent.KEYCODE_ENTER), closeSoftKeyboard());



        // get fare
        onView(withId(R.id.btnGetFare))
                .perform(click());

        // enter tip amount
        onView(withId(R.id.tipAmount))
                .perform(typeText(String.valueOf(tipAmount)), closeSoftKeyboard());

        // add tip to fare
        onView(withId(R.id.addTipButton))
                .perform(click());

        // get fare
        onView(withId(R.id.btnGetFare))
                .perform(click());

        /* create request
        onView(withId(R.id.btnConfirmRequest))
                .perform(click()); */

    }

}