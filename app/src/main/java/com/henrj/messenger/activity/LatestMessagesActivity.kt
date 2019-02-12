package com.henrj.messenger.activity

import android.content.Intent
import android.os.Bundle
import android.renderscript.Sampler
import android.support.v7.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.henrj.messenger.R
import com.henrj.messenger.model.User

private const val TAG = "LatestMessageActivity"

class LatestMessagesActivity : AppCompatActivity() {

    companion object {
        lateinit var currentUser: User
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_latest_messages)

        val uid = FirebaseAuth.getInstance().uid

        if (uid.isNullOrBlank()) {
            launchSignInActivity()
            return
        }

        FirebaseDatabase.getInstance().getReference("/users/$uid")
            .addListenerForSingleValueEvent(singleUserEventListener)

    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.nav_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item?.itemId) {
            R.id.menu_new_message -> { startActivity(Intent(this, NewMessageActivity::class.java)) }
            R.id.menu_sign_out -> {
                FirebaseAuth.getInstance().signOut()
                launchSignInActivity()
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private val singleUserEventListener = object: ValueEventListener {
        override fun onDataChange(p0: DataSnapshot) {
            currentUser = p0.getValue(User::class.java)!!
        }
        override fun onCancelled(p0: DatabaseError) {}
    }

    private fun launchSignInActivity() = startActivity(Intent(this, LoginActivity::class.java)
            .setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)))
}
