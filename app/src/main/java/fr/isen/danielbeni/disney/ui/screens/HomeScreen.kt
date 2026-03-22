package fr.isen.danielbeni.disney.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import fr.isen.danielbeni.disney.Categorie
import fr.isen.danielbeni.disney.DataBaseHelper
import fr.isen.danielbeni.disney.Film
import fr.isen.danielbeni.disney.Franchise
import fr.isen.danielbeni.disney.SousSaga
import androidx.compose.ui.draw.clip
import coil3.compose.AsyncImage
import fr.isen.danielbeni.disney.TmdbHelper

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(navController: NavController) {
    val categories = remember { mutableStateListOf<Categorie>() }
    val expandableCategories = remember { mutableStateListOf<Categorie>() }

    // État de la barre de recherche
    var searchQuery by remember { mutableStateOf("") }

    LaunchedEffect(Unit) {
        DataBaseHelper().getCategories {
            categories.clear()
            categories.addAll(it)
        }
    }

    /**
     * Filtrage des films par recherche.
     *
     * Quand searchQuery n'est pas vide, on parcourt toutes les catégories,
     * franchises et sous-sagas pour trouver les films dont le titre
     * contient le texte recherché (insensible à la casse).
     *
     * On retourne une liste plate de Film pour l'afficher directement.
     */
    val filteredFilms: List<Film> = if (searchQuery.isBlank()) {
        emptyList()
    } else {
        categories.flatMap { categorie ->
            categorie.franchises.flatMap { franchise ->
                franchise.tousLesFilms().filter { film ->
                    film.titre.contains(searchQuery, ignoreCase = true)
                }
            }
        }
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = { Text("Disney App") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background,
                    titleContentColor = MaterialTheme.colorScheme.onBackground
                ),
                actions = {
                    IconButton(onClick = { navController.navigate("profile") }) {
                        Icon(
                            imageVector = Icons.Default.Person,
                            contentDescription = "Voir le profil"
                        )
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(modifier = Modifier.padding(innerPadding)) {

            // ── Barre de recherche ──────────────────────────
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                placeholder = { Text("Rechercher un film...") },
                leadingIcon = {
                    Icon(Icons.Filled.Search, contentDescription = "Rechercher")
                },
                trailingIcon = {
                    if (searchQuery.isNotEmpty()) {
                        IconButton(onClick = { searchQuery = "" }) {
                            Icon(Icons.Filled.Clear, contentDescription = "Effacer")
                        }
                    }
                },
                singleLine = true,
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            )

            if (searchQuery.isNotBlank()) {
                // ── Mode recherche : affiche les résultats filtrés ──
                if (filteredFilms.isEmpty()) {
                    Text(
                        text = "Aucun film trouvé pour \"$searchQuery\"",
                        modifier = Modifier.padding(16.dp),
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                } else {
                    LazyColumn {
                        items(filteredFilms) { film ->
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 16.dp, vertical = 4.dp)
                                    .clickable {
                                        navController.navigate("film_detail/${film.titre}")
                                    },
                                colors = CardDefaults.cardColors(
                                    containerColor = MaterialTheme.colorScheme.surfaceVariant
                                )
                            ) {
                                Row(
                                    modifier = Modifier.padding(12.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column {
                                        Text(
                                            text = film.titre,
                                            fontWeight = FontWeight.Bold,
                                            color = MaterialTheme.colorScheme.primary
                                        )
                                        film.annee?.let {
                                            Text(
                                                text = "$it",
                                                fontSize = 13.sp,
                                                color = MaterialTheme.colorScheme.onSurfaceVariant
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            } else {
                // ── Mode navigation : affiche l'arborescence ──
                LazyColumn {
                    items(categories) { categorie ->
                        Column {
                            // Card de catégorie
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 16.dp, vertical = 4.dp)
                                    .clickable {
                                        if (expandableCategories.any { it.categorie == categorie.categorie }) {
                                            expandableCategories.removeAll { it.categorie == categorie.categorie }
                                        } else {
                                            expandableCategories.add(categorie)
                                        }
                                    },
                                colors = CardDefaults.cardColors(
                                    containerColor = MaterialTheme.colorScheme.surface
                                )
                            ) {
                                Text(
                                    text = categorie.categorie,
                                    modifier = Modifier.padding(16.dp),
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 18.sp,
                                    color = MaterialTheme.colorScheme.primary
                                )
                            }

                            // Animation d'expansion
                            AnimatedVisibility(
                                visible = expandableCategories.any { it.categorie == categorie.categorie },
                                enter = expandVertically() + fadeIn(),
                                exit = shrinkVertically() + fadeOut()
                            ) {
                                franchises(categorie.franchises, navController)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun franchises(franchises: List<Franchise>, navController: NavController) {
    val expandableFranchises = remember { mutableStateListOf<Franchise>() }
    Column(Modifier.padding(start = 32.dp, end = 16.dp)) {
        franchises.forEach { franchise ->
            Text(
                text = franchise.nom,
                fontWeight = FontWeight.SemiBold,
                fontSize = 16.sp,
                color = MaterialTheme.colorScheme.secondary,
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable {
                        if (expandableFranchises.any { it.nom == franchise.nom }) {
                            expandableFranchises.removeAll { it.nom == franchise.nom }
                        } else {
                            expandableFranchises.add(franchise)
                        }
                    }
                    .padding(vertical = 8.dp)
            )

            AnimatedVisibility(
                visible = expandableFranchises.any { it.nom == franchise.nom },
                enter = expandVertically() + fadeIn(),
                exit = shrinkVertically() + fadeOut()
            ) {
                val saga = franchise.sousSagas
                saga?.let { saga(it, navController) } ?: run { films(franchise.tousLesFilms(), navController) }
            }
        }
    }
}

@Composable
fun saga(sousSaga: List<SousSaga>, navController: NavController) {
    val expandableSaga = remember { mutableStateListOf<SousSaga>() }
    Column(Modifier.padding(start = 16.dp)) {
        sousSaga.forEach { saga ->
            Text(
                text = saga.nom,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.tertiary,
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable {
                        if (expandableSaga.any { it.nom == saga.nom }) {
                            expandableSaga.removeAll { it.nom == saga.nom }
                        } else {
                            expandableSaga.add(saga)
                        }
                    }
                    .padding(vertical = 6.dp)
            )

            AnimatedVisibility(
                visible = expandableSaga.any { it.nom == saga.nom },
                enter = expandVertically() + fadeIn(),
                exit = shrinkVertically() + fadeOut()
            ) {
                films(saga.films, navController)
            }
        }
    }
}

@Composable
fun films(films: List<Film>, navController: NavController) {
    Column(Modifier.padding(start = 16.dp)) {
        films.forEach { film ->
            // État pour l'URL de l'affiche
            var posterUrl by remember { mutableStateOf<String?>(null) }

            // Charge l'affiche depuis TMDB à la première apparition
            LaunchedEffect(film.titre) {
                posterUrl = TmdbHelper.getPosterUrl(film.titre)
            }

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 2.dp)
                    .clickable {
                        navController.navigate("film_detail/${film.titre}")
                    },
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant
                )
            ) {
                Row(
                    modifier = Modifier.padding(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Miniature de l'affiche (comme dans TheGreatestCocktailApp)
                    AsyncImage(
                        model = posterUrl,
                        contentDescription = film.titre,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .size(60.dp, 85.dp)
                            .clip(RoundedCornerShape(6.dp))
                    )

                    Column(modifier = Modifier.padding(start = 12.dp)) {
                        Text(
                            text = film.titre,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                        film.annee?.let {
                            Text(
                                text = "$it",
                                fontSize = 13.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }
        }
    }
}