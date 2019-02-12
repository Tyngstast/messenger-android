package com.henrj.messenger.adapter

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.firebase.auth.FirebaseAuth
import com.henrj.messenger.R
import com.henrj.messenger.activity.LatestMessagesActivity
import com.henrj.messenger.model.ChatMessage
import com.henrj.messenger.model.User
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.mymessage_row.view.*
import kotlinx.android.synthetic.main.othermessage_row.view.*

private const val TAG = "MessageAdapter"
private const val VIEW_TYPE_MY_MESSAGE = 1;
private const val VIEW_TYPE_OTHER_MESSAGE = 2;

class MessageAdapter(val toUser: User): RecyclerView.Adapter<MessageViewHolder>() {

    private val messages: ArrayList<ChatMessage> = ArrayList()

    fun addMessage(message: ChatMessage) {
        messages.add(message)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MessageViewHolder {
        return if (viewType == VIEW_TYPE_MY_MESSAGE) {
            MyMessageViewHolder(LayoutInflater.from(parent.context)
                .inflate(R.layout.mymessage_row, parent, false))
        } else {
            OtherMessageViewHolder(LayoutInflater.from(parent.context)
                .inflate(R.layout.othermessage_row, parent, false))
        }
    }

    override fun getItemViewType(position: Int): Int {
        val message = messages.get(position)
        return if (FirebaseAuth.getInstance().uid == message.fromId) {
            VIEW_TYPE_MY_MESSAGE
        } else {
            VIEW_TYPE_OTHER_MESSAGE
        }
    }

    override fun getItemCount(): Int {
        return messages.count()
    }

    override fun onBindViewHolder(viewHolder: MessageViewHolder, position: Int) {
        val message = messages.get(position)
        viewHolder.bind(message)
    }

    inner class OtherMessageViewHolder(val view: View): MessageViewHolder(view) {
        override fun bind(chatMessage: ChatMessage) {
            view.othermessage_textview.text = chatMessage.text
            Picasso.get().load(toUser.profileImageUrl).into(view.othermessage_imageview)
        }
    }

    inner class MyMessageViewHolder(val view: View): MessageViewHolder(view) {
        override fun bind(chatMessage: ChatMessage) {
            view.mymessage_textview.text = chatMessage.text
            Picasso.get().load(LatestMessagesActivity.currentUser.profileImageUrl).into(view.mymessage_imageview)
        }
    }
}

abstract class MessageViewHolder(view: View): RecyclerView.ViewHolder(view) {
    abstract fun bind(chatMessage: ChatMessage)
}

