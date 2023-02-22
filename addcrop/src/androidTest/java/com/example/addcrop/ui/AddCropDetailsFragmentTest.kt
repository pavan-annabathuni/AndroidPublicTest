package com.example.addcrop.ui


import androidx.navigation.Navigation
import androidx.navigation.testing.TestNavHostController
import androidx.test.core.app.ActivityScenario
import androidx.test.core.app.ApplicationProvider

import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.*
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.MediumTest
import androidx.test.internal.runner.junit4.AndroidJUnit4ClassRunner
import androidx.test.rule.ActivityTestRule
import com.example.addcrop.AddCropActivity
import com.example.addcrop.test.R
import com.example.addcrop.ui.free.AddCropDetailsFragment
import com.google.common.truth.Truth.assertThat
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith


@RunWith(AndroidJUnit4ClassRunner::class)
class AddCropDetailsFragmentTest {
//    @Test
//    fun testAddCropFragmentNavigation(){
//        val activityRule = ActivityScenarioRule(AddCropActivity::class.java)
//
//
//    }

//    @get:Rule
//    val activityScenarioRule = activityScenarioRule<AddCropActivity>()

    //    @get:Rule
//    val activityRule = ActivityTestRule(AddCropActivity::class.java)
//    @Test
//    val activityScenario = ActivityScenario.launch(AddCropActivity::class.java)

    @Test
    fun testEditTextViews() {
        val activityScenario = ActivityScenario.launch(AddCropActivity::class.java)
        onView(withId(com.example.addcrop.R.id.et_nickname_crop)).perform(typeText("Hello"))
        onView(withId(com.example.addcrop.R.id.et_crop_area)).perform(typeText("123"))

        onView(withId(com.example.addcrop.R.id.et_nickname_crop)).check(matches(withText("Hello")))
        onView(withId(com.example.addcrop.R.id.et_crop_area)).check(matches(withText("123")))
    }
        //    @Test
//    fun testDatePicker() {
//        onView(withId(com.example.addcrop.R.id.cl_calender_date_select)).perform(click())
//
//        onView(withId(R.id.date_picker)).perform(PickerActions.setDate(2023, 2, 20))
//        onView(withId(R.id.iv_calender_icon)).perform(click())
//
//        onView(withId(R.id.selected_date_text_view)).check(matches(withText("2023-02-20")))
//    }
        @Test
        fun testNavigation() {
            // Assuming the fragment with the edit text fields and date picker is called "MyFragment"
            onView(withId(com.example.addcrop.R.id.addCropDetailsFragment)).perform(click())
            onView(withId(com.example.addcrop.R.id.et_nickname_crop)).check(matches(isDisplayed()))
            onView(withId(com.example.addcrop.R.id.et_crop_area)).check(matches(isDisplayed()))

//        onView(withId(R.id.date_picker_button)).perform(click())
//        onView(withId(R.id.date_picker)).check(matches(isDisplayed()))
        }
//    @get:Rule
//    val activityRule = ActivityScenarioRule(AddCropActivity::class.java)

//    @Test
//    fun testNavigation() {
//        val navController = TestNavHostController(ApplicationProvider.getApplicationContext())
//        val myFragmentScenario = launchFragmentInContainer<AddCropDetailsFragment>()
//
//        myFragmentScenario.onFragment { fragment ->
//            Navigation.setViewNavController(fragment.requireView(), navController)
//        }
//
//        navController.setGraph(com.example.addcrop.R.navigation.nav_add_crop)
//        navController.setCurrentDestination(com.example.addcrop.R.id.selectAddCropFragment)
//
//        onView(withId(com.example.addcrop.R.id.card_save_details_crop)).perform(click())
//
//        assertThat(navController.currentDestination?.id).isEqualTo(com.example.addcrop.R.id.addCropDetailsFragment)
//    }


//
//    @get:Rule
//    val activityRule = ActivityTestRule(AddCropActivity::class.java)
//
//    private lateinit var navController: TestNavHostController
//
//    @Before
//    fun setUp() {
//        navController = TestNavHostController(ApplicationProvider.getApplicationContext())
//        navController.setGraph(com.example.addcrop.R.navigation.nav_add_crop)
//        navController.setCurrentDestination(com.example.addcrop.R.id.selectAddCropFragment)
//    }
//
//    @Test
//    fun testNavigateToInnerFragment() {
//        navController.navigate(com.example.addcrop.R.id.addCropDetailsFragment)
//        onView(withId(R.id.inner_fragment_text)).check(matches(withText("Inner Fragment")))
//        onView(withId(R.id.inner_fragment_button)).perform(click())
//        onView(withId(R.id.inner_fragment_text)).check(matches(withText("Button clicked")))
//
//
//        onView(withId(com.example.addcrop.R.id.et_nickname_crop)).perform(typeText("praveen"))
//        onView(withId(R.id.cl_calender_date_select)).perform(click())
//        onView(withClassName(equalTo(CalendarView::class.java.name))).check(matches(isDisplayed()))
//        onView(withId(com.example.addcrop.R.id.et_crop_area)).perform(typeText("123"), closeSoftKeyboard())
//        onView(withId(R.id.cardCheckHealth)).perform(closeSoftKeyboard())
//        onView(withId(com.example.addcrop.R.id.card_save_details_crop)).perform(click())
//        onView(withText("Error: Field cannot be empty")).check(matches(isDisplayed()))
//    }
//}
//
//
//    @Test
//    fun testNavigationToInGameScreen() {
//        // Create a TestNavHostController
//        val navController = TestNavHostController(
//            ApplicationProvider.getApplicationContext()
//        )
//
//        // Create a graphical FragmentScenario for the TitleScreen
//        val titleScenario = launchFragmentInContainer<AddCropDetailsFragment>()
//
//        titleScenario.onFragment { fragment ->
//            // Set the graph on the TestNavHostController
//            navController.setGraph(com.example.addcrop.R.navigation.nav_add_crop)
//
//            // Make the NavController available via the findNavController() APIs
//            Navigation.setViewNavController(fragment.requireView(), navController)
//        }
//    }
//
//    @get:Rule
//    val activityRule = ActivityTestRule(AddCropActivity::class.java)
//    @Before
//    fun setUp() {
//            val navController =
//                Navigation.findNavController(activityRule.activity, R.id.selectAddCropFragment)
//            navController.navigate(R.id.addCropDetailsFragment)
//
//
//    }
//
//    @Before
//    fun setUp() {
//        CoroutineScope(Dispatchers.IO).launch {
//            val navController =
//                Navigation.findNavController(activityRule.activity, R.id.selectAddCropFragment)
//            navController.navigate(R.id.addCropDetailsFragment)
//        }
//    }
//    @Rule
//    @JvmField
//    val activityTestRule =
//        ActivityTestRule(AddCropActivity::class.java, true, false)
//
//    @Before
//    fun setUp() {
//        val activity = activityTestRule.launchActivity(null)
//        // Increase timeout value
//        activity.asyncMonitor.setLaunchTimeout(60000)
//    }
//    @Test
//    fun testSave_cropDetails() {
//        onView(withId(R.id.et_nickname_crop)).perform(typeText("praveen"))
//        onView(withId(R.id.cl_calender_date_select)).perform(click())
//        onView(withClassName(equalTo(CalendarView::class.java.name))).check(matches(isDisplayed()))
//        onView(withId(R.id.et_crop_area)).perform(typeText("123"), closeSoftKeyboard())
//        onView(withId(R.id.cardCheckHealth)).perform(closeSoftKeyboard())
//        onView(withId(R.id.card_save_details_crop)).perform(click())
//        onView(withText("Error: Field cannot be empty")).check(matches(isDisplayed()))
//    }


}