package com.example.ppo_kursach.deal_package

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView.OnItemClickListener
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResult
import androidx.fragment.app.setFragmentResultListener
import androidx.navigation.findNavController
import androidx.navigation.fragment.navArgs
import com.example.ppo_kursach.R
import com.example.ppo_kursach.deals_decoration_package.DealsDecorationClass
import com.example.ppo_kursach.decoration_package.DecorationClass
import com.example.ppo_kursach.user_package.UserClass
import com.example.ppo_kursach.users_deal_package.DealsUserFragmentDirections
import com.example.ppo_kursach.users_deal_package.UsersDealClass
import com.google.android.material.appbar.MaterialToolbar
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import kotlin.properties.Delegates


class DealInfoFragment : Fragment() {

    private val args: DealInfoFragmentArgs by navArgs()
    var idUser by Delegates.notNull<Int>()
    lateinit var userName: String
    lateinit var deal: DealClass
    lateinit var nameUser: TextView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        deal = args.deal
        userName = ""
        idUser = deal.idUser

        setFragmentResultListener("deals_user_key") { key, bundle ->
            val returnedAddUser = bundle.getParcelable<UserClass>("add_deals_user_key")
            if (returnedAddUser != null) {
                Firebase.database.getReference("UserClass").child(deal.idUser.toString()).get().addOnSuccessListener{
                    idUser = returnedAddUser.idUser
                    userName = it.getValue(UserClass::class.java)?.name.toString()
                    nameUser.text = userName
                }
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        var difficultyInt = deal.difficulty
        var statusInt = deal.status

        val view = inflater.inflate(R.layout.fragment_deal_info, container, false)
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
        var difficulty = view.findViewById<AutoCompleteTextView>(R.id.difficulty)
        difficulty.setText(when(deal.difficulty){
            1 -> "Легко"
            2 -> "Средне"
            3 -> "Сложно"
            4 -> "Очень сложно"
            else -> ""
        })
        if (difficulty != null) {
            var difficultyAdapter = ArrayAdapter(
                requireContext(),
                android.R.layout.simple_list_item_1, difficultyArray)
            difficulty.setAdapter(difficultyAdapter)
            difficulty.setOnItemClickListener { parent, view, position, id ->
                difficultyInt = when(difficultyArray[position]){
                    "Легко" -> 1
                    "Средне" -> 2
                    "Сложно" -> 3
                    "Очень сложно" -> 4
                    else -> 0
                }
            }
        }

        val statusArray = resources.getStringArray(R.array.status_array)
        var status = view.findViewById<AutoCompleteTextView>(R.id.status)
        status.setText(when(deal.status){
            1 -> "Планируется"
            2 -> "Заключена"
            3 -> "Оплачена"
            4 -> "Исполнена"
            else -> ""
        })
        if (status != null) {
            var statusAdapter = ArrayAdapter(
                requireContext(),
                android.R.layout.simple_list_item_1, statusArray)
            status.setAdapter(statusAdapter)

            status.setOnItemClickListener { parent, view, position, id ->
                statusInt = when(statusArray[position]){
                    "Планируется" -> 1
                    "Заключена" -> 2
                    "Оплачена" -> 3
                    "Исполнена" -> 4
                    else -> 0
                }
            }

        }

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

        view.findViewById<Button>(R.id.save_deal).setOnClickListener{
            val model = DealClass(
                deal.idDeal,
                idUser,
                date.text.toString(),
                address.text.toString(),
                client.text.toString(),
                clientNumber.text.toString(),
                price.text.toString().toInt(),
                difficultyInt,
                statusInt,
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
                idUser,
                date.text.toString(),
                address.text.toString(),
                client.text.toString(),
                clientNumber.text.toString(),
                price.text.toString().toInt(),
                difficultyInt,
                4,
                comment.text.toString()
            )
            setFragmentResult(
                "request_key",
                bundleOf("complete_key" to model)
            )
            view.findNavController().navigateUp()
        }

        view.findViewById<Button>(R.id.update_price).setOnClickListener{
            var sumPrice = 0
            Firebase.database.getReference("DealsDecorationClass").get().addOnSuccessListener{
                for (dataSnapshot in it.children) {
                    val dealsDecoration = dataSnapshot.getValue(DealsDecorationClass::class.java)
                    if (dealsDecoration != null) {
                        if(dealsDecoration.idDeal == deal.idDeal)
                            sumPrice += dealsDecoration.price * dealsDecoration.quantity
                    }
                }
                price.text = sumPrice.toString()
            }
        }

        view.findViewById<Button>(R.id.change_user).setOnClickListener{
            val action =
                DealInfoFragmentDirections.actionDealInfoFragmentToDealInfoUserChangeFragment(deal)
            view.findNavController().navigate(action)
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