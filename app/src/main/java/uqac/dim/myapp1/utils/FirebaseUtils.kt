package uqac.dim.myapp1.utils

import android.content.Context
import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import org.json.JSONArray
import java.io.IOException

object FirebaseUtils {

    // Instance de Firestore pour interagir avec la bd
    private val db = FirebaseFirestore.getInstance()
    private var uploadEffectue = false // Flag pour éviter le rechargement multiple

    // Charger la bd sous forme fichier JSON depuis assets
    private fun chargerJSONDepuisAssets(contexte: Context, nomFichier: String): String {
        return try {
            val inputStream = contexte.assets.open(nomFichier)
            val taille = inputStream.available()
            val buffer = ByteArray(taille)
            inputStream.read(buffer)
            inputStream.close()
            String(buffer, Charsets.UTF_8)
        } catch (ex: IOException) {
            Log.e("Firestore", "Erreur lors du chargement du fichier: ${ex.message}")
            ""
        }
    }

    // Envoyer le JSON dans Firestore
    fun envoyerQuestionsVersFirestore(contexte: Context, onComplete: () -> Unit) {
        if (uploadEffectue) {
            Log.d("Firestore", "Les questions ont déjà été mises à jour.")
            onComplete()
            return
        }

        try {
            // Charger le JSON contenant ma bd de questions
            val jsonString = chargerJSONDepuisAssets(contexte, "questions.json")
            val jsonArray = JSONArray(jsonString)

            // Vérifier si le fichier JSON est vide ou npas
            if (jsonArray.length() == 0) {
                Log.e("Firestore", "Le fichier JSON est vide.")
                onComplete()
                return
            }

            // Compteur pour suivre le nombre de quiz téléchargés
            var compteurUpload = 0
            val totalQuizzes = jsonArray.length()

            // Parcourir chaque quiz dans le JSON
            for (i in 0 until jsonArray.length()) {
                val quizObject = jsonArray.getJSONObject(i)
                val quizId = quizObject.getString("id")

                //structure de données pour le quiz
                val quizData = hashMapOf<String, Any>(
                    "title" to quizObject.getString("title"),
                    "subtitle" to quizObject.getString("subtitle"),
                    "time" to quizObject.getString("time"),
                    "id" to quizId
                )

                val listeQuestions = mutableListOf<Map<String, Any>>()
                val listeQuestionsArray = quizObject.getJSONArray("questionList")

                // Parcourir les questions du quiz
                for (j in 0 until listeQuestionsArray.length()) {
                    val questionObj = listeQuestionsArray.getJSONObject(j)
                    val optionsArray = questionObj.getJSONArray("options")

                    val optionsList = mutableListOf<String>()
                    // Récupérer les options de réponse
                    for (k in 0 until optionsArray.length()) {
                        optionsList.add(optionsArray.getString(k))
                    }

                    //structure de données pour n'importe quelle question
                    val questionMap = hashMapOf<String, Any>(
                        "question" to questionObj.getString("question"),
                        "correct" to questionObj.getString("correct"),
                        "options" to optionsList
                    )
                    listeQuestions.add(questionMap)
                }
                quizData["questionList"] = listeQuestions

                // Mise à jour - envoie dans Firestore avec les nouvelles questions
                db.collection("quizzes").document(quizId)
                    .set(quizData, SetOptions.merge()) // Merge pour éviter d’écraser toutes les données
                    .addOnSuccessListener {
                        Log.d("Firestore", "Quiz $quizId mis à jour avec succès")
                        compteurUpload++
                        // Vérifier si tous les quiz ont été envoyés
                        if (compteurUpload == totalQuizzes) {
                            uploadEffectue = true
                            Log.d("Firestore", "Tous les quiz ont été mis à jour.")
                            onComplete() // Une fois terminé, appeler la fonction pour récupérer les questions
                        }
                    }
                    .addOnFailureListener { e ->
                        Log.e("Firestore", "Erreur lors de l'upload du quiz $quizId", e)
                    }
            }
        } catch (e: Exception) {
            Log.e("Firestore", "Erreur parsing JSON - upload Firestore: ${e.message}")
            onComplete()
        }
    }

    // Récupérer la liste de quiz depuis Firestore
    fun chargerQuestionsDepuisFirestore(onResult: (List<Map<String, Any>>) -> Unit) {
        db.collection("quizzes").get()
            .addOnSuccessListener { snapshot ->
                val listeQuiz = mutableListOf<Map<String, Any>>()
                // Récupérer les données de chaque document et les ajouter à la liste
                for (doc in snapshot.documents) {
                    doc.data?.let { listeQuiz.add(it) }
                }
                Log.d("Firestore", "${listeQuiz.size} quiz récupérés depuis Firestore.")
                onResult(listeQuiz)
            }
            .addOnFailureListener { e ->
                Log.e("Firestore", "Erreur lors du chargement des quiz", e)
                onResult(emptyList())
            }
    }
}