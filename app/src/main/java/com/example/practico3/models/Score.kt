package com.example.practico3.models


    import androidx.room.ColumnInfo
    import androidx.room.Entity
    import androidx.room.PrimaryKey

    @Entity(tableName = "scores")
    data class Score(
        @PrimaryKey(autoGenerate = true) val id: Int = 0,
        @ColumnInfo(name = "player_name") val playerName: String,
        @ColumnInfo(name = "score_value") var scoreValue: Int
    )
