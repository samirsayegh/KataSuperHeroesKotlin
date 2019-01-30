package com.karumi

import android.os.Bundle
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
import com.karumi.ui.view.SuperHeroDetailActivity
import com.nhaarman.mockitokotlin2.whenever
import org.hamcrest.Matchers.allOf
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock

@RunWith(AndroidJUnit4::class)
class SuperHeroDetailActivityTest : AcceptanceTest<SuperHeroDetailActivity>(SuperHeroDetailActivity::class.java) {

    @Mock
    lateinit var repository: SuperHeroRepository

    @Before
    override fun setup() {
        super.setup()
        whenever(repository.getByName(NAME)).thenReturn(SUPER_HERO)
    }

    @Test
    fun verifyNameOfSuperHeroShown() {
        startActivity(Bundle().apply { putString(SuperHeroDetailActivity.SUPER_HERO_NAME_KEY, NAME) })

        onView(allOf(withId(R.id.tv_super_hero_name), withText(NAME))).check(matches(isDisplayed()))
    }


    override val testDependencies = Kodein.Module(allowSilentOverride = true) {
        bind<SuperHeroRepository>() with instance(repository)
    }

    companion object {
        private const val NAME = "name"
        private const val DESCRIPTION = "description"
        private const val IS_AVENGER = true
        private const val PHOTO = "photo"
        private val SUPER_HERO = SuperHero(
            name = NAME,
            description = DESCRIPTION,
            isAvenger = IS_AVENGER,
            photo = PHOTO
        )
    }
}
