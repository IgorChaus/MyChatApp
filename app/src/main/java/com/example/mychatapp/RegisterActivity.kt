package com.example.mychatapp

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth

class RegisterActivity : AppCompatActivity() {

    private val auth = FirebaseAuth.getInstance()
    private lateinit var editTextEmail: EditText
    private lateinit var editTextPassword: EditText
    private lateinit var textViewHaveAccount: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        editTextEmail = findViewById(R.id.editTextEmail)
        editTextPassword = findViewById(R.id.editTextPassword)
        textViewHaveAccount = findViewById(R.id.textViewHaveAnAccount)
        textViewHaveAccount.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }


    }

    fun onClickRegister(view: View) {
        val email = editTextEmail.text.toString().trim()
        val password = editTextPassword.text.toString().trim()
        if (email.isEmpty() || password.isEmpty()){
            return
        }

        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    val intent = Intent(this, MainActivity::class.java)
                    startActivity(intent)
                } else {
                    Toast.makeText(this, "Ошибка: ${task.exception}", Toast.LENGTH_SHORT).show()
                }
            }
    }
}