package uqac.dim.myapp1.utils

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import uqac.dim.myapp1.R

object UserProfileUtils {

    // Référence à la bd Firestore
    private val db = FirebaseFirestore.getInstance()

    // Enregistre le profil utilisateur (nom + avatar) dans Firestore et SharedPreferences
    fun sauvegarderProfilUtilisateur(context: Context, utilisateurId: String, nomUtilisateur: String, avatar: Int) {
        val profilData = mapOf(
            "userId" to utilisateurId,
            "username" to nomUtilisateur,
            "avatar" to avatar
        )

        // Sauvegarde dans Firestore
        db.collection("users").document(utilisateurId)
            .set(profilData)
            .addOnSuccessListener {
                Log.d("Firestore", "Profil enregistré pour userId: $utilisateurId")
            }
            .addOnFailureListener { e ->
                Log.e("Firestore", "Erreur enregistrement profil: ${e.message}")
            }

        // Sauvegarde locale avec SharedPreferences pour acceder plus rapidement
        val sharedPreferences = context.getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
        sharedPreferences.edit().apply {
            putString("user_name", nomUtilisateur)// Enregistre le nom
            putInt("user_image", avatar)// Enregistre l'avatar
            apply()// Applique les modifications
        }
    }

    // Récupérer le profil utilisateur depuis SharedPreferences pour un affichage rapide
    fun recupererProfilUtilisateurLocal(context: Context): ProfilUtilisateur {
        val sharedPreferences = context.getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
        val nomUtilisateur = sharedPreferences.getString("user_name", "Utilisateur") ?: "Utilisateur"
        val avatar = sharedPreferences.getInt("user_image", R.drawable.ic_profile_1)
        return ProfilUtilisateur(nomUtilisateur, avatar)
    }

    // Récupérer le profil utilisateur depuis Firestore (mise à jour en arrière-plan car un peu plus lent)
    suspend fun recupererProfilUtilisateurFirestore(): ProfilUtilisateur? {
        val utilisateurId = FirebaseAuth.getInstance().currentUser?.uid ?: return null

        return try {
            val document = db.collection("users").document(utilisateurId).get().await()
            if (document.exists()) {
                val nomUtilisateur = document.getString("username") ?: "Utilisateur"
                val avatar = document.getLong("avatar")?.toInt() ?: R.drawable.ic_profile_1

                Log.d("Firestore", "Profil mis à jour depuis Firestore: $nomUtilisateur")
                ProfilUtilisateur(nomUtilisateur, avatar)
            } else {
                null // Si le document dans Firestore n'existe pas, renvoyer null
            }
        } catch (e: Exception) {
            Log.e("Firestore", "Erreur Firestore: ${e.message}")
            null
        }
    }
}

// Modèle de données pour stocker les informations du profil
data class ProfilUtilisateur(
    val nomUtilisateur: String,//Nom ou pseudo
    val avatar: Int//avatar
)
