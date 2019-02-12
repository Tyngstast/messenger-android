package com.henrj.messenger.activity

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.View
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.henrj.messenger.R
import com.henrj.messenger.adapter.MessageAdapter
import com.henrj.messenger.adapter.UserRowAdapter
import com.henrj.messenger.model.ChatMessage
import com.henrj.messenger.model.User
import kotlinx.android.synthetic.main.activity_chat_log.*

private const val TAG = "ChatLogActivity"

class ChatLogActivity : AppCompatActivity() {

    private lateinit var adapter: MessageAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat_log)

        val toUser = intent.getParcelableExtra<User>(UserRowAdapter.USER_KEY)

        supportActionBar?.title = toUser?.name

        adapter = MessageAdapter(toUser)
        chatlog_recyclerview_messages.adapter = adapter

        chatlog_button_send.setOnClickListener(onSendMessageListener)

        FirebaseDatabase.getInstance().getReference("/messages")
            .addChildEventListener(onMessageChildEventListener)

    }

    private val onMessageChildEventListener = object: ChildEventListener {
        override fun onChildAdded(dataSnapshot: DataSnapshot, p1: String?) {
            val chatMessage = dataSnapshot.getValue(ChatMessage::class.java)
            Log.d(TAG, "Chatmessage: ${chatMessage?.text}")
            adapter.addMessage(chatMessage!!)
            chatlog_recyclerview_messages.scrollToPosition(adapter.itemCount - 1)
        }

        override fun onCancelled(p0: DatabaseError) {}
        override fun onChildMoved(p0: DataSnapshot, p1: String?) {}
        override fun onChildChanged(p0: DataSnapshot, p1: String?) {}
        override fun onChildRemoved(p0: DataSnapshot) {}
    }

    private val onSendMessageListener = View.OnClickListener {
        if (chatlog_button_send.text.isNullOrEmpty()) return@OnClickListener

        val ref = FirebaseDatabase.getInstance().getReference("/messages").push()
        val fromId = FirebaseAuth.getInstance().uid

        if (fromId.isNullOrBlank()) return@OnClickListener

        val toId = intent.getParcelableExtra<User>(UserRowAdapter.USER_KEY).uid
        val text = chatlog_edittext_message.text.toString()
        val chatMessage = ChatMessage(ref.key!!, text, fromId, toId)

        ref.setValue(chatMessage)
            .addOnSuccessListener { Log.d(TAG, "Saved chatMessage: ${ref.key}") }
            .addOnFailureListener { Log.d(TAG, "Failed to save chatMessage: ${it.message}")}
            .addOnCompleteListener { chatlog_edittext_message.text.clear() }
    }
}
