package com.henrj.messenger.activity

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.henrj.messenger.R
import kotlinx.android.synthetic.main.activity_login.*

private const val TAG = "LoginActivity"

class LoginActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        login_textview_gotoregister.setOnClickListener(onGoToRegisterListener)
        login_button_login.setOnClickListener(onLoginListener)
    }

    private val onGoToRegisterListener = View.OnClickListener { startActivity(Intent(this, RegisterActivity::class.java)) }
    private val onLoginListener = View.OnClickListener {
        val email = login_edittext_email.text.toString()
        val password = login_edittext_password.text.toString()

        FirebaseAuth.getInstance().signInWithEmailAndPassword(email, password)
            .addOnCompleteListener {
                if (!it.isSuccessful) return@addOnCompleteListener

                Log.d(TAG, "Successfully logged in: ${it.result?.user?.email}")

                startActivity(Intent(this, LatestMessagesActivity::class.java)
                    .setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)))
            }
            .addOnFailureListener {
                Log.e(TAG, "Failed to login: ${it.message}")
                Toast.makeText(this, "Failed to login; ${it.message}", Toast.LENGTH_SHORT).show()
            }
    }
}
