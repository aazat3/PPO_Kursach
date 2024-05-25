package com.example.ppo_kursach.users_deal_package

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.navArgs
import com.example.ppo_kursach.R
import com.example.ppo_kursach.deal_package.DealClass
import com.example.ppo_kursach.user_package.UserClass
import com.google.firebase.database.DatabaseReference
import kotlin.properties.Delegates

class DealsUserFragment : Fragment() {
    private lateinit var firebaseUsersDealDatabase: DatabaseReference
    private lateinit var firebaseLastIdDatabase: DatabaseReference
    private lateinit var firebaseDealDatabase: DatabaseReference
    lateinit var dealsUserList: ArrayList<UserClass>
    lateinit var dealsUserAdapter: DealsUserAdapter
    var lastIdDealsDecoration by Delegates.notNull<Int>()
    private val args: UsersDealFragmentArgs by navArgs()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        return inflater.inflate(R.layout.fragment_deals_user, container, false)
    }

}