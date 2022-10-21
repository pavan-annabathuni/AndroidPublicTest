package com.example.profile.fragments



import androidx.core.content.MimeTypeFilter.matches
import androidx.fragment.app.testing.FragmentScenario
import androidx.fragment.app.testing.launchFragmentInContainer
import androidx.lifecycle.Lifecycle
import androidx.test.espresso.Espresso
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click

import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.profile.R
import com.google.common.truth.Truth.assertThat
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import androidx.test.espresso.action.ViewActions.typeText


@RunWith(AndroidJUnit4::class)
class MyProfileFragmentTest{
    private lateinit var sscenario: FragmentScenario<MyProfileFragment>

    @Before
    fun setup(){
        sscenario = launchFragmentInContainer(themeResId = R.style.Theme_OutGrow)
      sscenario.moveToState(Lifecycle.State.STARTED)

    }

    @Test
    fun textClick(){
        val name = "sdsdsd"
        onView(withId(R.id.username)).perform(typeText(name))
 //    onView(withId(R.id.ll4)).perform(click())
 //       onView(withId(R.id.username)).perform(typeText("Hello"))
//        onView(withId(R.id.username)).perform(typeText(""))

    }
}