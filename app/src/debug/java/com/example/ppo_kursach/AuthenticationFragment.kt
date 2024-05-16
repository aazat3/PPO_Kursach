package com.example.ppo_kursach

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import com.google.firebase.auth.FirebaseAuth


class AuthenticationFragment : Fragment() {

    lateinit var mAuth: FirebaseAuth
    lateinit var email: String
    lateinit var passord: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mAuth = FirebaseAuth.getInstance();
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_authentication, container, false)
        view.findViewById<Button>(R.id.login_button).setOnClickListener{
            loginUserAccount(view)
        }
        view.findViewById<Button>(R.id.register_button).setOnClickListener{
            registerNewUser(view)
        }
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

    companion object {
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            AuthenticationFragment().apply {
                arguments = Bundle().apply {
                }
            }
    }
}