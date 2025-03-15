package uqac.dim.myapp1.utils

import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import uqac.dim.myapp1.historique.Score

object HistoriqueUtils {

    // Instance de Firestore pour bd
    private val db = FirebaseFirestore.getInstance()

    // Sauvegarde du score dans Firestore avec userId et tempsTotal
    fun sauvegarderScoreVersFirestore(utilisateurId: String, nomUtilisateur: String, score: Int, totalQuestions: Int, tempsTotal: Int) {
        val scoreData = mapOf(
            "userId" to utilisateurId,
            "username" to nomUtilisateur,
            "score" to score,
            "totalQuestions" to totalQuestions,
            "totalTime" to tempsTotal,  // temps total
            "timestamp" to System.currentTimeMillis() //timestamps pour trier, filtrer et analyser les scores sur le temps.
        )

        // Ajout des données dans la collection scores de Firestore
        db.collection("scores").add(scoreData)
            .addOnSuccessListener {
                Log.d("Firestore", "Score enregistré avec succès pour userId: $utilisateurId")
            }
            .addOnFailureListener { e ->
                Log.e("Firestore", "Erreur lors de l'enregistrement du score: ${e.message}")
            }
    }

    // Récupération des scores de l'utilisateur connecté avec le bon nom d'utilisateur
    suspend fun recupererScoresUtilisateur(): List<Score> {
        val auth = FirebaseAuth.getInstance()
        val utilisateurId = auth.currentUser?.uid ?: return emptyList() // Vérifie si l'utilisateur est connecté

        Log.d("FirestoreDebug", "Récupération des scores pour userId: $utilisateurId")

        return try {
            // Récupération du nom d'utilisateur depuis Firestore
            val userDoc = db.collection("users").document(utilisateurId).get().await()
            val nomUtilisateur = userDoc.getString("username") ?: "Utilisateur" // Utilise le vrai nom a la place

            // Requête pour récupérer les scores de l'utilisateur avec un tri du plus récent au plus ancien
            val querySnapshot = db.collection("scores")
                .whereEqualTo("userId", utilisateurId)
                .orderBy("timestamp", com.google.firebase.firestore.Query.Direction.DESCENDING) // Trie du plus récent au plus ancien
                .get()
                .await()

            //ici, on Vérifie si des scores ont été trouvés
            if (querySnapshot.isEmpty) {
                Log.e("FirestoreDebug", "Aucun score trouvé pour userId: $utilisateurId")
            } else {
                Log.d("FirestoreDebug", "Nombre de scores récupérés: ${querySnapshot.size()}")
            }

            // Conversion des documents Firestore en objet Score
            querySnapshot.documents.mapNotNull { doc ->
                val score = doc.getLong("score")?.toInt()
                val totalQuestions = doc.getLong("totalQuestions")?.toInt()
                val tempsTotal = doc.getLong("totalTime")?.toInt() ?: 0  // Récupération du temps

                Log.d("FirestoreDebug", "Score récupéré: $nomUtilisateur - $score/$totalQuestions en $tempsTotal sec")

                // Vérification que les valeurs sont valides avant de créer l'objet Score
                if (score != null && totalQuestions != null) {
                    Score(nomUtilisateur, score, totalQuestions, tempsTotal) // Création de l'objet Score
                } else {
                    Log.e("FirestoreDebug", "Données corrompues dans Firestore: ${doc.id}")
                    null
                }
            }
        } catch (e: Exception) {
            Log.e("FirestoreDebug", "Erreur Firestore: ${e.message}")
            emptyList()// Retourne une liste vide en cas d'erreur
        }
    }
}

//jai mis beaucoup de log pour m'aider a debugger car au debut rien ne fonctionnait
// et j'avais trop d'erreurs
// meme chose pour les try...catch