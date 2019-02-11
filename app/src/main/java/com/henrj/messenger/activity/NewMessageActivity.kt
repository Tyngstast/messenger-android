package com.henrj.messenger.activity

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.henrj.messenger.R
import com.henrj.messenger.adapter.UserRowAdapter
import com.henrj.messenger.model.User
import kotlinx.android.synthetic.main.activity_new_message.*

private const val TAG = "NewMessageActivity"

class NewMessageActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_new_message)

        supportActionBar?.title = "Select User"

        FirebaseDatabase.getInstance().getReference("/users")
            .addListenerForSingleValueEvent(usersSingleEventListener)
    }

    private val usersSingleEventListener = object: ValueEventListener {

        override fun onDataChange(dataSnapshot: DataSnapshot) {
            Log.d(TAG, "Fetched data")

            val users = dataSnapshot.children
                .map { it.getValue(User::class.java) }
                .toList()

            newmessage_recyclerview.adapter = UserRowAdapter(users)
        }

        override fun onCancelled(databaseError: DatabaseError) {}
    }
}
