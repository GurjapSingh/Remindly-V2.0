package com.blanyal.remindme;

import android.support.test.InstrumentationRegistry;
import android.support.test.espresso.Espresso;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.support.v7.widget.RecyclerView;
import android.test.suitebuilder.annotation.LargeTest;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.InstrumentationRegistry.getInstrumentation;
import static android.support.test.espresso.Espresso.onData;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.Espresso.openActionBarOverflowOrOptionsMenu;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.closeSoftKeyboard;
import static android.support.test.espresso.action.ViewActions.typeText;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.RootMatchers.isDialog;
import static android.support.test.espresso.matcher.RootMatchers.isPlatformPopup;
import static android.support.test.espresso.matcher.ViewMatchers.hasDescendant;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;

import android.support.test.espresso.contrib.RecyclerViewActions;

import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.is;


@RunWith(AndroidJUnit4.class)
@LargeTest
public class UITest {
    @Rule
    public ActivityTestRule<MainActivity> mActivityRule = new ActivityTestRule<MainActivity>(MainActivity.class);

    @Test
    public void addNewReminderAndSetRepeat() {
        onView(withId(R.id.add_reminder)).perform(click());
        onView(withId(R.id.reminder_title)).perform(typeText("Test Repeat"), closeSoftKeyboard());
        onView(withId(R.id.repeat_ll)).perform(click());
        onView(withId(R.id.RepeatNo_Dialog)).perform(typeText("33"), closeSoftKeyboard());
        onView(withId(R.id.RepeatTypeSpinner)).perform(click());
        onData(allOf(is(instanceOf(String.class)), is("Week(s)"))).inRoot(isPlatformPopup()).perform(click());
        onView(withText("Week(s)")).check(matches(isDisplayed()));
        onView(withText("OK")).inRoot(isDialog()).check(matches(isDisplayed())).perform(click());
        onView(withId(R.id.save_reminder)).perform(click());
    }

    @Test
    public void editReminderAndSetRepeat() {
        onView(withId(R.id.reminder_list)).perform(RecyclerViewActions.actionOnItemAtPosition(0, click()));
        Espresso.closeSoftKeyboard();
        onView(withId(R.id.repeat_ll)).perform(click());
        onView(withId(R.id.RepeatNo_Dialog)).perform(typeText("66"), closeSoftKeyboard());
        onView(withId(R.id.RepeatTypeSpinner)).perform(click());
        onData(allOf(is(instanceOf(String.class)), is("Month(s)"))).inRoot(isPlatformPopup()).perform(click());
        onView(withText("Month(s)")).check(matches(isDisplayed()));
        onView(withText("OK")).inRoot(isDialog()).check(matches(isDisplayed())).perform(click());
        onView(withId(R.id.save_reminder)).perform(click());
    }


    @Test
    public void sortSelectionTest() {
        onView(withId(R.id.sorting)).perform(click());
        onView(withText("Alphabetically")).perform(click());
        onView(withId(R.id.sorting)).perform(click());
        onView(withText("Date of Creation")).perform(click());
        onView(withId(R.id.sorting)).perform(click());
        onView(withText("Date of Event")).perform(click());
        onView(withId(R.id.sorting)).perform(click());
        onView(withText("Priority Level")).perform(click());
        onView(withId(R.id.sorting)).perform(click());
        onView(withText("Alarm Status")).perform(click());
        onView(withId(R.id.sorting)).perform(click());
        onView(withText("Type")).perform(click());
    }
}
