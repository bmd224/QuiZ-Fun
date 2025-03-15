package uqac.dim.myapp1.gestionProfil

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.OnBackPressedCallback
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import com.google.firebase.auth.FirebaseAuth
import uqac.dim.myapp1.theme.Footer
import uqac.dim.myapp1.R
import uqac.dim.myapp1.utils.UserProfileUtils
import uqac.dim.myapp1.activities.HomeActivity

class CreerProfilActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            CreerProfilActivityScreen()
        }

        //bouton retour
        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                val intent = Intent(this@CreerProfilActivity, HomeActivity::class.java)
                startActivity(intent)
                finish()
            }
        })
    }
}

@Composable
fun CreerProfilActivityScreen() {
    val contexte = LocalContext.current
    val sharedPreferences = contexte.getSharedPreferences("user_prefs", Context.MODE_PRIVATE)

    // Récupération de l'image de profil et du nom de l'utilisateur depuis sharedPreferences
    var imageSelectionnee by remember { mutableIntStateOf(sharedPreferences.getInt("user_image", R.drawable.ic_profile_1)) }
    var nom by remember { mutableStateOf(TextFieldValue(sharedPreferences.getString("user_name", "") ?: "")) }
    var afficherSelectionImage by remember { mutableStateOf(false) }

    // Affiche l'écran de sélection d'avatar
    if (afficherSelectionImage) {
        ImageActivityScreen { imageResId ->
            imageSelectionnee = imageResId
            afficherSelectionImage = false
        }
    } else {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            //bouton retour vers l'accueil
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Image(
                    painter = painterResource(id = R.drawable.ic_retour),
                    contentDescription = "Retour",
                    modifier = Modifier
                        .size(32.dp)
                        .clickable {
                            val intent = Intent(contexte, HomeActivity::class.java)
                            contexte.startActivity(intent)
                        }
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Image de profil sélectionnée aussi cliquable
            Image(
                painter = painterResource(id = imageSelectionnee),
                contentDescription = "Avatar sélectionné",
                modifier = Modifier
                    .size(100.dp)
                    .clickable { afficherSelectionImage = true }
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Champ pour le nom ou pseudo
            OutlinedTextField(
                value = nom,
                onValueChange = { nom = it },
                label = { Text("Nom ou pseudo") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Bouton pour enregistrer le profil et va s'enregistrer dans la bd Firestore
            Button(
                onClick = {
                    val auth = FirebaseAuth.getInstance()
                    val userId = auth.currentUser?.uid

                    if (userId != null) {
                        UserProfileUtils.sauvegarderProfilUtilisateur(
                            contexte, // Ajout du contexte pour SharedPreferences
                            userId,
                            nom.text,
                            imageSelectionnee // imageSelectionnee est un Int qui représente l'ID de l'image
                        )
                    }


                    // Redirection vers l'accueil après l'enregistrement
                    val intent = Intent(contexte, HomeActivity::class.java)
                    contexte.startActivity(intent)
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Enregistrer le profil")
            }

            // Espacement pour le footer
            Spacer(modifier = Modifier.weight(1f))

            // Footer
            Footer()
        }
    }
}