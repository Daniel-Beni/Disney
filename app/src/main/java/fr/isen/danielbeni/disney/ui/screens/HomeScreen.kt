package fr.isen.danielbeni.disney.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import fr.isen.danielbeni.disney.Categorie
import fr.isen.danielbeni.disney.DataBaseHelper
import fr.isen.danielbeni.disney.Film
import fr.isen.danielbeni.disney.Franchise
import fr.isen.danielbeni.disney.SousSaga

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(navController: NavController) {
    val categories = remember { mutableStateListOf<Categorie>() }
    val expandableCategories = remember { mutableStateListOf<Categorie>() }

    LaunchedEffect(Unit) {
        // Chargement des données Firebase
        DataBaseHelper().getCategories {
            categories.clear() // Nettoyage pour éviter les doublons
            categories.addAll(it)
        }
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        // Ajout de la barre supérieure avec le titre et l'action
        topBar = {
            TopAppBar(
                title = { Text("Disney App") },
                actions = {
                    // Icône de profil pour naviguer vers ProfileScreen
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
        // Affichage de la liste des catégories
        LazyColumn(modifier = Modifier.padding(innerPadding)) {
            items(categories) { categorie ->
                Column() {
                    Card(Modifier.clickable {
                        if (expandableCategories.firstOrNull { it.categorie == categorie.categorie } != null) {
                            expandableCategories.removeAll { it.categorie == categorie.categorie }
                        } else {
                            expandableCategories.add(categorie)
                        }
                    }) {
                        Text("${categorie.categorie}")
                    }
                    if(expandableCategories.firstOrNull { it.categorie == categorie.categorie } != null) {
                        // On passe le navController
                        franchises(categorie.franchises, navController)
                    }
                }
            }
        }
    }
}

@Composable
fun franchises(franchises: List<Franchise>, navController: NavController) {
    val expandableFranchises = remember { mutableStateListOf<Franchise>() }
    Column(Modifier.padding(start = 16.dp)) {
        franchises.forEach { franchise ->
            Text(franchise.nom, Modifier.clickable {
                if (expandableFranchises.firstOrNull { it.nom == franchise.nom } != null) {
                    expandableFranchises.removeAll { it.nom == franchise.nom }
                } else {
                    expandableFranchises.add(franchise)
                }
            })
            if(expandableFranchises.firstOrNull { it.nom == franchise.nom } != null) {
                val saga = franchise.sousSagas
                // On passe le navController à la suite
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
            Text(saga.nom, Modifier.clickable {
                if (expandableSaga.firstOrNull { it.nom == saga.nom } != null) {
                    expandableSaga.removeAll { it.nom == saga.nom }
                } else {
                    expandableSaga.add(saga)
                }
            })
            if(expandableSaga.firstOrNull { it.nom == saga.nom } != null) {
                // On passe le navController aux films
                films(saga.films, navController)
            }
        }
    }
}

@Composable
fun films(films: List<Film>, navController: NavController) {
    Column(Modifier.padding(start = 16.dp)) {
        films.forEach { film ->
            // On affiche le titre ET la date
            Text(
                text = "${film.titre} (${film.annee})",
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
                    .clickable {
                        navController.navigate("film_detail/${film.titre}")
                    }
            )
        }
    }
}