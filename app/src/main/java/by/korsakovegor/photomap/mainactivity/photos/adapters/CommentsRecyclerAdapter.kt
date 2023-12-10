package by.korsakovegor.photomap.mainactivity.photos.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import by.korsakovegor.photomap.R
import by.korsakovegor.photomap.models.CommentDtoOut
import by.korsakovegor.photomap.models.ImageDtoOut
import com.squareup.picasso.Picasso

class CommentsRecyclerAdapter() :
    RecyclerView.Adapter<CommentsRecyclerAdapter.ViewHolder>() {

    private val comments: ArrayList<CommentDtoOut> = arrayListOf()

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val commentText: TextView = itemView.findViewById(R.id.commentText)
        val commentDate: TextView = itemView.findViewById(R.id.commentDate)
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
        val currentComments = comments[position]
        holder.commentText.text = currentComments.text
        holder.commentDate.text = currentComments.time
    }

    fun updateData(newComments: ArrayList<CommentDtoOut>){
        comments.clear()
        comments.addAll(newComments.reversed())
        notifyDataSetChanged()
    }

    fun addItem(item: CommentDtoOut){
        comments.add(0, item)
        notifyItemInserted(0)
    }
}