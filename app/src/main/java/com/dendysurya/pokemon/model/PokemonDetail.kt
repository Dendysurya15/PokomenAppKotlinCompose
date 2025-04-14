package com.dendysurya.pokemon.model

data class PokemonDetail(
    val id: Int,                  // Pokémon ID number
    val name: String,             // Pokémon name
    val height: Int,              // Height in decimeters
    val weight: Int,              // Weight in hectograms
    val base_experience: Int,     // Base experience gained when defeating this Pokémon
    val sprites: Sprites,         // Various sprite images
    val abilities: List<AbilityInfo>,  // List of abilities
    val types: List<TypeInfo>,    // List of types
    val stats: List<StatInfo>     // Base stats
)

data class PokemonListState(
    val isLoading: Boolean = false,         // Whether list is currently being loaded
    val pokemonList: List<PokemonBasic> = emptyList(), // The list of Pokemon
    val canLoadMore: Boolean = true,        // Whether there are more Pokemon to load
    val errorMessage: String? = null         // Error message if loading failed
)

data class PokemonDetailState(
    val isLoading: Boolean = false,
    val pokemon: PokemonDetail? = null,
    val errorMessage: String? = null
)

data class Sprites(
    val front_default: String?,   // URL to default front sprite
    val back_default: String?,    // URL to default back sprite
    val front_shiny: String?,     // URL to shiny front sprite
    val back_shiny: String?       // URL to shiny back sprite
    // There are more sprite URLs but these are the main ones
)

data class AbilityInfo(
    val ability: Ability,         // Ability details
    val is_hidden: Boolean,       // Whether this is a hidden ability
    val slot: Int                 // Slot number for this ability
)

data class Ability(
    val name: String,             // Ability name
    val url: String               // URL to get more details about this ability
)

data class TypeInfo(
    val slot: Int,                // Slot number for this type
    val type: Type                // Type details
)

data class Type(
    val name: String,             // Type name (e.g., "fire", "water")
    val url: String               // URL to get more details about this type
)

data class StatInfo(
    val base_stat: Int,           // Base value for this stat
    val effort: Int,              // Effort points (EVs) gained for this stat
    val stat: Stat                // Stat details
)

data class Stat(
    val name: String,             // Stat name (e.g., "hp", "attack")
    val url: String               // URL to get more details about this stat
)