package fr.isen.danielbeni.disney.ui.screens

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.google.firebase.auth.FirebaseAuth

@Composable
fun LoginScreen(navController: NavController) {
    // variables pour stocker ce que l'user tape
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    // outils nécessaires pour afficher des messages (Toast) et appeler Firebase
    val context = LocalContext.current
    val auth = FirebaseAuth.getInstance()

    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text("Bienvenue sur DisneyApp", style = MaterialTheme.typography.headlineMedium)

        Spacer(modifier = Modifier.height(32.dp))

        // champ Email
        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Email") },
            singleLine = true
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Champ Mot de passe
        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Mot de passe") },
            visualTransformation = PasswordVisualTransformation(),
            singleLine = true
        )

        Spacer(modifier = Modifier.height(32.dp))

        // Bouton de Connexion
        Button(onClick = {
            if (email.isNotEmpty() && password.isNotEmpty()) {
                // Appel à Firebase pour SE CONNECTER
                auth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            Toast.makeText(context, "Connexion réussie", Toast.LENGTH_SHORT).show()
                            // Navigation vers l'accueil et suppression de l'écran de login de l'historique
                            navController.navigate("home") {
                                popUpTo("login") { inclusive = true }
                            }
                        } else {
                            Toast.makeText(context, "Erreur : ${task.exception?.message}", Toast.LENGTH_LONG).show()
                        }
                    }
            } else {
                Toast.makeText(context, "Veuillez remplir tous les champs", Toast.LENGTH_SHORT).show()
            }
        }) {
            Text("Se connecter")
        }

        Spacer(modifier = Modifier.height(16.dp))

        // bouton d'Inscription
        TextButton(onClick = {
            if (email.isNotEmpty() && password.isNotEmpty()) {
                // appel à Firebase pour CRÉER UN COMPTE
                auth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            Toast.makeText(context, "Compte créé avec succès !", Toast.LENGTH_SHORT).show()
                            // Navigation automatique vers l'accueil
                            navController.navigate("home") {
                                popUpTo("login") { inclusive = true }
                            }
                        } else {
                            Toast.makeText(context, "Erreur : ${task.exception?.message}", Toast.LENGTH_LONG).show()
                        }
                    }
            } else {
                Toast.makeText(context, "Veuillez remplir tous les champs", Toast.LENGTH_SHORT).show()
            }
        }) {
            Text("Créer un compte")
        }
    }
}