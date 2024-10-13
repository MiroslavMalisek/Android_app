package eu.mcomputng.mobv.zadanie.feed

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import eu.mcomputng.mobv.zadanie.R

data class ItemModel(val imageResId: Int, val text: String)

class FeedAdapter: RecyclerView.Adapter<FeedAdapter.FeedViewHolder>() {
    private var items: List<ItemModel> = listOf()

    class FeedViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imageView: ImageView = itemView.findViewById(R.id.item_image)
        val textView: TextView = itemView.findViewById(R.id.item_text)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FeedViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.fragment_feed_item,
            parent, false)
        return FeedViewHolder(view)
    }

    override fun onBindViewHolder(holder: FeedViewHolder, position: Int) {
        val currentItem = items[position]
        holder.imageView.setImageResource(currentItem.imageResId)
        holder.textView.text = currentItem.text
    }

    override fun getItemCount(): Int {
        return items.size
    }

    fun updateItems(newItems: List<ItemModel>) {
        items = newItems
        notifyDataSetChanged()
    }
}