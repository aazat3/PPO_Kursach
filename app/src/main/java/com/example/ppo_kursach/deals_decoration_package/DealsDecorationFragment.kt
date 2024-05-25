package com.example.ppo_kursach.deals_decoration_package

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
import com.example.ppo_kursach.decoration_package.DecorationClass
import com.example.ppo_kursach.R
import com.google.android.material.appbar.MaterialToolbar
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
    private lateinit var firebaseDecorationDatabase: DatabaseReference
    lateinit var dealsDecorationList: ArrayList<DecorationClass>
    lateinit var dealsDecorationAdapter: DealsDecorationAdapter
    var lastIdDealsDecoration by Delegates.notNull<Int>()
    private val args: DealsDecorationFragmentArgs by navArgs()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        firebaseDealsDecorationDatabase = Firebase.database.getReference("DealsDecorationClass")
        firebaseDecorationDatabase = Firebase.database.getReference("DecorationClass")
        firebaseLastIdDatabase = Firebase.database.getReference("LastIdentifiers/lastIdDealsDecoration")

        setFragmentResultListener("deals_decoration_key") { key, bundle ->
            val returnedSaveDealsDecoration = bundle.getParcelable<DecorationClass>("save_key")
            val returnedDeleteDealsDecoration = bundle.getParcelable<DecorationClass>("delete_key")
            val returnedAddDecoration = bundle.getParcelable<DecorationClass>("add_deals_decoration_key")


//            val returnedSaveDealsDecoration = bundle.getParcelable("extra_key", DealsDecorationClass::class.java)
            if (returnedSaveDealsDecoration != null) {
                Toast.makeText(context, "New decoration saved", Toast.LENGTH_SHORT).show()

//                if(lastIdDealsDecoration <= returnedSaveDealsDecoration.idDealsDecoration)
//                    firebaseLastIdDatabase.setValue(returnedSaveDealsDecoration.idDealsDecoration)
//                firebaseDealsDecorationDatabase.child(returnedSaveDealsDecoration.idDealsDecoration.toString()).setValue(returnedSaveDealsDecoration)
            }
            if (returnedDeleteDealsDecoration != null) {
                Toast.makeText(context, "New decoration deleted", Toast.LENGTH_SHORT).show()

//                firebaseDealsDecorationDatabase.child(returnedDeleteDealsDecoration.idDealsDecoration.toString()).removeValue()
            }
            if (returnedAddDecoration != null) {
                Toast.makeText(context, "New decoration added", Toast.LENGTH_SHORT).show()
                val returnedAddDealsDecoration = DealsDecorationClass(lastIdDealsDecoration + 1, args.deal.idDeal, returnedAddDecoration.idDecoration, returnedAddDecoration.quantity, returnedAddDecoration.price, args.deal.date)
                if(lastIdDealsDecoration <= returnedAddDealsDecoration.idDealsDecoration)
                    firebaseLastIdDatabase.setValue(returnedAddDealsDecoration.idDealsDecoration)
                firebaseDealsDecorationDatabase.child(returnedAddDealsDecoration.idDealsDecoration.toString()).setValue(returnedAddDealsDecoration)
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
        val deal = args.deal

        val navController = view.findNavController()
        val dealsDecorationRecyclerView = view.findViewById<RecyclerView>(R.id.deals_decoration_recycler_view)
        dealsDecorationList= arrayListOf()
        dealsDecorationAdapter = DealsDecorationAdapter(dealsDecorationList)
        dealsDecorationRecyclerView.layoutManager = LinearLayoutManager(context)
        dealsDecorationRecyclerView.adapter = dealsDecorationAdapter

        dealsDecorationAdapter.setOnClickListener(object :
            DealsDecorationAdapter.OnClickListener {
            override fun onClick(position: Int, model: DecorationClass) {
                val action =
                    DealsDecorationFragmentDirections.actionDealsDecorationFragmentToDealsDecorationInfoFragment(
                        model
                    )
                navController.navigate(action)
            }
        })

        view.findViewById<MaterialToolbar>(R.id.toolbar).setOnMenuItemClickListener {
            when (it.itemId) {
                R.id.new_deal -> {
//                    val model = DealsDecorationClass(idDealsDecoration = lastIdDealsDecoration + 1)
//                    val model = DealClass(idDealsDecoration = lastIdDealsDecoration + 1)
                    if (deal.date != ""){
                        val action =
                            DealsDecorationFragmentDirections.actionDealsDecorationFragmentToDealsDecorationAddFragment(deal)
                        navController.navigate(action)
                    } else{
                        Toast.makeText(context, "Please set data for deal", Toast.LENGTH_SHORT).show()
                    }

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

        firebaseDealsDecorationDatabase.addValueEventListener(object :ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                for (dataSnapshot in snapshot.children) {
                    val dealsDecoration = dataSnapshot.getValue(DealsDecorationClass::class.java)
                    if (dealsDecoration != null) {
                        if(dealsDecoration.idDeal == deal.idDeal && deal.date != "")
                            firebaseDecorationDatabase.addValueEventListener(object :ValueEventListener{
                                override fun onDataChange(snapshot: DataSnapshot) {
                                    for (dataSnapshot2 in snapshot.children) {
                                        val getDecoration = dataSnapshot2.getValue(DecorationClass::class.java)
                                        if (getDecoration != null){
                                            if(getDecoration.idDecoration == dealsDecoration.idDecoration){
                                                getDecoration.quantity = dealsDecoration.quantity
                                                getDecoration.price = dealsDecoration.price
                                                dealsDecorationList.add(getDecoration)
                                            }
                                        }
                                    }
                                    dealsDecorationAdapter.notifyDataSetChanged()
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

//        firebaseDealsDecorationDatabase.addChildEventListener(object : ChildEventListener {
//            override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
//                val dealsDecoration: DealsDecorationClass? = snapshot.getValue(DealsDecorationClass::class.java)
//                if (dealsDecoration != null) {
//                    if(dealsDecoration.idDeal == deal.idDeal){
//                        firebaseDecorationDatabase.addChildEventListener(object : ChildEventListener {
//                            override fun onChildAdded(
//                                snapshot: DataSnapshot,
//                                previousChildName: String?,
//                            ) {
//                                val getDecoration: DecorationClass? = snapshot.getValue(DecorationClass::class.java)
//                                if (getDecoration != null) {
//                                    if(getDecoration.idDecoration == dealsDecoration.idDecoration) {
////                                        Toast.makeText(context, "added", Toast.LENGTH_SHORT).show()
//                                        getDecoration.quantity = dealsDecoration.quantity
//                                        getDecoration.price = dealsDecoration.price
//
//                                        dealsDecorationList.add(getDecoration)
//                                        dealsDecorationAdapter.notifyItemInserted(dealsDecorationList.size)
//                                    }
//                                }
//                            }
//                            override fun onChildChanged(
//                                snapshot: DataSnapshot,
//                                previousChildName: String?,
//                            ) {
//                            }
//                            override fun onChildRemoved(snapshot: DataSnapshot) {
//                            }
//                            override fun onChildMoved(
//                                snapshot: DataSnapshot,
//                                previousChildName: String?,
//                            ) {
//                            }
//                            override fun onCancelled(error: DatabaseError) {
//                                Toast.makeText(context, "Fail to get data.", Toast.LENGTH_SHORT).show()
//                            }
//                        })
////                        dealsDecorationList.add(dealsDecoration)
////                        dealsDecorationAdapter.notifyItemInserted(dealsDecorationList.size)
//                    }
//                }
//            }
//
//            override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {
//                Toast.makeText(context, "changed", Toast.LENGTH_SHORT).show()
//            }
//
//            override fun onChildRemoved(snapshot: DataSnapshot) {
//                dealsDecorationAdapter.notifyDataSetChanged()
//                Toast.makeText(context, "removed", Toast.LENGTH_SHORT).show()
//            }
//
//            override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {
//                Toast.makeText(context, "moved", Toast.LENGTH_SHORT).show()
//            }
//
//            override fun onCancelled(error: DatabaseError) {
//                Toast.makeText(context, "Ошибка", Toast.LENGTH_LONG).show()
//            }
//
//        })

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