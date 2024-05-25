package com.example.ppo_kursach.users_deal_package

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.ppo_kursach.R
import com.example.ppo_kursach.deal_package.DealClass
import com.example.ppo_kursach.deal_package.DealFragmentDirections
import com.example.ppo_kursach.deals_decoration_package.DealsDecorationAdapter
import com.example.ppo_kursach.deals_decoration_package.DealsDecorationAddFragmentDirections
import com.example.ppo_kursach.deals_decoration_package.DealsDecorationClass
import com.example.ppo_kursach.deals_decoration_package.DealsDecorationFragmentArgs
import com.example.ppo_kursach.decoration_package.DecorationClass
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import kotlin.properties.Delegates

class UsersDealFragment : Fragment() {
    private lateinit var firebaseUsersDealDatabase: DatabaseReference
    private lateinit var firebaseLastIdDatabase: DatabaseReference
    private lateinit var firebaseDealDatabase: DatabaseReference
    lateinit var usersDealList: ArrayList<DealClass>
    lateinit var usersDealAdapter: UsersDealAdapter
//    var lastIdDealsDecoration by Delegates.notNull<Int>()
    private val args: UsersDealFragmentArgs by navArgs()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        firebaseUsersDealDatabase = Firebase.database.getReference("UsersDealClass")
        firebaseDealDatabase = Firebase.database.getReference("DealClass")
        firebaseLastIdDatabase = Firebase.database.getReference("LastIdentifiers/lastIdUsersDeal")
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        return inflater.inflate(R.layout.fragment_users_deal, container, false)

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val user = args.user
        val usersDealRecyclerView = view.findViewById<RecyclerView>(R.id.users_deal_recycler_view)

        usersDealList= arrayListOf()
        usersDealAdapter = UsersDealAdapter(usersDealList)
        usersDealRecyclerView.layoutManager = LinearLayoutManager(context)
        usersDealRecyclerView.adapter = usersDealAdapter

        usersDealAdapter.setOnClickListener(object :
            UsersDealAdapter.OnClickListener {
            override fun onClick(position: Int, model: DealClass) {

                val action = UsersDealFragmentDirections.actionUsersDealFragmentToDealInfoFragment(model)
                view.findNavController().navigate(action)
            }
        })

        firebaseDealDatabase.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                for (dataSnapshot in snapshot.children) {
                    val deal = dataSnapshot.getValue(DealClass::class.java)
                    if (deal != null) {
                        firebaseUsersDealDatabase.addValueEventListener(object :
                            ValueEventListener {
                            override fun onDataChange(snapshot: DataSnapshot) {
                                for (dataSnapshot2 in snapshot.children) {
                                    val usersDeal = dataSnapshot2.getValue(
                                        UsersDealClass::class.java)
                                    Toast.makeText(context, "added", Toast.LENGTH_SHORT).show()
                                    if (usersDeal != null) {
                                        if (usersDeal.idUser == user.idUser && usersDeal.idDeal == deal.idDeal) {
                                            usersDealList.add(deal)
                                        }
                                    }
                                }
                                usersDealAdapter.notifyDataSetChanged()
                            }
                            override fun onCancelled(error: DatabaseError) {
                                Toast.makeText(context, "Error", Toast.LENGTH_LONG).show()
                            }

                        })
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(context, "Error", Toast.LENGTH_LONG).show()
            }
        })
    }

}