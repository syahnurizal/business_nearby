package com.vorto.businessnearby.ui.search.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.vorto.businessnearby.databinding.RowBusinessBinding
import com.vorto.businessnearby.model.BusinessModel
import java.util.*
import kotlin.collections.ArrayList


class BusinessAdapter(private var data: ArrayList<BusinessModel>, var mListener: (String,BusinessModel) -> Unit) :
    RecyclerView.Adapter<BusinessAdapter.ItemHolder>() {

    private var mContext: Context? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemHolder {
        mContext = parent.context
        val itemBinding = RowBusinessBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ItemHolder(itemBinding)
    }

    override fun getItemCount(): Int {
        return data.size
    }

    override fun onBindViewHolder(holder: ItemHolder, position: Int) {
        val item : BusinessModel = data[position]

        //Bind each data to item holder
        holder.bindView(item, mContext)

    }

    fun addItems(list: ArrayList<BusinessModel>){
        if(list.size > 0) {
            data.addAll(list)
            notifyItemRangeInserted(data.size - list.size, list.size)
        }
    }

    fun refreshList(list: ArrayList<BusinessModel>){
        data = list
        notifyDataSetChanged()
    }


    /*
    *       Item holder class
    * */
    inner class ItemHolder(private val view: RowBusinessBinding) : RecyclerView.ViewHolder(view.root) {

        private var model: BusinessModel? = null

        init {

        }

        fun bindView(item: BusinessModel, context : Context?) {
            this.model = item

            val distance =  item.distance
            val distanceKM = distance?.div(1000) ?: 0

            view.tvName.text = item.name ?: "-"
            view.tvRating.text = item.rating.toString() ?: "0"
            view.tvDistance.text = "${String.format("%.2f", distanceKM)} KM"

            view.root.setOnClickListener {
                mListener.invoke("view",item)
            }
        }
    }

}