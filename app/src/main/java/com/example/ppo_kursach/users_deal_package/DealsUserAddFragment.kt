package com.example.ppo_kursach.users_deal_package

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.appcompat.widget.Toolbar
import androidx.core.os.bundleOf
import androidx.fragment.app.setFragmentResult
import androidx.navigation.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.ppo_kursach.R
import com.example.ppo_kursach.deal_package.DealClass
import com.example.ppo_kursach.user_package.UserClass
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import kotlin.properties.Delegates

class DealsUserAddFragment : Fragment() {

    private lateinit var firebaseDealsUserDatabase: DatabaseReference
    private lateinit var firebaseLastIdDatabase: DatabaseReference
    private lateinit var firebaseUserDatabase: DatabaseReference
    lateinit var dealsUserList: ArrayList<UserClass>
    lateinit var dealsUserAdapter: DealsUserAdapter
    var lastIdDealsUser by Delegates.notNull<Int>()
    private val args: DealsUserAddFragmentArgs by navArgs()
    private lateinit var deal: DealClass

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        firebaseDealsUserDatabase = Firebase.database.getReference("UsersDealClass")
        firebaseUserDatabase = Firebase.database.getReference("UserClass")
        firebaseLastIdDatabase = Firebase.database.getReference("LastIdentifiers/lastIdUsersDeal")
        deal = args.deal

        setHasOptionsMenu(true)

        dealsUserList= arrayListOf()
        dealsUserAdapter = DealsUserAdapter(dealsUserList)
        firebaseUserDatabaseUpdate()
        firebaseLastIdDatabase.get().addOnSuccessListener{
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
        val view = inflater.inflate(R.layout.fragment_deals_user_add, container, false)
        val toolbar = view.findViewById<Toolbar>(R.id.toolbar)
        (requireActivity() as AppCompatActivity).setSupportActionBar(toolbar)
        return view
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val dealsUserRecyclerView = view.findViewById<RecyclerView>(R.id.deals_user_recycler_view)
        dealsUserRecyclerView.layoutManager = LinearLayoutManager(context)
        dealsUserRecyclerView.adapter = dealsUserAdapter

        dealsUserAdapter.setOnClickListener(object :
            DealsUserAdapter.OnClickListener {
            override fun onClick(position: Int, model: UserClass) {
                val dialogBuilder = context!!.let { AlertDialog.Builder(it) }
                dialogBuilder.setTitle("Добавить")
                dialogBuilder.setPositiveButton("Добавить") { dialog, whichButton ->
                    setFragmentResult(
                        "deals_user_key",
                        bundleOf("add_deals_user_key" to model)
                    )
                    view.findNavController().navigateUp()
                }
                dialogBuilder.setNeutralButton("Отмена") { dialog, whichButton ->
                    dialog.cancel()
                }
                dialogBuilder.show()
            }
        })
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {

        inflater.inflate(R.menu.toolbar_menu, menu)
        val searchItem: MenuItem = menu.findItem(R.id.search)
        val searchView: SearchView = searchItem.actionView as SearchView
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(p0: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(msg: String): Boolean {
                filter(msg)
                return false
            }
        })
        super.onCreateOptionsMenu(menu, inflater)

    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return super.onOptionsItemSelected(item)
    }

    private fun filter(text: String) {
        val filteredList: ArrayList<UserClass> = ArrayList()

        for (item in dealsUserList) {
            if (item.name.lowercase().contains(text.lowercase()) || item.type.lowercase().contains(text.lowercase()) || item.login.lowercase().contains(text.lowercase()) || item.userNumber.lowercase().contains(text.lowercase())) {
                filteredList.add(item)
            }
        }
        if (filteredList.isEmpty()) {
            Toast.makeText(context, "No Data Found..", Toast.LENGTH_SHORT).show()
        } else {
            dealsUserAdapter.filterList(filteredList)
        }
    }

    fun firebaseUserDatabaseUpdate(){
        dealsUserList.clear()
        firebaseUserDatabase.get().addOnSuccessListener{
            for (dataSnapshot in it.children) {
                val user = dataSnapshot.getValue(UserClass::class.java)
                var flag = false
                if (user != null) {
                    firebaseDealsUserDatabase.get().addOnSuccessListener{
                        for (dataSnapshot2 in it.children) {
                            val dealsUser = dataSnapshot2.getValue(
                                UsersDealClass::class.java)
                            if (dealsUser != null) {
                                if (dealsUser.idUser == user.idUser && dealsUser.idDeal == deal.idDeal) {
                                    flag = true
                                }
                            }
                        }
                        if (!flag) {
                            dealsUserList.add(user)
                            dealsUserAdapter.notifyDataSetChanged()
                        }
                    }
                }
            }
        }
    }
}