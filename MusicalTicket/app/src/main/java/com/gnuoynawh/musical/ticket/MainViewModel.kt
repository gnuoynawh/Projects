package com.gnuoynawh.musical.ticket

import android.app.Application
import androidx.fragment.app.Fragment
import androidx.lifecycle.AndroidViewModel

class MainViewModel(application: Application) : AndroidViewModel(application) {

    fun changeFragment(activity: MainActivity, fragment: Fragment) {
        activity.supportFragmentManager
            .beginTransaction()
            .replace(R.id.body, fragment)
            .commit()
    }
}