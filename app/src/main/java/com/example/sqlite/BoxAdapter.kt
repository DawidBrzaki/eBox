package com.example.sqlite
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import android.widget.ImageView
import android.widget.TextView
import androidx.compose.ui.text.toLowerCase
import androidx.recyclerview.widget.RecyclerView

class BoxAdapter: RecyclerView.Adapter<BoxAdapter.BoxViewHolder>(),Filterable {
    private var stdList: ArrayList<BoxModel> = ArrayList()
    private var onClickItem: ((BoxModel) -> Unit)? = null
    private var onLongClickItem: ((BoxModel) -> Unit)? = null
    private var filteredBoxList: List<BoxModel> = ArrayList()

    fun addItems(items: ArrayList<BoxModel>) {
        this.stdList = items
        notifyDataSetChanged()
    }
    fun setOnClickItem(callback: (BoxModel) -> Unit) {
        this.onClickItem = callback
    }

    fun setOnLongClickItem(callback: (BoxModel) -> Unit) {
        this.onLongClickItem = callback
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = BoxViewHolder(
        LayoutInflater.from(parent.context).inflate(R.layout.card_items_std, parent, false)
    )

    override fun getItemCount(): Int {
        return filteredBoxList.size
    }

    override fun onBindViewHolder(holder: BoxViewHolder, position: Int) {
        val std = filteredBoxList[position]
        holder.bindView(std)
        holder.itemView.setOnClickListener { onClickItem?.invoke(std) }

        holder.itemView.setOnLongClickListener {
            onLongClickItem?.invoke(std)
            true
        }
    }
    fun getFilteredItemByName(nameToFind: String): BoxModel? {
        return filteredBoxList.firstOrNull { it.name == nameToFind }
    }

    class BoxViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var name = view.findViewById<TextView>(R.id.tvName)
        var pointer = view.findViewById<ImageView>(R.id.pointer_card1)


        fun bindView(std: BoxModel) {
            name.text = std.name
            pointer.setImageResource(R.drawable.box_icon)
        }
    }
    override fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(constraint: CharSequence?): FilterResults {
                val filteredResults = ArrayList<BoxModel>()
                if (constraint.isNullOrEmpty()) {
                    filteredResults.addAll(stdList)
                } else {
                    val filterPattern = constraint.toString().toLowerCase().trim()
                    for (item in stdList) {
                        if (item.name.toLowerCase().contains(filterPattern)) {
                            filteredResults.add(item)
                        }
                    }
                }
                val filterResults = FilterResults()
                filterResults.values = filteredResults
                return filterResults
            }
            override fun publishResults(constraint: CharSequence?, results: FilterResults?) {

                filteredBoxList = results?.values as ArrayList<BoxModel>
                notifyDataSetChanged()
            }
        }
    }
}