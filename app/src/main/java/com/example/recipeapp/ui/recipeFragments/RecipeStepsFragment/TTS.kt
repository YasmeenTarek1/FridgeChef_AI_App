package com.example.recipeapp.ui.recipeFragments.RecipeStepsFragment

import android.app.Activity
import android.speech.tts.TextToSpeech
import android.speech.tts.Voice
import android.widget.Toast
import java.util.Locale

class TTS(private val activity: Activity, private val message: String) : TextToSpeech.OnInitListener {

    private val tts: TextToSpeech = TextToSpeech(activity, this)

    override fun onInit(i: Int) {
        if (i == TextToSpeech.SUCCESS) {

            val localeEN = Locale("en", "GB")
            val voice = Voice("en-GB-Standard-A", localeEN, Voice.QUALITY_VERY_HIGH, Voice.LATENCY_VERY_HIGH, true, emptySet())
            tts.setVoice(voice)
            val result = tts.setLanguage(localeEN)


            if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                Toast.makeText(activity, "This Language is not supported", Toast.LENGTH_SHORT).show()
            } else {
                speakOut(message)
            }

        } else {
            Toast.makeText(activity, "Initilization Failed!", Toast.LENGTH_SHORT).show()
        }
    }

    private fun speakOut(message: String) {
        tts.speak(message, TextToSpeech.QUEUE_FLUSH, null, null)
    }

    fun stopTTS() {
        tts.stop() // Stop the ongoing speech if any
    }

    fun release() {
        tts.shutdown() // Call this to release resources when TTS is no longer needed
    }
}