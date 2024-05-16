package com.example.ppo_kursach

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.ppo_kursach.databinding.DealsDecorationItemBinding

class DealsDecorationAdapter(private var dealsDecorationList: List<DealsDecorationClass>): RecyclerView.Adapter<DealsDecorationAdapter.DealsDecorationViewHolder>() {
    private var onClickListener: OnClickListener? = null

    class DealsDecorationViewHolder(val binding: DealsDecorationItemBinding): RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DealsDecorationViewHolder {
        return DealsDecorationViewHolder(
            DealsDecorationItemBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun getItemCount(): Int {
        return dealsDecorationList.size
    }

    override fun onBindViewHolder(holder: DealsDecorationViewHolder, position: Int) {

        val item = dealsDecorationList[position]
        with(holder.binding){
            idDealsDecoration.text = item.idDealsDecoration.toString()
            idDeal.text = item.idDeal.toString()
            idDecoration.text = item.idDecoration.toString()
            quantity.text = item.quantity.toString()
            price.text = item.price.toString()

        }
        holder.itemView.setOnClickListener {
            if (onClickListener != null) {
                onClickListener!!.onClick(position, item )
            }
        }
    }

    interface OnClickListener {
        fun onClick(position: Int, model: DealsDecorationClass)
    }

    fun setOnClickListener(onClickListener: OnClickListener) {
        this.onClickListener = onClickListener
    }

}