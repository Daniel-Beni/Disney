package fr.isen.danielbeni.disney.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

// classe de données pour structurer les films de l'utilisateur
data class MyFilm(
    val title: String,
    val watched: Boolean,
    val wantToWatch: Boolean,
    val ownDvd: Boolean,
    val wantToGetRidOf: Boolean
)

@Composable
fun ProfileScreen(navController: NavController) {
    val auth = FirebaseAuth.getInstance()
    val currentUser = auth.currentUser
    val database = FirebaseDatabase.getInstance("https://disneyapp-isen-default-rtdb.europe-west1.firebasedatabase.app").reference

    // Liste observable pour l'interface
    val myFilms = remember { mutableStateListOf<MyFilm>() }

    // Chargement dynamique de la liste de films de l'utilisateur
    LaunchedEffect(currentUser) {
        currentUser?.let { user ->
            database.child("users").child(user.uid).child("films")
                .addValueEventListener(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        myFilms.clear()
                        for (filmSnapshot in snapshot.children) {
                            val title = filmSnapshot.key ?: continue
                            val watched = filmSnapshot.child("watched").getValue(Boolean::class.java) ?: false
                            val wantToWatch = filmSnapshot.child("wantToWatch").getValue(Boolean::class.java) ?: false
                            val ownDvd = filmSnapshot.child("ownDvd").getValue(Boolean::class.java) ?: false
                            val wantToGetRidOf = filmSnapshot.child("wantToGetRidOf").getValue(Boolean::class.java) ?: false

                            // On ajoute le film à la liste seulement si une case est cochée
                            if (watched || wantToWatch || ownDvd || wantToGetRidOf) {
                                myFilms.add(MyFilm(title, watched, wantToWatch, ownDvd, wantToGetRidOf))
                            }
                        }
                    }
                    override fun onCancelled(error: DatabaseError) {}
                })
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // --- En-tête Profil ---
        Icon(
            imageVector = Icons.Filled.Person,
            contentDescription = "Utilisateur",
            modifier = Modifier.size(80.dp),
            tint = MaterialTheme.colorScheme.primary
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = currentUser?.email ?: "Email non disponible",
            style = MaterialTheme.typography.bodyLarge,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                auth.signOut()
                navController.navigate("login") {
                    popUpTo(0) { inclusive = true }
                }
            },
            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
        ) {
            Text("Se déconnecter")
        }

        Spacer(modifier = Modifier.height(24.dp))
        HorizontalDivider()
        Spacer(modifier = Modifier.height(16.dp))

        // --- Liste des Films ---
        Text(
            text = "Mes Films",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.align(Alignment.Start)
        )

        Spacer(modifier = Modifier.height(8.dp))

        if (myFilms.isEmpty()) {
            Text(
                text = "Vous n'avez pas encore interagi avec des films.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.secondary,
                modifier = Modifier.align(Alignment.Start)
            )
        } else {
            LazyColumn(modifier = Modifier.fillMaxWidth()) {
                items(myFilms) { film ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp)
                            // LA MAGIE EST LÀ : On rend la carte cliquable pour aller gérer le film !
                            .clickable {
                                navController.navigate("film_detail/${film.title}")
                            },
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Text(film.title, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                            if (film.watched) Text("- Vu")
                            if (film.wantToWatch) Text("- À voir")
                            if (film.ownDvd) Text("- Possédé en DVD/Blu-Ray")
                            if (film.wantToGetRidOf) Text("- Envie de s'en débarrasser", color = MaterialTheme.colorScheme.error)
                        }
                    }
                }
            }
        }
    }
}