package com.yarinov.ourgoal.user.auth


import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.yarinov.ourgoal.MainActivity
import com.yarinov.ourgoal.R
import com.yarinov.ourgoal.user.User


/**
 * A simple [Fragment] subclass.
 */
class RegistrationFragment : Fragment() {

    private var mAuth: FirebaseAuth? = null

    private var firstNameEditText: EditText? = null
    private var lastNameEditText: EditText? = null
    private var emailEditText: EditText? = null
    private var passwordEditText: EditText? = null
    private var repasswordEditText: EditText? = null

    private var createAccountButton: Button? = null

    var rootDB: FirebaseDatabase? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        rootDB = FirebaseDatabase.getInstance()
        mAuth = FirebaseAuth.getInstance()

        val registrationFragmentView =
            inflater.inflate(R.layout.fragment_registration, container, false)

        firstNameEditText = registrationFragmentView.findViewById(R.id.firstNameEditText)
        lastNameEditText = registrationFragmentView.findViewById(R.id.lastNameEditText)
        emailEditText = registrationFragmentView.findViewById(R.id.emailEditText)
        passwordEditText = registrationFragmentView.findViewById(R.id.passwordEditText)
        repasswordEditText = registrationFragmentView.findViewById(R.id.repasswordEditText)
        createAccountButton = registrationFragmentView.findViewById(R.id.createAccountButton)

        createAccountButton!!.setOnClickListener {
            signup()
        }

        return registrationFragmentView
    }

    private fun signup() {

        val firstNameInput = firstNameEditText!!.text.toString()
        val lastNameInput = lastNameEditText!!.text.toString()
        val emailInput = emailEditText!!.text.toString()
        val passwordInput = passwordEditText!!.text.toString()
        val repasswordInput = repasswordEditText!!.text.toString()


        if (firstNameInput.isEmpty() || lastNameInput.isEmpty() || emailInput.isEmpty() || passwordInput.isEmpty() || repasswordInput.isEmpty()) {
            Toast.makeText(context, "Fill All The Required Fields.", Toast.LENGTH_SHORT).show()
            return
        }


        if (passwordInput.length < 6) {
            Toast.makeText(
                context,
                "Password Must Be At Least 6 Characters.",
                Toast.LENGTH_SHORT
            ).show()
            return
        }

        if (passwordInput != repasswordInput) {
            Toast.makeText(
                context,
                "Password Must Be Match.",
                Toast.LENGTH_SHORT
            ).show()

            return
        }

        activity?.let {
            mAuth!!.createUserWithEmailAndPassword(emailInput, passwordInput)
                .addOnCompleteListener(
                    it
                ) { task ->
                    if (task.isSuccessful) {

                        val user = mAuth!!.currentUser

                        var newUser = User(
                            user!!.uid,
                            emailInput,
                            firstNameInput,
                            lastNameInput
                        )
                        rootDB!!.getReference("users/${newUser.userId}").setValue(newUser)
                        rootDB!!.getReference("users/${newUser.userId}/privateAccount").setValue(false)

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
