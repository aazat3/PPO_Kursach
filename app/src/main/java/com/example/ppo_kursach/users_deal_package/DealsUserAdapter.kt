package com.example.ppo_kursach.users_deal_package

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.ppo_kursach.databinding.DealItemBinding
import com.example.ppo_kursach.databinding.UserItemBinding
import com.example.ppo_kursach.deal_package.DealClass
import com.example.ppo_kursach.user_package.UserClass

class DealsUserAdapter(private var dealsUserList: List<UserClass>): RecyclerView.Adapter<DealsUserAdapter.DealsUserViewHolder>() {
    private var onClickListener: OnClickListener? = null

    fun filterList(filterList: ArrayList<UserClass>) {
        dealsUserList = filterList
        notifyDataSetChanged()
    }

    class DealsUserViewHolder(val binding: UserItemBinding): RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DealsUserViewHolder {
        return DealsUserViewHolder(
            UserItemBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun getItemCount(): Int {
        return dealsUserList.size
    }

    override fun onBindViewHolder(holder: DealsUserViewHolder, position: Int) {

        val item = dealsUserList[position]
        with(holder.binding){
            idUser.text = item.idUser.toString()
            name.text = item.name
            type.text = item.type
            login.text = item.login
            userNumber.text = item.userNumber
        }

        holder.itemView.setOnClickListener {
            if (onClickListener != null) {
                onClickListener!!.onClick(position, item )
            }
        }
    }

    interface OnClickListener {
        fun onClick(position: Int, model: UserClass)
    }

    fun setOnClickListener(onClickListener: OnClickListener) {
        this.onClickListener = onClickListener
    }

}