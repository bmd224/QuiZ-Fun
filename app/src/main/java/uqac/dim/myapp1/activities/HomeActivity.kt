package uqac.dim.myapp1.activities

import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.activity.ComponentActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import uqac.dim.myapp1.R
import uqac.dim.myapp1.gestionProfil.CreerProfilActivity
import uqac.dim.myapp1.historique.HistoriqueScoreActivity
import uqac.dim.myapp1.quiz.CategoriesActivity
import uqac.dim.myapp1.utils.UserProfileUtils

class HomeActivity : ComponentActivity() {

    private lateinit var boutonModifierProfil: LinearLayout
    private lateinit var boutonHistoriqueScores: LinearLayout
    private lateinit var boutonCategories: LinearLayout
    private lateinit var boutonMenu: LinearLayout
    private lateinit var imageProfil: ImageView
    private lateinit var texteNom: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        //on lie avec les éléments du XML
        boutonModifierProfil = findViewById(R.id.btnModifierProfil)
        boutonHistoriqueScores = findViewById(R.id.btnHistoriqueScores)
        boutonCategories = findViewById(R.id.btnCategories)
        boutonMenu = findViewById(R.id.btnMenu)
        imageProfil = findViewById(R.id.img)
        texteNom = findViewById(R.id.nom_principal)

        //Affiche le nom de l'utilisateur localement avec SharedPreferences
        val profilLocal = UserProfileUtils.recupererProfilUtilisateurLocal(this)
        texteNom.text = profilLocal.nomUtilisateur
        imageProfil.setImageResource(profilLocal.avatar)

        // Mise à jour en arrière-plan avec Firestore pour eviter les delais
        CoroutineScope(Dispatchers.IO).launch {
            val profilFirestore = UserProfileUtils.recupererProfilUtilisateurFirestore()
            if (profilFirestore != null) {
                runOnUiThread {
                    texteNom.text = profilFirestore.nomUtilisateur
                    imageProfil.setImageResource(profilFirestore.avatar)
                }
            }
        }

        // Navigue vers modification de profil
        boutonModifierProfil.setOnClickListener {
            val intent = Intent(this, CreerProfilActivity::class.java)
            startActivity(intent)
        }

        // Navigue vers l'historique des scores
        boutonHistoriqueScores.setOnClickListener {
            val intent = Intent(this, HistoriqueScoreActivity::class.java)
            startActivity(intent)
        }

        // Navigue vers les catégories
        boutonCategories.setOnClickListener {
            val intent = Intent(this, CategoriesActivity::class.java)
            startActivity(intent)
        }

        // Navigue vers le menu
        boutonMenu.setOnClickListener {
            val intent = Intent(this, MenuActivity::class.java)
            startActivity(intent)
        }

        // l'utilisateur peut modifier son profil a partir de l'image
        imageProfil.setOnClickListener {
            val intent = Intent(this, CreerProfilActivity::class.java)
            startActivity(intent)
        }
    }
}
