package eu.mcomputng.mobv.zadanie.fragments.feed

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import eu.mcomputng.mobv.zadanie.R

//data class ItemModel(val imageResId: Int, val text: String)
data class ItemModel(val id: Int, val imageResId: Int, val text: String){
    override fun equals(other: Any?): Boolean {
        if (this === other){
            return true
        }
        if (javaClass != other?.javaClass) {
            return false
        }
        other as ItemModel
        if (id != other.id) return false
        if (imageResId != other.imageResId) return false
        if (text != other.text) return false

        return true
    }

    override fun hashCode(): Int {
        var result = id
        result = 31 * result + imageResId
        result = 31 * result + text.hashCode()
        return result
    }
}

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
        /*items = newItems
        notifyDataSetChanged()
        */

        val diffCallback = CustomRVDiffCallback(items, newItems)
        val diffResult = DiffUtil.calculateDiff(diffCallback)

        items = newItems
        diffResult.dispatchUpdatesTo(this)
    }

    fun addItem(newItem: ItemModel){
        val newItems = items + newItem
        this.updateItems(newItems)
    }
}