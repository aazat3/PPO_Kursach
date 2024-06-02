package com.example.ppo_kursach.deal_package

import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.appcompat.widget.Toolbar
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResultListener
import androidx.lifecycle.Lifecycle
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.ppo_kursach.R
import com.example.ppo_kursach.deals_decoration_package.DealsDecorationClass
import com.google.android.material.appbar.MaterialToolbar
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import kotlin.properties.Delegates

class DealFragment : Fragment(){

    private lateinit var firebaseDealDatabase: DatabaseReference
    private lateinit var firebaseLastIdDatabase: DatabaseReference
    private lateinit var firebaseDealDatabaseListener: ValueEventListener
    lateinit var dealList: ArrayList<DealClass>
    lateinit var dealAdapter: DealAdapter
    var lastIdDeal by Delegates.notNull<Int>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        firebaseDealDatabase = Firebase.database.getReference("DealClass")
        firebaseLastIdDatabase = Firebase.database.getReference("LastIdentifiers/lastIdDeal")

        setHasOptionsMenu(true)

        dealList = arrayListOf()
        dealAdapter = DealAdapter(dealList)

        setFragmentResultListener("request_key") { key, bundle ->
            val returnedSaveDeal = bundle.getParcelable<DealClass>("save_key")
            val returnedDeleteDeal = bundle.getParcelable<DealClass>("delete_key")
            val returnedCompleteDeal = bundle.getParcelable<DealClass>("complete_key")

            if (returnedSaveDeal != null) {
                if(lastIdDeal <= returnedSaveDeal.idDeal)
                    lastIdDeal = returnedSaveDeal.idDeal
                    firebaseLastIdDatabase.setValue(returnedSaveDeal.idDeal)
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
            firebaseLastIdDatabase.get().addOnSuccessListener {
                val getLastIdDeal: Int? = it.getValue(Int::class.java)
                if (getLastIdDeal != null) {
                    lastIdDeal = getLastIdDeal
                }
            }

        }

        firebaseDealDatabaseUpdate()
        firebaseLastIdDatabase.get().addOnSuccessListener {
            val getLastIdDeal: Int? = it.getValue(Int::class.java)
            if (getLastIdDeal != null) {
                lastIdDeal = getLastIdDeal
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val view = inflater.inflate(R.layout.fragment_deal, container, false)
        val toolbar = view.findViewById<Toolbar>(R.id.toolbar)
        (requireActivity() as AppCompatActivity).setSupportActionBar(toolbar)

        return view
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val dealRecyclerView = view.findViewById<RecyclerView>(R.id.deal_recycler_view)
        dealRecyclerView.layoutManager = LinearLayoutManager(context)
        dealRecyclerView.adapter = dealAdapter

        dealAdapter.setOnClickListener(object :
            DealAdapter.OnClickListener {
            override fun onClick(position: Int, model: DealClass) {
                val action = DealFragmentDirections.actionDealFragmentToDealInfoFragment(model)
                view.findNavController().navigate(action)
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
        when (item.itemId) {
                R.id.add_new -> {
                    val model = DealClass(idDeal = lastIdDeal + 1)
                    val action = DealFragmentDirections.actionDealFragmentToDealInfoFragment(model)
                    view?.findNavController()?.navigate(action)
                    true
                }

                else -> false
            }
        return super.onOptionsItemSelected(item)
    }

    private fun filter(text: String) {
        val filteredList: ArrayList<DealClass> = ArrayList()

        for (item in dealList) {
            if (item.date.lowercase().contains(text.lowercase()) || item.address.lowercase().contains(text.lowercase()) || item.idUser.toString().lowercase().contains(text.lowercase()) || item.client.lowercase().contains(text.lowercase())) {
                filteredList.add(item)
            }
        }
        if (filteredList.isEmpty()) {
            Toast.makeText(context, "No Data Found..", Toast.LENGTH_SHORT).show()
        } else {
            dealAdapter.filterList(filteredList)
        }
    }

    private fun firebaseDealDatabaseUpdate(){
        firebaseDealDatabase.get().addOnSuccessListener {
            dealList.clear()
            for (dataSnapshot in it.children) {
                val item = dataSnapshot.getValue(DealClass::class.java)
                item?.let { dealList.add(it) }
            }
            dealAdapter.notifyDataSetChanged()
        }
    }

}