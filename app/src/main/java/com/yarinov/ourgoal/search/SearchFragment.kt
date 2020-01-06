package com.yarinov.ourgoal.search


import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.yarinov.ourgoal.R
import com.yarinov.ourgoal.user.User

/**
 * A simple [Fragment] subclass.
 */
class SearchFragment : Fragment() {

    private var searchInputEditText: EditText? = null

    var searchResults: ArrayList<User>? = null

    var searchRecyclerView: RecyclerView? = null
    var searchResultAdapter: SearchResultAdapter? = null

    var rootDB: FirebaseDatabase? = null


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val searchFragmentView = inflater
            .inflate(R.layout.fragment_search, container, false)

        searchRecyclerView = searchFragmentView.findViewById(R.id.searchRecyclerView)
        searchInputEditText = searchFragmentView.findViewById(R.id.searchInputEditText)


        rootDB = FirebaseDatabase.getInstance()

        //Init Search Results
        searchResults = ArrayList()
        searchResultAdapter = SearchResultAdapter(context!!, searchResults!!)

        searchInputEditText?.setOnKeyListener { v, keyCode, event ->
            if (keyCode == KeyEvent.KEYCODE_ENTER) {

                //Soft Hide Keypad
                val imm =
                    context?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                imm.hideSoftInputFromWindow(searchInputEditText?.windowToken, 0)

                val userSearchInput = searchInputEditText!!.text.toString()

                if (userSearchInput.isNotEmpty()) searchUser(userSearchInput)

            }
            false
        }


        searchRecyclerView!!.setHasFixedSize(true)
        searchRecyclerView!!.layoutManager = LinearLayoutManager(context)
        searchRecyclerView!!.adapter = searchResultAdapter


        return searchFragmentView
    }

    @SuppressLint("DefaultLocale")
    private fun searchUser(userSearchInput: String) {

        searchResults?.clear()

        val usersRootDB = rootDB?.reference!!.child("users")

        val userRootDBListener = object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {}

            override fun onDataChange(p0: DataSnapshot) {

                searchResults?.clear()

                //For each user in DB
                for (user in p0.children) {

                    var userFullName =
                        user.child("firstName").value.toString() + user.child("lastName").value.toString()

                    if (userFullName.toLowerCase().contains(userSearchInput.toLowerCase())) {
                        val currentUserSearchResult =
                            User(
                                user.child("userId").value.toString(),
                                user.child("userEmail").value.toString(),
                                user.child("firstName").value.toString(),
                                user.child("lastName").value.toString(),
                                user.child("userImageUri").value.toString()
                            )

                        searchResults!!.add(currentUserSearchResult)
                    }
                }

                searchResultAdapter?.notifyDataSetChanged()
            }

        }

        usersRootDB.addValueEventListener(userRootDBListener)

    }


}
