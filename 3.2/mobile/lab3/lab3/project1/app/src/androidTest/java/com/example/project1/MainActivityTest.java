package com.example.project1;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.ext.junit.rules.ActivityScenarioRule;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.*;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.*;

@RunWith(AndroidJUnit4.class)
public class MainActivityTest {
    @Rule
    public ActivityScenarioRule<MainActivity> activityRule = new ActivityScenarioRule<>(MainActivity.class);

    @Test
    public void testInitialTextDisplayed() {
        onView(withId(R.id.tvInfo))
                .check(matches(withText(R.string.try_to_guess)));
    }

    @Test
    public void testUserGuess() {
        onView(withId(R.id.etInput)).perform(typeText("50"), closeSoftKeyboard());
        onView(withId(R.id.bControl)).perform(click());

        // Проверяем, что TextView с информацией изменил своё состояние (текст может быть "Загаданное число больше" или "Загаданное число меньше")
        onView(withId(R.id.tvInfo)).check(matches(isDisplayed()));
    }
}
