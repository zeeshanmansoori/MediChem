package com.example.anew.ui.admin.add

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore.ACTION_IMAGE_CAPTURE
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.example.anew.R
import com.example.anew.databinding.FragmentAddBinding
import com.example.anew.model.*
import com.example.anew.utils.CustomLoadingDialog
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import com.google.firebase.storage.ktx.storageMetadata
import java.io.ByteArrayOutputStream

const val TAKE_IMAGE = 4
const val PRODUCT_REF = "Products"
const val IMAGE_REF = "Images"
const val CART_REF  = "cart"

class AddFragment : Fragment(), View.OnClickListener {


    private lateinit var firebaseStore: FirebaseFirestore
    private lateinit var mAuth: FirebaseAuth

    private lateinit var binding: FragmentAddBinding

    //image from gallery app
    private var imageUri: Uri? = null

    //image from camera app
    private var imageByteArray: ByteArray? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        activity?.run {
            if (
                ActivityCompat.checkSelfPermission(
                    baseContext,
                    Manifest.permission.CAMERA
                ) == PackageManager.PERMISSION_GRANTED

            ) {
                Log.d("mytag", "permission granted")
            } else {
                Log.d("mytag", "permission not granted")
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.CAMERA), TAKE_IMAGE
                )
            }
        }
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_add, container, false)
        firebaseStore = FirebaseFirestore.getInstance()
        mAuth = FirebaseAuth.getInstance()
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.medicineImageImageView.setOnClickListener(this)
        binding.submitBtn.setOnClickListener(this)


    }


    private fun addToDb() {

        val dialog = CustomLoadingDialog(activity as AppCompatActivity)

        val imageRef = Firebase.storage.getReference(IMAGE_REF)

        with(binding) {
            val id = medicineIdEditText.text.toString().trim()
            val description = medicineDescriptionEditText.text.toString().trim()
            val prize = if (medicinePrizeEditText.text.toString()
                    .isEmpty()
            ) (0.0).toFloat() else medicinePrizeEditText.text.toString().toFloat()
            val expDate = expDateEditText.text.toString().trim()
            val manName = manufacturerNameEditText.text.toString().trim()
            val name = medicineNameEditText.text.toString().trim()
            val quantity = numberPicker.number.toInt()

            if (id.isEmpty()) {
                medicineIdEditText.error = "id is empty"
                medicineIdEditText.requestFocus()
                return
            }
            if (id.length < 10) {
                medicineIdEditText.error = "id should be of length 10"
                medicineIdEditText.requestFocus()
                return
            }

            if (name.isEmpty()) {
                medicineNameEditText.error = "medicine Name is empty"
                medicineNameEditText.requestFocus()
                return
            }

            if (description.isEmpty()) {
                medicineDescriptionEditText.error = "medicine Description is empty"
                medicineDescriptionEditText.requestFocus()
                return
            }

            if (expDate.isEmpty()) {
                expDateEditText.error = "expDate is empty"
                expDateEditText.requestFocus()
                return
            }

            dialog.startDialog()

            firebaseStore.collection(PRODUCT_REF).document(id).get().addOnCompleteListener {
                if (it.isSuccessful) {
                    val exist = it.result?.exists() ?: false
                    if (exist) {
                        dialog.dismissDialog()
                        Snackbar.make(root, "data already exist ", Snackbar.LENGTH_SHORT)
                            .show()
                        return@addOnCompleteListener
                    }

                    //check whether user has selected any image or not
                    // adding image with data

                    val medicineImageRef = imageRef.child("${id}r1.jpg")
                    var metadata = storageMetadata { contentType = "image/jpg" }

                    if (imageByteArray == null && imageUri == null) {

//                            val bitmap =
//                                BitmapFactory.decodeResource(resources, R.drawable.admin_medicine)

                        //setimage resource first

                        //val bitmap = (binding.medicineImageImageView.drawable as BitmapDrawable).bitmap
//
//                            val baos = ByteArrayOutputStream()
//
//                            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos)
//
//                            val data = baos.toByteArray()

                        firebaseStore.collection(PRODUCT_REF).document(id)
                            .set(
                                Product(
                                    id,
                                    name,
                                    description,
                                    expDate,
                                    quantity,
                                    prize,
                                    manName


                                )
                            )
                            .addOnSuccessListener {
                                dialog.dismissDialog()
                                Snackbar.make(
                                    binding.root,
                                    "medicine added",
                                    Snackbar.LENGTH_SHORT
                                )
                                    .show()

                            }

                    }

                    // put image picked from gallery
                    imageUri?.let { uri ->
                        medicineImageRef.putFile(uri)
                            .addOnSuccessListener { _ ->
                                medicineImageRef.downloadUrl.addOnSuccessListener { downloadUri ->
                                    val image = downloadUri.toString()

                                    firebaseStore.collection(PRODUCT_REF).document(id)
                                        .set(
                                            Product(
                                                id,
                                                name,
                                                description,
                                                expDate,
                                                quantity,
                                                prize,
                                                manName,
                                                image
                                            )
                                        )
                                        .addOnSuccessListener {
                                            dialog.dismissDialog()
                                            Snackbar.make(
                                                binding.root,
                                                "medicine added",
                                                Snackbar.LENGTH_SHORT
                                            )
                                                .show()

                                        }

                                }
                            }

                        // put image captured from camera
                    } ?: imageByteArray?.let {
                        medicineImageRef.putBytes(it)
                            .addOnSuccessListener { _ ->
                                medicineImageRef.downloadUrl.addOnSuccessListener { downloadUri ->
                                    val image = downloadUri.toString()
                                    firebaseStore.collection(PRODUCT_REF).document(id)
                                        .set(
                                            Product(
                                                id,
                                                name,
                                                description,
                                                expDate,
                                                quantity,
                                                prize,
                                                manName,
                                                image
                                            )
                                        )
                                        .addOnSuccessListener {
                                            dialog.dismissDialog()
                                            Snackbar.make(
                                                binding.root,
                                                "medicine added",
                                                Snackbar.LENGTH_SHORT
                                            )
                                                .show()

                                        }

                                }
                            }
                    }
                } else {
                    dialog.dismissDialog()
                }
            }

//                databaseReference.addListenerForSingleValueEvent(object : ValueEventListener {
//
//                    override fun onDataChange(snapshot: DataSnapshot) {
//                        if (!snapshot.child(id).exists()) {
//                            val map = HashMap<String, Any>()
//                            map["id"] = id
//                            map["name"] = name
//                            map["description"] = description
//                            map["expDate"] = expDate
//                            map["prize"] = prize
//                            map["manName"] = manName
//                            map["quantity"] = quantity
//
//                            databaseReference.child(id).updateChildren(map).addOnCompleteListener {
//                                if (it.isSuccessful) {
//                                    Snackbar.make(
//                                        binding.root,
//                                        "medicine added",
//                                        Snackbar.LENGTH_SHORT
//                                    )
//                                        .show()
//                                } else {
//                                    dialog.dismissDialog()
//                                    Snackbar.make(
//                                        binding.root,
//                                        "task is unsuccessful",
//                                        Snackbar.LENGTH_SHORT
//                                    )
//                                        .show()
//                                }
//                            }
//
//                            val medicineImageRef = imageRef.child("$id.jpg")
//                            imageUri?.let {
//
//                                val uploadTask = medicineImageRef.putFile(it)
//                                uploadTask.addOnSuccessListener {
//                                    Log.d("mytag", "uploadtask file success")
//                                    Snackbar.make(
//                                        binding.root,
//                                        "image upload done",
//                                        Snackbar.LENGTH_SHORT
//                                    ).show()
//                                }.addOnFailureListener {
//                                    Log.d("mytag", "uploadtask file failure")
//                                    Snackbar.make(
//                                        binding.root,
//                                        "image upload failed $it",
//                                        Snackbar.LENGTH_SHORT
//                                    ).show()
//                                }
//
//
//                            } ?: imageByteArray?.let {
//                                val uploadTask = medicineImageRef.putBytes(it)
//                                uploadTask.addOnSuccessListener {
//                                    Log.d("mytag", "uploadtask byte success")
//                                    Snackbar.make(
//                                        binding.root,
//                                        "image upload done",
//                                        Snackbar.LENGTH_SHORT
//                                    ).show()
//                                }.addOnFailureListener {
//                                    Log.d("mytag", "uploadtask byte failure")
//                                    Snackbar.make(
//                                        binding.root,
//                                        "image upload failed $it",
//                                        Snackbar.LENGTH_SHORT
//                                    ).show()
//                                }
//
//                            }
//                            dialog.dismissDialog()
//                        } else {
//                            dialog.dismissDialog()
//                            Snackbar.make(
//                                binding.root,
//                                "medicine already exists",
//                                Snackbar.LENGTH_SHORT
//                            ).show()
//                        }
//
//                    }
//
//                    override fun onCancelled(error: DatabaseError) {
//                        Log.d("mytag", "database error " + error.message)
//                    }
//                })


        }
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.medicine_image_image_view -> takePhoto()
            R.id.submit_btn -> addToDb()
        }
    }

    private fun takePhoto() {
        val itemList = arrayOf<CharSequence>("take photo", "pick from gallery")

        //simple alert dialog
        val builder = AlertDialog.Builder(context).apply {
            setTitle("Add photo")
            setItems(itemList) { dialog, which ->

                val intent = Intent()
                when {
                    itemList[which] == itemList[0] -> intent.action = ACTION_IMAGE_CAPTURE
                    itemList[which] == itemList[1] -> {
                        intent.action = Intent.ACTION_GET_CONTENT
                        intent.type = "image/*"

                    }
                    else -> dialog.dismiss()
                }
                startActivityForResult(intent, TAKE_IMAGE)
            }
        }
        builder.show()
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == TAKE_IMAGE && resultCode == Activity.RESULT_OK) {
            Log.d("mytag", "if" + data?.data.toString())

            data?.data?.let {

                binding.medicineImageImageView.setImageURI(it)
                imageUri = it

            } ?: with(data?.extras?.get("data") as Bitmap) {

                binding.medicineImageImageView.setImageBitmap(this)
                val baos = ByteArrayOutputStream()
                compress(Bitmap.CompressFormat.JPEG, 100, baos)
                imageByteArray = baos.toByteArray()

            }
            Log.d("mytag", "clip data " + data?.clipData.toString())
        }

    }


    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            Log.d("mytag", "storage granted ")
        } else {
            activity?.let {
                ActivityCompat.requestPermissions(
                    it,
                    arrayOf(Manifest.permission.CAMERA), TAKE_IMAGE
                )
            }

        }
    }
}

