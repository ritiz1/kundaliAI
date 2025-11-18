package com.example.kundaliai

import android.content.Context
import androidx.core.content.edit

//This is used for storing preference keys
//This is used for making onboarding screen.
object Prefs {
    private const val NAME = "MyAppPrefs"
    private const val ONBOARDING_DONE = "ONBOARDING_DONE"

    fun setOnboardingDone(context: Context,done:Boolean){
        val sp = context.getSharedPreferences(NAME, Context.MODE_PRIVATE)
        sp.edit{
            putBoolean(ONBOARDING_DONE, done)
        }

    }

    fun isOnBoardingDone(context: Context):Boolean{
        val sp = context.getSharedPreferences(NAME, Context.MODE_PRIVATE)
        return sp.getBoolean(ONBOARDING_DONE, false)
    }

}