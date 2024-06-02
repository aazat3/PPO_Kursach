package com.example.ppo_kursach.deals_decoration_package

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.ppo_kursach.decoration_package.DecorationClass
import com.example.ppo_kursach.databinding.DealsDecorationItemBinding
import com.example.ppo_kursach.deal_package.DealClass
import com.google.firebase.storage.FirebaseStorage

class DealsDecorationAdapter(private var dealsDecorationList: List<DecorationClass>): RecyclerView.Adapter<DealsDecorationAdapter.DealsDecorationViewHolder>() {
    private var onClickListener: OnClickListener? = null

    fun filterList(filterList: ArrayList<DecorationClass>) {
        dealsDecorationList = filterList
        notifyDataSetChanged()
    }

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
            idDecoration.text = item.idDecoration.toString()
            name.text = item.name
            type.text = item.type.toString()
            price.text = item.price.toString()
            quantity.text = item.quantity.toString()
            val storage = FirebaseStorage.getInstance().getReference("Decoration")
            if (item.photo != ""){
                val gsReference = storage.child(item.photo)
                Glide.with(photo.context)
                    .load(gsReference)
                    .into(photo)
            }
        }

        holder.itemView.setOnClickListener {
            if (onClickListener != null) {
                onClickListener!!.onClick(position, item )
            }
        }
    }

    interface OnClickListener {
        fun onClick(position: Int, model: DecorationClass)
    }

    fun setOnClickListener(onClickListener: OnClickListener) {
        this.onClickListener = onClickListener
    }

}