package com.example.acervia.ui.components

import android.content.res.Configuration
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.MenuBook
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.acervia.ui.theme.AcerviaTheme
import com.example.acervia.ui.theme.Space

@Composable
fun CapaSubstituta(
    titulo: String,
    autor: String,
    modifier: Modifier = Modifier,
    isDark: Boolean = isSystemInDarkTheme(),
) {
    val backgroundColor: Color
    val contentColor: Color

    if (isDark) {
        backgroundColor = MaterialTheme.colorScheme.primaryContainer
        contentColor = MaterialTheme.colorScheme.onPrimaryContainer
    } else {
        backgroundColor = MaterialTheme.colorScheme.primary
        contentColor = MaterialTheme.colorScheme.onPrimary
    }

    Column(
        modifier = modifier
            .aspectRatio(2f / 3f)
            .clip(MaterialTheme.shapes.small)
            .background(backgroundColor)
            .border(
                width = 1.dp,
                color = MaterialTheme.colorScheme.outlineVariant,
                shape = MaterialTheme.shapes.small,
            )
            .padding(Space.s2),
        verticalArrangement = Arrangement.SpaceBetween,
        horizontalAlignment = Alignment.Start,
    ) {
        Column(
            modifier = Modifier.weight(1f, fill = false),
            verticalArrangement = Arrangement.spacedBy(Space.s1),
        ) {
            Text(
                text = titulo,
                style = MaterialTheme.typography.labelLarge,
                color = contentColor,
                maxLines = 4,
                overflow = TextOverflow.Ellipsis,
            )
            Text(
                text = autor,
                style = MaterialTheme.typography.bodySmall,
                color = contentColor.copy(alpha = 0.8f),
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
            )
        }

        Spacer(modifier = Modifier.fillMaxHeight(0.1f))

        Icon(
            imageVector = Icons.AutoMirrored.Outlined.MenuBook,
            contentDescription = null,
            tint = contentColor,
            modifier = Modifier.size(24.dp),
        )
    }
}

@Preview(name = "Capa Substituta - Claro", showBackground = true)
@Composable
private fun CapaSubstitutaLightPreview() {
    AcerviaTheme(darkTheme = false) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            CapaSubstituta(
                titulo = "A Biblioteca da Meia-Noite",
                autor = "Matt Haig",
                modifier = Modifier.width(132.dp),
            )
        }
    }
}

@Preview(name = "Capa Substituta - Escuro", uiMode = Configuration.UI_MODE_NIGHT_YES, showBackground = true)
@Composable
private fun CapaSubstitutaDarkPreview() {
    AcerviaTheme(darkTheme = true) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            CapaSubstituta(
                titulo = "A Biblioteca da Meia-Noite",
                autor = "Matt Haig",
                modifier = Modifier.width(132.dp),
            )
        }
    }
}
