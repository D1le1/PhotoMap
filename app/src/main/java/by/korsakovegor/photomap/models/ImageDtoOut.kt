package by.korsakovegor.photomap.models

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable

@Entity(tableName = "images")
data class ImageDtoOut(
    @PrimaryKey(autoGenerate = false)
    val id: Int,
    @ColumnInfo(name = "url")
    val url: String,
    @ColumnInfo(name="date")
    val date: Long,
    @ColumnInfo(name = "lat")
    val lat: Double,
    @ColumnInfo(name = "lng")
    val lng: Double
): Serializable