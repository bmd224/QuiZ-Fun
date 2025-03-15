package uqac.dim.myapp1.quiz

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import uqac.dim.myapp1.historique.HistoriqueScoreActivity
import uqac.dim.myapp1.utils.HistoriqueUtils
import uqac.dim.myapp1.utils.UserProfileUtils

class QuizResultScreen : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Récupération des valeurs passées en paramètre depuis l'activité précédente(QuizQuestionActivity)
        val reponsesCorrectes = intent.getIntExtra("correctAnswers", 0)
        val totalQuestions = intent.getIntExtra("totalQuestions", 17)//sur le nbre total de questions
        val tempsTotal = intent.getIntExtra("totalTime", 0)

        // Affichage de l'interface
        setContent {
            QuizResultScreen(
                reponsesCorrectes = reponsesCorrectes,
                totalQuestions = totalQuestions,
                tempsTotal = tempsTotal
            ) {
                finish() // Ferme l'écran lorsque l'utilisateur clique sur Terminer
            }
        }
    }
}

@Composable
fun QuizResultScreen(reponsesCorrectes: Int, totalQuestions: Int, tempsTotal: Int, onFinish: () -> Unit) {
    val contexte = LocalContext.current
    val auth = FirebaseAuth.getInstance()
    val utilisateurId = auth.currentUser?.uid ?: "utilisateur_inconnu"

    // Utilisation de SharedPreferences pour récupérer le nom de l'utilisateur localement
    val sharedPreferences = contexte.getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
    var nomUtilisateur by remember { mutableStateOf(sharedPreferences.getString("user_name", "Utilisateur") ?: "Utilisateur") }

    // Mise à jour du nom de l'utilisateur avec Firestore
    LaunchedEffect(Unit) {
        CoroutineScope(Dispatchers.IO).launch {
            val profilUtilisateur = UserProfileUtils.recupererProfilUtilisateurFirestore()
            if (profilUtilisateur != null) {
                nomUtilisateur = profilUtilisateur.nomUtilisateur

                //nom utilisateur stock dans SharedPreferences pour éviter les recharges fréquentes
                sharedPreferences.edit().apply {
                    putString("user_name", profilUtilisateur.nomUtilisateur)
                    apply()
                }
            }
        }
    }

    // Calcul du pourcentage de bonnes réponses en %
    val pourcentageScore = (reponsesCorrectes.toFloat() / totalQuestions.toFloat()) * 100

    // Sauvegarde du score dans Firestore
    LaunchedEffect(Unit) {
        HistoriqueUtils.sauvegarderScoreVersFirestore(
            utilisateurId,
            nomUtilisateur,
            reponsesCorrectes,
            totalQuestions,
            tempsTotal
        )
    }

    Column(
        modifier = Modifier.fillMaxSize().background(Color(0xFFFFF6FA)),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(100.dp))

        Text(text = nomUtilisateur, fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color.Black)
        Spacer(modifier = Modifier.height(8.dp))

        //graphe affichant le pourcentage de réussite
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .size(120.dp)
                .clip(CircleShape)
                .background(Color.White)
        ) {
            CircularProgressIndicator(
                progress = { pourcentageScore / 100f },
                modifier = Modifier.size(100.dp),
                color = Color(0xFFFF9800),
                strokeWidth = 8.dp,
                trackColor = Color(0xFFE0E0E0),
            )
            Text(
                text = "${pourcentageScore.toInt()}%",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Affichage du score sous
        Text(
            text = "$reponsesCorrectes sur $totalQuestions bonnes réponses",
            fontSize = 16.sp,
            fontWeight = FontWeight.Medium,
            color = Color.Black
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Affichage du temps total
        Text(
            text = "Temps total: ${formatTime(tempsTotal)}",
            fontSize = 16.sp,
            fontWeight = FontWeight.Medium,
            color = Color.Gray
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Bouton pour afficher l'historique des scores
        Button(
            onClick = {
                val intent = Intent(contexte, HistoriqueScoreActivity::class.java)
                contexte.startActivity(intent)
            },
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF9800)),
            modifier = Modifier.padding(horizontal = 32.dp)
        ) {
            Text("Votre Historique", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color.White, textAlign = TextAlign.Center)
        }

        Spacer(modifier = Modifier.height(16.dp))

        //on ferme l'activité et retourne à l'activité categories
        Button(
            onClick = { onFinish() },
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1976D2)),
            modifier = Modifier.padding(horizontal = 32.dp)
        ) {
            Text("Terminer", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color.White, textAlign = TextAlign.Center)
        }
    }
}

// Fonction pour formater le temps en minutes et secondes
fun formatTime(tempsTotal: Int): String {
    val minutes = tempsTotal / 60
    val secondes = tempsTotal % 60
    return if (minutes > 0) "$minutes min $secondes sec" else "$secondes sec"
}
