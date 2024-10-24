package com.example.linksharecompose.auth

import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore

object FirebaseCollection {
    private val firestore: FirebaseFirestore by lazy { FirebaseFirestore.getInstance() }
    val userCollection: CollectionReference by lazy { firestore.collection("users") }
}