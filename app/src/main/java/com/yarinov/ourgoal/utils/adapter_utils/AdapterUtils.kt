package com.yarinov.ourgoal.utils.adapter_utils

import android.annotation.SuppressLint
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.view.View
import android.view.ViewGroup
import android.view.animation.AlphaAnimation
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageException
import com.squareup.picasso.Picasso
import com.yarinov.ourgoal.R
import com.yarinov.ourgoal.goal.Goal
import de.hdodenhof.circleimageview.CircleImageView
import java.text.SimpleDateFormat
import java.util.*

class AdapterUtils {


    public fun setFadeAnimation(view: View, animationDuration: Int) {
        val anim = AlphaAnimation(0.0f, 1.0f)
        anim.duration = animationDuration.toLong()
        view.startAnimation(anim)
    }

    @SuppressLint("SetTextI18n")
    public fun getTimeSincePosted(position: Int, datePosted: String): String {


        val sdf = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:sss'Z'")
        sdf.timeZone = TimeZone.getTimeZone("UTC")
        val postedDate = sdf.parse(datePosted)
        var postedDateCalendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"))
        postedDateCalendar.time = postedDate

        val currentDate = Calendar.getInstance()

        val currentDateHr = currentDate[Calendar.HOUR_OF_DAY]
        val currentDateMin = currentDate[Calendar.MINUTE]
        val currentDateDay = currentDate[Calendar.DAY_OF_MONTH]
        val currentDateMonth = currentDate[Calendar.MONTH]
        val currentDateYear = currentDate[Calendar.YEAR]

        val postedDateDateHr = postedDateCalendar[Calendar.HOUR_OF_DAY]
        val postedDateDateMin = postedDateCalendar[Calendar.MINUTE]
        val postedDateDateDay = postedDateCalendar[Calendar.DAY_OF_MONTH]
        val postedDateDateMonth = postedDateCalendar[Calendar.MONTH]
        val postedDateYear = postedDateCalendar[Calendar.YEAR]

        val postedDateToShow =
            "$postedDateDateHr:$postedDateDateMin ${getMonthName(postedDateDateMonth)} ${postedDateCalendar[Calendar.DAY_OF_MONTH]} $postedDateYear"

        return if (currentDateYear == postedDateYear) {
            if (currentDateMonth == postedDateDateMonth) {
                if (currentDateDay == postedDateDateDay) {
                    if (currentDateHr == postedDateDateHr) {
                        if (currentDateMin == postedDateDateMin) {
                            "Now"
                        } else {
                            "${currentDateMin - postedDateDateMin} m"
                        }
                    } else {
                        "${currentDateHr - postedDateDateHr} h"
                    }
                } else {
                    if ((currentDateDay - postedDateDateDay) < 7)
                        "${currentDateDay - postedDateDateDay} d"
                    else postedDateToShow

                }
            } else {
                postedDateToShow
            }
        } else {
            postedDateToShow
        }

    }

    fun getMonthName(monthNumber: Int): String {

        return when (monthNumber) {
            0 -> {
                "Jan"
            }
            1 -> {
                "Feb"
            }
            2 -> {
                "Mar"
            }
            3 -> {
                "Apr"
            }
            4 -> {
                "May"
            }
            5 -> {
                "Jun"
            }
            6 -> {
                "Jul"
            }
            7 -> {
                "Aug"
            }
            8 -> {
                "Sep"
            }
            9 -> {
                "Oct"
            }
            10 -> {
                "Nov"
            }
            else -> {
                "Dec"
            }
        }


    }

    fun applyDim(parent: ViewGroup, dimAmount: Float) {
        val dim: Drawable = ColorDrawable(Color.BLACK)
        dim.setBounds(0, 0, parent.width, parent.height)
        dim.alpha = (255 * dimAmount).toInt()
        val overlay = parent.overlay
        overlay.add(dim)
    }

    fun clearDim(parent: ViewGroup) {
        val overlay = parent.overlay
        overlay.clear()
    }

    fun loadUserProfilePic(
        circleImageView: CircleImageView?,
        userId: String
    ) {
        val storage = FirebaseStorage.getInstance()

        val gsReference =
            storage.getReferenceFromUrl("gs://ourgoal-ebee9.appspot.com/users/profile_pic/$userId.jpg")

        gsReference.downloadUrl
            .addOnSuccessListener {

                Picasso.get().load(it).placeholder(R.drawable.default_user_ic).noFade()
                    .into(circleImageView)

            }
            .addOnFailureListener { exception ->
                val errorCode = (exception as StorageException).errorCode
                if (errorCode == StorageException.ERROR_OBJECT_NOT_FOUND) {
                    //Not Found
                }
            }
    }

    fun getStepWeight(goal: Goal): Long {

        return 100 / (goal.goalSteps + 1)
    }
}