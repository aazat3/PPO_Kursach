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
        val view = inflater.inflate(R.layout.fragment_decoration_info, container, false)
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
        } else { photoName = ""}


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

        var imagePickerActivityResult: ActivityResultLauncher<Intent> =
            registerForActivityResult( ActivityResultContracts.StartActivityForResult()) { result ->
                if (result != null) {
                    val imageUri: Uri? = result.data?.data
                    val sd = getFileName(requireContext(), imageUri!!)
                    val uploadTask = storage.child("$sd").putFile(imageUri)
                    val deleteRef = storage.child(photoName)
                    photoName = sd.toString()

                    uploadTask.addOnSuccessListener {
                        storage.child("$sd").downloadUrl.addOnSuccessListener {
                            Glide.with(this)
                                .load(it)
                                .into(photo)
                            deleteRef.delete().addOnSuccessListener {
                                // File deleted successfully
                            }.addOnFailureListener {
                                Log.e("Firebase", "Failed in deleting")
                            }
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