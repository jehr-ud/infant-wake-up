package com.neworesearchgroup.bemarkalarm.data.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth

class RegisterViewModel : ViewModel() {

    var email by mutableStateOf("")
        private set

    var password by mutableStateOf("")
        private set

    var confirmPassword by mutableStateOf("")
        private set

    var error by mutableStateOf<String?>(null)
        private set

    var loading by mutableStateOf(false)
        private set

    private val auth = FirebaseAuth.getInstance()

    fun onEmailChange(value: String) {
        email = value
    }

    fun onPasswordChange(value: String) {
        password = value
    }

    fun onConfirmPasswordChange(value: String) {
        confirmPassword = value
    }

    fun register(onSuccess: () -> Unit) {
        error = null

        if (email.isBlank() || password.isBlank()) {
            error = "Email and password are required"
            return
        }

        if (password.length < 6) {
            error = "Password must be at least 6 characters"
            return
        }

        if (password != confirmPassword) {
            error = "Passwords do not match"
            return
        }

        loading = true

        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                loading = false
                if (task.isSuccessful) {
                    onSuccess()
                } else {
                    error = task.exception?.localizedMessage ?: "Registration failed"
                }
            }
    }
}
