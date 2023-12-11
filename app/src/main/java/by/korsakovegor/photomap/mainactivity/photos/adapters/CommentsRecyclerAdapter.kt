package by.korsakovegor.photomap.mainactivity.photos.adapters

import android.util.Log
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.View.OnTouchListener
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import by.korsakovegor.photomap.R
import by.korsakovegor.photomap.models.CommentDtoIn
import by.korsakovegor.photomap.models.CommentDtoOut
import by.korsakovegor.photomap.models.ImageDtoOut
import com.squareup.picasso.Picasso

class CommentsRecyclerAdapter() :
    RecyclerView.Adapter<CommentsRecyclerAdapter.ViewHolder>() {

    private val comments: ArrayList<CommentDtoOut> = arrayListOf()
    private var onCommentLongClickListener: OnCommentLongClickListener? = null

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val commentText: TextView = itemView.findViewById(R.id.commentText)
        val commentDate: TextView = itemView.findViewById(R.id.commentDate)
        val commentCard: CardView = itemView.findViewById(R.id.commentCard)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemView =
            LayoutInflater.from(parent.context).inflate(R.layout.comment_item, parent, false)
        return ViewHolder(itemView)
    }

    override fun getItemCount(): Int {
        return comments.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val currentComment = comments[position]
        holder.commentText.text = currentComment.text
        holder.commentDate.text = currentComment.time

        holder.commentCard.setOnLongClickListener {
            onCommentLongClickListener?.onCommentLongClick(currentComment, position)
            return@setOnLongClickListener false
        }
    }

    fun updateData(newComments: ArrayList<CommentDtoOut>) {
        comments.clear()
        comments.addAll(newComments.reversed())
        notifyDataSetChanged()
    }

    fun addData(newComments: ArrayList<CommentDtoOut>){
        val pos = comments.size
        comments.addAll(newComments)
        notifyItemRangeInserted(pos, comments.size)
    }

    fun addItem(item: CommentDtoOut) {
        comments.add(0, item)
        notifyItemInserted(0)
    }

    fun deleteItem(pos: Int)
    {
        comments.removeAt(pos)
        notifyItemRemoved(pos)
    }

    fun setOnCommentLongClick(listener: OnCommentLongClickListener){
        onCommentLongClickListener = listener
    }

    interface OnCommentLongClickListener {
        fun onCommentLongClick(commentDtoOut: CommentDtoOut, pos: Int)
    }
}