package com.example.ppo_kursach

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
import com.google.android.material.appbar.MaterialToolbar
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import kotlin.properties.Delegates

class DealsDecorationAddFragment : Fragment() {

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


    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        return inflater.inflate(R.layout.fragment_deals_decoration_add, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setFragmentResultListener("deals_decoration_add_key") { key, bundle ->
            val returnedAddDecoration = bundle.getParcelable<DecorationClass>("add_decoration_key")
            if (returnedAddDecoration != null) {
                Toast.makeText(context, "NNNN", Toast.LENGTH_SHORT).show()

                val model = DecorationClass(
                    returnedAddDecoration.idDecoration,
                    returnedAddDecoration.name,
                    returnedAddDecoration.type,
                    returnedAddDecoration.quantity,
                    returnedAddDecoration.condition,
                    returnedAddDecoration.price,
                    returnedAddDecoration.difficultyInst,
                    returnedAddDecoration.difficultyTr,
                    returnedAddDecoration.photo)
                setFragmentResult(
                    "deals_decoration_key",
                    bundleOf("add_deals_decoration_key" to model)
                )

                view.findNavController().navigateUp()

            }
        }

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

                val action = DealsDecorationAddFragmentDirections.actionDealsDecorationAddFragmentToDealsDecorationAddInfoFragment(model)
                navController.navigate(action)
            }
        })

//        view.findViewById<MaterialToolbar>(R.id.toolbar).setOnMenuItemClickListener {
//            when (it.itemId) {
//                R.id.new_deal -> {
//                    val model = DealsDecorationClass(idDealsDecoration = lastIdDealsDecoration + 1)
//                    val action = DealsDecorationFragmentDirections.actionDealsDecorationFragmentToDealsDecorationAddFragment()
//                    navController.navigate(action)
//                    true
//                }
//                R.id.search -> {
//                    true
//                }
//                else -> false
//            }
//        }

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

        firebaseDecorationDatabase.addValueEventListener(object :ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                for (dataSnapshot in snapshot.children) {
                    val decoration = dataSnapshot.getValue(DecorationClass::class.java)
                    if (decoration != null) {
                        var decorationQuantity = decoration.quantity
                        firebaseDealsDecorationDatabase.addValueEventListener(object :ValueEventListener{
                            override fun onDataChange(snapshot: DataSnapshot) {
                                for (dataSnapshot2 in snapshot.children) {
                                    val dealsDecoration = dataSnapshot2.getValue(DealsDecorationClass::class.java)
                                    if (dealsDecoration != null){
                                        if(decoration.idDecoration == dealsDecoration.idDecoration && deal.date == dealsDecoration.date){
                                            decorationQuantity -= dealsDecoration.quantity
                                        }
                                    }
                                }
                                if (decorationQuantity > 0){
//                                    Toast.makeText(context, "added", Toast.LENGTH_SHORT).show()
                                    decoration.quantity = decorationQuantity
                                    dealsDecorationList.add(decoration)
                                    dealsDecorationAdapter.notifyDataSetChanged()
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

//        firebaseDecorationDatabase.addChildEventListener(object : ChildEventListener {
//            override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
//                val decoration: DecorationClass? = snapshot.getValue(DecorationClass::class.java)
//                if (decoration != null) {
//                    var decorationQuantity = decoration.quantity
//                    firebaseDealsDecorationDatabase.addChildEventListener(object : ChildEventListener {
//                        override fun onChildAdded(
//                            snapshot: DataSnapshot,
//                            previousChildName: String?,
//                        ) {
//                            val dealsDecoration: DealsDecorationClass? = snapshot.getValue(DealsDecorationClass::class.java)
//                            if (dealsDecoration != null) {
//                                if(decoration.idDecoration == dealsDecoration.idDecoration && deal.date == dealsDecoration.date) {
//                                    decorationQuantity -= dealsDecoration.quantity
////                                    Toast.makeText(context, decorationQuantity.toString() + dealsDecoration.quantity.toString(), Toast.LENGTH_SHORT).show()
//
//                                }
////                                else {
////                                    dealsDecorationList.add(decoration)
////                                    dealsDecorationAdapter.notifyItemInserted(dealsDecorationList.size)
////                                }
//
//                            }
//                        }
//                        override fun onChildChanged(
//                            snapshot: DataSnapshot,
//                            previousChildName: String?,
//                        ) {
//                        }
//                        override fun onChildRemoved(snapshot: DataSnapshot) {
//                        }
//                        override fun onChildMoved(
//                            snapshot: DataSnapshot,
//                            previousChildName: String?,
//                        ) {
//                        }
//                        override fun onCancelled(error: DatabaseError) {
//                            Toast.makeText(context, "Fail to get data.", Toast.LENGTH_SHORT).show()
//                        }
//                    })
//                    if (decorationQuantity > 0){
//                        Toast.makeText(context, "added", Toast.LENGTH_SHORT).show()
//                        decoration.quantity = decorationQuantity
//                        dealsDecorationList.add(decoration)
//                        dealsDecorationAdapter.notifyItemInserted(dealsDecorationList.size)
//                    }
//
//
//
////                        dealsDecorationList.add(dealsDecoration)
////                        dealsDecorationAdapter.notifyItemInserted(dealsDecorationList.size)
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
            DealsDecorationAddFragment().apply {
                arguments = Bundle().apply {
                }
            }
    }
}