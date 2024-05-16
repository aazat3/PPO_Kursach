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

class StatisticFragment : Fragment() {

    private lateinit var firebaseDealDatabase: DatabaseReference
    lateinit var dealList: ArrayList<DealClass>
    lateinit var dealAdapter: DealAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        firebaseDealDatabase = Firebase.database.getReference("StatisticClass")

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val navController = view.findNavController()
        val dealRecyclerView = view.findViewById<RecyclerView>(R.id.deal_recycler_view)
        dealList= arrayListOf()
        dealAdapter = DealAdapter(dealList)
        dealRecyclerView.layoutManager = LinearLayoutManager(context)
        dealRecyclerView.adapter = dealAdapter

        dealAdapter.setOnClickListener(object :
            DealAdapter.OnClickListener {
            override fun onClick(position: Int, model: DealClass) {
                val action = DealFragmentDirections.actionDealFragmentToDealInfoFragment(model)
                navController.navigate(action)
            }
        })

        firebaseDealDatabase.addChildEventListener(object : ChildEventListener {
            override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                val deal: DealClass? = snapshot.getValue(DealClass::class.java)
                if (deal != null) {
                    Toast.makeText(context, "added", Toast.LENGTH_SHORT).show()
                    dealList.add(deal)
                    dealAdapter.notifyItemInserted(dealList.size)
                }
            }

            override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {
                Toast.makeText(context, "changed", Toast.LENGTH_SHORT).show()
            }

            override fun onChildRemoved(snapshot: DataSnapshot) {
                dealAdapter.notifyDataSetChanged()
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

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_statistic, container, false)
    }

    companion object {
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            StatisticFragment().apply {
                arguments = Bundle().apply {

                }
            }
    }
}