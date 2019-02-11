package com.henrj.messenger.activity

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.View
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.henrj.messenger.R
import com.henrj.messenger.model.User
import kotlinx.android.synthetic.main.activity_register.*
import java.util.*

private const val TAG = "RegisterActivity"
private const val IMAGE_PICK_REQUEST_CODE = 0

class RegisterActivity : AppCompatActivity() {

    private var selectedPhotoUri: Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        register_button_register.setOnClickListener(onRegisterListener)
        register_textview_backtosignin.setOnClickListener(onBackToSignInListener)
        register_button_select_photo.setOnClickListener(onSelectPhotoListener)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == IMAGE_PICK_REQUEST_CODE && resultCode == Activity.RESULT_OK && data != null) {
            Log.d(TAG, "Photo was selected")

            selectedPhotoUri = data.data
            val bitmap = MediaStore.Images.Media.getBitmap(contentResolver, selectedPhotoUri)
            register_imageview_select_photo.setImageBitmap(bitmap)
            register_button_select_photo.alpha = 0f
        }
    }

    private val onRegisterListener = View.OnClickListener {
        val email = register_edittext_email.text.toString()
        val password = register_edittext_password.text.toString()

        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Please enter email and password", Toast.LENGTH_SHORT).show()
        }
        Log.d(TAG, "Registering account with username: $email")

        FirebaseAuth.getInstance().createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener {
                if (!it.isSuccessful) {
                    return@addOnCompleteListener
                }

                Log.d(TAG, "Successully created user with uid: ${it.result?.user?.uid}")

                saveUser()
            }
            .addOnFailureListener {
                Log.e(TAG, "Failed to create user: ${it.message}")
                Toast.makeText(this, "Failed to create user; ${it.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private val onSelectPhotoListener = View.OnClickListener {
        startActivityForResult(Intent(Intent.ACTION_PICK).setType("image/*"), IMAGE_PICK_REQUEST_CODE)
    }

    private val onBackToSignInListener = View.OnClickListener { finish() }

    private fun uploadImage(uid: String) {
        val filename = UUID.randomUUID().toString()
        val ref = FirebaseStorage.getInstance().getReference("/images/$filename")

        ref.putFile(selectedPhotoUri!!)
            .addOnSuccessListener {
                Log.d(TAG, "Uploaded image: ${it.metadata?.path}")

                ref.downloadUrl.addOnSuccessListener {
                    Log.d(TAG, "File url: $it")

                    val userRef = FirebaseDatabase.getInstance().getReference("/users/$uid")
                    userRef.updateChildren(mapOf("profileImageUrl" to it.toString()))
                        .addOnSuccessListener { Log.d(TAG, "Updated user's profileImageUrl") }
                        .addOnFailureListener { Log.d(TAG, "Failed to update user's profileImageUrl") }
                }
            }
            .addOnFailureListener { Log.d(TAG, "Failed to upload image: ${it.message}") }
    }

    private fun saveUser() {
        val uid = FirebaseAuth.getInstance().uid ?: ""
        val userRef = FirebaseDatabase.getInstance().getReference("/users/$uid")

        val user = User(uid, register_edittext_name.text.toString())

        userRef.setValue(user)
            .addOnSuccessListener {
                Log.d(TAG, "Saved user: $user")

                uploadImage(uid)

                startActivity(Intent(this, LatestMessagesActivity::class.java)
                    .setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)))
            }
            .addOnFailureListener { Log.d(TAG, "Failed to save user: ${it.message}")}
    }

}
