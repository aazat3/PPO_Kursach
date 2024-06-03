package com.example.ppo_kursach.users_deal_package

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.setFragmentResultListener
import androidx.navigation.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.ppo_kursach.R
import com.example.ppo_kursach.deal_package.DealClass
import com.example.ppo_kursach.user_package.UserClass
import com.google.android.material.appbar.MaterialToolbar
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import kotlin.properties.Delegates

class DealsUserFragment : Fragment() {
    private lateinit var firebaseUserDatabase: DatabaseReference
    private lateinit var firebaseLastIdDatabase: DatabaseReference
    private lateinit var firebaseDealsUserDatabase: DatabaseReference
    lateinit var dealsUserList: ArrayList<UserClass>
    lateinit var userAdapter: UserAdapter
    lateinit var idDealsUserList: ArrayList<Int>
    var lastIdDealsUser by Delegates.notNull<Int>()
    private val args: DealsUserFragmentArgs by navArgs()
    lateinit var deal: DealClass

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        firebaseUserDatabase = Firebase.database.getReference("UserClass")
        firebaseDealsUserDatabase = Firebase.database.getReference("UsersDealClass")
        firebaseLastIdDatabase = Firebase.database.getReference("LastIdentifiers/lastIdUsersDeal")
        firebaseLastIdDatabase.get().addOnSuccessListener {
            val getLastIdDealsUser: Int? = it.getValue(Int::class.java)
            if (getLastIdDealsUser != null) {
                lastIdDealsUser = getLastIdDealsUser
            }
        }
        deal = args.deal
        idDealsUserList= arrayListOf()
        dealsUserList= arrayListOf()
        userAdapter = UserAdapter(dealsUserList)

        setFragmentResultListener("deals_user_key") { key, bundle ->
            val returnedAddUser = bundle.getParcelable<UserClass>("add_deals_user_key")
            if (returnedAddUser != null) {
                val returnedAddDealsUser= UsersDealClass(lastIdDealsUser + 1, returnedAddUser.idUser, args.deal.idDeal)
                if(lastIdDealsUser <= returnedAddDealsUser.idUsersDeal){
                    lastIdDealsUser = returnedAddDealsUser.idUsersDeal
                    firebaseLastIdDatabase.setValue(returnedAddDealsUser.idUsersDeal)
                }
                firebaseDealsUserDatabase.child(returnedAddDealsUser.idUsersDeal.toString()).setValue(returnedAddDealsUser)
            }
            firebaseUserDatabaseUpdate()
            firebaseLastIdDatabase.get().addOnSuccessListener {
                val getLastIdDealsUser: Int? = it.getValue(Int::class.java)
                if (getLastIdDealsUser != null) {
                    lastIdDealsUser = getLastIdDealsUser
                }
            }
        }

        firebaseUserDatabaseUpdate()
        firebaseLastIdDatabase.get().addOnSuccessListener {
            val getLastIdDealsUser: Int? = it.getValue(Int::class.java)
            if (getLastIdDealsUser != null) {
                lastIdDealsUser = getLastIdDealsUser
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        return inflater.inflate(R.layout.fragment_deals_user, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val dealsUserRecyclerView = view.findViewById<RecyclerView>(R.id.deals_user_recycler_view)
        dealsUserRecyclerView.layoutManager = LinearLayoutManager(context)
        dealsUserRecyclerView.adapter = userAdapter

        userAdapter.setOnClickListener(object :
            UserAdapter.OnClickListener {
            override fun onClick(position: Int, model: UserClass) {
                val dialogBuilder = context!!.let { AlertDialog.Builder(it) }
                dialogBuilder.setTitle("Удалить")
                dialogBuilder.setPositiveButton("Удалить") { dialog, whichButton ->
                    firebaseDealsUserDatabase.child(idDealsUserList[position].toString()).removeValue()
                    idDealsUserList.removeAt(position)
                    dealsUserList.removeAt(position)
                    userAdapter.notifyItemRemoved(position)
                    userAdapter.notifyItemRangeChanged(position, dealsUserList.size)
                    Toast.makeText(context, "Удалено", Toast.LENGTH_SHORT).show()
                }
                dialogBuilder.setNeutralButton("Отмена") { dialog, whichButton ->
                    dialog.cancel()
                }
                dialogBuilder.show()
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

    private fun firebaseUserDatabaseUpdate(){
        firebaseUserDatabase.get().addOnSuccessListener {
            dealsUserList.clear()
            idDealsUserList.clear()
            for (dataSnapshot in it.children) {
                val user = dataSnapshot.getValue(UserClass::class.java)
                if (user != null) {
                    firebaseDealsUserDatabase.get().addOnSuccessListener {
                        for (dataSnapshot2 in it.children) {
                            val dealsUser = dataSnapshot2.getValue(
                                UsersDealClass::class.java)
                            if (dealsUser != null) {
                                if (dealsUser.idUser == user.idUser && dealsUser.idDeal == deal.idDeal) {
                                    dealsUserList.add(user)
                                    idDealsUserList.add(dealsUser.idUsersDeal)
                                    userAdapter.notifyDataSetChanged()
                                }
                            }
                        }
                    }
                }
            }
        }
    }

}