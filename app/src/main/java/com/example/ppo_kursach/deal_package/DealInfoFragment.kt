package com.example.ppo_kursach.deal_package

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.core.os.bundleOf
import androidx.fragment.app.setFragmentResult
import androidx.navigation.findNavController
import androidx.navigation.fragment.navArgs
import com.example.ppo_kursach.R
import com.google.android.material.appbar.MaterialToolbar

class DealInfoFragment : Fragment() {

     private val args: DealInfoFragmentArgs by navArgs()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val deal = args.deal
        val view = inflater.inflate(R.layout.fragment_deal_info, container, false)
        val idDeal = view.findViewById<TextView>(R.id.id_deal)
        val idUser = view.findViewById<TextView>(R.id.id_user)
        val date = view.findViewById<TextView>(R.id.date)
        val address =  view.findViewById<TextView>(R.id.address)
        val client = view.findViewById<TextView>(R.id.client)
        val clientNumber = view.findViewById<TextView>(R.id.client_number)
        val price = view.findViewById<TextView>(R.id.price)
        val difficulty = view.findViewById<TextView>(R.id.difficulty)
        val status = view.findViewById<TextView>(R.id.status)
        val comment = view.findViewById<TextView>(R.id.comment)

        idDeal.text = deal.idDeal.toString()
        idUser.text = deal.idUser.toString()
        date.text = deal.date
        address.text = deal.address
        client.text = deal.client
        clientNumber.text = deal.clientNumber
        price.text = deal.price.toString()
        difficulty.text = deal.difficulty.toString()
        status.text = deal.status.toString()
        comment.text = deal.comment

        view.findViewById<Button>(R.id.save_deal).setOnClickListener{
            val model = DealClass(
                deal.idDeal,
                idUser.text.toString().toInt(),
                date.text.toString(),
                address.text.toString(),
                client.text.toString(),
                clientNumber.text.toString(),
                price.text.toString().toInt(),
                difficulty.text.toString().toInt(),
                status.text.toString().toInt(),
                comment.text.toString()
            )
            setFragmentResult(
                "request_key",
                bundleOf("save_key" to model)
            )
            view.findNavController().navigateUp()
        }

        view.findViewById<Button>(R.id.delete_deal).setOnClickListener{
            val model = DealClass(deal.idDeal)
            setFragmentResult(
                "request_key",
                bundleOf("delete_key" to model)
            )
            view.findNavController().navigateUp()
        }


        view.findViewById<Button>(R.id.complete_deal).setOnClickListener{
            val model = DealClass(
                deal.idDeal,
                idUser.text.toString().toInt(),
                date.text.toString(),
                address.text.toString(),
                client.text.toString(),
                clientNumber.text.toString(),
                price.text.toString().toInt(),
                difficulty.text.toString().toInt(),
                status.text.toString().toInt(),
                comment.text.toString()
            )
            setFragmentResult(
                "request_key",
                bundleOf("complete_key" to model)
            )
            view.findNavController().navigateUp()
        }

        view.findViewById<MaterialToolbar>(R.id.toolbar).setOnMenuItemClickListener {
            when (it.itemId) {
                R.id.deals_decoration -> {
                    val action =
                        DealInfoFragmentDirections.actionDealInfoFragmentToDealsDecorationFragment(deal)
                    view.findNavController().navigate(action)
                    true
                }
                R.id.deals_user -> {
                    val action =
                        DealInfoFragmentDirections.actionDealInfoFragmentToDealsUserFragment(deal)
                    view.findNavController().navigate(action)
                    true
                }
                else -> false
            }
        }
        return view
    }
}