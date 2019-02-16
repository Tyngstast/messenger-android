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

        FirebaseDatabase.getInstance().getReference("/user-messages/${LatestMessagesActivity.currentUser.uid}/${toUser.uid}")
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
        if (chatlog_edittext_message.text.isNullOrEmpty()) return@OnClickListener

        val fromId = FirebaseAuth.getInstance().uid ?: return@OnClickListener
        val toId = intent.getParcelableExtra<User>(UserRowAdapter.USER_KEY).uid
        val text = chatlog_edittext_message.text.toString()

        val fromRef = FirebaseDatabase.getInstance().getReference("/user-messages/$fromId/$toId").push()
        val toRef = FirebaseDatabase.getInstance().getReference("/user-messages/$toId/$fromId").push()

        val chatMessage = ChatMessage(fromRef.key!!, text, fromId, toId)

        fromRef.setValue(chatMessage).addOnCompleteListener { chatlog_edittext_message.text.clear() }
        toRef.setValue(chatMessage)

        val latestMessageRef = FirebaseDatabase.getInstance().getReference("latest-messages/$fromId/$toId");
    }
}
