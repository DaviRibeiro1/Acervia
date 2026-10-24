package com.example.acervia.ui.components

import android.content.res.Configuration
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Schedule
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.acervia.ui.theme.AcerviaTheme
import com.example.acervia.ui.theme.AppTheme
import com.example.acervia.ui.theme.Space

@Composable
fun SeloPrazo(
    texto: String,
    urgente: Boolean,
    modifier: Modifier = Modifier,
    expirada: Boolean = false,
) {
    val backgroundColor: Color
    val contentColor: Color

    when {
        expirada -> {
            backgroundColor = MaterialTheme.colorScheme.errorContainer
            contentColor = MaterialTheme.colorScheme.error
        }
        urgente -> {
            backgroundColor = AppTheme.extra.accentContainer
            contentColor = AppTheme.extra.onAccentContainer
        }
        else -> {
            backgroundColor = MaterialTheme.colorScheme.primaryContainer
            contentColor = MaterialTheme.colorScheme.onPrimaryContainer
        }
    }

    Row(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .background(backgroundColor)
            .heightIn(min = 32.dp)
            .padding(horizontal = Space.s3, vertical = Space.s1),
        horizontalArrangement = Arrangement.spacedBy(Space.s1),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(
            imageVector = Icons.Outlined.Schedule,
            contentDescription = null,
            tint = contentColor,
            modifier = Modifier.size(16.dp),
        )
        Text(
            text = texto,
            style = MaterialTheme.typography.labelLarge,
            color = contentColor,
        )
    }
}

@Preview(name = "Selo Prazo - Claro", showBackground = true)
@Composable
private fun SeloPrazoLightPreview() {
    AcerviaTheme(darkTheme = false) {
        Row(
            modifier = Modifier.padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            SeloPrazo(texto = "Retire até hoje · 18h", urgente = true)
            SeloPrazo(texto = "Retire até qui, 15/10 · 18h", urgente = false)
            SeloPrazo(texto = "Reserva expirada", urgente = false, expirada = true)
        }
    }
}

@Preview(name = "Selo Prazo - Escuro", uiMode = Configuration.UI_MODE_NIGHT_YES, showBackground = true)
@Composable
private fun SeloPrazoDarkPreview() {
    AcerviaTheme(darkTheme = true) {
        Row(
            modifier = Modifier.padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            SeloPrazo(texto = "Retire até hoje · 18h", urgente = true)
            SeloPrazo(texto = "Retire até qui, 15/10 · 18h", urgente = false)
            SeloPrazo(texto = "Reserva expirada", urgente = false, expirada = true)
        }
    }
}
