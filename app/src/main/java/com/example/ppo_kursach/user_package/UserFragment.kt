package com.example.ppo_kursach.user_package

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.navigation.findNavController
import com.example.ppo_kursach.R
import com.example.ppo_kursach.deal_package.DealClass
import com.example.ppo_kursach.deal_package.DealFragmentDirections
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import kotlin.math.log

class UserFragment : Fragment() {
    private lateinit var firebaseUserDatabase: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        firebaseUserDatabase = Firebase.database.getReference("UserClass")
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        return inflater.inflate(R.layout.fragment_user, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        var typeInt = 0
        val idUser = view.findViewById<TextView>(R.id.id_user)
        val name = view.findViewById<EditText>(R.id.name)
        val login = view.findViewById<EditText>(R.id.login)
//        val type = view.findViewById<EditText>(R.id.type)
        val userNumber =  view.findViewById<EditText>(R.id.user_number)
        val currentFirebaseUser = FirebaseAuth.getInstance().currentUser?.uid

        val typeArray = resources.getStringArray(R.array.user_type_array)
        var type = view.findViewById<AutoCompleteTextView>(R.id.type)



        firebaseUserDatabase.get().addOnSuccessListener{
                for (dataSnapshot in it.children) {
                    val item = dataSnapshot.getValue(UserClass::class.java)

                    if (item!!.uid == currentFirebaseUser!!.toString()){
                        idUser.text = item.idUser.toString()
                        name.setText(item.name)
                        login.setText(item.login)
//                        type.setText(item.type)
                        userNumber.setText(item.userNumber)

                        typeInt = item.type
                        type.setText(when(item.type){
                            1 -> "Администратор"
                            2 -> "Декоратор"
                            3 -> "Монтажник"
                            else -> ""
                        })

                        if (type != null) {
                            var typeAdapter = ArrayAdapter(
                                requireContext(),
                                android.R.layout.simple_list_item_1, typeArray)
                            type.setAdapter(typeAdapter)

                            type.setOnItemClickListener { parent, view, position, id ->
                                typeInt = when(typeArray[position]){
                                    "Администратор" -> 1
                                    "Декоратор" -> 2
                                    "Монтажник" -> 3
                                    else -> 0
                                }
                            }

                        }
                    }

                }
        }

        view.findViewById<Button>(R.id.save_user).setOnClickListener{
            val user = UserClass(idUser.text.toString().toInt(), name.text.toString(), currentFirebaseUser.toString(), typeInt, login.text.toString(), userNumber.text.toString())
            firebaseUserDatabase.child(user.idUser.toString()).setValue(user)
        }

        view.findViewById<Button>(R.id.users_deal).setOnClickListener{
            val user = UserClass(idUser.text.toString().toInt(), name.text.toString(), currentFirebaseUser.toString(), typeInt, login.text.toString(), userNumber.text.toString())
            val action = UserFragmentDirections.actionUserFragmentToUsersDealFragment(user)
            view.findNavController().navigate(action)
        }
    }

}