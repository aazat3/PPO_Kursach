package com.example.ppo_kursach.statistic_package

import android.os.Bundle
import android.text.InputType
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.text.isDigitsOnly
import androidx.fragment.app.setFragmentResultListener
import androidx.navigation.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.ppo_kursach.R
import com.example.ppo_kursach.deal_package.DealClass
import com.example.ppo_kursach.deals_decoration_package.DealsDecorationClass
import com.example.ppo_kursach.deals_decoration_package.DealsDecorationFragmentArgs
import com.example.ppo_kursach.deals_decoration_package.DealsDecorationFragmentDirections
import com.example.ppo_kursach.decoration_package.DecorationAdapter
import com.example.ppo_kursach.decoration_package.DecorationClass
import com.google.android.material.appbar.MaterialToolbar
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import kotlin.properties.Delegates

class StatisticsDecorationFragment : Fragment() {

    private lateinit var firebaseDealsDecorationDatabase: DatabaseReference
    private lateinit var firebaseDecorationDatabase: DatabaseReference
    lateinit var dealsDecorationList: ArrayList<DecorationClass>
    lateinit var dealsDecorationAdapter: DecorationAdapter
    lateinit var idDealsDecorationList: ArrayList<Int>
    private val args: DealsDecorationFragmentArgs by navArgs()
    lateinit var  deal: DealClass

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        deal = args.deal
        firebaseDealsDecorationDatabase = Firebase.database.getReference("DealsDecorationClass")
        firebaseDecorationDatabase = Firebase.database.getReference("DecorationClass")
        dealsDecorationList = arrayListOf()
        idDealsDecorationList = arrayListOf()
        dealsDecorationAdapter = DecorationAdapter(dealsDecorationList)

        firebaseDealsDecorationDatabaseUpdate()
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val view = inflater.inflate(R.layout.fragment_statistics_decoration, container, false)
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val dealsDecorationRecyclerView = view.findViewById<RecyclerView>(R.id.deals_decoration_recycler_view)
        dealsDecorationRecyclerView.layoutManager = LinearLayoutManager(context)
        dealsDecorationRecyclerView.adapter = dealsDecorationAdapter
    }

    fun firebaseDealsDecorationDatabaseUpdate(){
        firebaseDealsDecorationDatabase.get().addOnSuccessListener{
            dealsDecorationList.clear()
            idDealsDecorationList.clear()
            for (dataSnapshot in it.children) {
                val dealsDecoration = dataSnapshot.getValue(DealsDecorationClass::class.java)
                if (dealsDecoration != null) {
                    if(dealsDecoration.idDeal == deal.idDeal && deal.date != "")
                        firebaseDecorationDatabase.get().addOnSuccessListener{
                            for (dataSnapshot2 in it.children) {
                                val getDecoration = dataSnapshot2.getValue(DecorationClass::class.java)
                                if (getDecoration != null){
                                    if(getDecoration.idDecoration == dealsDecoration.idDecoration){
                                        getDecoration.quantity = dealsDecoration.quantity
                                        getDecoration.price = dealsDecoration.price
                                        dealsDecorationList.add(getDecoration)
                                        idDealsDecorationList.add(dealsDecoration.idDealsDecoration)
                                    }
                                }
                            }
                            dealsDecorationAdapter.notifyDataSetChanged()
                        }
                }
            }
        }
    }
}