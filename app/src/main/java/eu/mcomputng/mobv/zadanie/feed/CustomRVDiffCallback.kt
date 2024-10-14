package eu.mcomputng.mobv.zadanie.feed

import androidx.recyclerview.widget.DiffUtil

class CustomRVDiffCallback(
    private val oldList: List<ItemModel>,
    private val newList: List<ItemModel>
): DiffUtil.Callback() {
    override fun getOldListSize() = oldList.size
    override fun getNewListSize() = newList.size
    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldList[oldItemPosition].id == newList[newItemPosition].id
    }
    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldList[oldItemPosition] == newList[newItemPosition]
    }
}