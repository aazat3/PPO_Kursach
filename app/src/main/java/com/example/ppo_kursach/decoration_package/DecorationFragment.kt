package com.example.ppo_kursach.decoration_package

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
import androidx.fragment.app.setFragmentResultListener
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.ppo_kursach.decoration_package.DecorationFragmentDirections
import com.example.ppo_kursach.R
import com.example.ppo_kursach.deal_package.DealClass
import com.example.ppo_kursach.deal_package.DealFragmentDirections
import com.google.android.material.appbar.MaterialToolbar
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.ktx.storage
import kotlin.properties.Delegates

class DecorationFragment : Fragment() {

    private lateinit var firebaseDecorationDatabase: DatabaseReference
    private lateinit var firebaseLastIdDatabase: DatabaseReference
    lateinit var decorationList: ArrayList<DecorationClass>
    lateinit var decorationAdapter: DecorationAdapter
    var lastIdDecoration by Delegates.notNull<Int>()
    lateinit var storage: StorageReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        firebaseDecorationDatabase = Firebase.database.getReference("DecorationClass")
        storage = Firebase.storage.getReference("Decoration")
        firebaseLastIdDatabase = Firebase.database.getReference("LastIdentifiers/lastIdDecoration")
        firebaseLastIdDatabase.get().addOnSuccessListener {
            val getLastIdDecoration: Int? = it.getValue(Int::class.java)
            if (getLastIdDecoration != null) {
                lastIdDecoration = getLastIdDecoration
            }
        }
        setFragmentResultListener("request_key") { key, bundle ->
            val returnedSaveDecoration = bundle.getParcelable<DecorationClass>("save_key")
            val returnedDeleteDecoration = bundle.getParcelable<DecorationClass>("delete_key")
            if (returnedSaveDecoration != null) {
                if(lastIdDecoration <= returnedSaveDecoration.idDecoration)
                    firebaseLastIdDatabase.setValue(returnedSaveDecoration.idDecoration)
                firebaseDecorationDatabase.child(returnedSaveDecoration.idDecoration.toString()).setValue(returnedSaveDecoration)
            }
            if (returnedDeleteDecoration != null) {
                firebaseDecorationDatabase.child(returnedDeleteDecoration.idDecoration.toString()).removeValue()
            }

            firebaseDecorationDatabaseUpdate()
            firebaseLastIdDatabase.get().addOnSuccessListener {
                val getLastIdDecoration: Int? = it.getValue(Int::class.java)
                if (getLastIdDecoration != null) {
                    lastIdDecoration = getLastIdDecoration
                }
            }
        }
        setHasOptionsMenu(true)

        decorationList= arrayListOf()
        decorationAdapter = DecorationAdapter(decorationList)

        firebaseDecorationDatabaseUpdate()
        firebaseLastIdDatabase.get().addOnSuccessListener {
            val getLastIdDecoration: Int? = it.getValue(Int::class.java)
            if (getLastIdDecoration != null) {
                lastIdDecoration = getLastIdDecoration
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_decoration, container, false)
        val toolbar = view.findViewById<Toolbar>(R.id.toolbar)
        (requireActivity() as AppCompatActivity).setSupportActionBar(toolbar)
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val decorationRecyclerView = view.findViewById<RecyclerView>(R.id.decoration_recycler_view)

        decorationRecyclerView.layoutManager = LinearLayoutManager(context)
        decorationRecyclerView.adapter = decorationAdapter

        decorationAdapter.setOnClickListener(object :
            DecorationAdapter.OnClickListener {
            override fun onClick(position: Int, model: DecorationClass) {
                val action =
                    DecorationFragmentDirections.actionDecorationFragmentToDecorationInfoFragment(
                        model
                    )
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
                val model = DecorationClass(idDecoration = lastIdDecoration + 1)
                val action =
                    DecorationFragmentDirections.actionDecorationFragmentToDecorationInfoFragment(model)
                view?.findNavController()?.navigate(action)
                true
            }

            else -> false
        }
        return super.onOptionsItemSelected(item)
    }

    private fun filter(text: String) {
        val filteredList: ArrayList<DecorationClass> = ArrayList()

        for (item in decorationList) {
            val typeName = when(item.type){
                1 ->  "Цветы"
                2 ->  "Конструкции"
                3 ->  "Прочее"
                else -> ""
            }
            if (item.name.lowercase().contains(text.lowercase()) || typeName.lowercase().contains(text.lowercase())) {
                filteredList.add(item)
            }
        }
        if (filteredList.isEmpty()) {
            Toast.makeText(context, "No Data Found..", Toast.LENGTH_SHORT).show()
        } else {
            decorationAdapter.filterList(filteredList)
        }
    }

    private fun firebaseDecorationDatabaseUpdate(){
        firebaseDecorationDatabase.get().addOnSuccessListener{
            decorationList.clear()
            for (dataSnapshot in it.children) {
                val item = dataSnapshot.getValue(DecorationClass::class.java)
                item?.let { decorationList.add(it) }
            }
            decorationAdapter.notifyDataSetChanged()
        }
    }
}