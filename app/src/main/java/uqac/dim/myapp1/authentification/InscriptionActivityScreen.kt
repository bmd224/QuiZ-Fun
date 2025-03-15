package uqac.dim.myapp1.authentification

import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.google.firebase.auth.FirebaseAuth
import uqac.dim.myapp1.theme.Footer

@Composable
fun InscriptionActivityScreen(
    inscriptionReussie: () -> Unit,
    retourConnexion: () -> Unit
) {
    // Récupere le contexte actuel
    val contexte = LocalContext.current
    //Firebase auth
    val auth = remember { FirebaseAuth.getInstance() }

    var email by remember { mutableStateOf("") }
    var motDePasse by remember { mutableStateOf("") }
    var chargementEnCours by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(80.dp))

        // Bouton retour vers connexion
        Button(
            onClick = retourConnexion,
            modifier = Modifier.align(Alignment.End)
        ) {
            Text("Retour à la connexion")
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Titre
        Text(text = "Inscription", style = MaterialTheme.typography.headlineLarge)

        Spacer(modifier = Modifier.height(24.dp))

        // Champ Email
        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Email") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Champ Mot de Passe
        OutlinedTextField(
            value = motDePasse,
            onValueChange = { motDePasse = it },
            label = { Text("Mot de passe") },
            visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Bouton Créer un compte
        Button(
            onClick = {
                if (email.isNotEmpty() && motDePasse.isNotEmpty()) {
                    chargementEnCours = true
                    creerCompte(email, motDePasse, auth, inscriptionReussie, contexte)
                } else {
                    Toast.makeText(contexte, "Veuillez remplir tous les champs", Toast.LENGTH_SHORT).show()
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Créer un compte")
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

// Gere l'inscription avec Email/Password
private fun creerCompte(
    email: String,
    motDePasse: String,
    auth: FirebaseAuth,
    inscriptionReussie: () -> Unit,
    contexte: Context
) {
    auth.createUserWithEmailAndPassword(email, motDePasse)
        .addOnCompleteListener { tache ->
            if (tache.isSuccessful) {
                Toast.makeText(contexte, "Compte créé avec succès", Toast.LENGTH_SHORT).show()
                inscriptionReussie()
            } else {
                Toast.makeText(contexte, "Échec : ${tache.exception?.localizedMessage}", Toast.LENGTH_SHORT).show()
            }
        }
}

// Prévisualisation avec preview
@Preview(showBackground = true)
@Composable
fun PreviewInscriptionActivityScreen() {
    InscriptionActivityScreen(
        inscriptionReussie = {},
        retourConnexion = {}
    )
}