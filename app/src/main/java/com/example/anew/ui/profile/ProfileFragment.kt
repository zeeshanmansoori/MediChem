package com.example.anew.ui.profile

import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.*
import androidx.fragment.app.Fragment
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.viewModels
import com.example.anew.R
import com.example.anew.databinding.FragmentProfileBinding
import com.example.anew.model.*
import com.example.anew.ui.admin.add.TAKE_IMAGE
import com.example.anew.ui.admin.home.NavHeaderViewModel
import com.example.anew.ui.intialSetup.USER_REF
import com.example.anew.utils.CustomLoadingDialog
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import java.io.ByteArrayOutputStream

class ProfileFragment : Fragment(), View.OnClickListener {

    private lateinit var binding: FragmentProfileBinding
    private lateinit var firestore: FirebaseFirestore
    private lateinit var storageRef: StorageReference
    private var currentUserId: String? = null

    //image from gallery app
    private var imageUri: Uri? = null

    //image from camera app
    private var imageByteArray: ByteArray? = null


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_profile, container, false)

        firestore = FirebaseFirestore.getInstance()

        storageRef = FirebaseStorage.getInstance().getReference(USER_REF)

        currentUserId = FirebaseAuth.getInstance().currentUser?.uid

        setHasOptionsMenu(true)

        FirebaseAuth.getInstance().currentUser?.uid?.let {
            firestore.collection(USER_REF)
                .document(it).get()
                .addOnSuccessListener {
                    binding.user = it.toObject(User::class.java)!!
                    initialSetUp()

                }
                .addOnFailureListener {
                    binding.user = User()
                }
        }



        return binding.root
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
            binding.userImage.setImageURI(imageUri)

            if (data.hasExtra("data"))
                with(data.extras?.get("data") as Bitmap) {
                    val baos = ByteArrayOutputStream()
                    this.compress(Bitmap.CompressFormat.JPEG, 100, baos)
                    imageByteArray = baos.toByteArray()
                    binding.userImage.setImageBitmap(this)
                }

        }

    }

    override fun onClick(v: View?) {
        v?.let {
            when (v.id) {
                binding.userImage.id -> takePhoto()
                binding.fab.id -> currentUserId?.let { saveChanges(it) }

                else -> false
            }
        }
    }

    private fun saveChanges(id: String) {
        firestore.collection(USER_REF).document(id).get().addOnSuccessListener {

            val user = it.toObject(User::class.java)

            user?.let { currentUser ->

                with(binding) {
                    val userName = userName.text.toString()
                    val userEmail = emailEditText.text.toString()
                    val phoneNo = phoneNoEditText.text.toString()

                    // when no changes made
                    if (currentUser.name == userName && currentUser.email == userEmail &&
                        currentUser.phoneNo == phoneNo &&
                        (imageUri == null && imageByteArray == null)
                    ) {
                        Snackbar.make(
                            binding.root,
                            "you have not made any changes",
                            Snackbar.LENGTH_SHORT
                        )
                            .show()
                        return@addOnSuccessListener
                    }

                    val dialog = CustomLoadingDialog(activity as AppCompatActivity)
                    dialog.startDialog()

                    //name
                    if (currentUser.name != userName && currentUser.email == userEmail &&
                        currentUser.phoneNo == phoneNo && userName.isNotEmpty()
                    ) {
                        currentUserId?.apply {

                            firestore.collection(USER_REF).document(this).update(
                                USER_NAME, userName
                            ).addOnSuccessListener {
                                Snackbar.make(
                                    binding.root,
                                    "name updated successfully",
                                    Snackbar.LENGTH_SHORT
                                )
                                    .show()
                                uploadImage(this)

                                dialog.dismissDialog()
                            }.addOnFailureListener {
                                Snackbar.make(binding.root, "${it}", Snackbar.LENGTH_SHORT).show()
                                dialog.dismissDialog()
                            }
                        }
                        return@addOnSuccessListener
                    }


                    // phone no
                    if (currentUser.name == userName && currentUser.email == userEmail &&
                        currentUser.phoneNo != phoneNo && phoneNo.length == 10
                    ) {
                        currentUserId?.apply {

                            firestore.collection(USER_REF).document(this).update(
                                USER_PHONE_NO, phoneNo
                            ).addOnSuccessListener {
                                Snackbar.make(
                                    binding.root,
                                    "phone number updated successfully",
                                    Snackbar.LENGTH_SHORT
                                )
                                    .show()
                                uploadImage(this)
                                dialog.dismissDialog()
                            }.addOnFailureListener {
                                Snackbar.make(binding.root, "${it}", Snackbar.LENGTH_SHORT).show()
                                dialog.dismissDialog()
                            }
                        }
                        return@addOnSuccessListener
                    }


                    // address
//                    if (currentUser.name == userName && currentUser.email == userEmail &&
//                        currentUser.phoneNo == phoneNo && addresses.isNotEmpty()
//                    ) {
//                        currentUserId?.apply {
//
//                            firestore.collection(USER_REF).document(this).update(
//                                USER_ADDRESS, addresses
//                            ).addOnSuccessListener {
//                                Snackbar.make(
//                                    binding.root,
//                                    "address updated successfully",
//                                    Snackbar.LENGTH_SHORT
//                                )
//                                    .show()
//                                uploadImage(this)
//                                dialog.dismissDialog()
//                            }.addOnFailureListener {
//                                Snackbar.make(binding.root, "${it}", Snackbar.LENGTH_SHORT).show()
//                                dialog.dismissDialog()
//                            }
//                        }
//
//                        return@addOnSuccessListener
//                    }


                    //image
                    if (currentUser.name == userName && currentUser.email == userEmail &&
                        currentUser.phoneNo == phoneNo &&
                        (imageUri != null || imageByteArray != null)
                    ) {

                        currentUserId?.let { uploadImage(it) }
                        dialog.dismissDialog()
                        return@addOnSuccessListener
                    }


                    // uploading all
                    if (userName.isNotEmpty() && userName.isNotEmpty() && userEmail.isNotEmpty()
                        && phoneNo.isNotEmpty()
                    ) {
                        currentUserId?.apply {

                            firestore.collection(USER_REF).document(this).update(
                                USER_NAME, userName,
                                USER_EMAIL, userEmail,
                                USER_PHONE_NO, phoneNo
                            ).addOnSuccessListener {
                                Snackbar.make(
                                    binding.root,
                                    "changes updated successfully",
                                    Snackbar.LENGTH_SHORT
                                )
                                    .show()
                                uploadImage(this)
                                dialog.dismissDialog()
                            }.addOnFailureListener {
                                Snackbar.make(binding.root, "${it}", Snackbar.LENGTH_SHORT).show()
                                dialog.dismissDialog()
                            }

                        }

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
                                Snackbar.make(
                                    binding.root,
                                    "Image uploaded",
                                    Snackbar.LENGTH_SHORT
                                )
                                    .show()
                                dialog.dismissDialog()
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
                                Snackbar.make(
                                    binding.root,
                                    "Image uploaded",
                                    Snackbar.LENGTH_SHORT
                                )
                                    .show()
                                dialog.dismissDialog()
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

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.user_profile_menu, menu)
    }


    override fun onResume() {
        super.onResume()

    }
    private fun initialSetUp() {
        with(binding) {
            userName.clearFocus()
            addressEditText.clearFocus()
            phoneNoEditText.requestFocus(View.FOCUS_RIGHT)
            userImage.setOnClickListener(this@ProfileFragment)
            fab.setOnClickListener(this@ProfileFragment)
            //phoneNoEditText.setSelection(phoneNoEditText.text?.length!!)
        }
    }


    // selecting menu item
}