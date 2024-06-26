package com.example.ppo_kursach.deal_package

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.ppo_kursach.databinding.DealItemBinding
import com.example.ppo_kursach.user_package.UserClass
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class DealAdapter(private var dealList: List<DealClass>): RecyclerView.Adapter<DealAdapter.DealViewHolder>() {
    private var onClickListener: OnClickListener? = null

    fun filterList(filterList: ArrayList<DealClass>) {
        dealList = filterList
        notifyDataSetChanged()
    }

    class DealViewHolder(val binding: DealItemBinding): RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DealViewHolder {
        return DealViewHolder(
            DealItemBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun getItemCount(): Int {
        return dealList.size
    }

    override fun onBindViewHolder(holder: DealViewHolder, position: Int) {

        val item = dealList[position]
        var userName = ""

        with(holder.binding){
            idDeal.text = item.idDeal.toString()
            date.text = item.date
            address.text = item.address
//            user.text = item.idUser.toString()
            client.text = item.client
            price.text = item.price.toString()

            Firebase.database.getReference("UserClass").child(item.idUser.toString()).get().addOnSuccessListener{
                userName = it.getValue(UserClass::class.java)?.name.toString()
                user.text = userName
            }

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