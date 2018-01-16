package com.github.laurenttreguier.ratebook

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.media.AudioManager
import android.media.MediaPlayer
import android.os.Build
import android.provider.Telephony
import android.telephony.SmsMessage

class SmsListener : BroadcastReceiver() {
    companion object {
        val sounds = arrayOf(
                R.raw.star1,
                R.raw.star2,
                R.raw.star3,
                R.raw.star4,
                R.raw.star5)
    }

    override fun onReceive(context: Context?, intent: Intent?) {
        if (intent?.action == Telephony.Sms.Intents.SMS_RECEIVED_ACTION) {
            val pdus: Array<*> = intent.extras.get("pdus") as Array<*>

            (0 until pdus.size)
                    .map {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                            SmsMessage.createFromPdu(pdus[it] as ByteArray?, intent.extras.getString("format"))
                        } else {
                            @Suppress("DEPRECATION")
                            SmsMessage.createFromPdu(pdus[it] as ByteArray?)
                        }
                    }
                    .filter { it.messageBody.contains(Regex("^[★☆]{5}")) }
                    .forEach {
                        var rating = 1

                        while (rating < it.messageBody.length && it.messageBody[rating] == '★') {
                            ++rating
                        }

                        context?.let {
                            val audioManager = context.getSystemService(Context.AUDIO_SERVICE) as AudioManager
                            val currentVolume = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC)

                            audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC), 0)

                            val mediaPlayer = MediaPlayer.create(context, sounds[rating - 1])
                            mediaPlayer.setOnCompletionListener { audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, currentVolume, 0) }
                            mediaPlayer.start()
                        }
                    }
        }
    }
}