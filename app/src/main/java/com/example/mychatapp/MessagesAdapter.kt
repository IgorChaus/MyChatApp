package com.example.mychatapp

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class MessagesAdapter: RecyclerView.Adapter<MessagesAdapter.MessagesViewHolder>(){

    var messages = listOf<Message>()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    class MessagesViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val textViewAuthor: TextView = itemView.findViewById(R.id.textViewAuthor)
        val textViewMessage: TextView = itemView.findViewById(R.id.textViewTextOfMessage)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MessagesViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(
            R.layout.layout_item_my_message,
            parent,
            false
        )
        return MessagesViewHolder(view)
    }

    override fun onBindViewHolder(holder: MessagesViewHolder, position: Int) {
        holder.textViewAuthor.text = messages[position].author
        holder.textViewMessage.text = messages[position].textOfMessage
    }

    override fun getItemCount(): Int {
        return messages.size
    }
}