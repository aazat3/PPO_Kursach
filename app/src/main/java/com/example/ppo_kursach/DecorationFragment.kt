package com.example.ppo_kursach

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.setFragmentResultListener
import androidx.navigation.findNavController
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
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.ktx.storage
import kotlin.properties.Delegates

class DecorationFragment : Fragment(), View.OnClickListener {

    private lateinit var firebaseDecorationDatabase: DatabaseReference
    private lateinit var firebaseLastIdDatabase: DatabaseReference
    lateinit var decorationList: ArrayList<DecorationClass>
    lateinit var decorationAdapter: DecorationAdapter
    var lastDecorationId by Delegates.notNull<Int>()
    lateinit var storage: StorageReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        firebaseDecorationDatabase = Firebase.database.getReference("DecorationClass")
        storage = Firebase.storage.getReference("Decoration")

        firebaseLastIdDatabase = Firebase.database.getReference("LastIdentifiers/lastDecorationId")
        setFragmentResultListener("request_key") { key, bundle ->
            val returnedSaveDecoration = bundle.getParcelable<DecorationClass>("save_key")
            val returnedDeleteDecoration = bundle.getParcelable<DecorationClass>("delete_key")
//            val returnedSaveDecoration = bundle.getParcelable("extra_key", DecorationClass::class.java)
            if (returnedSaveDecoration != null) {
                if(lastDecorationId <= returnedSaveDecoration.idDecoration)
                    firebaseLastIdDatabase.setValue(returnedSaveDecoration.idDecoration)
                firebaseDecorationDatabase.child(returnedSaveDecoration.idDecoration.toString()).setValue(returnedSaveDecoration)
            }
            if (returnedDeleteDecoration != null) {
                firebaseDecorationDatabase.child(returnedDeleteDecoration.idDecoration.toString()).removeValue()
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_decoration, container, false)
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val navController = view.findNavController()
        val decorationRecyclerView = view.findViewById<RecyclerView>(R.id.decoration_recycler_view)
        decorationList= arrayListOf()
        decorationAdapter = DecorationAdapter(decorationList)
        decorationRecyclerView.layoutManager = LinearLayoutManager(context)
        decorationRecyclerView.adapter = decorationAdapter

        decorationAdapter.setOnClickListener(object :
            DecorationAdapter.OnClickListener {
            override fun onClick(position: Int, model: DecorationClass) {
                val action = DecorationFragmentDirections.actionDecorationFragmentToDecorationInfoFragment(model)
                navController.navigate(action)
            }
        })

        view.findViewById<MaterialToolbar>(R.id.toolbar).setOnMenuItemClickListener {
            when (it.itemId) {
                R.id.new_deal -> {
                    val model = DecorationClass(idDecoration = lastDecorationId + 1)
                    val action = DecorationFragmentDirections.actionDecorationFragmentToDecorationInfoFragment(model)
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
                val getLastDecorationId: Int? = snapshot.getValue(Int::class.java)
                if (getLastDecorationId != null) {
                    lastDecorationId = getLastDecorationId
                }
            }
            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(context, "Fail to get data.", Toast.LENGTH_SHORT).show()
            }
        })

        firebaseDecorationDatabase.addChildEventListener(object : ChildEventListener {
            override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                val decoration: DecorationClass? = snapshot.getValue(DecorationClass::class.java)
                if (decoration != null) {
                    Toast.makeText(context, "added", Toast.LENGTH_SHORT).show()
                    decorationList.add(decoration)
                    println(decorationList.last().idDecoration.toString())
//                    decorationList.add(decoration)
                    decorationAdapter.notifyItemInserted(decorationList.size + 1)
//                    decorationAdapter.notifyDataSetChanged()

                }
            }

            override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {
                Toast.makeText(context, snapshot.key, Toast.LENGTH_SHORT).show()
            }

            override fun onChildRemoved(snapshot: DataSnapshot) {
                decorationAdapter.notifyDataSetChanged()
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
            DecorationFragment().apply {
                arguments = Bundle().apply {
                }
            }
    }

    private fun addDecoration(decoration: DecorationClass){
        decorationList.plusAssign(decoration)
        decorationAdapter.notifyDataSetChanged()
    }

    override fun onClick(p0: View?) {
        if (p0 != null) {
            when(p0.id){
//                R.id.add_decoration -> addDecoration(decorationClass(date = "1", address = "1", ))
            }
        }
    }

}