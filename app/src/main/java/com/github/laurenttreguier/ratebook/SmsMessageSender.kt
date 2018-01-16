package com.github.laurenttreguier.ratebook

import android.content.DialogInterface
import android.telephony.SmsManager
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView

class SmsMessageSender(private val number: String) : View.OnClickListener, DialogInterface.OnClickListener {
    private var rating = 1

    override fun onClick(star: View?) {
        star?.let {
            val layout = star.parent as ViewGroup
            rating = layout.indexOfChild(star) + 1

            for (i in 0 until layout.childCount) {
                (layout.getChildAt(i) as ImageView).setImageResource(if (i < rating) R.drawable.ic_star else R.drawable.ic_star_border)
            }
        }
    }

    override fun onClick(dialog: DialogInterface?, which: Int) {
        SmsManager.getDefault().sendTextMessage(number, null, "★".repeat(rating) + "☆".repeat(5 - rating), null, null)
    }
}