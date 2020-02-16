package com.yarinov.ourgoal.user.auth

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager.widget.ViewPager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.yarinov.ourgoal.MainActivity
import com.yarinov.ourgoal.R
import com.yarinov.ourgoal.user.User


class AuthenticationActivity : AppCompatActivity() {


    var loginMainLayout: LinearLayout? = null
    var signupMainLayout: LinearLayout? = null
    var goToSignupLayout: LinearLayout? = null
    var goToSigninLayout: LinearLayout? = null

    var inRegistrationFlag = false

    private var mAuth: FirebaseAuth? = null

    //Login section
    private var emailInput: EditText? = null
    private var passwordInput: EditText? = null

    private var loginButton: Button? = null

    //Signup Section
    private var firstNameEditText: EditText? = null
    private var lastNameEditText: EditText? = null
    private var emailEditText: EditText? = null
    private var passwordEditText: EditText? = null
    private var repasswordEditText: EditText? = null

    private var createAccountButton: Button? = null

    var rootDB: FirebaseDatabase? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_authentication)

        loginMainLayout = findViewById(R.id.loginMainLayout)
        signupMainLayout = findViewById(R.id.signupMainLayout)
        goToSigninLayout = findViewById(R.id.goToSigninLayout)
        goToSignupLayout = findViewById(R.id.goToSignupLayout)

        //login section
        emailInput = findViewById(R.id.emailInput)
        passwordInput = findViewById(R.id.passwordInput)

        loginButton = findViewById(R.id.loginButton)

        //Signup section init
        rootDB = FirebaseDatabase.getInstance()
        mAuth = FirebaseAuth.getInstance()

        firstNameEditText = findViewById(R.id.firstNameEditText)
        lastNameEditText = findViewById(R.id.lastNameEditText)
        emailEditText = findViewById(R.id.emailEditText)
        passwordEditText = findViewById(R.id.passwordEditText)
        repasswordEditText = findViewById(R.id.repasswordEditText)
        createAccountButton = findViewById(R.id.createAccountButton)

        //login to app
        loginButton!!.setOnClickListener {
            loginToApp()
        }

        //Create Account
        createAccountButton!!.setOnClickListener {
            signup()
        }

        //Move from signin to signup
        goToSignupLayout!!.setOnClickListener {
            goToSignupLayout()
        }

        //Move from signup to signin
        goToSigninLayout!!.setOnClickListener {
            goToLoginLayout()
        }
    }

    private fun loginToApp() {

        var email = emailInput!!.text
        var password = passwordInput!!.text

        if (email.isNullOrBlank() || password.isNullOrBlank()
        ) {
            Toast.makeText(this, "Fill All Fields", Toast.LENGTH_SHORT).show()

            return
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            Toast.makeText(this, "Email Not Valid", Toast.LENGTH_SHORT).show()
            return
        }


        this.let {
            mAuth!!.signInWithEmailAndPassword(email.toString(), password.toString())
                .addOnCompleteListener(
                    it
                ) { task ->
                    if (task.isSuccessful) { // Sign in success, update UI with the signed-in user's information
                        val toMainActivityIntent = Intent(this, MainActivity::class.java)
                        startActivity(toMainActivityIntent)
                    } else { // If sign in fails, display a message to the user.

                        Toast.makeText(
                            this, "Authentication failed.",
                            Toast.LENGTH_SHORT
                        ).show()

                    }
                    // ...
                }
        }

    }

    private fun signup() {

        val firstNameInput = firstNameEditText!!.text.toString()
        val lastNameInput = lastNameEditText!!.text.toString()
        val emailInput = emailEditText!!.text.toString()
        val passwordInput = passwordEditText!!.text.toString()
        val repasswordInput = repasswordEditText!!.text.toString()


        if (firstNameInput.isEmpty() || lastNameInput.isEmpty() || emailInput.isEmpty() || passwordInput.isEmpty() || repasswordInput.isEmpty()) {
            Toast.makeText(this, "Fill All The Required Fields.", Toast.LENGTH_SHORT).show()
            return
        }


        if (passwordInput.length < 6) {
            Toast.makeText(
                this,
                "Password Must Be At Least 6 Characters.",
                Toast.LENGTH_SHORT
            ).show()
            return
        }

        if (passwordInput != repasswordInput) {
            Toast.makeText(
                this,
                "Password Must Be Match.",
                Toast.LENGTH_SHORT
            ).show()

            return
        }

        this.let {
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
                        rootDB!!.getReference("users/${newUser.userId}/privateAccount")
                            .setValue(false)

                        val toMainActivityIntent = Intent(this, MainActivity::class.java)
                        startActivity(toMainActivityIntent)
                    } else { // If sign in fails, display a message to the user.

                        Toast.makeText(
                            this, "Authentication failed.",
                            Toast.LENGTH_SHORT
                        ).show()

                    }
                    // ...
                }
        }

    }

    private fun goToSignupLayout() {

        inRegistrationFlag = true

        //Hide the login layout
        loginMainLayout!!.animate().translationX(-loginMainLayout!!.width.toFloat()).alpha(0f)
            .setListener(object : AnimatorListenerAdapter() {

                override fun onAnimationEnd(animation: Animator?) {
                    super.onAnimationEnd(animation)
                    loginMainLayout!!.visibility = View.GONE

                    signupMainLayout!!.visibility = View.VISIBLE
                    signupMainLayout!!.alpha = 0.0f

                    signupMainLayout!!.animate()
                        .alpha(1.0f)
                        .setListener(null)


                }
            })
    }

    private fun goToLoginLayout() {

        signupMainLayout!!.animate()
            .alpha(0.0f)
            .setListener(object : AnimatorListenerAdapter() {
                override fun onAnimationEnd(animation: Animator?) {
                    super.onAnimationEnd(animation)
                    signupMainLayout!!.visibility = View.GONE


                    loginMainLayout!!.visibility = View.VISIBLE
                    loginMainLayout!!.alpha = 0.0f

                    loginMainLayout!!.animate()
                        .translationX(0f)
                        .alpha(1.0f)
                        .setListener(null)

                    inRegistrationFlag = false
                }
            })
    }

    override fun onBackPressed() {

        if (inRegistrationFlag) {
            goToLoginLayout()
        } else
            super.onBackPressed()
    }


}
