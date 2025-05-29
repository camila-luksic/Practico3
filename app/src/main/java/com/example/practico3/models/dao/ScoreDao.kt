package com.example.practico3.models.dao

    import androidx.room.Dao
    import androidx.room.Insert
    import androidx.room.Query
    import com.example.practico3.models.Score

@Dao
    interface ScoreDao {
        @Insert
        suspend fun insert(score: Score)

        @Query("SELECT * FROM scores WHERE score_value>0 ORDER BY score_value DESC ")
        suspend fun getAllScores(): List<Score>

        @Query("SELECT SUM(score_value) FROM scores WHERE player_name = :playerName")
        suspend fun getTotalScoreForPlayer(playerName: String): Int?

    }

