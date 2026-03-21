package fr.isen.danielbeni.disney

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.google.firebase.Firebase
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.database
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import fr.isen.danielbeni.disney.ui.screens.HomeScreen
import fr.isen.danielbeni.disney.ui.screens.LoginScreen
import fr.isen.danielbeni.disney.ui.screens.ProfileScreen
import fr.isen.danielbeni.disney.ui.theme.DisneyTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            DisneyTheme {
                // initialisation du contrôleur de navigation
                val navController = rememberNavController()

                // le navHost est le conteneur qui change d'écran.
                // startDestination indique l'écran qui s'affiche au lancement de l'app.
                NavHost(navController = navController, startDestination = "login") {

                    composable("login") {
                        LoginScreen(navController)
                    }

                    composable("home") {
                        HomeScreen(navController)
                    }

                    composable("profile") {
                        ProfileScreen(navController)
                    }
                }
            }
        }
    }
}

// on laisse DataBaseHelper ici
class DataBaseHelper {
    fun getCategories(handler: (List<Categorie>) -> Unit) {
        // RAPPEL : Mets bien ton URL Firebase ici !
        val database = Firebase.database("https://disneyapp-isen-default-rtdb.europe-west1.firebasedatabase.app")
        val myRef = database.getReference("categories")

        myRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val rawValue = snapshot.value
                val disneyGson = Gson()
                val jsonString = disneyGson.toJson(rawValue)
                val type = object : TypeToken<List<Categorie>>() {}.type
                val categories: List<Categorie> = disneyGson.fromJson(jsonString, type)
                handler(categories)
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("dataBase", error.toString())
                handler(emptyList())
            }
        })
    }
}