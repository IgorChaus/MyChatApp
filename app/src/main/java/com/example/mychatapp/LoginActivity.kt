package com.example.mychatapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth

class LoginActivity : AppCompatActivity() {

    private val auth = FirebaseAuth.getInstance()
    private lateinit var editTextEmail: EditText
    private lateinit var editTextPassword: EditText
    private lateinit var textViewRegister: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        editTextEmail = findViewById(R.id.editTextEmail)
        editTextPassword = findViewById(R.id.editTextPassword)
        textViewRegister = findViewById(R.id.textViewRegister)
        textViewRegister.setOnClickListener {
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
        }
    }

    fun onClickLogin(view: View) {
        val email = editTextEmail.text.toString().trim()
        val password = editTextPassword.text.toString().trim()
        if (email.isEmpty() || password.isEmpty()){
            return
        }

        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    val intent = Intent(this@LoginActivity, MainActivity::class.java)
                    startActivity(intent)
                } else {
                    Toast.makeText(this, "Ошибка: ${task.exception}", Toast.LENGTH_SHORT).show()
                }
            }
    }

    fun sum(a: Int, b: Int): Int = a+b
    
}