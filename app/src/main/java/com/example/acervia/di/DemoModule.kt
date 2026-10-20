package com.example.acervia.di

import com.example.acervia.data.repository.FakeAuthRepository
import com.example.acervia.data.repository.FakeLivroRepository
import com.example.acervia.data.repository.FakePreferenciasRepository
import com.example.acervia.data.repository.FakeReservaRepository
import com.example.acervia.domain.repository.AuthRepository
import com.example.acervia.domain.repository.LivroRepository
import com.example.acervia.domain.repository.PreferenciasRepository
import com.example.acervia.domain.repository.ReservaRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class DemoModule {

    @Binds
    @Singleton
    abstract fun bindLivroRepository(
        impl: FakeLivroRepository
    ): LivroRepository

    @Binds
    @Singleton
    abstract fun bindReservaRepository(
        impl: FakeReservaRepository
    ): ReservaRepository

    @Binds
    @Singleton
    abstract fun bindAuthRepository(
        impl: FakeAuthRepository
    ): AuthRepository

    @Binds
    @Singleton
    abstract fun bindPreferenciasRepository(
        impl: FakePreferenciasRepository
    ): PreferenciasRepository
}
