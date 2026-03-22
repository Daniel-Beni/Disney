package fr.isen.danielbeni.disney

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.net.URL

object TmdbHelper {

    private const val API_KEY = "3ef28fab3efe98cc9bfec3e47b1c13c6"
    private const val BASE_URL = "https://api.themoviedb.org/3"
    private const val IMAGE_BASE_URL = "https://image.tmdb.org/t/p/w500"

    suspend fun getPosterUrl(filmTitle: String): String? {
        return withContext(Dispatchers.IO) {
            try {
                val url = "$BASE_URL/search/movie?api_key=$API_KEY&query=$filmTitle"
                val response = URL(url).readText()
                val json = JSONObject(response)
                val results = json.getJSONArray("results")

                if (results.length() > 0) {
                    val poster = results.getJSONObject(0).getString("poster_path")
                    "$IMAGE_BASE_URL$poster"
                } else null
            } catch (e: Exception) {
                null
            }
        }
    }
}