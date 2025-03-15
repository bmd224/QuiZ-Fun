package uqac.dim.myapp1

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import com.google.firebase.auth.FirebaseAuth
import uqac.dim.myapp1.activities.HomeActivity
import uqac.dim.myapp1.authentification.ConnexionActivityScreen
import uqac.dim.myapp1.authentification.InscriptionActivityScreen

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            val contexte = LocalContext.current
            val auth = remember { FirebaseAuth.getInstance() }// Initialise FirebaseAuth
            var afficherEcranInscription by remember { mutableStateOf(false) }// Gère l'affichage de l'écran d'inscription

            // Vérifie si un utilisateur est déjà connecté
            var utilisateurConnecte by remember { mutableStateOf(auth.currentUser != null) }

            // Si l'utilisateur est connecté, rediriger vers HomeActivity
            LaunchedEffect(utilisateurConnecte) {
                if (utilisateurConnecte) {
                    val intent = Intent(contexte, HomeActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    contexte.startActivity(intent)
                }
            }

            // Affiche l'écran d'inscription
            if (afficherEcranInscription) {
                InscriptionActivityScreen(
                    inscriptionReussie = {
                        afficherEcranInscription = false // Retourne à l'écran de connexion après une inscription
                        utilisateurConnecte = true // Met à jour l'état de connexion
                    },
                    retourConnexion = { afficherEcranInscription = false }// Revenir à la connexion a travers le bouton
                )
            } else {
                ConnexionActivityScreen(
                    connexionReussie = { utilisateurConnecte = true }, // Met à jour l'état une fois connectée
                    inscriptionClick = { afficherEcranInscription = true } //on Passe à l'écran d'inscription
                )
            }
        }
    }
}