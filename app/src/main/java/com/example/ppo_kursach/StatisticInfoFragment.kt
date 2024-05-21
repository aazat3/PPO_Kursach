package com.example.ppo_kursach

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.navigation.fragment.navArgs

class StatisticInfoFragment : Fragment() {

    private val args: StatisticFragmentArgs by navArgs()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        val deal = args.deal
        val view = inflater.inflate(R.layout.fragment_statistic_info, container, false)
        val idDeal = view.findViewById<TextView>(R.id.id_deal)
        val idUser = view.findViewById<TextView>(R.id.id_user)
        val date = view.findViewById<TextView>(R.id.date)
        val address =  view.findViewById<TextView>(R.id.address)
        val client = view.findViewById<TextView>(R.id.client)
        val clientNumber = view.findViewById<TextView>(R.id.client_number)
        val price = view.findViewById<TextView>(R.id.price)
        val difficulty = view.findViewById<TextView>(R.id.difficulty)
        val status = view.findViewById<TextView>(R.id.status)

        idDeal.text = deal.idDeal.toString()
        idUser.text = deal.idUser.toString()
        date.text = deal.date
        address.text = deal.address
        client.text = deal.client
        clientNumber.text = deal.clientNumber
        price.text = deal.price.toString()
        difficulty.text = deal.difficulty.toString()
        status.text = deal.status.toString()

        return view
    }

    companion object {
        fun newInstance(param1: String, param2: String) =
            StatisticInfoFragment().apply {
                arguments = Bundle().apply {

                }
            }
    }
}