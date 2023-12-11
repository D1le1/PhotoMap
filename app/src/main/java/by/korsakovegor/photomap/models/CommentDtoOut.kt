package by.korsakovegor.photomap.models

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "comments")
data class CommentDtoOut(
    @PrimaryKey(autoGenerate = false)
    val id: Int,
    @ColumnInfo(name = "date")
    val date: Long,
    @ColumnInfo(name = "text")
    val text: String
)