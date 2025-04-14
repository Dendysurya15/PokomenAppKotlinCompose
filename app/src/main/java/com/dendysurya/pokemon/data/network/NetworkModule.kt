package com.dendysurya.pokemon.data.network

import com.dendysurya.pokemon.data.api.ApiService
import com.dendysurya.pokemon.data.repository.PokemonRepository
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object NetworkModule {
    fun provideRetrofit(): Retrofit {
        return Retrofit.Builder()
            .baseUrl("https://pokeapi.co/api/v2/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    fun provideApiService(retrofit: Retrofit): ApiService {
        return retrofit.create(ApiService::class.java)
    }

    fun providePokemonRepository(apiService: ApiService): PokemonRepository {
        return PokemonRepository(apiService)
    }
}