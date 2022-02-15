package com.zhang.change

object AppHolder {
    lateinit var application: MyApplication
    val db by lazy { AppDatabase.getDatabase(application) }

}