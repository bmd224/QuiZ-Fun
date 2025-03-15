package uqac.dim.myapp1.authentification

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.net.toUri
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import uqac.dim.myapp1.activities.HomeActivity
import uqac.dim.myapp1.theme.Footer

class ModifierMDPActivityScreen : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ModifierMDPScreen()
        }
    }
}

@Composable
fun ModifierMDPScreen() {
    val contexte = LocalContext.current
    var email by remember { mutableStateOf(TextFieldValue("")) } //email
    var ancienMotDePasse by remember { mutableStateOf(TextFieldValue("")) } //ancien mot de passe
    var nouveauMotDePasse by remember { mutableStateOf(TextFieldValue("")) } //nouveau mot de passe
    val auth = FirebaseAuth.getInstance()
    val utilisateurActuel = auth.currentUser

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(100.dp))

        //Titre
        Text(
            text = "Modifier votre mot de passe",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Black
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Champ Email
        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Email") },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(8.dp)
        )

        Spacer(modifier = Modifier.height(12.dp))

        // Champ Ancien Mot de Passe
        OutlinedTextField(
            value = ancienMotDePasse,
            onValueChange = { ancienMotDePasse = it },
            label = { Text("Ancien Mot de Passe") },
            visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(8.dp)
        )

        Spacer(modifier = Modifier.height(12.dp))

        // Champ Nouveau Mot de Passe
        OutlinedTextField(
            value = nouveauMotDePasse,
            onValueChange = { nouveauMotDePasse = it },
            label = { Text("Nouveau Mot de Passe") },
            visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(8.dp)
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Bouton Mettre à jour
        Button(
            onClick = {
                modifierMotDePasse(contexte, auth, utilisateurActuel, email.text, ancienMotDePasse.text, nouveauMotDePasse.text)
            },
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4AB5A1)),
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp),
            shape = RoundedCornerShape(8.dp)
        ) {
            Text(
                text = "Mettre à jour",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
        }

        // Espacement pour le footer
        Spacer(modifier = Modifier.weight(1f))

        // Footer
        Footer()
    }
}

// Fonction pour modifier le mot de passe dans Firebase
private fun modifierMotDePasse(
    contexte: Context,
    auth: FirebaseAuth,
    utilisateurActuel: com.google.firebase.auth.FirebaseUser?,
    emailSaisi: String,
    ancienMotDePasse: String,
    nouveauMotDePasse: String
) {
    if (emailSaisi.isBlank() || ancienMotDePasse.isBlank() || nouveauMotDePasse.isBlank()) {
        Toast.makeText(contexte, "Veuillez remplir tous les champs", Toast.LENGTH_SHORT).show()
        return
    }

    if (utilisateurActuel == null) {
        Toast.makeText(contexte, "Vous n'êtes pas connecté", Toast.LENGTH_SHORT).show()
        return
    }

    val emailUtilisateur = utilisateurActuel.email ?: ""

    // Vérification que l'email saisi correspond bien à l'utilisateur connecté
    if (emailSaisi != emailUtilisateur) {
        Toast.makeText(contexte, "Email incorrect. Utilisez l'email du compte actuel.", Toast.LENGTH_LONG).show()
        return
    }

    // Vérifier si l'utilisateur est connecté avec Google
    //Si l'utilisateur est connecté avec Google, il est renvoyé vers les services Google pour pouvoir modifier son mot de passe
    val estUtilisateurGoogle = utilisateurActuel.providerData.any { it.providerId == "google.com" }
    if (estUtilisateurGoogle) {
        Toast.makeText(contexte, "Vous utilisez un compte Google. Modifiez votre mot de passe via Google.", Toast.LENGTH_LONG).show()
        val intent = Intent(Intent.ACTION_VIEW,
            "https://myaccount.google.com/security-checkup".toUri())
        contexte.startActivity(intent)
        return
    }

    // Réauthentification avec modification du mot de passe
    val credential = EmailAuthProvider.getCredential(emailSaisi, ancienMotDePasse)

    utilisateurActuel.reauthenticate(credential).addOnCompleteListener { tacheReauth ->
        if (tacheReauth.isSuccessful) {
            // Modification du mot de passe
            utilisateurActuel.updatePassword(nouveauMotDePasse).addOnCompleteListener { tacheMiseAJour ->
                if (tacheMiseAJour.isSuccessful) {
                    Toast.makeText(contexte, "Votre mot de passe a été mis à jour avec succès", Toast.LENGTH_LONG).show()
                    val intent = Intent(contexte, HomeActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    contexte.startActivity(intent)
                } else {
                    Toast.makeText(contexte, "Échec de la mise à jour du mot de passe", Toast.LENGTH_LONG).show()
                }
            }
        } else {
            Toast.makeText(contexte, "Votre mot de passe est incorrect, Réessayez", Toast.LENGTH_LONG).show()
        }
    }
}

// Prévisualisation de l'écran
@Preview(showBackground = true)
@Composable
fun PreviewModifierMDPScreen() {
    ModifierMDPScreen()
}