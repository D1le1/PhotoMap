package by.korsakovegor.photomap.mainactivity.photos.adapters

import android.content.Intent
import android.os.Bundle
import android.transition.ChangeBounds
import android.transition.TransitionManager
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import by.korsakovegor.photomap.R
import by.korsakovegor.photomap.mainactivity.map.MapFragment
import by.korsakovegor.photomap.models.ImageDtoOut
import com.squareup.picasso.Picasso

class RecyclerAdapter(private val images: ArrayList<ImageDtoOut>) :
    RecyclerView.Adapter<RecyclerAdapter.ViewHolder>() {

    private var onItemClickListener: OnItemClickListener? = null

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val image = itemView.findViewById<ImageView>(R.id.photoImage)
        val text: TextView = itemView.findViewById(R.id.photoText)
        val card: CardView = itemView.findViewById(R.id.photoCard)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemView =
            LayoutInflater.from(parent.context).inflate(R.layout.image_item, parent, false)
        return ViewHolder(itemView)
    }

    override fun getItemCount(): Int {
        return images.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val currentImage = images[position]
        Picasso.get().load(currentImage.url).into(holder.image)
//        holder.text.text = currentImage.formattedDate
        Log.d("D1le", currentImage.formattedDate)
        holder.card.setOnClickListener {
            onItemClickListener?.onClick(it)
        }
    }

    fun setOnItemClickListener(listener: OnItemClickListener) {
        onItemClickListener = listener
    }

    interface OnItemClickListener {
        fun onClick(v: View)
    }
}