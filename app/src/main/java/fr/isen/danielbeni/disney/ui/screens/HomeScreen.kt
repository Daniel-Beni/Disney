package fr.isen.danielbeni.disney.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import fr.isen.danielbeni.disney.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(navController: NavController) {

    val categories = remember { mutableStateListOf<Categorie>() }
    val expandableCategories = remember { mutableStateListOf<Categorie>() }

    var searchQuery by remember { mutableStateOf("") }

    LaunchedEffect(Unit) {
        DataBaseHelper().getCategories {
            categories.clear()
            categories.addAll(it)
        }
    }

    val filteredFilms = if (searchQuery.isBlank()) {
        emptyList()
    } else {
        categories.flatMap { it.franchises.flatMap { f ->
            f.tousLesFilms().filter { film ->
                film.titre.contains(searchQuery, true)
            }
        }}
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Disney App") },
                actions = {
                    IconButton(onClick = { navController.navigate("profile") }) {
                        Icon(Icons.Default.Person, null)
                    }
                }
            )
        }
    ) { padding ->

        Column(Modifier.padding(padding)) {

            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                placeholder = { Text("Rechercher...") },
                leadingIcon = { Icon(Icons.Default.Search, null) },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            )

            LazyColumn {
                items(if (searchQuery.isBlank()) emptyList() else filteredFilms) { film ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp)
                            .clickable {
                                navController.navigate("film_detail/${film.titre}")
                            }
                    ) {
                        Text(
                            film.titre,
                            modifier = Modifier.padding(16.dp),
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}