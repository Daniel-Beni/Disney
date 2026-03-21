package fr.isen.danielbeni.disney.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
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

@Composable
fun HomeScreen(navController: NavController) {
    Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
        val categories = remember { mutableStateListOf<Categorie>() }
        val expandableCategories = remember { mutableStateListOf<Categorie>() }

        LaunchedEffect(Unit) {
            DataBaseHelper().getCategories {
                categories.addAll(it)
            }
        }

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
                        franchises(categorie.franchises)
                    }
                }
            }
        }
    }
}


@Composable
fun franchises(franchises: List<Franchise>) {
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
                saga?.let { saga(it) } ?: run { films(franchise.tousLesFilms()) }
            }
        }
    }
}

@Composable
fun saga(sousSaga: List<SousSaga>) {
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
                films(saga.films)
            }
        }
    }
}

@Composable
fun films(films: List<Film>) {
    Column(Modifier.padding(start = 16.dp)) {
        films.forEach { film ->
            Text(film.titre)
        }
    }
}