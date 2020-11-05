package com.example.anew.ui.admin.detail

import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.*
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.navArgs
import com.example.anew.AdminActivity
import com.example.anew.R
import com.example.anew.databinding.AdminFragmentDetailsBinding
import com.example.anew.model.*
import com.example.anew.ui.admin.add.IMAGE_REF
import com.example.anew.ui.admin.add.PRODUCT_REF
import com.example.anew.ui.admin.add.TAKE_IMAGE
import com.example.anew.ui.intialSetup.ADMIN_REF
import com.example.anew.ui.intialSetup.USER_REF
import com.example.anew.utils.CustomLoadingDialog
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import java.io.ByteArrayOutputStream

class DetailsFragment : Fragment(), MyImageClickListener {

    private lateinit var binding: AdminFragmentDetailsBinding
    private lateinit var firestore: FirebaseFirestore
    private val storageRef = FirebaseStorage.getInstance().getReference(IMAGE_REF)
    private lateinit var oldProduct: Product

    //image from gallery app
    private var imageUriArray = mutableListOf<Uri?>(null, null, null, null)

    //image from camera app
    private var imageByteArray = mutableListOf<ByteArray?>(null, null, null, null)


    // to get the reference of imageView clicked
    private var imageId = 0

    //nav args
    val args: DetailsFragmentArgs by navArgs()


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(
            layoutInflater,
            R.layout.admin_fragment_details,
            container,
            false
        )
        binding.listener = this
        binding.product = args.Product
        oldProduct = args.Product

        setHasOptionsMenu(true)

        firestore = FirebaseFirestore.getInstance()
        return binding.root
    }


    override fun onStart() {
        super.onStart()
        with(activity as AdminActivity) {
            drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED)
        }
    }

    override fun onStop() {
        super.onStop()
        with(activity as AdminActivity) {
            drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.admin_details_menu, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        Log.d("mytag", "${item.title}")
        when (item.itemId) {
            R.id.delete_admin_menu -> delete()
            R.id.save_admin_menu -> saveChanges()
        }
        return super.onOptionsItemSelected(item)
    }

    private fun saveChanges() {
        val dialog = CustomLoadingDialog(activity as AppCompatActivity)


        with(binding) {
            val oldName = oldProduct.name.trim()
            val oldDescription = oldProduct.description.trim()
            val oldMedUsage = oldProduct.medicineUsage.trim()
            val oldPrize = oldProduct.prize
            val oldQuantity = oldProduct.quantity

            //new fields
            val name = medicineName.text.toString().trim()
            val description = medicineDescription.text.toString().trim()
            val medUsage = medicineIndicationUsageEditText.text.toString().trim()
            val prize = medicinePrizeEditText.text.toString().toFloat()
            val quantity = quantityEditText.text.toString().toInt()


            // when no changes made
            if (oldName == name
                && oldDescription == description
                && oldMedUsage == medUsage
                && oldQuantity == quantity
                && imageByteArray.filterNotNull().count() == 0
                && imageUriArray.filterNotNull().count() == 0

            ) {
                Snackbar.make(binding.root, "you have not made any changes", Snackbar.LENGTH_SHORT)
                    .show()
                return
            }


            // updating single values
            uploadImages()
            dialog.startDialog()
            // name
            if (oldName != name && name.isNotEmpty() && oldDescription == description
                && oldMedUsage == medUsage && oldPrize == prize && oldQuantity == quantity
            ) {
                val updateTask = firestore.collection(PRODUCT_REF).document(oldProduct.id).update(
                    PRODUCT_NAME, name
                )
                updateTask.addOnSuccessListener {
                    dialog.dismissDialog()
                    Snackbar.make(binding.root, "Changes Updated", Snackbar.LENGTH_SHORT).show()
                }
                updateTask.addOnFailureListener {
                    dialog.dismissDialog()
                    Snackbar.make(binding.root, it.toString(), Snackbar.LENGTH_SHORT).show()
                }
                return
            }


            //description
            if (oldDescription != description && description.isNotEmpty() && oldName == name
                && oldMedUsage == medUsage && oldPrize == prize && oldQuantity == quantity
            ) {
                val updateTask = firestore.collection(PRODUCT_REF).document(oldProduct.id).update(
                    DESCRIPTION, description
                )
                updateTask.addOnSuccessListener {
                    dialog.dismissDialog()
                    Snackbar.make(binding.root, "Changes Updated", Snackbar.LENGTH_SHORT).show()

                }
                updateTask.addOnFailureListener {
                    dialog.dismissDialog()
                    Snackbar.make(binding.root, it.toString(), Snackbar.LENGTH_SHORT).show()
                }
                return
            }

            //medicine usage
            if (oldMedUsage != medUsage && medUsage.isNotEmpty() && oldName == name && oldDescription == description
                && oldPrize == prize && oldQuantity == quantity
            ) {
                val updateTask = firestore.collection(PRODUCT_REF).document(oldProduct.id).update(
                    MEDICINE_USAGE, medUsage
                )
                updateTask.addOnSuccessListener {
                    dialog.dismissDialog()
                    Snackbar.make(binding.root, "Usage updated", Snackbar.LENGTH_SHORT).show()

                }
                updateTask.addOnFailureListener {
                    dialog.dismissDialog()
                    Snackbar.make(binding.root, it.toString(), Snackbar.LENGTH_SHORT).show()
                }

                return
            }


            //prize
            if (oldPrize != prize && oldName == name && oldDescription == description
                && oldMedUsage == medUsage && oldQuantity == quantity
            ) {
                val updateTask = firestore.collection(PRODUCT_REF).document(oldProduct.id).update(
                    PRIZE, prize
                )
                updateTask.addOnSuccessListener {
                    dialog.dismissDialog()
                    Snackbar.make(binding.root, "Changes Updated", Snackbar.LENGTH_SHORT).show()

                }
                updateTask.addOnFailureListener {
                    dialog.dismissDialog()
                    Snackbar.make(binding.root, it.toString(), Snackbar.LENGTH_SHORT).show()
                }
                return
            }

            //quantity
            if (oldQuantity != quantity && oldName == name && oldDescription == description
                && oldMedUsage == medUsage && oldPrize == prize
            ) {
                val updateTask = firestore.collection(PRODUCT_REF).document(oldProduct.id).update(
                    QUANTITY, quantity
                )
                updateTask.addOnSuccessListener {
                    dialog.dismissDialog()
                    Snackbar.make(binding.root, "Changes Updated", Snackbar.LENGTH_SHORT).show()

                }
                updateTask.addOnFailureListener {
                    dialog.dismissDialog()
                    Snackbar.make(binding.root, it.toString(), Snackbar.LENGTH_SHORT).show()
                }
                return
            }


            // updating multiple values

            if (
                name.isEmpty()
                || description.isEmpty()
                || medUsage.isEmpty()
            ) {
                Snackbar.make(binding.root, "one of the field is empty", Snackbar.LENGTH_SHORT)
                    .show()
                dialog.dismissDialog()

            } else if (oldQuantity == quantity && oldName == name && oldDescription == description
                && oldMedUsage == medUsage && oldPrize == prize &&
                (imageByteArray.filterNotNull().count() > 0 || imageUriArray.filterNotNull()
                    .count() > 0)
            ) {
                dialog.dismissDialog()
                Snackbar.make(
                    binding.root,
                    "only images going to be updated",
                    Snackbar.LENGTH_SHORT
                ).show()

                return
            } else {
                val updateTask = firestore.collection(PRODUCT_REF).document(oldProduct.id).update(
                    PRODUCT_NAME, name,
                    DESCRIPTION, description,
                    QUANTITY, quantity,
                    PRIZE, prize,
                    MEDICINE_USAGE, medUsage
                )
                updateTask.addOnSuccessListener {
                    dialog.dismissDialog()
                    Snackbar.make(binding.root, "Changes Updated", Snackbar.LENGTH_SHORT).show()
                    dialog.dismissDialog()
                }
                updateTask.addOnFailureListener {
                    dialog.dismissDialog()
                    Snackbar.make(binding.root, it.toString(), Snackbar.LENGTH_SHORT).show()
                    dialog.dismissDialog()
                }

            }


        }
    }

    private fun uploadImages() {
        // uploading images taken from camera
        if (imageUriArray.filterNotNull().count() > 0) {
            for ((index, value) in imageUriArray.withIndex()) {
                if (value == null) continue
                uploadUri(index, value)
            }

        }

        // uploadig images choosen from gallery
        else if (imageByteArray.filterNotNull().count() > 0) {
            for ((index, value) in imageByteArray.withIndex()) {
                if (value == null) continue
                uploadBytes(index, value)
            }

        }

    }

    private fun delete() {
        val dialogBuilder = MaterialAlertDialogBuilder(activity as AdminActivity)
        with(dialogBuilder) {
            setTitle("Alert")
            setMessage("Do you really want to delete")
            setNeutralButton("CANCEL") { dialog, _ ->
                dialog.dismiss()
            }
            setPositiveButton("DELETE") { dialog, _ ->
                val deleteTask = firestore.collection(PRODUCT_REF).document(oldProduct.id).delete()
                deleteTask.addOnSuccessListener {
                    Snackbar.make(
                        binding.root,
                        "product removed successfully",
                        Snackbar.LENGTH_SHORT
                    )
                        .show()
                    dialog.dismiss()
                    //findNavController().navigate(R.id.action_detailFragment_to_nav_home)
                    with(activity
                     as AdminActivity){
                        onBackPressed()
                    }
                }.addOnFailureListener {
                    Snackbar.make(
                        binding.root,
                        it.toString(),
                        Snackbar.LENGTH_SHORT
                    )
                }
            }
        }.also {
            it.show()
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
                    itemList[which] == itemList[0] -> intent.action =
                        MediaStore.ACTION_IMAGE_CAPTURE
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

                when (imageId) {
                    binding.medImgImageView1.id -> {
                        imageUriArray.set(0, it)
                        binding.medImgImageView1.setImageURI(it)
                    }
                    binding.medImgImageView2.id -> {
                        imageUriArray.set(1, it)
                        binding.medImgImageView2.setImageURI(it)
                    }
                    binding.medImgImageView3.id -> {
                        imageUriArray.set(2, it)
                        binding.medImgImageView3.setImageURI(it)
                    }
                    binding.medImgImageView4.id -> {
                        imageUriArray.set(3, it)
                        binding.medImgImageView4.setImageURI(it)
                    }

                }


            } ?: with(data?.extras?.get("data") as Bitmap) {
                val baos = ByteArrayOutputStream()
                compress(Bitmap.CompressFormat.JPEG, 100, baos)
                val bytes = baos.toByteArray()
                when (imageId) {
                    binding.medImgImageView1.id -> {
                        imageByteArray[0] = bytes
                        binding.medImgImageView1.setImageBitmap(this)
                    }
                    binding.medImgImageView2.id -> {
                        imageByteArray.set(1, bytes)
                        binding.medImgImageView2.setImageBitmap(this)
                    }
                    binding.medImgImageView3.id -> {
                        imageByteArray.set(2, bytes)
                        binding.medImgImageView3.setImageBitmap(this)
                    }
                    binding.medImgImageView4.id -> {
                        imageByteArray.set(3, bytes)
                        binding.medImgImageView4.setImageBitmap(this)
                    }

                }

            }
            Log.d("mytag", "clip data " + data?.clipData.toString())
        }

    }

    override fun onImageClicked(view: View) {
        imageId = view.id
        takePhoto()
    }

    private fun getImageName(imageId: Int) = when (imageId) {
        binding.medImgImageView1.id -> "${oldProduct.id}r1.jpg"
        binding.medImgImageView2.id -> "${oldProduct.id}r2.jpg"
        binding.medImgImageView3.id -> "${oldProduct.id}r3.jpg"
        binding.medImgImageView4.id -> "${oldProduct.id}r4.jpg"
        else -> "${oldProduct.id}.jpg"
    }

    private fun getChildName(index: Int) = when (index) {
        0 -> IMAGE1
        1 -> IMAGE2
        2 -> IMAGE3
        3 -> IMAGE4
        else -> ""
    }

    private fun uploadUri(index: Int, value: Uri) {
        val dialog = CustomLoadingDialog(activity as AppCompatActivity)
        dialog.startDialog()
        val imageRef = storageRef.child(getImageName(imageId))
        value.let {
            imageRef.putFile(it).addOnSuccessListener {
                imageRef.downloadUrl.addOnSuccessListener {
                    firestore.collection(PRODUCT_REF).document(oldProduct.id).update(
                        getChildName(index), it.toString()
                    )
                        .addOnSuccessListener {
                            Log.d("myimage", "uploaded ${getChildName(index)}")
                            Snackbar.make(
                                binding.root,
                                "Image${index + 1} uploaded",
                                Snackbar.LENGTH_SHORT
                            )
                                .show()
                            dialog.dismissDialog()
                            imageUriArray[index] = null

                        }.addOnFailureListener {
                            Snackbar.make(binding.root, it.toString(), Snackbar.LENGTH_SHORT)
                                .show()
                            dialog.dismissDialog()
                        }

                }
            }
        }

    }

    private fun uploadBytes(index: Int, value: ByteArray) {
        val dialog = CustomLoadingDialog(activity as AppCompatActivity)
        dialog.startDialog()
        val imageRef = storageRef.child(getImageName(imageId))
        value.let {
            imageRef.putBytes(it).addOnSuccessListener {
                imageRef.downloadUrl.addOnSuccessListener {
                    firestore.collection(PRODUCT_REF).document(oldProduct.id).update(
                        getChildName(index), it.toString()
                    )
                        .addOnSuccessListener {
                            Log.d("myimage", "uploaded ${getChildName(index)}")
                            Snackbar.make(
                                binding.root,
                                "Image${index + 1} uploaded",
                                Snackbar.LENGTH_SHORT
                            )
                                .show()
                            dialog.dismissDialog()
                            imageByteArray[index] = null
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


interface MyImageClickListener {

    fun onImageClicked(view: View)
}