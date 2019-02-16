package com.henrj.messenger.activity

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.henrj.messenger.R
import com.henrj.messenger.adapter.LatestMessagesAdapter
import com.henrj.messenger.model.ChatMessage
import com.henrj.messenger.model.User
import kotlinx.android.synthetic.main.activity_latest_messages.*

private const val TAG = "LatestMessageActivity"

class LatestMessagesActivity : AppCompatActivity() {

    private lateinit var adapter: LatestMessagesAdapter

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

        FirebaseDatabase.getInstance().getReference("/user-messages/$uid")
            .addChildEventListener(messageChildEventListener)

        adapter = LatestMessagesAdapter()
        latestmessages_recyclerview.adapter = adapter
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

    private val messageChildEventListener = object: ChildEventListener {
        fun getLatest(data: DataSnapshot): ChatMessage? =
            data.children
                .map { it.getValue(ChatMessage::class.java) }
                .sortedByDescending { it?.timestamp }
                .get(0)

        override fun onChildAdded(data: DataSnapshot, p1: String?) {
            val message = getLatest(data) ?: return
            adapter.addMessage(data.key!!, message)
        }

        override fun onChildChanged(data: DataSnapshot, p1: String?) {
            val message = getLatest(data) ?: return
            adapter.addMessage(data.key!!, message)
        }
        override fun onChildMoved(p0: DataSnapshot, p1: String?) {}
        override fun onChildRemoved(p0: DataSnapshot) {}
        override fun onCancelled(p0: DatabaseError) {}
    }

    private fun launchSignInActivity() = startActivity(Intent(this, LoginActivity::class.java)
            .setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)))
}
