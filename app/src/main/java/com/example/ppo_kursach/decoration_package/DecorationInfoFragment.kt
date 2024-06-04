package com.example.ppo_kursach.decoration_package

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.OpenableColumns
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.os.bundleOf
import androidx.fragment.app.setFragmentResult
import androidx.navigation.findNavController
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.example.ppo_kursach.decoration_package.DecorationInfoFragmentArgs
import com.example.ppo_kursach.R
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference

class DecorationInfoFragment : Fragment() {
    private val args: DecorationInfoFragmentArgs by navArgs()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {


        val decoration = args.decoration
        var typeInt = decoration.type
        var conditionInt = decoration.condition
        var difficultyInstInt = decoration.difficultyInst
        var difficultyTrInt = decoration.difficultyTr

        val view = inflater.inflate(R.layout.fragment_decoration_info, container, false)
        val idDecoration = view.findViewById<TextView>(R.id.id_decoration)
        val name = view.findViewById<TextView>(R.id.name)
//        val type = view.findViewById<TextView>(R.id.type)
        val quantity =  view.findViewById<TextView>(R.id.quantity)
//        val condition = view.findViewById<TextView>(R.id.condition)
        val price = view.findViewById<TextView>(R.id.price)
//        val difficultyInst = view.findViewById<TextView>(R.id.difficultyInst)
//        val difficultyTr = view.findViewById<TextView>(R.id.difficultyTr)
        val photo = view.findViewById<ImageView>(R.id.photo)

        val typeArray = resources.getStringArray(R.array.decor_type_array)
        var type = view.findViewById<AutoCompleteTextView>(R.id.type)
        type.setText(when(decoration.type){
            1 -> "Цветы"
            2 -> "Конструкции"
            3 -> "Прочее"
            else -> ""
        })
        if (type != null) {
            var typeAdapter = ArrayAdapter(
                requireContext(),
                android.R.layout.simple_list_item_1, typeArray)
            type.setAdapter(typeAdapter)
            type.setOnItemClickListener { parent, view, position, id ->
                typeInt = when(typeArray[position]){
                    "Цветы" -> 1
                    "Конструкции" -> 2
                    "Прочее" -> 3
                    else -> 0
                }
            }
        }

        val conditionArray = resources.getStringArray(R.array.condition_array)
        var condition = view.findViewById<AutoCompleteTextView>(R.id.condition)
        condition.setText(when(decoration.condition){
            1 -> "Хорошее"
            2 -> "Среднее"
            3 -> "Плохое"
            else -> ""
        })
        if (condition != null) {
            var conditionAdapter = ArrayAdapter(
                requireContext(),
                android.R.layout.simple_list_item_1, conditionArray)
            condition.setAdapter(conditionAdapter)
            condition.setOnItemClickListener { parent, view, position, id ->
                conditionInt = when(conditionArray[position]){
                    "Хорошее" -> 1
                    "Среднее" -> 2
                    "Плохое" -> 3
                    else -> 0
                }
            }
        }

        val difficultyInstArray = resources.getStringArray(R.array.difficulty_array)
        var difficultyInst = view.findViewById<AutoCompleteTextView>(R.id.difficultyInst)
        difficultyInst.setText(when(decoration.difficultyInst){
            1 -> "Легко"
            2 -> "Средне"
            3 -> "Сложно"
            4 -> "Очень сложно"
            else -> ""
        })
        if (difficultyInst != null) {
            var difficultyInstAdapter = ArrayAdapter(
                requireContext(),
                android.R.layout.simple_list_item_1, difficultyInstArray)
            difficultyInst.setAdapter(difficultyInstAdapter)
            difficultyInst.setOnItemClickListener { parent, view, position, id ->
                difficultyInstInt = when(difficultyInstArray[position]){
                    "Легко" -> 1
                    "Средне" -> 2
                    "Сложно" -> 3
                    "Очень сложно" -> 4
                    else -> 0
                }
            }
        }

        val difficultyTrArray = resources.getStringArray(R.array.difficulty_array)
        var difficultyTr = view.findViewById<AutoCompleteTextView>(R.id.difficultyTr)
        difficultyTr.setText(when(decoration.difficultyTr){
            1 -> "Легко"
            2 -> "Средне"
            3 -> "Сложно"
            4 -> "Очень сложно"
            else -> ""
        })
        if (difficultyTr != null) {
            var difficultyTrAdapter = ArrayAdapter(
                requireContext(),
                android.R.layout.simple_list_item_1, difficultyTrArray)
            difficultyTr.setAdapter(difficultyTrAdapter)
            difficultyTr.setOnItemClickListener { parent, view, position, id ->
                difficultyTrInt = when(difficultyTrArray[position]){
                    "Легко" -> 1
                    "Средне" -> 2
                    "Сложно" -> 3
                    "Очень сложно" -> 4
                    else -> 0
                }
            }
        }


        idDecoration.text = decoration.idDecoration.toString()
        name.text = decoration.name.toString()
//        type.text = decoration.type.toString()
        quantity.text = decoration.quantity.toString()
//        condition.text = decoration.condition.toString()
        price.text = decoration.price.toString()
//        difficultyInst.text = decoration.difficultyInst.toString()
//        difficultyTr.text = decoration.difficultyTr.toString()

        var photoName = ""
        val storage = FirebaseStorage.getInstance().getReference("Decoration")
        if (decoration.photo != ""){
            val gsReference = storage.child(decoration.photo)
            photoName = gsReference.name
            Glide.with(photo.context)
                .load(gsReference)
                .into(photo)
        } else { photoName = ""}


        view.findViewById<Button>(R.id.save_decoration).setOnClickListener{
            val model = DecorationClass(
                decoration.idDecoration,
                name.text.toString(),
                typeInt,
                quantity.text.toString().toInt(),
                conditionInt,
                price.text.toString().toInt(),
                difficultyInstInt,
                difficultyTrInt,
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

        var imagePickerActivityResult: ActivityResultLauncher<Intent> =
            registerForActivityResult( ActivityResultContracts.StartActivityForResult()) { result ->
                if (result != null) {
                    val imageUri: Uri? = result.data?.data
                    val sd = getFileName(requireContext(), imageUri!!)
                    val uploadTask = storage.child("$sd").putFile(imageUri)
                    var deleteRef: StorageReference
                    if(photoName != ""){
                        deleteRef = storage.child(photoName)
                        deleteRef.delete().addOnSuccessListener {
                            // File deleted successfully
                        }.addOnFailureListener {
                            Log.e("Firebase", "Failed in deleting")
                        }
                    }
                    photoName = sd.toString()

                    uploadTask.addOnSuccessListener {
                        storage.child("$sd").downloadUrl.addOnSuccessListener {
                            Glide.with(this)
                                .load(it)
                                .into(photo)
                            Log.e("Firebase", "download passed")
                        }.addOnFailureListener {
                            Log.e("Firebase", "Failed in downloading")
                        }
                    }.addOnFailureListener {
                        Log.e("Firebase", "Image Upload fail")
                    }
                }
            }


        view.findViewById<Button>(R.id.set_image).setOnClickListener{
            val galleryIntent = Intent(Intent.ACTION_PICK)
            galleryIntent.type = "image/*"
            imagePickerActivityResult.launch(galleryIntent)

        }

        return view
    }

    @SuppressLint("Range")
    fun getFileName(context: Context, uri: Uri): String? {
        if (uri.scheme == "content") {
            val cursor = context.contentResolver.query(uri, null, null, null, null)
            cursor.use {
                if (cursor != null) {
                    if(cursor.moveToFirst()) {
                        return cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME))
                    }
                }
            }
        }
        return uri.path?.lastIndexOf('/')?.let { uri.path?.substring(it) }
    }
}