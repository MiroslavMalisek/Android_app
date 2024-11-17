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
data class ItemModel(val id: Int, val imageResId: Int, val name: String, val updated: String){
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
        if (name != other.name) return false
        if (updated != other.updated) return false

        return true
    }

    override fun hashCode(): Int {
        var result = id
        result = 31 * result + imageResId
        result = 31 * result + name.hashCode()
        result = 31 * result + updated.hashCode()
        return result
    }
}

class FeedAdapter: RecyclerView.Adapter<FeedAdapter.FeedViewHolder>() {
    private var items: List<ItemModel> = listOf()

    class FeedViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imageView: ImageView = itemView.findViewById(R.id.user_image)
        val nameView: TextView = itemView.findViewById(R.id.user_name)
        val updatedView: TextView = itemView.findViewById(R.id.user_updated)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FeedViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.fragment_feed_item,
            parent, false)
        return FeedViewHolder(view)
    }

    override fun onBindViewHolder(holder: FeedViewHolder, position: Int) {
        val currentItem = items[position]
        holder.imageView.setImageResource(currentItem.imageResId)
        holder.nameView.text = currentItem.name
        holder.updatedView.text = currentItem.updated
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