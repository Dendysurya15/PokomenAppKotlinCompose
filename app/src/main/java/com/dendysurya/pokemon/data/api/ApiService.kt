package com.dendysurya.pokemon.data.api

import com.dendysurya.pokemon.model.ApiListResponse
import com.dendysurya.pokemon.model.PokemonDetail
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface ApiService {
    @GET("pokemon")
    suspend fun getPokemonList(@Query("offset") offset: Int, @Query("limit") limit: Int): ApiListResponse

    @GET("pokemon/{nameOrId}")
    suspend fun getPokemonDetail(@Path("nameOrId") nameOrId: String): PokemonDetail
}