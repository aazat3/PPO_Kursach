package com.example.ppo_kursach.users_deal_package

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.ppo_kursach.databinding.DealItemBinding
import com.example.ppo_kursach.databinding.DealsDecorationItemBinding
import com.example.ppo_kursach.deal_package.DealClass
import com.example.ppo_kursach.decoration_package.DecorationClass
import com.google.firebase.storage.FirebaseStorage

class UsersDealAdapter(private var usersDealList: List<DealClass>): RecyclerView.Adapter<UsersDealAdapter.UsersDealViewHolder>() {
    private var onClickListener: OnClickListener? = null

    class UsersDealViewHolder(val binding: DealItemBinding): RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UsersDealViewHolder {
        return UsersDealViewHolder(
            DealItemBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun getItemCount(): Int {
        return usersDealList.size
    }

    override fun onBindViewHolder(holder: UsersDealViewHolder, position: Int) {

        val item = usersDealList[position]
        with(holder.binding){
            idDeal.text = item.idDeal.toString()
            date.text = item.date
            address.text = item.address
            user.text = item.idUser.toString()
            client.text = item.client
            price.text = item.price.toString()

        }

        holder.itemView.setOnClickListener {
            if (onClickListener != null) {
                onClickListener!!.onClick(position, item )
            }
        }
    }

    interface OnClickListener {
        fun onClick(position: Int, model: DealClass)
    }

    fun setOnClickListener(onClickListener: OnClickListener) {
        this.onClickListener = onClickListener
    }

}