package com.example.acervia.ui.components

import android.content.res.Configuration
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ContentCopy
import androidx.compose.material.icons.outlined.LocationOn
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.ExperimentalTextApi
import androidx.compose.ui.text.VerbatimTtsAnnotation
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withAnnotation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.SubcomposeAsyncImage
import coil.request.ImageRequest
import com.example.acervia.ui.theme.AcerviaTheme
import com.example.acervia.ui.theme.AppTextStyles
import com.example.acervia.ui.theme.Space

@OptIn(ExperimentalTextApi::class)
@Composable
fun CartaoReserva(
    prazoTexto: String,
    prazoUrgente: Boolean,
    tituloLivro: String,
    autorLivro: String,
    codigoRetirada: String,
    localBiblioteca: String,
    statusBiblioteca: String,
    modifier: Modifier = Modifier,
    capaUrl: String? = null,
    expirada: Boolean = false,
    copiarHabilitado: Boolean = true,
    onCartaoClick: () -> Unit = {},
    onCopiarCodigoClick: (String) -> Unit = {},
) {
    val codigoFormatado = if (codigoRetirada.length == 6) {
        "${codigoRetirada.substring(0, 3)} ${codigoRetirada.substring(3)}"
    } else {
        codigoRetirada
    }

    Surface(
        modifier = modifier
            .fillMaxWidth()
            .clip(MaterialTheme.shapes.medium)
            .border(
                width = 1.dp,
                color = MaterialTheme.colorScheme.outlineVariant,
                shape = MaterialTheme.shapes.medium,
            )
            .clickable(onClick = onCartaoClick),
        color = MaterialTheme.colorScheme.surface,
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(Space.s4),
            verticalArrangement = Arrangement.spacedBy(Space.s3),
        ) {
            // 1. Selo de Prazo
            SeloPrazo(
                texto = prazoTexto,
                urgente = prazoUrgente,
                expirada = expirada,
            )

            // 2. Capa + Título + Autor
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(Space.s3),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                if (!capaUrl.isNullOrBlank()) {
                    SubcomposeAsyncImage(
                        model = ImageRequest.Builder(LocalContext.current)
                            .data(capaUrl)
                            .crossfade(enable = true)
                            .build(),
                        contentDescription = null,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .width(64.dp)
                            .height(96.dp)
                            .clip(MaterialTheme.shapes.small)
                            .border(
                                width = 1.dp,
                                color = MaterialTheme.colorScheme.outlineVariant,
                                shape = MaterialTheme.shapes.small,
                            ),
                        error = {
                            CapaSubstituta(
                                titulo = tituloLivro,
                                autor = autorLivro,
                                modifier = Modifier
                                    .width(64.dp)
                                    .height(96.dp),
                            )
                        },
                        loading = {
                            CapaSubstituta(
                                titulo = tituloLivro,
                                autor = autorLivro,
                                modifier = Modifier
                                    .width(64.dp)
                                    .height(96.dp),
                            )
                        },
                    )
                } else {
                    CapaSubstituta(
                        titulo = tituloLivro,
                        autor = autorLivro,
                        modifier = Modifier
                            .width(64.dp)
                            .height(96.dp),
                    )
                }

                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(Space.s1),
                ) {
                    Text(
                        text = tituloLivro,
                        style = AppTextStyles.button,
                        color = MaterialTheme.colorScheme.onSurface,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis,
                    )
                    Text(
                        text = autorLivro,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                }
            }

            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)

            // 3. Rótulo Código + Código de Retirada + Botão Copiar
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Column {
                    Text(
                        text = "Código de retirada",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                    val codigoTts = buildAnnotatedString {
                        withAnnotation(VerbatimTtsAnnotation(verbatim = codigoFormatado.filter { it.isDigit() })) {
                            append(codigoFormatado)
                        }
                    }
                    Text(
                        text = codigoTts,
                        style = AppTextStyles.otp,
                        color = if (copiarHabilitado) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }

                OutlinedButton(
                    onClick = { onCopiarCodigoClick(codigoRetirada) },
                    enabled = copiarHabilitado,
                    modifier = Modifier.heightIn(min = 48.dp),
                    shape = MaterialTheme.shapes.medium,
                ) {
                    Icon(
                        imageVector = Icons.Outlined.ContentCopy,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp),
                    )
                    Spacer(modifier = Modifier.width(Space.s2))
                    Text(
                        text = "Copiar",
                        style = AppTextStyles.button,
                    )
                }
            }

            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)

            // 4. Local e Situação
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(Space.s2),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Icon(
                    imageVector = Icons.Outlined.LocationOn,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.size(20.dp),
                )
                Text(
                    text = "$localBiblioteca · $statusBiblioteca",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
            }
        }
    }
}

@Preview(name = "Cartão Reserva - Claro", showBackground = true)
@Composable
private fun CartaoReservaLightPreview() {
    AcerviaTheme(darkTheme = false) {
        CartaoReserva(
            prazoTexto = "Retire até amanhã, 10/10 · 18h",
            prazoUrgente = true,
            tituloLivro = "A Biblioteca da Meia-Noite",
            autorLivro = "Matt Haig",
            codigoRetirada = "482716",
            localBiblioteca = "Biblioteca Central",
            statusBiblioteca = "Aberta agora, fecha às 18h",
            modifier = Modifier.padding(16.dp),
        )
    }
}

@Preview(name = "Cartão Reserva - Escuro", uiMode = Configuration.UI_MODE_NIGHT_YES, showBackground = true)
@Composable
private fun CartaoReservaDarkPreview() {
    AcerviaTheme(darkTheme = true) {
        CartaoReserva(
            prazoTexto = "Reserva expirada",
            prazoUrgente = false,
            tituloLivro = "O Senhor dos Anéis: A Sociedade do Anel",
            autorLivro = "J.R.R. Tolkien",
            codigoRetirada = "931204",
            localBiblioteca = "Biblioteca Central",
            statusBiblioteca = "Aberta agora, fecha às 18h",
            expirada = true,
            copiarHabilitado = false,
            modifier = Modifier.padding(16.dp),
        )
    }
}
