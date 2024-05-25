package com.example.ppo_kursach.deals_decoration_package

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.core.os.bundleOf
import androidx.fragment.app.setFragmentResult
import androidx.navigation.findNavController
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.example.ppo_kursach.decoration_package.DecorationClass
import com.example.ppo_kursach.R
import com.google.firebase.storage.FirebaseStorage

class DealsDecorationInfoFragment : Fragment() {
    private val args: DealsDecorationInfoFragmentArgs by navArgs()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        val decoration = args.decoration
        val view = inflater.inflate(R.layout.fragment_deals_decoration_info, container, false)
        val idDecoration = view.findViewById<TextView>(R.id.id_decoration)
        val name = view.findViewById<TextView>(R.id.name)
        val type = view.findViewById<TextView>(R.id.type)
        val quantity =  view.findViewById<TextView>(R.id.quantity)
        val condition = view.findViewById<TextView>(R.id.condition)
        val price = view.findViewById<TextView>(R.id.price)
        val difficultyInst = view.findViewById<TextView>(R.id.difficultyInst)
        val difficultyTr = view.findViewById<TextView>(R.id.difficultyTr)
        val photo = view.findViewById<ImageView>(R.id.photo)

        idDecoration.text = decoration.idDecoration.toString()
        name.text = decoration.name.toString()
        type.text = decoration.type.toString()
        quantity.text = decoration.quantity.toString()
        condition.text = decoration.condition.toString()
        price.text = decoration.price.toString()
        difficultyInst.text = decoration.difficultyInst.toString()
        difficultyTr.text = decoration.difficultyTr.toString()
        var photoName = ""
        val storage = FirebaseStorage.getInstance().getReference("Decoration")
        if (decoration.photo != ""){
            val gsReference = storage.child(decoration.photo)
            photoName = gsReference.name
            Glide.with(photo.context)
                .load(gsReference)
                .into(photo)
        }else { photoName = ""}



        view.findViewById<Button>(R.id.save_decoration).setOnClickListener{
            val model = DecorationClass(
                decoration.idDecoration,
                name.text.toString(),
                type.text.toString().toInt(),
                quantity.text.toString().toInt(),
                condition.text.toString().toInt(),
                price.text.toString().toInt(),
                difficultyInst.text.toString().toInt(),
                difficultyTr.text.toString().toInt(),
                photoName)
            setFragmentResult(
                "request_key",
                bundleOf("save_key" to model)
            )
            view.findNavController().navigateUp()
        }

        view.findViewById<Button>(R.id.delete_decoration).setOnClickListener{
            val model = DecorationClass(decoration.idDecoration)
            setFragmentResult(
                "request_key",
                bundleOf("delete_key" to model)
            )
            view.findNavController().navigateUp()
        }
        return view
    }

    companion object {
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            DealsDecorationInfoFragment().apply {
                arguments = Bundle().apply {

                }
            }
    }
}