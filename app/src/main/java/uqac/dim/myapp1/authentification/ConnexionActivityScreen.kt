package uqac.dim.myapp1.authentification

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.google.android.gms.auth.api.signin.*
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import uqac.dim.myapp1.theme.Footer
import uqac.dim.myapp1.R
import uqac.dim.myapp1.activities.HomeActivity

@Composable
fun ConnexionActivityScreen(
    connexionReussie: () -> Unit,
    inscriptionClick: () -> Unit
) {
    val contexte = LocalContext.current
    val auth = remember { Firebase.auth }

    var email by remember { mutableStateOf("") }
    var motDePasse by remember { mutableStateOf("") }
    var chargementEnCours by remember { mutableStateOf(false) }

    // Configuration de Google Sign-In
    val optionsGoogle = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
        .requestIdToken(contexte.getString(R.string.default_web_client_id))
        .requestEmail()
        .build()

    val clientGoogle = remember { GoogleSignIn.getClient(contexte, optionsGoogle) }

    val lanceurGoogle = rememberLauncherForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val tache = GoogleSignIn.getSignedInAccountFromIntent(result.data)
            try {
                val compte = tache.getResult(ApiException::class.java)
                if (compte != null) {
                    authentificationAvecGoogle(compte.idToken!!, auth, connexionReussie, contexte)
                }
            } catch (e: ApiException) {
                Toast.makeText(contexte, "Échec Google Sign-In: ${e.localizedMessage}", Toast.LENGTH_SHORT).show()
                Log.w("LoginActivity", "Google sign in failed", e)
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(80.dp))

        Text(text = "Connexion", style = MaterialTheme.typography.headlineLarge)

        Spacer(modifier = Modifier.height(32.dp))

        // Champ de saisie pour l'email
        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Nom d'utilisateur ou Email") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Champ de saisie pour le mot de passe
        OutlinedTextField(
            value = motDePasse,
            onValueChange = { motDePasse = it },
            label = { Text("Mot de passe") },
            visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Bouton de connexion
        Button(
            onClick = {
                if (email.isNotEmpty() && motDePasse.isNotEmpty()) {
                    chargementEnCours = true
                    connexionAvecEmailEtMotDePasse(email, motDePasse, auth, contexte)
                } else {
                    Toast.makeText(contexte, "Veuillez remplir tous les champs", Toast.LENGTH_SHORT).show()
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Se Connecter")
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Bouton de connexion avec Google
        Button(
            onClick = {
                clientGoogle.signOut().addOnCompleteListener {
                    lanceurGoogle.launch(clientGoogle.signInIntent)
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Se connecter avec Google")
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Bouton pour s'inscrire
        TextButton(onClick = inscriptionClick) {
            Text("Vous n'avez pas de compte ? Inscrivez vous")
        }

        if (chargementEnCours) {
            CircularProgressIndicator(modifier = Modifier.padding(top = 16.dp))
        }

        // Espacement pour le footer
        Spacer(modifier = Modifier.weight(1f))

        // Footer
        Footer()
    }
}

// Fonction pour la connexion avec email et mot de passe
fun connexionAvecEmailEtMotDePasse(
    email: String,
    motDePasse: String,
    auth: FirebaseAuth,
    contexte: Context
) {
    auth.signInWithEmailAndPassword(email, motDePasse)
        .addOnCompleteListener { tache ->
            if (tache.isSuccessful) {
                Toast.makeText(contexte, "Connexion réussie !", Toast.LENGTH_SHORT).show()
                val intent = Intent(contexte, HomeActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                contexte.startActivity(intent)
            } else {
                Toast.makeText(contexte, "Échec de la connexion: ${tache.exception?.localizedMessage}", Toast.LENGTH_SHORT).show()
            }
        }
}

// Fonction pour la connexion avec Google
fun authentificationAvecGoogle(
    idToken: String,
    auth: FirebaseAuth,
    connexionReussie: () -> Unit,
    contexte: Context
) {
    val credential = GoogleAuthProvider.getCredential(idToken, null)
    auth.signInWithCredential(credential)
        .addOnCompleteListener { tache ->
            if (tache.isSuccessful) {
                Toast.makeText(contexte, "Connexion réussie avec Google", Toast.LENGTH_SHORT).show()
                val intent = Intent(contexte, HomeActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                contexte.startActivity(intent)
                connexionReussie()
            } else {
                Toast.makeText(contexte, "Échec de connexion avec Google: ${tache.exception?.localizedMessage}", Toast.LENGTH_SHORT).show()
            }
        }
}

// Prévisualisation de l'écran de connexion
@Preview(showBackground = true)
@Composable
fun PreviewConnexionActivityScreen() {
    ConnexionActivityScreen(
        connexionReussie = {},
        inscriptionClick = {}
    )
}