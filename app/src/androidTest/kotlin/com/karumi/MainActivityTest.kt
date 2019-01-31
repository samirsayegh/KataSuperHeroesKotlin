package com.karumi

import android.accounts.NetworkErrorException
import android.support.test.espresso.Espresso.onView
import android.support.test.espresso.action.ViewActions.click
import android.support.test.espresso.assertion.ViewAssertions.matches
import android.support.test.espresso.contrib.RecyclerViewActions.actionOnItemAtPosition
import android.support.test.espresso.intent.Intents.intended
import android.support.test.espresso.intent.matcher.IntentMatchers.hasComponent
import android.support.test.espresso.intent.matcher.IntentMatchers.hasExtra
import android.support.test.espresso.matcher.ViewMatchers
import android.support.test.espresso.matcher.ViewMatchers.hasDescendant
import android.support.test.espresso.matcher.ViewMatchers.isDisplayed
import android.support.test.espresso.matcher.ViewMatchers.withEffectiveVisibility
import android.support.test.espresso.matcher.ViewMatchers.withId
import android.support.test.espresso.matcher.ViewMatchers.withText
import android.support.test.runner.AndroidJUnit4
import android.support.v7.widget.RecyclerView
import arrow.core.Either
import com.github.salomonbrys.kodein.Kodein
import com.github.salomonbrys.kodein.bind
import com.github.salomonbrys.kodein.instance
import com.karumi.data.repository.SuperHeroRepository
import com.karumi.domain.model.SuperHero
import com.karumi.recyclerview.RecyclerViewInteraction
import com.karumi.ui.view.MainActivity
import com.karumi.ui.view.SuperHeroDetailActivity
import com.nhaarman.mockitokotlin2.whenever
import org.hamcrest.Matchers.allOf
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

    @Test
    fun validateBadgeIsNotShownIfSuperHeroIsNotAvenger() {
        val superHeroList = givenAnAvengerSuperHero()

        startActivity()

        RecyclerViewInteraction.onRecyclerView<SuperHero>(withId(R.id.recycler_view))
            .withItems(superHeroList)
            .check { _, view, exception ->
                matches(hasDescendant(allOf(withId(R.id.iv_avengers_badge), withEffectiveVisibility(ViewMatchers.Visibility.GONE))))
                    .check(view, exception)
            }
    }

    @Test
    fun validateNavigatingToDetailsActivity() {
        val superHeroList = givenAnAvengerSuperHero()
        val superHero = superHeroList[0]
        whenever(repository.getByName(superHero.name)).thenReturn(superHero)

        startActivity()

        onView(withId(R.id.recycler_view)).perform(actionOnItemAtPosition<RecyclerView.ViewHolder>(0, click()))


        intended(hasComponent(SuperHeroDetailActivity::class.java.canonicalName))
        intended(hasExtra(SuperHeroDetailActivity.SUPER_HERO_NAME_KEY, superHero.name))
    }

    @Test
    fun validateNetworkExceptionActivity() {
        whenever(repository.getAllSuperHeroes()).thenReturn(Either.left(NetworkErrorException()))

        startActivity()

        onView(withId(R.id.tv_network_error_case)).check(matches((isDisplayed())))
    }

    private fun givenThereAreNoSuperHeroes() {
        whenever(repository.getAllSuperHeroes()).thenReturn(Either.right(emptyList()))
    }

    private fun givenThereAreTwoSuperHeroes() {
        whenever(repository.getAllSuperHeroes()).thenReturn(
            Either.right(
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
        )
    }

    private fun givenAnAvengerSuperHero(): List<SuperHero> {
        with(
            listOf(
                SuperHero(
                    name = "Hero 1",
                    description = "Description 1",
                    isAvenger = false,
                    photo = "photo1"
                )
            )
        ) {
            whenever(repository.getAllSuperHeroes()).thenReturn(Either.right(this))
            return this
        }
    }

    override val testDependencies = Kodein.Module(allowSilentOverride = true) {
        bind<SuperHeroRepository>() with instance(repository)
    }
}
