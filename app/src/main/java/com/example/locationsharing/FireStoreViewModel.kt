package com.example.locationsharing


import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.FirebaseFirestore

class FireStoreViewModel: ViewModel() {
    private val firestore = FirebaseFirestore.getInstance()
    private val userCollection = firestore.collection("users")

    fun saveUser(userId:String, displayName:String, email:String, location:String){
        val user = hashMapOf(
            "displayName" to displayName,
            "email" to email,
            "location" to location
        )
        userCollection.document(userId).set(user)
            .addOnSuccessListener {  }
            .addOnFailureListener { e-> }
    }
    fun getAllUser(callback: (List<User>)->Unit){
        userCollection.get()
            .addOnSuccessListener{result->
                val userList = mutableListOf<User>()
                for(document in result){
                    val userId = document.id
                    val displayName = document.getString("displayName")?: ""
                    val email = document.getString("email")?: ""
                    val location = document.getString("location")?: ""

                    userList.add(User(userId, displayName, email, location))
                }
                callback(userList)
            }
            .addOnFailureListener{e->}
    }
    fun updateUser(userId: String, displayName: String, location: String){
        val user = hashMapOf(
            "displayName" to displayName,
            "location" to location
        )
        val userMap = user.toMap()
        userCollection.document(userId).set(userMap)
            .addOnSuccessListener {  }
            .addOnFailureListener { e-> }
    }
    fun updateUserLocation(userId: String, location: String){
        if (userId.isEmpty()){
            return
        }
        val user = hashMapOf(
            "location" to location
        )
        val userMap = user.toMap()
        userCollection.document(userId).update(userMap)
            .addOnSuccessListener {  }
            .addOnFailureListener{}
    }
    fun getUser(userId: String, callback:(User?) -> Unit){
        userCollection.document(userId).get()
            .addOnSuccessListener { documentSnapshot ->
                val user = documentSnapshot.toObject(User::class.java)
                callback(user)
            }
            .addOnFailureListener{
                callback(null)
            }
    }
    fun getUserLocation(userId: String, callback: (String) -> Unit){
        userCollection.document(userId).get()
            .addOnSuccessListener { documentSnapShort->
                val location = documentSnapShort.getString("location")?:""
                callback(location)
            }
            .addOnFailureListener{
                callback("")
            }
    }
}