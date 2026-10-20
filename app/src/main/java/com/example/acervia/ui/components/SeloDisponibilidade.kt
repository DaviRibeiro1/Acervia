package com.example.acervia.ui.components

import android.content.res.Configuration
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.RemoveCircleOutline
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.acervia.ui.theme.AcerviaTheme
import com.example.acervia.ui.theme.AppTheme
import com.example.acervia.ui.theme.Space

@Composable
fun SeloDisponibilidade(
    disponivel: Boolean,
    modifier: Modifier = Modifier,
    textoPersonalizado: String? = null,
) {
    val color: Color = if (disponivel) {
        AppTheme.extra.success
    } else {
        MaterialTheme.colorScheme.onSurfaceVariant
    }

    val icon = if (disponivel) {
        Icons.Outlined.CheckCircle
    } else {
        Icons.Outlined.RemoveCircleOutline
    }

    val texto = textoPersonalizado ?: if (disponivel) "Disponível" else "Indisponível"

    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(Space.s1),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = color,
            modifier = Modifier.size(16.dp),
        )
        Text(
            text = texto,
            style = MaterialTheme.typography.bodySmall,
            color = color,
        )
    }
}

@Preview(name = "Selo Disponibilidade - Claro", showBackground = true)
@Composable
private fun SeloDisponibilidadeLightPreview() {
    AcerviaTheme(darkTheme = false) {
        Row(
            modifier = Modifier.padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            SeloDisponibilidade(disponivel = true, textoPersonalizado = "3 disponíveis")
            SeloDisponibilidade(disponivel = false)
        }
    }
}

@Preview(name = "Selo Disponibilidade - Escuro", uiMode = Configuration.UI_MODE_NIGHT_YES, showBackground = true)
@Composable
private fun SeloDisponibilidadeDarkPreview() {
    AcerviaTheme(darkTheme = true) {
        Row(
            modifier = Modifier.padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            SeloDisponibilidade(disponivel = true, textoPersonalizado = "3 disponíveis")
            SeloDisponibilidade(disponivel = false)
        }
    }
}
