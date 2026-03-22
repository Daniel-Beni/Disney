package fr.isen.danielbeni.disney

import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.net.URL

import fr.isen.danielbeni.disney.BuildConfig

object TmdbHelper {

    // LA CORRECTION EST ICI : On utilise BuildConfig.TMDB_API_KEY
    private val API_KEY = BuildConfig.TMDB_API_KEY
    private const val BASE_URL = "https://api.themoviedb.org/3"
    private const val IMAGE_BASE_URL = "https://image.tmdb.org/t/p/w500"

    /**
     * Cherche un film par titre et retourne l'URL de l'affiche.
     */
    suspend fun getPosterUrl(filmTitle: String): String? {
        return withContext(Dispatchers.IO) {
            try {
                val encodedTitle = java.net.URLEncoder.encode(filmTitle, "UTF-8")
                val url = "$BASE_URL/search/movie?api_key=$API_KEY&query=$encodedTitle&language=fr-FR"
                val response = URL(url).readText()
                val json = JSONObject(response)
                val results = json.getJSONArray("results")

                if (results.length() > 0) {
                    val posterPath = results.getJSONObject(0).optString("poster_path", "")
                    if (posterPath.isNotEmpty()) {
                        "$IMAGE_BASE_URL$posterPath"
                    } else null
                } else null
            } catch (e: Exception) {
                Log.e("TmdbHelper", "Erreur TMDB: ${e.message}")
                null
            }
        }
    }
}