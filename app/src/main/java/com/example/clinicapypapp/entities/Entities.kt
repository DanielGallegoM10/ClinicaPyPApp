package com.example.clinicapypapp.entities


import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import kotlinx.serialization.Serializable


@Serializable
data class Section(val image: Int, val sectionName: String, val specialist: String)

@Serializable
data class Service(val serviceName: String, val splainText: String, val time: Int)