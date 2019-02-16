package com.henrj.messenger.adapter

import android.content.Intent
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.henrj.messenger.R
import com.henrj.messenger.activity.ChatLogActivity
import com.henrj.messenger.model.ChatMessage
import com.henrj.messenger.model.User
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.latest_message_row.view.*

class LatestMessagesAdapter: RecyclerView.Adapter<LatestMessagesAdapter.LatestMessageRowViewHolder>() {

    companion object {
        val USER_KEY = "USER_KEY"
    }

    var latestMessages = hashMapOf<String, ChatMessage>()
    var sortedMessages = listOf<ChatMessage>()

    fun addMessage(key: String, message: ChatMessage) {
        latestMessages.put(key, message)
        sortedMessages = latestMessages
            .toList()
            .sortedByDescending { (_, value) -> value.timestamp }
            .map { it.second }
            .also { notifyDataSetChanged() }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LatestMessageRowViewHolder {
        return LatestMessageRowViewHolder(LayoutInflater.from(parent.context)
            .inflate(R.layout.latest_message_row, parent, false))
    }

    override fun getItemCount(): Int {
        return sortedMessages.count()
    }

    override fun onBindViewHolder(viewHolder: LatestMessageRowViewHolder, position: Int) {
        val message = sortedMessages.get(position)
        val chatPartnerId: String

        val messageBuilder = StringBuilder()

        if (message.fromId == FirebaseAuth.getInstance().uid) {
            chatPartnerId = message.toId
            messageBuilder.append("You: ")
        } else {
            chatPartnerId = message.fromId
        }

        viewHolder.view.latestmessages_textview_name.text = message.id
        viewHolder.view.latestmessages_textview_message.text = messageBuilder.append(message.text).toString()

        FirebaseDatabase.getInstance().getReference("/users/${chatPartnerId}")
            .addListenerForSingleValueEvent(object: ValueEventListener {
                override fun onDataChange(data: DataSnapshot) {
                    val user = data.getValue(User::class.java)
                    viewHolder.user = user
                    viewHolder.view.latestmessages_textview_name.text = user?.name
                    Picasso.get().load(user?.profileImageUrl).into(viewHolder.view.latestmessages_imageview)
                }
                override fun onCancelled(p0: DatabaseError) {}
            })
    }

    inner class LatestMessageRowViewHolder(val view: View, var user: User? = null): RecyclerView.ViewHolder(view) {

        init {
            view.setOnClickListener {
                val intent = Intent(view.context, ChatLogActivity::class.java)
                    .putExtra(USER_KEY, user)

                view.context.startActivity(intent)
            }
        }
    }
}

