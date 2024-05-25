package com.example.ppo_kursach

import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.annotation.NonNull
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import com.example.ppo_kursach.user_package.UserClass
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import kotlin.properties.Delegates


class AuthenticationFragment : Fragment() {

    lateinit var mAuth: FirebaseAuth
    lateinit var email: String
    lateinit var passord: String
    private lateinit var firebaseUserDatabase: DatabaseReference
    private lateinit var firebaseLastIdDatabase: DatabaseReference
    var lastIdUser by Delegates.notNull<Int>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mAuth = FirebaseAuth.getInstance()
        firebaseUserDatabase = Firebase.database.getReference("UserClass")
        firebaseLastIdDatabase = Firebase.database.getReference("LastIdentifiers/lastIdUser")
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        val view = inflater.inflate(R.layout.fragment_authentication, container, false)
        view.findViewById<Button>(R.id.login_button).setOnClickListener{
            loginUserAccount(view)
        }
        view.findViewById<Button>(R.id.register_button).setOnClickListener{
            registerNewUser(view)
        }

        firebaseLastIdDatabase.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val getLastIdUser: Int? = snapshot.getValue(Int::class.java)
                if (getLastIdUser != null) {
                    lastIdUser = getLastIdUser
                }
            }
            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(context, "Fail to get data.", Toast.LENGTH_SHORT).show()
            }
        })

        return view
    }

    private fun loginUserAccount(view: View) {

        val email = view.findViewById<EditText>(R.id.email).text.toString()
        val password = view.findViewById<EditText>(R.id.password).text.toString()

        if (TextUtils.isEmpty(email)) {
            Toast.makeText(
                context,
                "Please enter email!!",
                Toast.LENGTH_LONG
            ).show()
            return
        }
        if (TextUtils.isEmpty(password)) {
            Toast.makeText(
                context,
                "Please enter password!!",
                Toast.LENGTH_LONG
            ).show()
            return
        }

        mAuth
            .signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Toast.makeText(
                        context,
                        "Login successful!!",
                        Toast.LENGTH_LONG
                    ).show()
                    view.findNavController().navigate(R.id.dealFragment)
                } else {
                    Toast.makeText(
                        context,
                        "Login failed!!",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
    }

    private fun registerNewUser(view: View) {

        val email = view.findViewById<EditText>(R.id.email).text.toString()
        val password = view.findViewById<EditText>(R.id.password).text.toString()

        if (TextUtils.isEmpty(email)) {
            Toast.makeText(
                context,
                "Please enter email!!",
                Toast.LENGTH_LONG
            ).show()
            return
        }
        if (TextUtils.isEmpty(password)) {
            Toast.makeText(
                context,
                "Please enter password!!",
                Toast.LENGTH_LONG
            ).show()
            return
        }

        mAuth
            .createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Toast.makeText(
                        context,
                        "Registration successful!",
                        Toast.LENGTH_LONG
                    ).show()

                    val currentFirebaseUser = FirebaseAuth.getInstance().currentUser
                    val user = currentFirebaseUser?.let {
                        UserClass(lastIdUser + 1, email, it.uid)
                    }

                    firebaseUserDatabase.child((lastIdUser + 1).toString()).setValue(user)
                    firebaseLastIdDatabase.setValue(lastIdUser + 1)

                    view.findNavController().navigate(R.id.dealFragment)

                } else {
                    Toast.makeText(
                        context, "Registration failed!!"
                                + " Please try again later",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
    }

}