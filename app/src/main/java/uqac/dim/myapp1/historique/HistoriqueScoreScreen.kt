package uqac.dim.myapp1.historique

import android.content.Context
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import uqac.dim.myapp1.utils.HistoriqueUtils
import uqac.dim.myapp1.utils.UserProfileUtils
import uqac.dim.myapp1.quiz.formatTime

//modele de donnees pour firestore
data class Score(val nomUtilisateur: String, val score: Int, val totalQuestions: Int, val tempsTotal: Int)

@Composable
fun HistoriqueScoreScreen() {
    val auth = FirebaseAuth.getInstance()
    val utilisateurActuelId = auth.currentUser?.uid ?: "utilisateur_inconnu"

    val contexte = androidx.compose.ui.platform.LocalContext.current
    val sharedPreferences = contexte.getSharedPreferences("user_prefs", Context.MODE_PRIVATE)

    // Récupere le nom de l'utilisateur depuis SharedPreferences
    var nomUtilisateur by remember { mutableStateOf(sharedPreferences.getString("user_name", "Utilisateur") ?: "Utilisateur") }

    var resultats by remember { mutableStateOf<List<Score>>(emptyList()) }
    var chargementEnCours by remember { mutableStateOf(true) }

    LaunchedEffect(utilisateurActuelId) {
        chargementEnCours = true

        // Récupération du profil utilisateur depuis Firestore
        CoroutineScope(Dispatchers.IO).launch {
            val profilUtilisateur = UserProfileUtils.recupererProfilUtilisateurFirestore()
            if (profilUtilisateur != null) {
                // Mise à jour du nom de l'utilisateur
                nomUtilisateur = profilUtilisateur.nomUtilisateur

                // Sauvegarde du nom dans SharedPreferences pour un chargement plus rapide la prochaine fois
                sharedPreferences.edit().apply {
                    putString("user_name", profilUtilisateur.nomUtilisateur)
                    apply()
                }
            }
        }

        // Récupération des scores de l'utilisateur depuis le Firestore
        resultats = HistoriqueUtils.recupererScoresUtilisateur()
        chargementEnCours = false
    }

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        //titre avec le nom de l'utilisateur
        Text("Historique de $nomUtilisateur", style = MaterialTheme.typography.headlineSmall)

        if (chargementEnCours) {
            CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally))
        }
        //message si aucun pas de score enregistré
        else if (resultats.isEmpty()) {
            Text("Aucun score n'est enregistré pour le moment", modifier = Modifier.align(Alignment.CenterHorizontally), color = Color.Gray)
        }
        //liste des scores sera affichée
        else {
            LazyColumn {
                items(resultats) { score ->
                    ScoreItem(score)
                }
            }
        }
    }
}

@Composable
fun ScoreItem(score: Score) {
    //affiche le score de l'utilisateur avec son nom et le temps total
    Card(
        modifier = Modifier.fillMaxWidth().padding(8.dp),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("${score.nomUtilisateur} : ${score.score}/${score.totalQuestions}", style = MaterialTheme.typography.bodyMedium)
            Text("Temps total: ${formatTime(score.tempsTotal)}", style = MaterialTheme.typography.bodySmall, color = Color.Gray)
        }
    }
}
