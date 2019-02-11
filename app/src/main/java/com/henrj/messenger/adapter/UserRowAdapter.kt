package com.henrj.messenger.adapter

import android.app.Activity
import android.content.Intent
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.henrj.messenger.R
import com.henrj.messenger.activity.ChatLogActivity
import com.henrj.messenger.model.User
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.user_row_new_message.view.*

class UserRowAdapter(val users: List<User?>): RecyclerView.Adapter<UserRowAdapter.UserRowViewHolder>() {

    companion object {
        val USER_KEY = "USER_KEY"
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserRowViewHolder {
        return UserRowViewHolder(LayoutInflater.from(parent.context)
            .inflate(R.layout.user_row_new_message, parent, false))
    }

    override fun getItemCount(): Int {
        return users.count()
    }

    override fun onBindViewHolder(viewHolder: UserRowViewHolder, position: Int) {
        val user = users.get(position)
        viewHolder.view.userrow_textview_name.text = user?.name
        viewHolder.user = user

        Picasso.get().load(user?.profileImageUrl).into(viewHolder.view.userrow_imageview_profileimage)
    }

    inner class UserRowViewHolder(val view: View, var user: User? = null): RecyclerView.ViewHolder(view) {

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

