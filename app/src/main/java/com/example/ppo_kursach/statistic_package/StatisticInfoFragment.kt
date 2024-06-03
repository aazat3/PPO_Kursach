package com.example.ppo_kursach.statistic_package

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Button
import android.widget.TextView
import androidx.navigation.findNavController
import androidx.navigation.fragment.navArgs
import com.example.ppo_kursach.R
import com.example.ppo_kursach.deal_package.DealClass
import com.example.ppo_kursach.deal_package.DealInfoFragmentDirections
import com.example.ppo_kursach.statistic_package.StatisticFragmentArgs
import com.example.ppo_kursach.user_package.UserClass
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class StatisticInfoFragment : Fragment() {

    private val args: StatisticFragmentArgs by navArgs()
    lateinit var deal: DealClass
    lateinit var userName: String
    lateinit var nameUser: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        deal = args.deal
        userName = ""

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        var difficultyInt = deal.difficulty
        var statusInt = deal.status

        val view = inflater.inflate(R.layout.fragment_statistic_info, container, false)
        val idDeal = view.findViewById<TextView>(R.id.id_deal)
        nameUser = view.findViewById<TextView>(R.id.id_user)
        val date = view.findViewById<TextView>(R.id.date)
        val address =  view.findViewById<TextView>(R.id.address)
        val client = view.findViewById<TextView>(R.id.client)
        val clientNumber = view.findViewById<TextView>(R.id.client_number)
        val price = view.findViewById<TextView>(R.id.price)
//        val difficulty = view.findViewById<TextView>(R.id.difficulty)
//        val status = view.findViewById<TextView>(R.id.status)
        val comment = view.findViewById<TextView>(R.id.comment)

        Firebase.database.getReference("UserClass").child(deal.idUser.toString()).get().addOnSuccessListener{
            userName = it.getValue(UserClass::class.java)?.name.toString()
            nameUser.text = userName
        }

        val difficultyArray = resources.getStringArray(R.array.difficulty_array)
        var difficulty = view.findViewById<TextView>(R.id.difficulty)
        difficulty.setText(when(deal.difficulty){
            1 -> "Легко"
            2 -> "Средне"
            3 -> "Сложно"
            4 -> "Очень сложно"
            else -> ""
        })

        val statusArray = resources.getStringArray(R.array.status_array)
        var status = view.findViewById<TextView>(R.id.status)
        status.setText(when(deal.status){
            1 -> "Планируется"
            2 -> "Заключена"
            3 -> "Оплачена"
            4 -> "Исполнена"
            else -> ""
        })


        idDeal.text = deal.idDeal.toString()
//        idUser.text = deal.idUser.toString()
//        nameUser.text = userName
        date.text = deal.date
        address.text = deal.address
        client.text = deal.client
        clientNumber.text = deal.clientNumber
        price.text = deal.price.toString()
//        difficulty.setText(deal.difficulty.toString())
//        status.setText(deal.status.toString())
        comment.text = deal.comment

        view.findViewById<Button>(R.id.deals_decoration).setOnClickListener{
            val action =
                StatisticInfoFragmentDirections.actionStatisticInfoFragmentToStatisticsDecorationFragment(deal)
            view.findNavController().navigate(action)
        }

        return view
    }

}