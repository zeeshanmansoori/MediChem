package com.example.anew.ui.profile

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.*
import androidx.fragment.app.Fragment
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.NavigationUI
import com.example.anew.MainActivity
import com.example.anew.R
import com.example.anew.databinding.FragmentProfileBinding
import com.example.anew.model.*
import com.example.anew.ui.admin.add.TAKE_IMAGE
import com.example.anew.ui.admin.home.NavHeaderViewModel
import com.example.anew.ui.intialSetup.USER_REF
import com.example.anew.utils.CustomLoadingDialog
import com.example.anew.utils.MyUtil
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import java.io.ByteArrayOutputStream

class ProfileFragment : Fragment(), View.OnClickListener {

    private lateinit var binding: FragmentProfileBinding
    private lateinit var firestore: FirebaseFirestore
    private lateinit var storageRef: StorageReference
    private val userId = Firebase.auth.currentUser?.uid!!

    private var snackbar: Snackbar? = null

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


        setHasOptionsMenu(true)


        firestore.collection(USER_REF)
            .document(userId).get()
            .addOnSuccessListener {
                user->
                firestore.collection(USER_REF)
                    .document(userId).collection(USER_ADDRESSES)
                    .document(ADDRESS1)
                    .get().
                        addOnSuccessListener {
                            binding.user = user.toObject(User::class.java)!!
                            binding.address = it.toObject(Address::class.java)
                        }
                    .addOnFailureListener {
                        binding.user = user.toObject(User::class.java)!!
                        binding.address = Address()
                    }


            }
            .addOnFailureListener {
                binding.user = User()
                binding.address = Address()
            }


        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        with(binding) {
            addressEditText.setOnClickListener(this@ProfileFragment)
            emailEditText.setOnClickListener(this@ProfileFragment)
            userName.clearFocus()
            addressEditText.clearFocus()
            phoneNoEditText.requestFocus(View.FOCUS_RIGHT)

            userImage.setOnClickListener{
                if (MyUtil.checkCameraPermission(activity))
                    takePhoto()
                else MyUtil.requestCameraPermission(activity)
            }

            fab.setOnClickListener(this@ProfileFragment)
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

                binding.fab.id -> saveChanges(userId)
                binding.addressEditText.id -> {
                    snackbar = Snackbar.make(
                        binding.root,
                        getString(R.string.address_cant_edited),
                        Snackbar.LENGTH_SHORT
                    )
                    snackbar?.show()
                }
                binding.emailEditText.id -> {
                    snackbar = Snackbar.make(
                        binding.root,
                        getString(R.string.email_cant_edited),
                        Snackbar.LENGTH_SHORT
                    )
                    snackbar?.show()
                }

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
                    if (currentUser.name != userName && currentUser.email == userEmail &&
                        currentUser.phoneNo == phoneNo && userName.isNotEmpty()
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
                    if (currentUser.name == userName && currentUser.email == userEmail &&
                        currentUser.phoneNo != phoneNo && phoneNo.length == 10
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

                        uploadImage(userId)
                        dialog.dismissDialog()
                        return@addOnSuccessListener
                    }


                    // uploading all
                    if (userName.isNotEmpty() && userName.isNotEmpty() && userEmail.isNotEmpty()
                        && phoneNo.isNotEmpty()
                    ) {


                        firestore.collection(USER_REF).document(userId).update(
                            USER_NAME, userName,
                            USER_EMAIL, userEmail,
                            USER_PHONE_NO, phoneNo
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

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.user_profile_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        when (item.itemId) {
            R.id.user_profile_save -> saveChanges(userId)
            R.id.user_profile_help -> navigateToAbout()
        }

        return super.onOptionsItemSelected(item)
    }


    private fun navigateToAbout(): Boolean {
        with(activity as MainActivity) {
            val menuId = navView.menu.findItem(R.id.nav_about)
            return NavigationUI.onNavDestinationSelected(menuId, findNavController())
        }
    }


    override fun onPause() {
        snackbar?.dismiss()
        super.onPause()

    }
    // selecting menu item
}