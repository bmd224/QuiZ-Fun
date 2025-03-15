package uqac.dim.myapp1.historique

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent

class HistoriqueScoreActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            HistoriqueScoreScreen()
        }
    }
}
