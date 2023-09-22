package com.example.sqlite
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class BoxAdapter2: RecyclerView.Adapter<BoxAdapter2.BoxViewHolder>() {
    private var stdList:ArrayList<BoxModel2> = ArrayList()
    private var onClickItem: ((BoxModel2) -> Unit)? =null

    fun addItems(items: ArrayList<BoxModel2>)
    {
        this.stdList=items
        notifyDataSetChanged()
    }

    fun setOnClickItem(callback:(BoxModel2) -> Unit)
    {
        this.onClickItem = callback
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int)= BoxViewHolder(
        LayoutInflater.from(parent.context).inflate(R.layout.card_items_std,parent,false)
    )

    override fun getItemCount(): Int {
        return stdList.size
    }

    override fun onBindViewHolder(holder: BoxViewHolder, position: Int) {
        val std =stdList[position]
        holder.bindView(std)
        holder.itemView.setOnClickListener{onClickItem?.invoke(std)}
    }

    class BoxViewHolder (view:View):RecyclerView.ViewHolder(view){
        private var name=view.findViewById<TextView>(R.id.tvName)
        var pointer = view.findViewById<ImageView>(R.id.pointer_card1)

        fun bindView(std: BoxModel2)
        {
            name.text=std.name
            pointer.setImageResource(R.drawable.lightbulb_insidebox)
        }
    }



}