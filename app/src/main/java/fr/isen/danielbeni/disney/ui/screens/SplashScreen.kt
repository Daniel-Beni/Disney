package fr.isen.danielbeni.disney.ui.screens

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import fr.isen.danielbeni.disney.ui.theme.DisneyDarkBlue
import fr.isen.danielbeni.disney.ui.theme.DisneyGold
import fr.isen.danielbeni.disney.ui.theme.DisneyRoyalBlue
import fr.isen.danielbeni.disney.ui.theme.DisneyTitleFont
import kotlinx.coroutines.delay

/**
 * Splash Screen — Écran de lancement avec animation de fondu.
 *
 * Affiche le nom de l'app avec un effet de fade-in pendant 2 secondes,
 * puis navigue automatiquement vers le login.
 *
 * L'animation utilise animateFloatAsState pour faire apparaître
 * progressivement le texte (alpha de 0 à 1).
 */
@Composable
fun SplashScreen(navController: NavController) {
    // Contrôle l'animation de fondu
    var startAnimation by remember { mutableStateOf(false) }
    val alphaAnim by animateFloatAsState(
        targetValue = if (startAnimation) 1f else 0f,
        animationSpec = tween(durationMillis = 1500),
        label = "splashFade"
    )

    // Lance l'animation puis navigue après 2.5 secondes
    LaunchedEffect(Unit) {
        startAnimation = true
        delay(2500)
        navController.navigate("login") {
            popUpTo("splash") { inclusive = true }
        }
    }

    // Fond dégradé identique au login
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        DisneyDarkBlue,
                        DisneyRoyalBlue,
                        DisneyDarkBlue
                    )
                )
            ),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.alpha(alphaAnim)
        ) {
            Text(
                text = "Disney",
                fontSize = 56.sp,
                fontFamily = DisneyTitleFont,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = "Filmothèque",
                style = MaterialTheme.typography.titleLarge,
                color = DisneyGold
            )
        }
    }
}