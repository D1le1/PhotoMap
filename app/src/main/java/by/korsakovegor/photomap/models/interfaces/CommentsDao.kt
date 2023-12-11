package by.korsakovegor.photomap.models.interfaces

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import by.korsakovegor.photomap.models.CommentDtoOut

@Dao
interface CommentsDao {
    @Insert
    fun insertNewComment(comment: CommentDtoOut)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertNewComments(comments: List<CommentDtoOut>)

    @Delete
    fun deleteComment(comment: CommentDtoOut)

    @Query("Select * from comments")
    fun getComments():List<CommentDtoOut>
}