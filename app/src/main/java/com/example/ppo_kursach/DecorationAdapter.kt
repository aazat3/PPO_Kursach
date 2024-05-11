package com.example.ppo_kursach

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.ppo_kursach.databinding.DecorationItemBinding
import com.firebase.ui.storage.images.FirebaseImageLoader
import com.google.firebase.storage.FirebaseStorage


class DecorationAdapter(private var decorationList: List<DecorationClass>): RecyclerView.Adapter<DecorationAdapter.DecorationViewHolder>() {
    private var onClickListener: OnClickListener? = null

    class DecorationViewHolder(val binding: DecorationItemBinding): RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DecorationViewHolder {
        return DecorationViewHolder(
            DecorationItemBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun getItemCount(): Int {
        return decorationList.size
    }

    override fun onBindViewHolder(holder: DecorationViewHolder, position: Int) {

        val item = decorationList[position]
        with(holder.binding){
            id.text = item.idDecoration.toString()
            name.text = item.name
            type.text = item.type.toString()
            price.text = item.price.toString()

//            val storage = FirebaseStorage.getInstance().getReference("Decoration")
//            val gsReference = storage.child(item.photo)
//            GlideApp.with(photo.context)
//                .load(gsReference)
//                .into(photo)

              Glide.with(photo.context)
                .load(item.photo)
                .into(photo)

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