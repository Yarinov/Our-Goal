package com.yarinov.ourgoal.user.auth

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.facebook.FacebookButtonBase
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.OAuthProvider
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.yarinov.ourgoal.MainActivity
import com.yarinov.ourgoal.R
import com.yarinov.ourgoal.user.User


class AuthenticationActivity : AppCompatActivity() {

    var a: FacebookButtonBase? = null


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

    //Social login
    var facebookSignInIcon: ImageView? = null
    var googleSignInIcon: ImageView? = null
    var twitterSignInIcon: ImageView? = null

    //Google Login Request Code
    private val RC_SIGN_IN = 7
    //Google Sign In Client
    private lateinit var mGoogleSignInClient: GoogleSignInClient


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

        //Social login
        facebookSignInIcon = findViewById(R.id.facebookSignInIcon)
        googleSignInIcon = findViewById(R.id.googleSignInIcon)
        twitterSignInIcon = findViewById(R.id.twitterSignInIcon)

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
            emailAndPasswordLoginToApp()
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

        //Google login setup
        //Configure Google Sign In
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso)

        //login with google
        googleSignInIcon!!.setOnClickListener {
            googleLogin()
        }

        //Twitter login setup
        //login with twitter
        twitterSignInIcon!!.setOnClickListener {
            twitterLogin()
        }

    }

    private fun twitterLogin() {

        val twitterProvider = OAuthProvider.newBuilder("twitter.com")

        mAuth!!.startActivityForSignInWithProvider(this, twitterProvider.build())
            .addOnSuccessListener {
                checkForExistingUserAndLogin()
            }.addOnFailureListener {
            Toast.makeText(this, "Auth Failed", Toast.LENGTH_SHORT).show()
        }
    }

    private fun googleLogin() {

        val signInIntent = mGoogleSignInClient.signInIntent
        startActivityForResult(signInIntent, RC_SIGN_IN)

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                // Google Sign In was successful, authenticate with Firebase
                val account = task.getResult(ApiException::class.java)
                firebaseAuthWithGoogle(account!!)
            } catch (e: ApiException) {
                // Google Sign In failed, update UI appropriately
            }

        }

    }

    private fun firebaseAuthWithGoogle(acct: GoogleSignInAccount) {

        val credential = GoogleAuthProvider.getCredential(acct.idToken, null)
        mAuth!!.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {

                    checkForExistingUserAndLogin()
                } else {
                    // If sign in fails, display a message to the user.
                    Toast.makeText(this, "Auth Failed", Toast.LENGTH_SHORT).show()
                }

                // ...
            }
    }

    private fun checkForExistingUserAndLogin() {

        // Sign in success, update UI with the signed-in user's information
        val user = mAuth!!.currentUser

        //Check if user already exits
        val userExistFlag =
            FirebaseDatabase.getInstance().reference.child("users/${user!!.uid}")

        userExistFlag.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {}

            override fun onDataChange(p0: DataSnapshot) {
                //If this is a new user, add his data to users DB
                if (!p0.exists()) {

                    //Get user first and last name
                    val userNameArray = user.displayName!!.split(" ")

                    var userFirstName: String
                    var userLastName: String

                    when (userNameArray.size) {
                        1 -> {
                            userFirstName = userNameArray[0]
                            userLastName = ""
                        }
                        2 -> {
                            userFirstName = userNameArray[0]
                            userLastName = userNameArray[1]
                        }
                        else -> {

                            userFirstName = "${userNameArray[0]} ${userNameArray[1]}"
                            userLastName = userNameArray[userNameArray.size - 1]
                        }
                    }

                    var newUser = User(
                        user.uid,
                        user.email.toString(),
                        userFirstName,
                        userLastName
                    )

                    rootDB!!.getReference("users/${newUser.userId}").setValue(newUser)
                    rootDB!!.getReference("users/${newUser.userId}/privateAccount")
                        .setValue(false)
                }


                val toMainActivityIntent =
                    Intent(this@AuthenticationActivity, MainActivity::class.java)
                startActivity(toMainActivityIntent)
            }

        })


    }

    private fun emailAndPasswordLoginToApp() {

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
