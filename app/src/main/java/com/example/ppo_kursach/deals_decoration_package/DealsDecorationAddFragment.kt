package com.example.ppo_kursach.deals_decoration_package

import android.content.DialogInterface
import android.os.Bundle
import android.text.InputType
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.appcompat.widget.Toolbar
import androidx.core.os.bundleOf
import androidx.core.text.isDigitsOnly
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResult
import androidx.fragment.app.setFragmentResultListener
import androidx.navigation.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.ppo_kursach.R
import com.example.ppo_kursach.deal_package.DealClass
import com.example.ppo_kursach.decoration_package.DecorationClass
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import kotlin.properties.Delegates


class DealsDecorationAddFragment : Fragment() {

    private lateinit var firebaseDealsDecorationDatabase: DatabaseReference
    private lateinit var firebaseDecorationDatabase: DatabaseReference
    lateinit var dealsDecorationList: ArrayList<DecorationClass>
    lateinit var dealsDecorationAdapter: DealsDecorationAdapter
    private val args: DealsDecorationFragmentArgs by navArgs()
    lateinit var deal: DealClass
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        firebaseDealsDecorationDatabase = Firebase.database.getReference("DealsDecorationClass")
        firebaseDecorationDatabase = Firebase.database.getReference("DecorationClass")
        setHasOptionsMenu(true)
        deal = args.deal

        dealsDecorationList= arrayListOf()
        dealsDecorationAdapter = DealsDecorationAdapter(dealsDecorationList)


        firebaseDecorationDatabaseUpdate()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {


        val view = inflater.inflate(R.layout.fragment_deals_decoration_add, container, false)
        val toolbar = view.findViewById<Toolbar>(R.id.toolbar)
        (requireActivity() as AppCompatActivity).setSupportActionBar(toolbar)
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
                val editText = EditText(context)
                editText.inputType = InputType.TYPE_CLASS_NUMBER
                val layout = LinearLayout(context)
                layout.orientation = LinearLayout.VERTICAL
                layout.addView(editText)

                val dialogBuilder = context!!.let { AlertDialog.Builder(it) }
                dialogBuilder.setTitle("Добавить")
                dialogBuilder.setMessage("Выберите количество").setView(layout)
                dialogBuilder.setPositiveButton("Добавить"
                ) { dialog, whichButton ->
                    var quantity = dealsDecorationList[position].quantity
                    if(editText.text.isNotEmpty() && editText.text.isDigitsOnly()){
                        quantity = editText.text.toString().toInt()
                    }
                    val newModel = DecorationClass(
                        model.idDecoration,
                        model.name,
                        model.type,
                        quantity,
                        model.condition,
                        model.price,
                        model.difficultyInst,
                        model.difficultyTr,
                        model.photo)
                    setFragmentResult(
                        "deals_decoration_key",
                        bundleOf("add_deals_decoration_key" to newModel)
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
        val filteredList: ArrayList<DecorationClass> = ArrayList()

        for (item in dealsDecorationList) {
            if (item.name.lowercase().contains(text.lowercase()) || item.type.toString().lowercase().contains(text.lowercase())) {
                filteredList.add(item)
            }
        }
        if (filteredList.isEmpty()) {
            Toast.makeText(context, "No Data Found..", Toast.LENGTH_SHORT).show()
        } else {
            dealsDecorationAdapter.filterList(filteredList)
        }
    }

    private fun firebaseDecorationDatabaseUpdate(){
        firebaseDecorationDatabase.get().addOnSuccessListener{
            dealsDecorationList.clear()
            for (dataSnapshot in it.children) {
                val decoration = dataSnapshot.getValue(DecorationClass::class.java)
                if (decoration != null) {
                    var decorationQuantity = decoration.quantity
                    firebaseDealsDecorationDatabase.get().addOnSuccessListener{
                        for (dataSnapshot2 in it.children) {
                            val dealsDecoration = dataSnapshot2.getValue(
                                DealsDecorationClass::class.java)
                            if (dealsDecoration != null){
                                if(decoration.idDecoration == dealsDecoration.idDecoration
                                    && (deal.date == dealsDecoration.date && deal.date != "")){
                                    decorationQuantity -= dealsDecoration.quantity
                                }
                            }
                        }
                        if (decorationQuantity > 0){
                            decoration.quantity = decorationQuantity
                            dealsDecorationList.add(decoration)
                            dealsDecorationAdapter.notifyDataSetChanged()
                        }
                    }
                }
            }
        }
    }
}