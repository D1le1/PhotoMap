package by.korsakovegor.photomap.mainactivity.photos.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import by.korsakovegor.photomap.R
import by.korsakovegor.photomap.models.ImageDtoOut
import com.squareup.picasso.Picasso

class ImageRecyclerAdapter :
    RecyclerView.Adapter<ImageRecyclerAdapter.ViewHolder>() {

    private var onItemClickListener: OnImageClickListener? = null
    private var onItemLongClickListener: OnImageLongClickListener? = null
    private val images: ArrayList<ImageDtoOut> = arrayListOf()

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val image: ImageView = itemView.findViewById(R.id.photoImage)
        val date: TextView = itemView.findViewById(R.id.photoText)
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
        holder.date.text = currentImage.formattedDate
        holder.card.setOnClickListener {
            onItemClickListener?.onImageClick(it, currentImage)
        }
        holder.card.setOnLongClickListener {
            onItemLongClickListener?.onImageLongClick(currentImage, position)
            return@setOnLongClickListener false
        }
    }

    fun setOnItemClickListener(listener: OnImageClickListener) {
        onItemClickListener = listener
    }

    fun setOnItemLongClickListener(listener: OnImageLongClickListener) {
        onItemLongClickListener = listener
    }

    fun updateData(newImages: ArrayList<ImageDtoOut>) {
        images.clear()
        images.addAll(newImages)
        notifyDataSetChanged()
    }

    fun addData(newImages: ArrayList<ImageDtoOut>)
    {
        val pos = images.size
        images.addAll(newImages)
        notifyItemRangeChanged(pos, images.size)
    }

    fun addItem(image: ImageDtoOut) {
        images.add(0, image)
        notifyItemInserted(0)
    }

    fun deleteItem(pos: Int) {
        if (pos == images.size)
            images.removeAt(images.size - 1)
        else
            images.removeAt(pos)
        notifyItemRemoved(pos)
    }

    interface OnImageClickListener {
        fun onImageClick(v: View, image: ImageDtoOut)
    }

    interface OnImageLongClickListener {
        fun onImageLongClick(image: ImageDtoOut, pos: Int)
    }
}