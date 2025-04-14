package com.dendysurya.pokemon.ui.page

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import com.dendysurya.pokemon.model.PokemonListState
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.dendysurya.pokemon.ui.component.CardItem
import com.dendysurya.pokemon.ui.component.CardItemDetail
import com.dendysurya.pokemon.ui.navigation.Routes
import com.dendysurya.pokemon.viewmodel.MainViewModel
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filter

@Composable
fun HomePage(
    viewModel: MainViewModel,
    navController: NavController
) {
    var selectedTab by remember { mutableStateOf(0) }
    val tabs = listOf("Home", "Profile")


    Scaffold(
        bottomBar = {
            NavigationBar {
                tabs.forEachIndexed { index, title ->
                    NavigationBarItem(
                        icon = {
                            Icon(
                                imageVector = when(index) {
                                    0 -> Icons.Default.Home
                                    else -> Icons.Default.Person
                                },
                                contentDescription = title
                            )
                        },
                        label = { Text(title) },
                        selected = selectedTab == index,
                        onClick = { selectedTab = index }
                    )
                }
            }
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            when (selectedTab) {
                0 -> HomeContent(viewModel)
                1 -> ProfileContent(viewModel, navController)
            }
        }
    }
}

@Composable
fun HomeContent(viewModel: MainViewModel) {
    val pokemonList by viewModel.filteredPokemonList.collectAsState(initial = emptyList())
    val listState = rememberLazyListState()
    val pokemonDetailState by viewModel.pokemonDetailState.collectAsState(initial = null)
    val searchQuery by viewModel.searchQuery.collectAsState(initial = "")
    val loadingState by viewModel.pokemonListState.collectAsState(initial = PokemonListState())

    // Load more Pokémon when scrolling to the bottom
    LaunchedEffect(listState) {
        snapshotFlow {
            val layoutInfo = listState.layoutInfo
            val visibleItemsInfo = layoutInfo.visibleItemsInfo
            if (visibleItemsInfo.isEmpty()) false
            else {
                val lastVisibleItem = visibleItemsInfo.last()
                val lastIndex = layoutInfo.totalItemsCount - 1
                lastVisibleItem.index == lastIndex
            }
        }
            .distinctUntilChanged()
            .filter { it }
            .collect {
                viewModel.loadMorePokemon()
            }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(modifier = Modifier.fillMaxSize()) {
            // Search bar - uncomment when ready to use
            // PokemonSearchBar(
            //     query = searchQuery,
            //     onQueryChange = { viewModel.updateSearchQuery(it) },
            //     modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
            // )

            // Pokemon list
            if (pokemonList.isEmpty() && loadingState.isLoading) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            } else if (pokemonList.isEmpty() && !loadingState.isLoading && searchQuery.isNotEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text("No Pokémon found for '$searchQuery'")
                }
            } else {
                LazyColumn(
                    state = listState,
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    itemsIndexed(pokemonList) { index, pokemon ->
                        // Extract pokemon ID from URL
                        val url = pokemon.url
                        val id = url.split("/").filter { it.isNotEmpty() }.last()

                        CardItem(
                            name = pokemon.name,
                            id = id,
                            onClick = { viewModel.loadPokemonDetail(id) }
                        )
                    }

                    // Loading indicator
                    if (loadingState.isLoading) {
                        item {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                CircularProgressIndicator()
                            }
                        }
                    }
                }
            }
        }

        // Pokemon detail sheet
        pokemonDetailState?.let { detailState ->
            CardItemDetail(
                state = detailState,
                onDismiss = { viewModel.clearPokemonDetail() }
            )
        }
    }
}

@Composable
fun ProfileContent(viewModel: MainViewModel, navController: NavController) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(
            text = "Profile",
            style = MaterialTheme.typography.headlineMedium
        )

        // Display user info
        LaunchedEffect(Unit) {
            viewModel.loadUserProfile()
        }

        val userState by viewModel.userProfileState.collectAsState()

        if (userState.isLoading) {
            CircularProgressIndicator()
        } else {
            userState.userData?.let { user ->
                Text("Name: ${user.name}")
                Text("Email: ${user.email}")

                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = {
                        viewModel.logout()
                        navController.navigate(Routes.LoginPage) {
                            popUpTo(Routes.HomePage) { inclusive = true }
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Logout")
                }
            }
        }
    }
}