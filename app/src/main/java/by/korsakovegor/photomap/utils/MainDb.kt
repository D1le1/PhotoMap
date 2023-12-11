package by.korsakovegor.photomap.utils

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import by.korsakovegor.photomap.models.CommentDtoOut
import by.korsakovegor.photomap.models.ImageDtoOut
import by.korsakovegor.photomap.models.interfaces.CommentsDao
import by.korsakovegor.photomap.models.interfaces.ImagesDao

@Database (entities = [ImageDtoOut::class, CommentDtoOut::class], version = 2)
abstract class MainDb: RoomDatabase() {
    abstract fun getImagesDao(): ImagesDao
    abstract fun getCommentsDao(): CommentsDao

    companion object{
        fun getInstance(context: Context): MainDb{
            return Room.databaseBuilder(
                context.applicationContext,
                MainDb::class.java,
                "photoMapDb.db"
            ).build()
        }
    }
}