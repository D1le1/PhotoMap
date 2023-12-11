package by.korsakovegor.photomap.models.interfaces

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import by.korsakovegor.photomap.models.ImageDtoOut

@Dao
interface ImagesDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertNewImages(images: ArrayList<ImageDtoOut>)

    @Insert
    fun insertNewImage(image: ImageDtoOut)

    @Delete
    fun deleteImage(image: ImageDtoOut)

    @Query("Select * from images")
    fun getImages():List<ImageDtoOut>
}