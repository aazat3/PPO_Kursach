package com.example.ppo_kursach.users_deal_package

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.setFragmentResultListener
import androidx.navigation.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.ppo_kursach.R
import com.example.ppo_kursach.deal_package.DealAdapter
import com.example.ppo_kursach.deal_package.DealClass
import com.example.ppo_kursach.deal_package.DealFragmentDirections
import com.example.ppo_kursach.deals_decoration_package.DealsDecorationClass
import com.example.ppo_kursach.user_package.UserClass
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import kotlin.properties.Delegates

class UsersDealFragment : Fragment() {
    private lateinit var firebaseUsersDealDatabase: DatabaseReference
    private lateinit var firebaseDealDatabase: DatabaseReference
    lateinit var usersDealList: ArrayList<DealClass>
    lateinit var usersDealAdapter: DealAdapter
    private val args: UsersDealFragmentArgs by navArgs()
    lateinit var user: UserClass

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        firebaseUsersDealDatabase = Firebase.database.getReference("UsersDealClass")
        firebaseDealDatabase = Firebase.database.getReference("DealClass")
        user = args.user

        usersDealList= arrayListOf()
        usersDealAdapter = DealAdapter(usersDealList)

        setFragmentResultListener("request_key") { key, bundle ->
            val returnedSaveDeal = bundle.getParcelable<DealClass>("save_key")
            val returnedDeleteDeal = bundle.getParcelable<DealClass>("delete_key")
            val returnedCompleteDeal = bundle.getParcelable<DealClass>("complete_key")

            if (returnedSaveDeal != null) {
                firebaseDealDatabase.child(returnedSaveDeal.idDeal.toString()).setValue(returnedSaveDeal)
            }

            if (returnedDeleteDeal != null) {
                firebaseDealDatabase.child(returnedDeleteDeal.idDeal.toString()).removeValue()
                val firebaseDealsDecorationDatabase = Firebase.database.getReference("DealsDecorationClass")
                firebaseDealsDecorationDatabase.get().addOnSuccessListener{
                    for (dataSnapshot in it.children) {
                        val item = dataSnapshot.getValue(DealsDecorationClass::class.java)
                        if (item!!.idDeal == returnedDeleteDeal.idDeal){
                            firebaseDealsDecorationDatabase.child(item.idDealsDecoration.toString()).removeValue()
                        }
                    }
                }
            }

            if (returnedCompleteDeal != null) {
                Firebase.database.getReference("StatisticClass").child(returnedCompleteDeal.idDeal.toString()).setValue(returnedCompleteDeal)
                val action = DealFragmentDirections.actionDealFragmentToStatisticFragment(
                    returnedCompleteDeal
                )
                view?.findNavController()?.navigate(action)
            }
            firebaseDealDatabaseUpdate()
        }

        firebaseDealDatabaseUpdate()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        return inflater.inflate(R.layout.fragment_users_deal, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val usersDealRecyclerView = view.findViewById<RecyclerView>(R.id.users_deal_recycler_view)
        usersDealRecyclerView.layoutManager = LinearLayoutManager(context)
        usersDealRecyclerView.adapter = usersDealAdapter

        usersDealAdapter.setOnClickListener(object :
            DealAdapter.OnClickListener {
            override fun onClick(position: Int, model: DealClass) {

                val action = UsersDealFragmentDirections.actionUsersDealFragmentToDealInfoFragment(model)
                view.findNavController().navigate(action)
            }
        })
    }

    private fun firebaseDealDatabaseUpdate(){
        usersDealList.clear()
        firebaseDealDatabase.get().addOnSuccessListener {
            for (dataSnapshot in it.children) {
                val deal = dataSnapshot.getValue(DealClass::class.java)
                if (deal != null) {
                    firebaseUsersDealDatabase.get().addOnSuccessListener {
                        for (dataSnapshot2 in it.children) {
                            val usersDeal = dataSnapshot2.getValue(
                                UsersDealClass::class.java)
                            if (usersDeal != null) {
                                if (usersDeal.idUser == user.idUser && usersDeal.idDeal == deal.idDeal) {
                                    usersDealList.add(deal)
                                }
                            }
                        }
                        usersDealAdapter.notifyDataSetChanged()
                    }
                }
            }
        }
    }
}