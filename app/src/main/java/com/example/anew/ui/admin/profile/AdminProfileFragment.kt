package com.example.anew.ui.admin.profile

import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.*
import androidx.fragment.app.Fragment
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.example.anew.R
import com.example.anew.databinding.AdminFragmentProfileBinding
import com.example.anew.model.*
import com.example.anew.ui.admin.add.TAKE_IMAGE
import com.example.anew.ui.intialSetup.USER_REF
import com.example.anew.utils.CustomLoadingDialog
import com.google.firebase.firestore.FirebaseFirestore
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import java.io.ByteArrayOutputStream

class AdminProfileFragment : Fragment() {

    private lateinit var binding: AdminFragmentProfileBinding
    private lateinit var firestore: FirebaseFirestore
    private lateinit var storageRef: StorageReference
    private val userId = Firebase.auth.currentUser?.uid!!

    private var snackbar: Snackbar? = null

    //image from gallery app
    private var imageUri: Uri? = null

    //image from camera app
    private var imageByteArray: ByteArray? = null

    private lateinit var adminUser: AdminUser


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding =
            DataBindingUtil.inflate(inflater, R.layout.admin_fragment_profile, container, false)

        firestore = FirebaseFirestore.getInstance()

        storageRef = FirebaseStorage.getInstance().getReference(USER_REF)



        firestore.collection(USER_REF)
            .document(userId).get()
            .addOnSuccessListener {
                adminUser = it.toObject(AdminUser::class.java)!!
                binding.adminUser = adminUser
                binding.address = Address(
                    "mumbai",
                    "andheri east",
                    pinCode = "400058",
                    state = "Maharashtra",
                    buildingName = "aklnfd",
                    landMark = "opposite metro station"

                )
            }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        with(binding) {
            addressTextView.setOnClickListener {
                snackbar = Snackbar.make(
                    binding.root,
                    "you cant edit address in this version",
                    Snackbar.LENGTH_SHORT
                )
                snackbar?.show()
            }
            emailEditText.setOnClickListener {
                snackbar = Snackbar.make(
                    binding.root,
                    getString(R.string.email_cant_edited),
                    Snackbar.LENGTH_SHORT
                )
                snackbar?.show()
            }
            addressTextView.clearFocus()
            fab.setOnClickListener { saveChanges(userId) }
            adminUserImage.setOnClickListener { takePhoto() }


        }
    }


    private fun takePhoto() {
        val itemList = arrayOf<CharSequence>("take photo", "pick from gallery")
        AlertDialog.Builder(context).apply {
            setTitle("Choose Photo")
            val intent = Intent()
            setItems(itemList) { dialog, which ->
                when (which) {
                    0 -> intent.action = MediaStore.ACTION_IMAGE_CAPTURE
                    1 -> {
                        intent.action = Intent.ACTION_GET_CONTENT
                        intent.type = "image/*"
                    }
                    else -> dialog.dismiss()
                }

                startActivityForResult(intent, TAKE_IMAGE)
            }

        }.also {
            it.show()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == TAKE_IMAGE && resultCode == Activity.RESULT_OK && data != null) {
            imageUri = data.data
            binding.adminUserImage.setImageURI(imageUri)

            if (data.hasExtra("data"))
                with(data.extras?.get("data") as Bitmap) {
                    val baos = ByteArrayOutputStream()
                    this.compress(Bitmap.CompressFormat.JPEG, 100, baos)
                    imageByteArray = baos.toByteArray()
                    binding.adminUserImage.setImageBitmap(this)
                }

        }

    }

    private fun saveChanges(id: String) {

        firestore.collection(USER_REF).document(id).get().addOnSuccessListener {

            val currentUser = it.toObject(AdminUser::class.java)!!


            with(binding) {

                val userName = adminUserName.text.toString()
                val phoneNo = phoneNoEditText.text.toString()
                val upiId = paymentUpi.text.toString()

                // when no changes made
                if (currentUser.name == userName &&
                    currentUser.phoneNo == phoneNo && currentUser.upiId == upiId
                    && imageUri == null && imageByteArray == null
                ) {
                    snackbar = Snackbar.make(
                        binding.root,
                        "you have not made any changes",
                        Snackbar.LENGTH_SHORT
                    )
                    snackbar?.show()
                    return@addOnSuccessListener
                }

                val dialog = CustomLoadingDialog(activity as AppCompatActivity)
                dialog.startDialog()

                //name
                if (currentUser.name != userName &&
                    currentUser.phoneNo == phoneNo && userName.isNotEmpty()
                    && currentUser.upiId == upiId

                ) {


                    firestore.collection(USER_REF).document(userId).update(
                        USER_NAME, userName
                    ).addOnSuccessListener {
                        snackbar = Snackbar.make(
                            binding.root,
                            getString(R.string.name_updated_successfully),
                            Snackbar.LENGTH_SHORT
                        )
                        snackbar?.show()
                        uploadImage(userId)

                        dialog.dismissDialog()
                    }.addOnFailureListener {
                        Snackbar.make(binding.root, "${it}", Snackbar.LENGTH_SHORT).show()
                        dialog.dismissDialog()
                    }
                    return@addOnSuccessListener
                }


                // phone no
                if (currentUser.name == userName &&
                    currentUser.phoneNo != phoneNo && phoneNo.length == 10
                    && currentUser.upiId == upiId
                ) {


                    firestore.collection(USER_REF).document(userId).update(
                        USER_PHONE_NO, phoneNo
                    ).addOnSuccessListener {
                        snackbar = Snackbar.make(
                            binding.root,
                            getString(R.string.phone_no_updated_successfully),
                            Snackbar.LENGTH_SHORT
                        )
                        snackbar?.show()
                        uploadImage(userId)
                        dialog.dismissDialog()
                    }.addOnFailureListener {
                        Snackbar.make(binding.root, "${it}", Snackbar.LENGTH_SHORT).show()
                        dialog.dismissDialog()
                    }

                    return@addOnSuccessListener
                }


                // upi
                if (currentUser.name == userName &&
                    currentUser.phoneNo == phoneNo && phoneNo.length == 10
                    && currentUser.upiId != upiId
                ) {


                    firestore.collection(USER_REF).document(userId).update(
                        "upiId", upiId
                    ).addOnSuccessListener {
                        snackbar = Snackbar.make(
                            binding.root,
                            "Upi id updated successfully",
                            Snackbar.LENGTH_SHORT
                        )
                        snackbar?.show()
                        uploadImage(userId)
                        dialog.dismissDialog()
                    }

                    return@addOnSuccessListener
                }


                //image
                if (currentUser.name == userName &&
                    currentUser.phoneNo == phoneNo &&
                    (imageUri != null || imageByteArray != null)
                ) {

                    uploadImage(userId)
                    dialog.dismissDialog()
                    return@addOnSuccessListener
                }


                // uploading all
                if (userName.isNotEmpty() && userName.isNotEmpty()
                    && phoneNo.isNotEmpty()
                ) {


                    firestore.collection(USER_REF).document(userId).update(
                        USER_NAME, userName,
                        USER_PHONE_NO, phoneNo,
                        "upiId", upiId
                    ).addOnSuccessListener {
                        snackbar = Snackbar.make(
                            binding.root,
                            getString(R.string.changes_updated_successfully),
                            Snackbar.LENGTH_SHORT
                        )
                        snackbar?.show()
                        uploadImage(userId)
                        dialog.dismissDialog()
                    }.addOnFailureListener {
                        Snackbar.make(binding.root, "${it}", Snackbar.LENGTH_SHORT).show()
                        dialog.dismissDialog()
                    }


                }

            }


    }

}


private fun uploadImage(userId: String) {
    imageUri?.let {
        val dialog = CustomLoadingDialog(activity as AppCompatActivity)
        dialog.startDialog()
        val imageRef = storageRef.child("${userId}.jpg")
        it.let {
            imageRef.putFile(it).addOnSuccessListener {
                imageRef.downloadUrl.addOnSuccessListener { uri ->
                    firestore.collection(USER_REF).document(userId).update(
                        USER_IMAGE, uri.toString()
                    )
                        .addOnSuccessListener {
                            snackbar = Snackbar.make(
                                binding.root,
                                "Image uploaded",
                                Snackbar.LENGTH_SHORT
                            )
                            snackbar?.show()
                            dialog.dismissDialog()
                            imageUri = null
                        }.addOnFailureListener {
                            Snackbar.make(binding.root, it.toString(), Snackbar.LENGTH_SHORT)
                                .show()
                            dialog.dismissDialog()
                        }

                }
            }
        }
    }

    // upload from bytes
    imageByteArray?.let {
        val dialog = CustomLoadingDialog(activity as AppCompatActivity)
        dialog.startDialog()
        val imageRef = storageRef.child("${userId}.jpg")
        it.let {
            imageRef.putBytes(it).addOnSuccessListener {
                imageRef.downloadUrl.addOnSuccessListener { uri ->
                    firestore.collection(USER_REF).document(userId).update(
                        USER_IMAGE, uri.toString()
                    )
                        .addOnSuccessListener {
                            snackbar = Snackbar.make(
                                binding.root,
                                getString(R.string.img_uploaded_msg),
                                Snackbar.LENGTH_SHORT
                            )
                            snackbar?.show()
                            dialog.dismissDialog()
                            imageByteArray = null
                        }.addOnFailureListener {
                            Snackbar.make(binding.root, it.toString(), Snackbar.LENGTH_SHORT)
                                .show()
                            dialog.dismissDialog()
                        }

                }
            }
        }
    }
}


override fun onPause() {
    snackbar?.dismiss()
    super.onPause()

}
}