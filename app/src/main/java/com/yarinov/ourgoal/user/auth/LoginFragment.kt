package com.yarinov.ourgoal.user.auth


import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.Fragment

import com.google.firebase.auth.FirebaseAuth
import com.yarinov.ourgoal.MainActivity
import com.yarinov.ourgoal.R


class LoginFragment : Fragment() {

    private var mAuth: FirebaseAuth? = null

    private var emailInput: EditText? = null
    private var passwordInput: EditText? = null
    private var loginButton: Button? = null


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val loginFragmentView = inflater.inflate(R.layout.fragment_login, container, false)

        mAuth = FirebaseAuth.getInstance()

//        emailInput = loginFragmentView.findViewById(R.id.emailInput)
//        passwordInput = loginFragmentView.findViewById(R.id.passwordInput)



        return loginFragmentView
    }


    private fun loginToApp() {

        var email = emailInput!!.text
        var password = passwordInput!!.text

        if (email.isNullOrBlank() || password.isNullOrBlank()
        ) {
            Toast.makeText(context, "Fill All Fields", Toast.LENGTH_SHORT).show()

            return
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            Toast.makeText(context, "Email Not Valid", Toast.LENGTH_SHORT).show()
            return
        }


        activity?.let {
            mAuth!!.signInWithEmailAndPassword(email.toString(), password.toString())
                .addOnCompleteListener(
                    it
                ) { task ->
                    if (task.isSuccessful) { // Sign in success, update UI with the signed-in user's information
                        val toMainActivityIntent = Intent(context, MainActivity::class.java)
                        startActivity(toMainActivityIntent)
                    } else { // If sign in fails, display a message to the user.

                        Toast.makeText(
                            context, "Authentication failed.",
                            Toast.LENGTH_SHORT
                        ).show()

                    }
                    // ...
                }
        }

    }

}
