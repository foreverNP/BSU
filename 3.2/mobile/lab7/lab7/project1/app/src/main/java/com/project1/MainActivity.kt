package com.project1

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.room.Room
import kotlinx.coroutines.launch

object ApiConfig {
    const val apiKey = ""
}

class MainActivity : ComponentActivity() {
    private lateinit var database: AppDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Logger.d("MainActivity: onCreate")

        try {
            database = Room.databaseBuilder(applicationContext, AppDatabase::class.java, "music-db").build()
            Logger.d("Database successfully initialized")
        } catch (e: Exception) {
            Logger.e("Error initializing database", e)
            Toast.makeText(this, "Database initialization error: ${e.message}", Toast.LENGTH_LONG).show()
        }

        setContent {
            Logger.d("Setting up UI")
            MusicAppTheme {
                MusicAppUI(database, isInternetAvailable())
            }
        }

        val networkStatus = if (isInternetAvailable()) "available" else "unavailable"
        Logger.d("Internet $networkStatus")

        if (!isInternetAvailable()) {
            Toast.makeText(this, "Starting in offline mode", Toast.LENGTH_LONG).show()
        }
    }

    private fun isInternetAvailable(): Boolean {
        val connectivityManager = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val network = connectivityManager.activeNetwork ?: return false
        val activeNetwork = connectivityManager.getNetworkCapabilities(network) ?: return false
        return activeNetwork.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
    }
}

@Composable
fun MusicAppTheme(content: @Composable () -> Unit) {
    val colorScheme = darkColorScheme(
        primary = Color(0xFF6200EE),
        secondary = Color(0xFF03DAC6),
        background = Color(0xFF121212),
        surface = Color(0xFF1E1E1E),
        onPrimary = Color.White,
        onSecondary = Color.Black,
        onBackground = Color.White,
        onSurface = Color.White
    )

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography(
            bodyLarge = TextStyle(
                fontWeight = FontWeight.Normal,
                fontSize = 16.sp,
                lineHeight = 24.sp,
                letterSpacing = 0.5.sp
            ),
            titleLarge = TextStyle(
                fontWeight = FontWeight.Bold,
                fontSize = 22.sp,
                lineHeight = 28.sp,
                letterSpacing = 0.sp
            ),
            titleMedium = TextStyle(
                fontWeight = FontWeight.SemiBold,
                fontSize = 18.sp,
                lineHeight = 24.sp,
                letterSpacing = 0.15.sp
            ),
            bodyMedium = TextStyle(
                fontWeight = FontWeight.Normal,
                fontSize = 14.sp,
                lineHeight = 20.sp,
                letterSpacing = 0.25.sp
            ),
            bodySmall = TextStyle(
                fontWeight = FontWeight.Normal,
                fontSize = 12.sp,
                lineHeight = 16.sp,
                letterSpacing = 0.4.sp
            ),
        ),
        content = content
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MusicAppUI(database: AppDatabase, isOnline: Boolean) {
    val artist = remember { mutableStateOf("") }
    val tracks = remember { mutableStateOf(emptyList<Track>()) }
    val isLoading = remember { mutableStateOf(false) }
    val errorMessage = remember { mutableStateOf<String?>(null) }
    val coroutineScope = rememberCoroutineScope()
    val api = remember { provideLastFmApi() }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Music Explorer") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(16.dp)
        ) {
            Text(
                text = "Find your favorite music",
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.onBackground,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    OutlinedTextField(
                        value = artist.value,
                        onValueChange = { artist.value = it },
                        label = { Text("Enter artist name") },
                        placeholder = { Text("e.g., The Beatles") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                        leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Search") },
                        trailingIcon = {
                            if (artist.value.isNotEmpty()) {
                                IconButton(onClick = { artist.value = "" }) {
                                    Icon(Icons.Default.Clear, contentDescription = "Clear")
                                }
                            }
                        },
                        colors = TextFieldDefaults.outlinedTextFieldColors(
                            focusedBorderColor = MaterialTheme.colorScheme.primary,
                            unfocusedBorderColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f),
                            cursorColor = MaterialTheme.colorScheme.primary
                        )
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Button(
                        onClick = {
                            if (artist.value.isNotEmpty()) {
                                coroutineScope.launch {
                                    isLoading.value = true
                                    errorMessage.value = null
                                    Logger.d("Searching tracks for artist: ${artist.value}")

                                    try {
                                        // First check local database
                                        Logger.d("Checking cache for ${artist.value}")
                                        val localTracks = database.trackDao().getTracksByArtist(artist.value)

                                        if (localTracks.isNotEmpty()) {
                                            Logger.d("Found ${localTracks.size} tracks in local DB")
                                            tracks.value = localTracks
                                        } else {
                                            Logger.d("No local tracks found")

                                            if (isOnline) {
                                                Logger.d("Requesting from Last.fm API")
                                                try {
                                                    val response = api.getTopTracks(artist = artist.value, apiKey = ApiConfig.apiKey)
                                                    Logger.d("Received API response: ${response.toptracks.track.size} tracks")

                                                    val newTracks = response.toptracks.track.map { trackInfo ->
                                                        Track(
                                                            artist = artist.value,
                                                            title = trackInfo.name,
                                                            playcount = trackInfo.playcount,
                                                            listeners = trackInfo.listeners
                                                        )
                                                    }

                                                    if (newTracks.isNotEmpty()) {
                                                        Logger.d("Saving ${newTracks.size} tracks to database")
                                                        newTracks.forEach {
                                                            try {
                                                                database.trackDao().insertTrack(it)
                                                            } catch (e: Exception) {
                                                                Logger.e("Error saving track: ${it.title}", e)
                                                            }
                                                        }
                                                        tracks.value = newTracks
                                                    } else {
                                                        Logger.d("API returned empty tracks list")
                                                        errorMessage.value = "No tracks found for ${artist.value}"
                                                    }
                                                } catch (e: Exception) {
                                                    Logger.e("Error during API request", e)
                                                    errorMessage.value = "Network error: ${e.message}"
                                                }
                                            } else {
                                                Logger.d("No internet connection")
                                                errorMessage.value = "No internet connection. Tracks for ${artist.value} not found in cache."
                                            }
                                        }
                                    } catch (e: Exception) {
                                        Logger.e("General error", e)
                                        errorMessage.value = "An error occurred: ${e.message}"
                                    } finally {
                                        isLoading.value = false
                                    }
                                }
                            } else {
                                errorMessage.value = "Please enter an artist name"
                            }
                        },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primary
                        ),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text("Search Tracks")
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            if (isLoading.value) {
                Box(
                    modifier = Modifier.fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(48.dp)
                    )
                }
            }

            errorMessage.value?.let {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.errorContainer
                    )
                ) {
                    Text(
                        text = it,
                        color = MaterialTheme.colorScheme.onErrorContainer,
                        modifier = Modifier.padding(16.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            if (tracks.value.isNotEmpty()) {
                Text(
                    text = "Found ${tracks.value.size} tracks for ${artist.value}",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onBackground,
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    itemsIndexed(tracks.value) { index, track ->
                        TrackItem(track)
                    }
                }
            }
        }
    }
}

@Composable
fun TrackItem(track: Track) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp)),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.2f)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = track.title.firstOrNull()?.toString() ?: "♪",
                    style = TextStyle(fontWeight = FontWeight.Bold, fontSize = 20.sp),
                    color = MaterialTheme.colorScheme.primary
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = track.title,
                    style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = "by ${track.artist}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(4.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    StatText(label = "Plays", value = track.playcount.toString())
                    StatText(label = "Listeners", value = track.listeners.toString())
                }
            }
        }
    }
}

@Composable
fun StatText(label: String, value: String) {
    Column(horizontalAlignment = Alignment.Start) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
        )
        Text(
            text = formatNumberWithCommas(value),
            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}

fun formatNumberWithCommas(number: String): String {
    return try {
        val longValue = number.toLong()
        if (longValue >= 1_000_000) {
            String.format("%.1fM", longValue / 1_000_000.0)
        } else if (longValue >= 1_000) {
            String.format("%.1fK", longValue / 1_000.0)
        } else {
            longValue.toString()
        }
    } catch (e: NumberFormatException) {
        number
    }
}