package com.project1

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface TrackDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTrack(track: Track)

    @Query("SELECT * FROM tracks WHERE artist LIKE :artistName LIMIT 100")
    suspend fun getTracksByArtist(artistName: String): List<Track>

    @Query("SELECT COUNT(*) FROM tracks WHERE artist LIKE :artistName")
    suspend fun checkIfArtistExists(artistName: String): Int
}
