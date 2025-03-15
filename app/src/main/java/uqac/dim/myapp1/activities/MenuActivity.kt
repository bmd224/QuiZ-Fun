package uqac.dim.myapp1.activities

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.activity.ComponentActivity
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import uqac.dim.myapp1.MainActivity
import uqac.dim.myapp1.R
import uqac.dim.myapp1.authentification.ModifierMDPActivityScreen
import uqac.dim.myapp1.utils.UserProfileUtils
import androidx.core.net.toUri

class MenuActivity : ComponentActivity() {

    private lateinit var boutonDeconnexion: LinearLayout
    private lateinit var boutonModifierMDP: LinearLayout
    private lateinit var boutonRetour: ImageView
    private lateinit var imageProfil: ImageView
    private lateinit var texteNom: TextView
    private lateinit var boutonTechnologie: LinearLayout
    private lateinit var boutonScience: LinearLayout
    private lateinit var boutonHistoireGeographie: LinearLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_menu)

        boutonDeconnexion = findViewById(R.id.btnLogout)
        boutonModifierMDP = findViewById(R.id.btnModifierMDP)
        boutonRetour = findViewById(R.id.btnRetour)
        imageProfil = findViewById(R.id.userProfileImage)
        texteNom = findViewById(R.id.userName)
        boutonTechnologie = findViewById(R.id.btnTechnologie)
        boutonScience = findViewById(R.id.btnScience)
        boutonHistoireGeographie = findViewById(R.id.btnHistoireGeographie)

        val profilLocal = UserProfileUtils.recupererProfilUtilisateurLocal(this)
        texteNom.text = profilLocal.nomUtilisateur
        imageProfil.setImageResource(profilLocal.avatar)

        CoroutineScope(Dispatchers.IO).launch {
            val profilFirestore = UserProfileUtils.recupererProfilUtilisateurFirestore()
            if (profilFirestore != null) {
                runOnUiThread {
                    texteNom.text = profilFirestore.nomUtilisateur
                    imageProfil.setImageResource(profilFirestore.avatar)
                }
            }
        }
        //bouton retour
        boutonRetour.setOnClickListener {
            finish()
        }

        //bouton pour se déconnecter
        boutonDeconnexion.setOnClickListener {
            FirebaseAuth.getInstance().signOut()
            val intent = Intent(this, MainActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            finish()
        }

        //bouton pour modifier le mot de passe
        boutonModifierMDP.setOnClickListener {
            val intent = Intent(this, ModifierMDPActivityScreen::class.java)
            startActivity(intent)
        }

        // Liens d'apprentissage
        boutonTechnologie.setOnClickListener {
            val intent = Intent(Intent.ACTION_VIEW, "https://www.freecodecamp.org/".toUri())
            startActivity(intent)
        }

        boutonScience.setOnClickListener {
            val intent = Intent(Intent.ACTION_VIEW, "https://science.nasa.gov/".toUri())
            startActivity(intent)
        }

        boutonHistoireGeographie.setOnClickListener {
            val intent = Intent(Intent.ACTION_VIEW, "https://www.worldhistory.org/".toUri())
            startActivity(intent)
        }

    }
}
