package uqac.dim.myapp1.gestionProfil

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import uqac.dim.myapp1.theme.Footer
import uqac.dim.myapp1.R

@Composable
fun ImageActivityScreen(onImageSelected: (Int) -> Unit) {
    val images = listOf(
        R.drawable.ic_profile_1,
        R.drawable.ic_profile_2,
        R.drawable.ic_profile_3,
        R.drawable.ic_profile_4
    )

    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        images.forEach { imageRes ->
            Card(
                shape = CircleShape,
                elevation = CardDefaults.cardElevation(4.dp),
                modifier = Modifier
                    .padding(8.dp)
                    .size(100.dp)
                    .clickable {
                        onImageSelected(imageRes) // Renvoie l'image sélectionnée
                    }
            ) {
                Image(
                    painter = painterResource(id = imageRes),
                    contentDescription = "Avatar",
                    modifier = Modifier.fillMaxSize()
                )
            }
        }

        // Espacement pour le footer
        Spacer(modifier = Modifier.weight(1f))

        // Footer
        Footer()
    }
}