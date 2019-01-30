package com.karumi

import android.support.test.espresso.Espresso.onView
import android.support.test.espresso.assertion.ViewAssertions.matches
import android.support.test.espresso.matcher.ViewMatchers.isDisplayed
import android.support.test.espresso.matcher.ViewMatchers.withId
import android.support.test.espresso.matcher.ViewMatchers.withText
import android.support.test.runner.AndroidJUnit4
import com.github.salomonbrys.kodein.Kodein
import com.github.salomonbrys.kodein.bind
import com.github.salomonbrys.kodein.instance
import com.karumi.data.repository.SuperHeroRepository
import com.karumi.domain.model.SuperHero
import com.karumi.ui.view.MainActivity
import com.nhaarman.mockitokotlin2.whenever
import org.hamcrest.Matchers.not
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock

@RunWith(AndroidJUnit4::class)
class MainActivityTest : AcceptanceTest<MainActivity>(MainActivity::class.java) {

    @Mock
    lateinit var repository: SuperHeroRepository

    @Test
    fun showsEmptyCaseIfThereAreNoSuperHeroes() {
        givenThereAreNoSuperHeroes()

        startActivity()

        onView(withText("¯\\_(ツ)_/¯")).check(matches(isDisplayed()))
    }


//    @Test
//    fun validateProgressBarIsShownAndHidden() {
//        givenThereAreNoSuperHeroes()
//
//        startActivity()
//
//        onView(withId(R.id.progress_bar)).check(matches(not(isDisplayed())))
//    }


    @Test
    fun validateProgressBarHiddenWithNoSuperHeroes() {
        givenThereAreNoSuperHeroes()

        startActivity()

        onView(withId(R.id.progress_bar)).check(matches(not(isDisplayed())))
    }


    @Test
    fun validateProgressBarHiddenWithTwoSuperHeroes() {
        givenThereAreTwoSuperHeroes()

        startActivity()

        onView(withId(R.id.progress_bar)).check(matches(not(isDisplayed())))
        onView(withId(R.id.tv_empty_case)).check(matches(not(isDisplayed())))
    }

    private fun givenThereAreNoSuperHeroes() {
        whenever(repository.getAllSuperHeroes()).thenReturn(emptyList())
    }

    private fun givenThereAreTwoSuperHeroes() {
        whenever(repository.getAllSuperHeroes()).thenReturn(
            listOf(
                SuperHero(
                    name = "Hero 1",
                    description = "Description 1",
                    isAvenger = true,
                    photo = "photo1"
                ),
                SuperHero(
                    name = "Hero 2",
                    description = "Description 2",
                    isAvenger = false,
                    photo = "photo2"
                )
            )
        )
    }

    override val testDependencies = Kodein.Module(allowSilentOverride = true) {
        bind<SuperHeroRepository>() with instance(repository)
    }
}
