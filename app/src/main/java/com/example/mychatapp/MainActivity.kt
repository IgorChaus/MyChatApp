package com.example.mychatapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.auth.FirebaseAuthUIActivityResultContract
import com.firebase.ui.auth.IdpResponse
import com.firebase.ui.auth.data.model.FirebaseAuthUIAuthenticationResult
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.firestore

class MainActivity : AppCompatActivity() {

    private val db = Firebase.firestore
    private val auth = FirebaseAuth.getInstance()

    private val signInLauncher = registerForActivityResult(
        FirebaseAuthUIActivityResultContract(),
    ) { res ->
        this.onSignInResult(res)
    }

    private lateinit var editTextMessage: EditText
    private lateinit var recyclerViewMessages: RecyclerView
    private lateinit var imageViewSendMessage: ImageView
    private val messagesAdapter = MessagesAdapter()
    private var author = "Игорь"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        recyclerViewMessages = findViewById<RecyclerView>(R.id.recyclerViewMessages)

        editTextMessage = findViewById(R.id.editTextMessage)
        imageViewSendMessage = findViewById<ImageView>(R.id.imageViewSendMessage)

        recyclerViewMessages.layoutManager = LinearLayoutManager(this)
        recyclerViewMessages.adapter = messagesAdapter
        imageViewSendMessage.setOnClickListener{
            sendMessage()
        }

        if (auth.currentUser != null) {
            Toast.makeText(this, "Logged", Toast.LENGTH_LONG).show()
        } else {
            signOut()
        }

    }

    override fun onResume() {
        super.onResume()
        db.collection("messages").orderBy("date").addSnapshotListener { snapshots, error ->
            if (snapshots != null) {
                val messages = snapshots.toObjects(Message::class.java)
                messagesAdapter.messages = messages
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if(item.itemId == R.id.itemSignOut){
            AuthUI.getInstance().signOut(this)
            signOut()
        }
        return super.onOptionsItemSelected(item)
    }

    private fun signOut(){
//        val intent = Intent(this, RegisterActivity::class.java)
//        startActivity(intent)
        val providers = arrayListOf(
            AuthUI.IdpConfig.EmailBuilder().build(),
            AuthUI.IdpConfig.GoogleBuilder().build()
        )
        val signInIntent = AuthUI.getInstance()
            .createSignInIntentBuilder()
            .setAvailableProviders(providers)
            .setIsSmartLockEnabled(false)
            .build()
        signInLauncher.launch(signInIntent)
    }

    private fun onSignInResult(result: FirebaseAuthUIAuthenticationResult) {
        val response = result.idpResponse
        if (result.resultCode == RESULT_OK) {
            val user = FirebaseAuth.getInstance().currentUser
            author = user?.email ?: ""
            Toast.makeText(this, "Loggined ${user?.email}",Toast.LENGTH_LONG).show()
        } else {
            Toast.makeText(this, "Error ${response?.error}",Toast.LENGTH_LONG).show()
        }
    }


    private fun sendMessage(){
        val textOfMessage = editTextMessage.text.toString().trim()
        if(textOfMessage.isNotEmpty()){
            val editableText = Editable.Factory.getInstance().newEditable("")
            editTextMessage.text= editableText
            recyclerViewMessages.scrollToPosition(messagesAdapter.itemCount - 1)
            db.collection("messages").add(Message(author, textOfMessage, System.currentTimeMillis()))
                .addOnSuccessListener {
                    editTextMessage.text= editableText
                }
                .addOnFailureListener {
                    Toast.makeText(this, "Сообщение не отправлено",Toast.LENGTH_LONG).show()
                }

        }
    }

}