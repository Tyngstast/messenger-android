package com.henrj.messenger.adapter

import android.app.Activity
import android.content.Intent
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.firebase.database.FirebaseDatabase
import com.henrj.messenger.R
import com.henrj.messenger.activity.ChatLogActivity
import com.henrj.messenger.model.ChatMessage
import com.henrj.messenger.model.User
import kotlinx.android.synthetic.main.latest_message_row.view.*

class LatestMessagesAdapter: RecyclerView.Adapter<LatestMessagesAdapter.LatestMessageRowViewHolder>() {

    companion object {
        val USER_KEY = "USER_KEY"
    }

    var latestMessages = hashMapOf<String, ChatMessage>()
    var sortedMessages = mapOf<String, ChatMessage>()

    fun addMessage(key: String, message: ChatMessage) {
        latestMessages.put(key, message)
        sortedMessages = latestMessages
            .toList()
            .sortedByDescending { (_, value) -> value.timestamp }
            .toMap()
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
        val message = sortedMessages.values.toList().get(position)
        viewHolder.view.latestmessages_textview_name.text = message.id
        viewHolder.view.latestmessages_textview_message.text = message.text
//        viewHolder.user = FirebaseDatabase.getInstance().getReference("/users/${message.fromId}")

//        Picasso.get().load(user.profileImageUrl).into(viewHolder.view.userrow_imageview_profileimage)
    }

    inner class LatestMessageRowViewHolder(val view: View, var user: User? = null): RecyclerView.ViewHolder(view) {

        init {
            view.setOnClickListener {
                val intent = Intent(view.context, ChatLogActivity::class.java)
                    .putExtra(USER_KEY, user)

                view.context.startActivity(intent)

                (view.context as Activity).finish()
            }
        }
    }
}

