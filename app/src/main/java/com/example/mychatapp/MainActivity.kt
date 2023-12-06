package com.example.mychatapp

import android.content.Context
import android.content.SharedPreferences
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.auth.FirebaseAuthUIActivityResultContract
import com.firebase.ui.auth.data.model.FirebaseAuthUIAuthenticationResult
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.firestore
import com.google.firebase.storage.storage

class MainActivity : AppCompatActivity() {

    private val db = Firebase.firestore
    private val auth = FirebaseAuth.getInstance()
    private val storage = Firebase.storage
    var storageRef = storage.reference

    private lateinit var preferences: SharedPreferences

    private val signInLauncher = registerForActivityResult(
        FirebaseAuthUIActivityResultContract(),
    ) { res ->
        this.onSignInResult(res)
    }

    private val chooseFileLauncher = registerForActivityResult(
        ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        if (uri != null) {
            Log.i("MyTag", "uri $uri")
            val referenceToImage = storageRef.child("images/${uri.lastPathSegment}")
            referenceToImage.putFile(uri)
                .addOnSuccessListener {
                    referenceToImage.downloadUrl
                        .addOnSuccessListener { url ->
                            val downloadUrl = url.toString()
                            sendMessage(null, downloadUrl)
                            Log.i("MyTag", "Ссылка на загруженный файл: $downloadUrl")
                        }
                        .addOnFailureListener {
                            Log.e("MyTag", "Ошибка получения ссылки на загруженный файл", it)
                        }
                    Toast.makeText(this, "Load success", Toast.LENGTH_LONG).show()
                }
                .addOnFailureListener {
                    Toast.makeText(this, "Load failed", Toast.LENGTH_LONG).show()
                }
        }
    }

    private lateinit var editTextMessage: EditText
    private lateinit var recyclerViewMessages: RecyclerView
    private lateinit var imageViewSendMessage: ImageView
    private lateinit var imageViewAddImage: ImageView
    private val messagesAdapter = MessagesAdapter(this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        preferences = this.getSharedPreferences("com.example.mychatapp", Context.MODE_PRIVATE)

        recyclerViewMessages = findViewById<RecyclerView>(R.id.recyclerViewMessages)

        editTextMessage = findViewById(R.id.editTextMessage)
        imageViewSendMessage = findViewById<ImageView>(R.id.imageViewSendMessage)
        imageViewAddImage = findViewById<ImageView>(R.id.imageViewAddImage)

        recyclerViewMessages.layoutManager = LinearLayoutManager(this)
        recyclerViewMessages.adapter = messagesAdapter
        imageViewSendMessage.setOnClickListener{
            val textOfMessage = editTextMessage.text.toString().trim()
            sendMessage(textOfMessage, null)
        }

        imageViewAddImage.setOnClickListener {
            chooseFileLauncher.launch("image/*")
        }

        if (auth.currentUser != null) {
            preferences.edit().putString("author", auth.currentUser?.email).apply()
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
            preferences.edit().putString("author", user?.email).apply()
            Toast.makeText(this, "Loggined ${user?.email}",Toast.LENGTH_LONG).show()
        } else {
            Toast.makeText(this, "Error ${response?.error}",Toast.LENGTH_LONG).show()
        }
    }


    private fun sendMessage(textOfMessage: String?, urlToImage: String?){
        var message: Message? = null
        val author = preferences.getString("author", "Anonim") ?:"Anonim"
        if(textOfMessage != null && textOfMessage.isNotEmpty()){
            message = Message(author, textOfMessage, System.currentTimeMillis(), null)
        } else if (urlToImage != null && urlToImage.isNotEmpty()){
            message = Message(author, null, System.currentTimeMillis(), urlToImage)
        }
        if (message != null) {
            db.collection("messages").add(message)
                .addOnSuccessListener {
                    editTextMessage.setText("")
                    recyclerViewMessages.scrollToPosition(messagesAdapter.itemCount - 1)
                }
                .addOnFailureListener {
                    Toast.makeText(this, "Сообщение не отправлено",Toast.LENGTH_LONG).show()
                }
        }
    }

}