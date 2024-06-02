package com.example.ppo_kursach.users_deal_package

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.fragment.app.setFragmentResult
import androidx.fragment.app.setFragmentResultListener
import androidx.navigation.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.ppo_kursach.R
import com.example.ppo_kursach.deal_package.DealClass
import com.example.ppo_kursach.deal_package.DealInfoFragmentDirections
import com.example.ppo_kursach.deals_decoration_package.DealsDecorationClass
import com.example.ppo_kursach.decoration_package.DecorationClass
import com.example.ppo_kursach.user_package.UserClass
import com.google.android.material.appbar.MaterialToolbar
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import kotlin.properties.Delegates

class DealsUserFragment : Fragment() {
    private lateinit var firebaseUserDatabase: DatabaseReference
    private lateinit var firebaseLastIdDatabase: DatabaseReference
    private lateinit var firebaseDealsUserDatabase: DatabaseReference
    lateinit var dealsUserList: ArrayList<UserClass>
    lateinit var dealsUserAdapter: DealsUserAdapter
    var lastIdDealsUser by Delegates.notNull<Int>()
    private val args: DealsUserFragmentArgs by navArgs()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        firebaseUserDatabase = Firebase.database.getReference("UserClass")
        firebaseDealsUserDatabase = Firebase.database.getReference("UsersDealClass")
        firebaseLastIdDatabase = Firebase.database.getReference("LastIdentifiers/lastIdUsersDeal")
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        return inflater.inflate(R.layout.fragment_deals_user, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setFragmentResultListener("deals_user_key") { key, bundle ->
            val returnedAddUser = bundle.getParcelable<UserClass>("add_deals_user_key")
            if (returnedAddUser != null) {
                val returnedAddDealsUser= UsersDealClass(lastIdDealsUser + 1, returnedAddUser.idUser, args.deal.idDeal)
                if(lastIdDealsUser <= returnedAddDealsUser.idUsersDeal)
                    firebaseLastIdDatabase.setValue(returnedAddDealsUser.idUsersDeal)
                firebaseDealsUserDatabase.child(returnedAddDealsUser.idUsersDeal.toString()).setValue(returnedAddDealsUser)
            }
        }

        val deal = args.deal
        val dealsUserRecyclerView = view.findViewById<RecyclerView>(R.id.deals_user_recycler_view)

        dealsUserList= arrayListOf()
        dealsUserAdapter = DealsUserAdapter(dealsUserList)
        dealsUserRecyclerView.layoutManager = LinearLayoutManager(context)
        dealsUserRecyclerView.adapter = dealsUserAdapter

        dealsUserAdapter.setOnClickListener(object :
            DealsUserAdapter.OnClickListener {
            override fun onClick(position: Int, model: UserClass) {

//                val action = DealsUserFragmentDirections.actionDealsUserFragmentToDealsUserAddFragment(model)
//                view.findNavController().navigate(action)
            }
        })

        firebaseLastIdDatabase.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val getLastIdDealsUser: Int? = snapshot.getValue(Int::class.java)
                if (getLastIdDealsUser != null) {
                    lastIdDealsUser = getLastIdDealsUser
                }
            }
            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(context, "Fail to get data.", Toast.LENGTH_SHORT).show()
            }
        })

        firebaseUserDatabase.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                for (dataSnapshot in snapshot.children) {
                    val user = dataSnapshot.getValue(UserClass::class.java)
                    if (user != null) {
                        firebaseDealsUserDatabase.addValueEventListener(object :
                            ValueEventListener {
                            override fun onDataChange(snapshot: DataSnapshot) {
                                for (dataSnapshot2 in snapshot.children) {
                                    val dealsUser = dataSnapshot2.getValue(
                                        UsersDealClass::class.java)
                                    if (dealsUser != null) {
                                        if (dealsUser.idUser == user.idUser && dealsUser.idDeal == deal.idDeal) {
                                            dealsUserList.add(user)
                                            dealsUserAdapter.notifyDataSetChanged()
                                        }
                                    }
                                }
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

        view.findViewById<MaterialToolbar>(R.id.toolbar).setOnMenuItemClickListener {
            when (it.itemId) {
                R.id.add_new -> {
                    val action =
                        DealsUserFragmentDirections.actionDealsUserFragmentToDealsUserAddFragment(deal)
                    view.findNavController().navigate(action)
                    true
                }
                R.id.search -> {

                    true
                }

                else -> false
            }
        }
    }

}