package com.example.mychatapp

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso

class MessagesAdapter(val context: Context): RecyclerView.Adapter<MessagesAdapter.MessagesViewHolder>(){

    var messages = listOf<Message>()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    class MessagesViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val textViewAuthor: TextView = itemView.findViewById(R.id.textViewAuthor)
        val textViewMessage: TextView = itemView.findViewById(R.id.textViewTextOfMessage)
        val imageView: ImageView = itemView.findViewById(R.id.imageViewImage)
    }

    override fun getItemViewType(position: Int): Int {
        val message = messages[position]
        val author = message.author
        if (author == context.getSharedPreferences(
                "com.example.mychatapp",
                Context.MODE_PRIVATE
            ).getString("author", "Anonim"))
            return TYPE_MY_MESSAGE
        else
            return TYPE_OTHER_MESSAGE
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MessagesViewHolder {
        if(viewType == TYPE_MY_MESSAGE) {
            val view = LayoutInflater.from(parent.context).inflate(
                R.layout.layout_item_my_message,
                parent,
                false
            )
            return MessagesViewHolder(view)
        } else {
            val view = LayoutInflater.from(parent.context).inflate(
                R.layout.layout_item_other_message,
                parent,
                false
            )
            return MessagesViewHolder(view)
        }
    }

    override fun onBindViewHolder(holder: MessagesViewHolder, position: Int) {
        holder.textViewAuthor.text = messages[position].author
        val textOfMessage = messages[position].textOfMessage
        val urlToImage =  messages[position].imageUrl
        if (textOfMessage != null && textOfMessage.isNotEmpty()){
            holder.textViewMessage.text = messages[position].textOfMessage
            holder.imageView.visibility = View.GONE
            holder.textViewMessage.visibility = View.VISIBLE
        }
        if (urlToImage != null && urlToImage.isNotEmpty()){
            holder.imageView.visibility = View.VISIBLE
            Picasso.get().load(urlToImage).into(holder.imageView)
            holder.textViewMessage.visibility = View.GONE
        }

    }

    override fun getItemCount(): Int {
        return messages.size
    }

    companion object{
        const val TYPE_MY_MESSAGE = 1
        const val TYPE_OTHER_MESSAGE = 2
    }
}