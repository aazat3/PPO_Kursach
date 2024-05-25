package com.example.ppo_kursach.user_package

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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

        var idUser = view.findViewById<TextView>(R.id.id_user)
        var name = view.findViewById<EditText>(R.id.name)
        var login = view.findViewById<EditText>(R.id.login)
        var type = view.findViewById<EditText>(R.id.type)
        var userNumber =  view.findViewById<EditText>(R.id.user_number)
        val currentFirebaseUser = FirebaseAuth.getInstance().currentUser?.uid

        firebaseUserDatabase.addValueEventListener(object :ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                for (dataSnapshot in snapshot.children) {
                    val item = dataSnapshot.getValue(UserClass::class.java)

                    if (item!!.uid == currentFirebaseUser!!.toString()){
                        idUser.text = item.idUser.toString()
                        name.setText(item.name)
                        login.setText(item.login)
                        type.setText(item.type)
                        userNumber.setText(item.userNumber)
                    }

                }
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(context, "Error", Toast.LENGTH_LONG).show()
            }

        })

        view.findViewById<Button>(R.id.save_user).setOnClickListener{
            val user = UserClass(idUser.text.toString().toInt(), name.text.toString(), currentFirebaseUser.toString(), type.text.toString(), login.text.toString(), userNumber.text.toString())
            firebaseUserDatabase.child(user.idUser.toString()).setValue(user)
        }

        view.findViewById<Button>(R.id.users_deal).setOnClickListener{
            val user = UserClass(idUser.text.toString().toInt(), name.text.toString(), currentFirebaseUser.toString(), type.text.toString(), login.text.toString(), userNumber.text.toString())
            val action = UserFragmentDirections.actionUserFragmentToUsersDealFragment(user)
            view.findNavController().navigate(action)
        }
    }

}