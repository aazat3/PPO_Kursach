package com.example.ppo_kursach

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
import com.google.android.material.appbar.MaterialToolbar
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import kotlin.properties.Delegates

class DealsDecorationFragment : Fragment() {

    private lateinit var firebaseDealsDecorationDatabase: DatabaseReference
    private lateinit var firebaseLastIdDatabase: DatabaseReference
    lateinit var dealsDecorationList: ArrayList<DealsDecorationClass>
    lateinit var dealsDecorationAdapter: DealsDecorationAdapter
    var lastIdDealsDecoration by Delegates.notNull<Int>()
    private val args: DealsDecorationFragmentArgs by navArgs()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        firebaseDealsDecorationDatabase = Firebase.database.getReference("DealsDecorationClass")
        firebaseLastIdDatabase = Firebase.database.getReference("LastIdentifiers/lastIdDealsDecoration")

        setFragmentResultListener("request_key") { key, bundle ->
            val returnedSaveDealsDecoration = bundle.getParcelable<DealsDecorationClass>("save_key")
            val returnedDeleteDealsDecoration = bundle.getParcelable<DealsDecorationClass>("delete_key")
//            val returnedSaveDealsDecoration = bundle.getParcelable("extra_key", DealsDecorationClass::class.java)
            if (returnedSaveDealsDecoration != null) {
                if(lastIdDealsDecoration <= returnedSaveDealsDecoration.idDealsDecoration)
                    firebaseLastIdDatabase.setValue(returnedSaveDealsDecoration.idDealsDecoration)
                firebaseDealsDecorationDatabase.child(returnedSaveDealsDecoration.idDealsDecoration.toString()).setValue(returnedSaveDealsDecoration)
            }
            if (returnedDeleteDealsDecoration != null) {
                firebaseDealsDecorationDatabase.child(returnedDeleteDealsDecoration.idDealsDecoration.toString()).removeValue()
            }
        }
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_deals_decoration, container, false)
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val idDeal = args.idDeal

        val navController = view.findNavController()
        val dealsDecorationRecyclerView = view.findViewById<RecyclerView>(R.id.deals_decoration_recycler_view)
        dealsDecorationList= arrayListOf()
        dealsDecorationAdapter = DealsDecorationAdapter(dealsDecorationList)
        dealsDecorationRecyclerView.layoutManager = LinearLayoutManager(context)
        dealsDecorationRecyclerView.adapter = dealsDecorationAdapter

//        dealsDecorationAdapter.setOnClickListener(object :
//            DealsDecorationAdapter.OnClickListener {
//            override fun onClick(position: Int, model: DealsDecorationClass) {
//                val action = DealFragmentDirections.actionDealFragmentToDealInfoFragment(model)
//                navController.navigate(action)
//            }
//        })

        view.findViewById<MaterialToolbar>(R.id.toolbar).setOnMenuItemClickListener {
            when (it.itemId) {
                R.id.new_deal -> {
                    val model = DealsDecorationClass(idDealsDecoration = lastIdDealsDecoration + 1)
                    val action = DealsDecorationFragmentDirections.actionDealsDecorationFragmentToDealsDecorationInfoFragment()
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
                val getLastIdDealsDecoration: Int? = snapshot.getValue(Int::class.java)
                if (getLastIdDealsDecoration != null) {
                    lastIdDealsDecoration = getLastIdDealsDecoration
                }
            }
            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(context, "Fail to get data.", Toast.LENGTH_SHORT).show()
            }
        })


        firebaseDealsDecorationDatabase.addChildEventListener(object : ChildEventListener {
            override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                val dealsDecoration: DealsDecorationClass? = snapshot.getValue(DealsDecorationClass::class.java)
                if (dealsDecoration != null) {
                    if(dealsDecoration.idDeal == idDeal){
                        Toast.makeText(context, "added", Toast.LENGTH_SHORT).show()
                        dealsDecorationList.add(dealsDecoration)
                        dealsDecorationAdapter.notifyItemInserted(dealsDecorationList.size)
                    }
                }
            }

            override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {
                Toast.makeText(context, "changed", Toast.LENGTH_SHORT).show()
            }

            override fun onChildRemoved(snapshot: DataSnapshot) {
                dealsDecorationAdapter.notifyDataSetChanged()
                Toast.makeText(context, "removed", Toast.LENGTH_SHORT).show()
            }

            override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {
                Toast.makeText(context, "moved", Toast.LENGTH_SHORT).show()
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(context, "Ошибка", Toast.LENGTH_LONG).show()
            }

        })

    }


    companion object {
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            DealsDecorationFragment().apply {
                arguments = Bundle().apply {
                }
            }
    }
}