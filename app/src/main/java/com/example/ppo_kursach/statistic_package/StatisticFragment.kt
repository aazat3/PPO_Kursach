package com.example.ppo_kursach.statistic_package

import android.os.Bundle
import androidx.fragment.app.Fragment
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
import androidx.navigation.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.ppo_kursach.R
import com.example.ppo_kursach.statistic_package.StatisticFragmentArgs
import com.example.ppo_kursach.statistic_package.StatisticFragmentDirections
import com.example.ppo_kursach.deal_package.DealAdapter
import com.example.ppo_kursach.deal_package.DealClass
import com.example.ppo_kursach.deal_package.DealFragmentDirections
import com.example.ppo_kursach.deals_decoration_package.DealsDecorationFragmentArgs
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import java.lang.reflect.InvocationTargetException

class StatisticFragment : Fragment() {

    private lateinit var firebaseStatisticDatabase: DatabaseReference
    lateinit var dealList: ArrayList<DealClass>
    lateinit var dealAdapter: DealAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        firebaseStatisticDatabase = Firebase.database.getReference("StatisticClass")
        setHasOptionsMenu(true)
        dealList= arrayListOf()
        dealAdapter = DealAdapter(dealList)
        firebaseStatisticDatabaseUpdate()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_statistic, container, false)
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
                val action =
                    StatisticFragmentDirections.actionStatisticFragmentToStatisticInfoFragment(model)
                view.findNavController().navigate(action)
            }
        })
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {

        inflater.inflate(R.menu.toolbar_menu_4, menu)
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

    private fun firebaseStatisticDatabaseUpdate(){
        firebaseStatisticDatabase.get().addOnSuccessListener{
            for (dataSnapshot in it.children) {
                val item = dataSnapshot.getValue(DealClass::class.java)
                item?.let { dealList.add(it) }
            }
            dealAdapter.notifyDataSetChanged()
        }
    }
}