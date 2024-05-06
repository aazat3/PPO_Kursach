package com.example.ppo_kursach

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.navigation.fragment.navArgs

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "deal"
private const val ARG_PARAM2 = "param2"
 /**
 * A simple [Fragment] subclass.
 * Use the [DealInfoFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class DealInfoFragment : Fragment() {

    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
     private val args: DealInfoFragmentArgs by navArgs()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val deal = args.deal
        val view = inflater.inflate(R.layout.fragment_deal_info, container, false)
        view.findViewById<TextView>(R.id.date).text = deal.date
        view.findViewById<TextView>(R.id.address).text = deal.address
        view.findViewById<TextView>(R.id.user).text = deal.user
        view.findViewById<TextView>(R.id.client).text = deal.client

        return view
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment DealInfoFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            DealInfoFragment().apply {
                arguments = Bundle().apply {
                }
            }
    }
}