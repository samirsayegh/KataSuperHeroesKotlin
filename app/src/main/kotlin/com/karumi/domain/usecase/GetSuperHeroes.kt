package com.karumi.domain.usecase

import android.accounts.NetworkErrorException
import arrow.core.Either
import com.karumi.data.repository.SuperHeroRepository
import com.karumi.domain.model.SuperHero

class GetSuperHeroes(private val superHeroesRepository: SuperHeroRepository) {

    operator fun invoke(): Either<NetworkErrorException, List<SuperHero>> = superHeroesRepository.getAllSuperHeroes()
}
