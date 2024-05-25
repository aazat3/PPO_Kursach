package com.example.ppo_kursach.deal_package

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResultListener
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.ppo_kursach.R
import com.google.android.material.appbar.MaterialToolbar
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import kotlin.properties.Delegates

class DealFragment : Fragment(), View.OnClickListener {

    private lateinit var firebaseDealDatabase: DatabaseReference
    private lateinit var firebaseLastIdDatabase: DatabaseReference
    lateinit var dealList: ArrayList<DealClass>
    lateinit var dealAdapter: DealAdapter
    var lastIdDeal by Delegates.notNull<Int>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        firebaseDealDatabase = Firebase.database.getReference("DealClass")
        firebaseLastIdDatabase = Firebase.database.getReference("LastIdentifiers/lastIdDeal")


    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_deal, container, false)
        setFragmentResultListener("request_key") { key, bundle ->
            val returnedSaveDeal = bundle.getParcelable<DealClass>("save_key")
            val returnedDeleteDeal = bundle.getParcelable<DealClass>("delete_key")
            val returnedCompleteDeal = bundle.getParcelable<DealClass>("complete_key")

//            val returnedSaveDeal = bundle.getParcelable("extra_key", DealClass::class.java)
            if (returnedSaveDeal != null) {
                if(lastIdDeal <= returnedSaveDeal.idDeal)
                    firebaseLastIdDatabase.setValue(returnedSaveDeal.idDeal)
                firebaseDealDatabase.child(returnedSaveDeal.idDeal.toString()).setValue(returnedSaveDeal)
            }
            if (returnedDeleteDeal != null) {
                firebaseDealDatabase.child(returnedDeleteDeal.idDeal.toString()).removeValue()
            }
            if (returnedCompleteDeal != null) {
                val action = DealFragmentDirections.actionDealFragmentToStatisticFragment(
                    returnedCompleteDeal
                )
                view.findNavController().navigate(action)
                firebaseDealDatabase.child(returnedCompleteDeal.idDeal.toString()).removeValue()
            }
        }
        return view
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val navController = view.findNavController()
        val dealRecyclerView = view.findViewById<RecyclerView>(R.id.deal_recycler_view)
        dealList= arrayListOf()
        dealAdapter = DealAdapter(dealList)
        dealRecyclerView.layoutManager = LinearLayoutManager(context)
        dealRecyclerView.adapter = dealAdapter

        dealAdapter.setOnClickListener(object :
            DealAdapter.OnClickListener {
            override fun onClick(position: Int, model: DealClass) {
                val action = DealFragmentDirections.actionDealFragmentToDealInfoFragment(model)
                navController.navigate(action)
            }
        })

        view.findViewById<MaterialToolbar>(R.id.toolbar).setOnMenuItemClickListener {
            when (it.itemId) {
                R.id.new_deal -> {
                    val model = DealClass(idDeal = lastIdDeal + 1)
                    val action = DealFragmentDirections.actionDealFragmentToDealInfoFragment(model)
                    navController.navigate(action)
                    true
                }
                R.id.search -> {
                    true
                }
                else -> false
            }
        }

        firebaseLastIdDatabase.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val getLastIdDeal: Int? = snapshot.getValue(Int::class.java)
                if (getLastIdDeal != null) {
                    lastIdDeal = getLastIdDeal
                }
            }
            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(context, "Fail to get data.", Toast.LENGTH_SHORT).show()
            }
        })

        firebaseDealDatabase.addValueEventListener(object :ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                for (dataSnapshot in snapshot.children) {
                    val item = dataSnapshot.getValue(DealClass::class.java)
                    item?.let { dealList.add(it) }
                }
                dealAdapter.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(context, "Error", Toast.LENGTH_LONG).show()
            }

        })
    }

    override fun onClick(p0: View?) {
        if (p0 != null) {
            when(p0.id){
//                R.id.add_deal -> addDeal(DealClass(date = "1", address = "1", ))
            }
        }
    }

}