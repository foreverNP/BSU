package com.example.calculator16

import androidx.test.core.app.ActivityScenario
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import org.hamcrest.CoreMatchers.containsString
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
@LargeTest
class MainActivityTest {

    @Test
    fun testInitialDisplay() {
        ActivityScenario.launch(MainActivity::class.java)
        onView(withId(R.id.tvDisplay)).check(matches(withText("0")))
    }

    @Test
    fun testNumberInput() {
        ActivityScenario.launch(MainActivity::class.java)
        onView(withId(R.id.btn1)).perform(click())
        onView(withId(R.id.btn2)).perform(click())
        onView(withId(R.id.tvDisplay)).check(matches(withText(containsString("12"))))
    }

    @Test
    fun testConversionWithoutInputShowsError() {
        ActivityScenario.launch(MainActivity::class.java)
        onView(withId(R.id.btnClear)).perform(click())
        onView(withId(R.id.btnConvertUK)).perform(click())
        onView(withText(R.string.error_no_input)).check(matches(isDisplayed()))
    }

    @Test
    fun testNavigationToInfoFragment() {
        ActivityScenario.launch(MainActivity::class.java)
        onView(withId(R.id.btnInfo)).perform(click())
        onView(withText(R.string.info_message)).check(matches(isDisplayed()))
    }
}
