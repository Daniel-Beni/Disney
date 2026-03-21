package fr.isen.danielbeni.disney.ui.screens

import android.widget.Toast // NOUVEL IMPORT
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext // NOUVEL IMPORT
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

data class UserFilmStatus(val email: String, val ownDvd: Boolean, val wantToGetRidOf: Boolean)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FilmDetailScreen(navController: NavController, filmTitle: String) {
    val auth = FirebaseAuth.getInstance()
    val currentUser = auth.currentUser
    val database = FirebaseDatabase.getInstance("https://disneyapp-isen-default-rtdb.europe-west1.firebasedatabase.app").reference
    val context = LocalContext.current // Récupération du contexte pour le Toast

    var isWatched by remember { mutableStateOf(false) }
    var wantToWatch by remember { mutableStateOf(false) }
    var ownDvd by remember { mutableStateOf(false) }
    var wantToGetRidOf by remember { mutableStateOf(false) }
    val otherUsersStatus = remember { mutableStateListOf<UserFilmStatus>() }

    LaunchedEffect(filmTitle) {
        currentUser?.let { user ->
            database.child("users").child(user.uid).child("films").child(filmTitle).get()
                .addOnSuccessListener { snapshot ->
                    isWatched = snapshot.child("watched").getValue(Boolean::class.java) ?: false
                    wantToWatch = snapshot.child("wantToWatch").getValue(Boolean::class.java) ?: false
                    ownDvd = snapshot.child("ownDvd").getValue(Boolean::class.java) ?: false
                    wantToGetRidOf = snapshot.child("wantToGetRidOf").getValue(Boolean::class.java) ?: false
                }

            database.child("films_status").child(filmTitle)
                .addValueEventListener(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        otherUsersStatus.clear()
                        for (userSnapshot in snapshot.children) {
                            if (userSnapshot.key != user.uid) {
                                val email = userSnapshot.child("email").getValue(String::class.java) ?: "Anonyme"
                                val hasDvd = userSnapshot.child("ownDvd").getValue(Boolean::class.java) ?: false
                                val wantsToSell = userSnapshot.child("wantToGetRidOf").getValue(Boolean::class.java) ?: false
                                if (hasDvd || wantsToSell) {
                                    otherUsersStatus.add(UserFilmStatus(email, hasDvd, wantsToSell))
                                }
                            }
                        }
                    }
                    override fun onCancelled(error: DatabaseError) {}
                })
        }
    }

    fun saveStatus() {
        currentUser?.let { user ->
            val status = mapOf(
                "watched" to isWatched,
                "wantToWatch" to wantToWatch,
                "ownDvd" to ownDvd,
                "wantToGetRidOf" to wantToGetRidOf,
                "email" to user.email
            )
            database.child("users").child(user.uid).child("films").child(filmTitle).setValue(status)
            database.child("films_status").child(filmTitle).child(user.uid).setValue(status)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(filmTitle) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Retour")
                    }
                }
            )
        }
    ) { padding ->
        Column(modifier = Modifier.padding(padding).padding(16.dp).fillMaxSize()) {

            Text("Mon statut :", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(8.dp))

            // Ajout du Toast dans chaque onCheckedChange
            Row(verticalAlignment = Alignment.CenterVertically) {
                Checkbox(checked = isWatched, onCheckedChange = {
                    isWatched = it
                    saveStatus()
                    Toast.makeText(context, "Statut sauvegardé", Toast.LENGTH_SHORT).show()
                })
                Text("Vu")
            }
            Row(verticalAlignment = Alignment.CenterVertically) {
                Checkbox(checked = wantToWatch, onCheckedChange = {
                    wantToWatch = it
                    saveStatus()
                    Toast.makeText(context, "Statut sauvegardé", Toast.LENGTH_SHORT).show()
                })
                Text("À voir")
            }
            Row(verticalAlignment = Alignment.CenterVertically) {
                Checkbox(checked = ownDvd, onCheckedChange = {
                    ownDvd = it
                    saveStatus()
                    Toast.makeText(context, "Statut sauvegardé", Toast.LENGTH_SHORT).show()
                })
                Text("Possédé en DVD/Blu-Ray")
            }
            Row(verticalAlignment = Alignment.CenterVertically) {
                Checkbox(checked = wantToGetRidOf, onCheckedChange = {
                    wantToGetRidOf = it
                    saveStatus()
                    Toast.makeText(context, "Statut sauvegardé", Toast.LENGTH_SHORT).show()
                })
                Text("Envie de s'en débarrasser")
            }

            Spacer(modifier = Modifier.height(16.dp))
            HorizontalDivider()
            Spacer(modifier = Modifier.height(16.dp))

            Text("Communauté :", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(8.dp))

            if (otherUsersStatus.isEmpty()) {
                Text(text = "Aucun autre utilisateur ne possède ce film.", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.secondary)
            } else {
                LazyColumn {
                    items(otherUsersStatus) { userStatus ->
                        Card(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Text(userStatus.email, fontWeight = FontWeight.Bold)
                                if (userStatus.ownDvd) Text("- Possède le film")
                                if (userStatus.wantToGetRidOf) Text("- Souhaite s'en débarrasser", color = MaterialTheme.colorScheme.error)
                            }
                        }
                    }
                }
            }
        }
    }
}