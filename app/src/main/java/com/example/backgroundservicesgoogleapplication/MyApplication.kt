package com.example.backgroundservicesgoogleapplication

import android.app.Application

class MyApplication: Application() {

    companion object{
        private var locationListener: LocationListener? = null
        private var toastListener:ToastListener?=null

        fun setLocationListener(listener: LocationListener?){
            this.locationListener = listener
        }
        fun getLocationListener(): LocationListener? {
            return this.locationListener
        }

        fun setToastListener(listener: ToastListener?){
            this.toastListener =listener
        }
        fun getToastListener(): ToastListener? {
            return this.toastListener
        }

    }
}