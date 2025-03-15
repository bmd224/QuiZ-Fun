package uqac.dim.myapp1.theme

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview

// Footer composable qui sert juste a afficher le texte
//je le reutilise dans les autres activites
@Composable
fun Footer() {
    Column(
        modifier = Modifier.fillMaxWidth().padding(8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "© 2025 - QuiZ & Fun",
            fontSize = 14.sp,
            color = Color.Gray
        )
        Text(
            text = "Tous droits réservés",
            fontSize = 12.sp,
            color = Color.Gray
        )
    }
}

@Preview
@Composable
fun PreviewFooter() {
    Footer()
}
