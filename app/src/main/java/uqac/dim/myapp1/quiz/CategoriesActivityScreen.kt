package uqac.dim.myapp1.quiz

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.OnBackPressedCallback
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.tooling.preview.Preview
import uqac.dim.myapp1.theme.Footer
import uqac.dim.myapp1.R
import uqac.dim.myapp1.activities.HomeActivity

class CategoriesActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            CategoriesActivityScreen()
        }

        // Gestion du bouton retour pour revenir à HomeActivity
        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                val intent = Intent(this@CategoriesActivity, HomeActivity::class.java)
                startActivity(intent)
                finish()
            }
        })
    }
}

@Composable
fun CategoriesActivityScreen() {
    val contexte = LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF6F6F6))
            .padding(16.dp)
    ) {
        //bouton retour sous forme d'image en haut à gauche
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

        Spacer(modifier = Modifier.height(8.dp))

        // Affichage d'une image d'illustration
        Image(
            painter = painterResource(id = R.drawable.quiz_illustration),
            contentDescription = "Illustration Quiz",
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
        )

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = { /* Rien a faire */ },
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1E6FD9))
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = "Mes Quizz",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                Text(
                    text = "Cultivez votre mémoire en apprenant plus",
                    fontSize = 12.sp,
                    color = Color.White
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Liste des catégories
        Text(
            text = "TOUTES LES CATÉGORIES",
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Black,
            modifier = Modifier.padding(start = 8.dp)
        )

        Spacer(modifier = Modifier.height(8.dp))

        //Categorie disponible culture G
        ItemCategorie(
            title = "Culture Générale",
            subtitle = "Tout savoir sur la culture G",
            onClick = {
                val intent = Intent(contexte, QuizQuestionActivity::class.java)
                contexte.startActivity(intent)
            }
        )

        //Non disponible
        ItemCategorie(title = "Histoire", subtitle = "Apprenez l’histoire mondiale", enabled = false)
        ItemCategorie(title = "Sciences", subtitle = "Physique, Chimie, Biologie et plus", enabled = false)
        ItemCategorie(title = "Technologie", subtitle = "Découvrez les avancées technologiques", enabled = false)

        // Espacement pour le footer
        Spacer(modifier = Modifier.weight(1f))

        // Footer
        Footer()
    }
}

@Composable
fun ItemCategorie(title: String, subtitle: String, enabled: Boolean = true, onClick: (() -> Unit)? = null) {
    //affichage catégorie de quiz
    Card(
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(containerColor = if (enabled) Color.White else Color.Gray.copy(alpha = 0.3f)),
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
            .height(60.dp)
            .clickable(enabled = enabled) { onClick?.invoke() },
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            verticalArrangement = Arrangement.Center
        ) {
            Text(text = title, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color.Black)
            Text(text = subtitle, fontSize = 12.sp, color = Color.Gray)
        }
    }
}

// Prévisualisation de l'écran
@Preview(showBackground = true)
@Composable
fun PreviewCategoriesActivityScreen() {
    CategoriesActivityScreen()
}