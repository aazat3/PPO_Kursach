package com.example.ppo_kursach.deals_decoration_package

import android.os.Bundle
import android.text.InputType
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.os.bundleOf
import androidx.core.text.isDigitsOnly
import androidx.fragment.app.setFragmentResult
import androidx.fragment.app.setFragmentResultListener
import androidx.navigation.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.ppo_kursach.decoration_package.DecorationClass
import com.example.ppo_kursach.R
import com.example.ppo_kursach.deal_package.DealClass
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
    lateinit var idDealsDecorationList: ArrayList<Int>
    var lastIdDealsDecoration by Delegates.notNull<Int>()
    private val args: DealsDecorationFragmentArgs by navArgs()
    lateinit var  deal: DealClass

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        deal = args.deal
        firebaseDealsDecorationDatabase = Firebase.database.getReference("DealsDecorationClass")
        firebaseDecorationDatabase = Firebase.database.getReference("DecorationClass")
        firebaseLastIdDatabase = Firebase.database.getReference("LastIdentifiers/lastIdDealsDecoration")

        setFragmentResultListener("deals_decoration_key") { key, bundle ->

            val returnedAddDecoration = bundle.getParcelable<DecorationClass>("add_deals_decoration_key")
            if (returnedAddDecoration != null) {
                val returnedAddDealsDecoration = DealsDecorationClass(lastIdDealsDecoration+1, args.deal.idDeal, returnedAddDecoration.idDecoration, returnedAddDecoration.quantity, returnedAddDecoration.price, args.deal.date)
                if(lastIdDealsDecoration <= returnedAddDealsDecoration.idDealsDecoration)
                    lastIdDealsDecoration = returnedAddDealsDecoration.idDealsDecoration
                    firebaseLastIdDatabase.setValue(returnedAddDealsDecoration.idDealsDecoration)
                firebaseDealsDecorationDatabase.child(returnedAddDealsDecoration.idDealsDecoration.toString()).setValue(returnedAddDealsDecoration)
                Toast.makeText(context, "Добавлено", Toast.LENGTH_SHORT).show()
            }

            firebaseDealsDecorationDatabaseUpdate()
            firebaseLastIdDatabase.get().addOnSuccessListener{
                val getLastIdDealsDecoration: Int? = it.getValue(Int::class.java)
                if (getLastIdDealsDecoration != null) {
                    lastIdDealsDecoration = getLastIdDealsDecoration
                }
            }
        }

        dealsDecorationList = arrayListOf()
        idDealsDecorationList = arrayListOf()
        dealsDecorationAdapter = DealsDecorationAdapter(dealsDecorationList)

        firebaseDealsDecorationDatabaseUpdate()
        firebaseLastIdDatabase.get().addOnSuccessListener{
            val getLastIdDealsDecoration: Int? = it.getValue(Int::class.java)
            if (getLastIdDealsDecoration != null) {
                lastIdDealsDecoration = getLastIdDealsDecoration
                Toast.makeText(context, lastIdDealsDecoration.toString(), Toast.LENGTH_SHORT).show()
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

        val dealsDecorationRecyclerView = view.findViewById<RecyclerView>(R.id.deals_decoration_recycler_view)
        dealsDecorationRecyclerView.layoutManager = LinearLayoutManager(context)
        dealsDecorationRecyclerView.adapter = dealsDecorationAdapter

        dealsDecorationAdapter.setOnClickListener(object :
            DealsDecorationAdapter.OnClickListener {
            override fun onClick(position: Int, model: DecorationClass) {
                Toast.makeText(context, position.toString(), Toast.LENGTH_SHORT).show()
                val editText = EditText(context)
                editText.inputType = InputType.TYPE_CLASS_NUMBER
                val layout = LinearLayout(context)
                layout.orientation = LinearLayout.VERTICAL
                layout.addView(editText)

                val dialogBuilder = context!!.let { AlertDialog.Builder(it) }
                dialogBuilder.setTitle("Изменить")
                dialogBuilder.setMessage("Выберите количество").setView(layout)
                dialogBuilder.setPositiveButton("Сохранить"
                ) { dialog, whichButton ->
                    var quantity = dealsDecorationList[position].quantity
                    if(editText.text.isNotEmpty() && editText.text.isDigitsOnly()){
                        quantity = editText.text.toString().toInt()
                    }
                    val newModel = DealsDecorationClass(
                        idDealsDecorationList[position],
                        deal.idDeal,
                        dealsDecorationList[position].idDecoration,
                        quantity,
                        dealsDecorationList[position].price,
                        deal.date
                    )
                    firebaseDealsDecorationDatabase.child(idDealsDecorationList[position].toString()).setValue(newModel)
                    Toast.makeText(context, "Сохранено", Toast.LENGTH_SHORT).show()
                    firebaseDealsDecorationDatabaseUpdate()


                }
                dialogBuilder.setNeutralButton("Удалить") { dialog, whichButton ->
                    firebaseDealsDecorationDatabase.child(idDealsDecorationList[position].toString()).removeValue()
                    idDealsDecorationList.removeAt(position)
                    dealsDecorationAdapter.notifyItemRemoved(position)
                    dealsDecorationAdapter.notifyItemRangeChanged(position, dealsDecorationList.size)

                    Toast.makeText(context, "Удалено", Toast.LENGTH_SHORT).show()
                    firebaseDealsDecorationDatabaseUpdate()

                }
                dialogBuilder.setNegativeButton("Отмена") { dialog, whichButton ->
                    dialog.cancel()
                }
                dialogBuilder.show()
            }
        })

        view.findViewById<MaterialToolbar>(R.id.toolbar).setOnMenuItemClickListener {
            when (it.itemId) {
                R.id.add_new -> {
                    if (deal.date != ""){
                        val action =
                            DealsDecorationFragmentDirections.actionDealsDecorationFragmentToDealsDecorationAddFragment(deal)
                        view.findNavController().navigate(action)
                    } else{
                        Toast.makeText(context, "Please set data for deal", Toast.LENGTH_SHORT).show()
                    }
                    true
                }
                else -> false
            }
        }
    }

    fun firebaseDealsDecorationDatabaseUpdate(){
        firebaseDealsDecorationDatabase.get().addOnSuccessListener{
            dealsDecorationList.clear()
            idDealsDecorationList.clear()
            for (dataSnapshot in it.children) {
                val dealsDecoration = dataSnapshot.getValue(DealsDecorationClass::class.java)
                if (dealsDecoration != null) {
                    if(dealsDecoration.idDeal == deal.idDeal && deal.date != "")
                        firebaseDecorationDatabase.get().addOnSuccessListener{
                            for (dataSnapshot2 in it.children) {
                                val getDecoration = dataSnapshot2.getValue(DecorationClass::class.java)
                                if (getDecoration != null){
                                    if(getDecoration.idDecoration == dealsDecoration.idDecoration){
                                        getDecoration.quantity = dealsDecoration.quantity
                                        getDecoration.price = dealsDecoration.price
                                        dealsDecorationList.add(getDecoration)
                                        idDealsDecorationList.add(dealsDecoration.idDealsDecoration)
                                    }
                                }
                            }
                            dealsDecorationAdapter.notifyDataSetChanged()
                        }
                }
            }
        }
    }
}