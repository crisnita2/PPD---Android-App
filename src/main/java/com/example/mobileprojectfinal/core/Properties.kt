package com.example.mobileprojectfinal.core

import androidx.lifecycle.MutableLiveData

class Properties protected constructor() {
    var internetActive : MutableLiveData<Boolean> = MutableLiveData(false)
    var toastMessage : MutableLiveData<String> = MutableLiveData()

    companion object {
        private var mInstance: Properties? = null

        @get:Synchronized
        val instance: Properties
            get() {
                if (null == mInstance) {
                    mInstance = Properties()
                }
                return mInstance!!
            }
    }
}