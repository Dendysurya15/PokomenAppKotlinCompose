package com.dendysurya.pokemon.data.repository

import com.dendysurya.pokemon.data.api.ApiService
import com.dendysurya.pokemon.model.ApiListResponse
import com.dendysurya.pokemon.model.PokemonDetail

class PokemonRepository(private val apiService: ApiService) {
    // Get a list of Pokemon with pagination
    suspend fun getPokemonList(offset: Int, limit: Int): Result<ApiListResponse> {
        return try {
            val response = apiService.getPokemonList(offset, limit)
            Result.success(response)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // Get detailed information about a specific Pokemon
    suspend fun getPokemonDetail(nameOrId: String): Result<PokemonDetail> {
        return try {
            val response = apiService.getPokemonDetail(nameOrId)
            Result.success(response)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}