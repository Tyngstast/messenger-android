package com.henrj.messenger.activity

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import com.google.firebase.auth.FirebaseAuth
import com.henrj.messenger.R

private const val TAG = "LatestMessageActivity"

class LatestMessagesActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_latest_messages)

        val uid = FirebaseAuth.getInstance().uid
        Log.d(TAG, "Authenticated user: $uid")
        if (uid.isNullOrBlank()) {
            launchSignInActivity()
        }
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

    private fun launchSignInActivity() = startActivity(Intent(this, LoginActivity::class.java)
            .setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)))
}
