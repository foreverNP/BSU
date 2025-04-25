package com.project1

import androidx.room.*
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

@Entity(tableName = "tracks")
data class Track(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val artist: String,
    val title: String,
    val playcount: Int,
    val listeners: Int
)

@Database(entities = [Track::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun trackDao(): TrackDao
}

interface LastFmApi {
    @GET("2.0/")
    suspend fun getTopTracks(
        @Query("method") method: String = "artist.gettoptracks",
        @Query("artist") artist: String,
        @Query("api_key") apiKey: String,
        @Query("format") format: String = "json"
    ): LastFmResponse
}

data class LastFmResponse(val toptracks: TopTracks)
data class TopTracks(val track: List<TrackInfo>)
data class TrackInfo(val name: String, val playcount: Int, val listeners: Int)

fun provideLastFmApi(): LastFmApi {
    val retrofit = Retrofit.Builder()
        .baseUrl("https://ws.audioscrobbler.com/")
        .addConverterFactory(GsonConverterFactory.create())
        .build()
    return retrofit.create(LastFmApi::class.java)
}
