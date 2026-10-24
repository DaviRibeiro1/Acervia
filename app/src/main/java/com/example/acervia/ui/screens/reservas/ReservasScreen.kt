package com.example.acervia.ui.screens.reservas

import android.content.res.Configuration
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.KeyboardArrowRight
import androidx.compose.material.icons.outlined.Cancel
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.Schedule
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.SubcomposeAsyncImage
import coil.request.ImageRequest
import com.example.acervia.domain.model.Reserva
import com.example.acervia.domain.model.StatusReserva
import com.example.acervia.ui.components.CapaSubstituta
import com.example.acervia.ui.components.SeloPrazo
import com.example.acervia.ui.theme.AcerviaTheme
import com.example.acervia.ui.theme.AppTextStyles
import com.example.acervia.ui.theme.AppTheme
import com.example.acervia.ui.theme.Space
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun ReservasScreen(
    viewModel: ReservasViewModel,
    onReservaClick: (String) -> Unit,
    onExplorarCatalogoClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.carregarReservas()
    }

    ReservasContent(
        uiState = uiState,
        onReservaClick = onReservaClick,
        onExplorarCatalogoClick = onExplorarCatalogoClick,
        onCarregarMaisHistorico = viewModel::carregarMaisHistorico,
        onTentarNovamente = viewModel::carregarReservas,
        formatarPrazoTextual = viewModel::formatarPrazoTextual,
        ehPrazoUrgente = viewModel::ehPrazoUrgente,
        modifier = modifier,
    )
}

@Composable
fun ReservasContent(
    uiState: ReservasUiState,
    onReservaClick: (String) -> Unit,
    onExplorarCatalogoClick: () -> Unit,
    onCarregarMaisHistorico: () -> Unit,
    onTentarNovamente: () -> Unit,
    formatarPrazoTextual: (Reserva) -> String,
    ehPrazoUrgente: (Reserva) -> Boolean,
    modifier: Modifier = Modifier,
) {
    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = MaterialTheme.colorScheme.background,
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = Space.s5),
            verticalArrangement = Arrangement.spacedBy(Space.s3),
        ) {
            Spacer(modifier = Modifier.height(Space.s2))

            // Título da Barra
            Text(
                text = "Reservas",
                style = MaterialTheme.typography.headlineMedium,
                color = MaterialTheme.colorScheme.onBackground,
                modifier = Modifier.semantics { heading() },
            )

            Box(modifier = Modifier.fillMaxSize()) {
                when {
                    uiState.isLoading -> {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center,
                        ) {
                            CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
                        }
                    }

                    uiState.erro != null -> {
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(Space.s4),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center,
                        ) {
                            Text(
                                text = uiState.erro,
                                style = MaterialTheme.typography.bodyLarge,
                                color = MaterialTheme.colorScheme.error,
                            )
                            Spacer(modifier = Modifier.height(Space.s4))
                            Button(onClick = onTentarNovamente) {
                                Text("Tentar de novo")
                            }
                        }
                    }

                    uiState.reservasAtivas.isEmpty() && uiState.historicoReservas.isEmpty() -> {
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(Space.s4),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center,
                        ) {
                            Text(
                                text = "Suas reservas vão aparecer aqui.",
                                style = MaterialTheme.typography.bodyLarge,
                                color = MaterialTheme.colorScheme.onSurface,
                            )
                            Spacer(modifier = Modifier.height(Space.s4))
                            Button(onClick = onExplorarCatalogoClick) {
                                Text("Explorar catálogo", style = AppTextStyles.button)
                            }
                        }
                    }

                    else -> {
                        LazyColumn(
                            modifier = Modifier.fillMaxSize(),
                            verticalArrangement = Arrangement.spacedBy(Space.s4),
                        ) {
                            // Seção Ativas
                            item {
                                Column(verticalArrangement = Arrangement.spacedBy(Space.s2)) {
                                    Text(
                                        text = "Ativas (${uiState.reservasAtivas.size})",
                                        style = MaterialTheme.typography.titleLarge,
                                        color = MaterialTheme.colorScheme.onBackground,
                                        modifier = Modifier.semantics { heading() },
                                    )

                                    if (uiState.reservasAtivas.isEmpty()) {
                                        Surface(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .clip(MaterialTheme.shapes.medium)
                                                .border(
                                                    width = 1.dp,
                                                    color = MaterialTheme.colorScheme.outlineVariant,
                                                    shape = MaterialTheme.shapes.medium,
                                                ),
                                            color = MaterialTheme.colorScheme.surface,
                                        ) {
                                            Column(
                                                modifier = Modifier.padding(Space.s4),
                                                horizontalAlignment = Alignment.CenterHorizontally,
                                                verticalArrangement = Arrangement.spacedBy(Space.s2),
                                            ) {
                                                Text(
                                                    text = "Você não tem reservas ativas.",
                                                    style = MaterialTheme.typography.bodyMedium,
                                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                                )
                                                OutlinedButton(onClick = onExplorarCatalogoClick) {
                                                    Text("Explorar catálogo")
                                                }
                                            }
                                        }
                                    }
                                }
                            }

                            items(
                                items = uiState.reservasAtivas,
                                key = { "ativa_${it.id}" },
                            ) { reserva ->
                                CartaoReservaAtivaItem(
                                    reserva = reserva,
                                    prazoTexto = formatarPrazoTextual(reserva),
                                    prazoUrgente = ehPrazoUrgente(reserva),
                                    onReservaClick = { onReservaClick(reserva.id) },
                                )
                            }

                            // Seção Histórico
                            if (uiState.historicoReservas.isNotEmpty()) {
                                item {
                                    Spacer(modifier = Modifier.height(Space.s2))
                                    Text(
                                        text = "Histórico",
                                        style = MaterialTheme.typography.titleLarge,
                                        color = MaterialTheme.colorScheme.onBackground,
                                        modifier = Modifier.semantics { heading() },
                                    )
                                }

                                items(
                                    items = uiState.historicoExibido,
                                    key = { "hist_${it.id}" },
                                ) { reserva ->
                                    Column {
                                        ItemLinhaHistorico(
                                            reserva = reserva,
                                            onReservaClick = { onReservaClick(reserva.id) },
                                        )
                                        HorizontalDivider(
                                            color = MaterialTheme.colorScheme.outlineVariant,
                                            modifier = Modifier.padding(top = Space.s2),
                                        )
                                    }
                                }

                                if (uiState.temMaisHistorico) {
                                    item {
                                        Box(
                                            modifier = Modifier.fillMaxWidth(),
                                            contentAlignment = Alignment.Center,
                                        ) {
                                            TextButton(
                                                onClick = onCarregarMaisHistorico,
                                                modifier = Modifier.heightIn(min = 48.dp),
                                            ) {
                                                Text(
                                                    text = "Ver mais",
                                                    style = AppTextStyles.button,
                                                    color = MaterialTheme.colorScheme.primary,
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun CartaoReservaAtivaItem(
    reserva: Reserva,
    prazoTexto: String,
    prazoUrgente: Boolean,
    onReservaClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .clip(MaterialTheme.shapes.medium)
            .border(
                width = 1.dp,
                color = MaterialTheme.colorScheme.outlineVariant,
                shape = MaterialTheme.shapes.medium,
            )
            .clickable(onClick = onReservaClick),
        color = MaterialTheme.colorScheme.surface,
    ) {
        Column(
            modifier = Modifier.padding(Space.s4),
            verticalArrangement = Arrangement.spacedBy(Space.s3),
        ) {
            SeloPrazo(texto = prazoTexto, urgente = prazoUrgente)

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(Space.s3),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                if (!reserva.capaUrl.isNullOrBlank()) {
                    SubcomposeAsyncImage(
                        model = ImageRequest.Builder(LocalContext.current)
                            .data(reserva.capaUrl)
                            .crossfade(enable = true)
                            .build(),
                        contentDescription = null,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .width(44.dp)
                            .height(66.dp)
                            .clip(MaterialTheme.shapes.small)
                            .border(
                                width = 1.dp,
                                color = MaterialTheme.colorScheme.outlineVariant,
                                shape = MaterialTheme.shapes.small,
                            ),
                        error = {
                            CapaSubstituta(
                                titulo = reserva.livroTitulo,
                                autor = reserva.livroAutor,
                                modifier = Modifier
                                    .width(44.dp)
                                    .height(66.dp),
                            )
                        },
                        loading = {
                            CapaSubstituta(
                                titulo = reserva.livroTitulo,
                                autor = reserva.livroAutor,
                                modifier = Modifier
                                    .width(44.dp)
                                    .height(66.dp),
                            )
                        },
                    )
                } else {
                    CapaSubstituta(
                        titulo = reserva.livroTitulo,
                        autor = reserva.livroAutor,
                        modifier = Modifier
                            .width(44.dp)
                            .height(66.dp),
                    )
                }

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = reserva.livroTitulo,
                        style = AppTextStyles.button,
                        color = MaterialTheme.colorScheme.onSurface,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis,
                    )
                    Text(
                        text = reserva.livroAutor,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                }

                Icon(
                    imageVector = Icons.AutoMirrored.Outlined.KeyboardArrowRight,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.size(24.dp),
                )
            }
        }
    }
}

@Composable
private fun ItemLinhaHistorico(
    reserva: Reserva,
    onReservaClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val dataFormatada = SimpleDateFormat("dd/MM", Locale.getDefault()).format(Date(reserva.reservadoEm))

    val (iconeStatus, corStatus, textoStatus) = when (reserva.status) {
        StatusReserva.RETIRADA -> Triple(Icons.Outlined.CheckCircle, AppTheme.extra.success, "Retirado em $dataFormatada")
        StatusReserva.EXPIRADA -> Triple(Icons.Outlined.Schedule, MaterialTheme.colorScheme.error, "Expirada em $dataFormatada")
        StatusReserva.CANCELADA -> Triple(Icons.Outlined.Cancel, MaterialTheme.colorScheme.onSurfaceVariant, "Cancelada em $dataFormatada")
        StatusReserva.ATIVA -> Triple(Icons.Outlined.CheckCircle, AppTheme.extra.success, "Ativa em $dataFormatada")
    }

    Row(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onReservaClick)
            .padding(vertical = Space.s2),
        horizontalArrangement = Arrangement.spacedBy(Space.s3),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        if (!reserva.capaUrl.isNullOrBlank()) {
            SubcomposeAsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(reserva.capaUrl)
                    .crossfade(enable = true)
                    .build(),
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .width(32.dp)
                    .height(48.dp)
                    .clip(MaterialTheme.shapes.small)
                    .border(
                        width = 1.dp,
                        color = MaterialTheme.colorScheme.outlineVariant,
                        shape = MaterialTheme.shapes.small,
                    ),
                error = {
                    CapaSubstituta(
                        titulo = reserva.livroTitulo,
                        autor = reserva.livroAutor,
                        modifier = Modifier
                            .width(32.dp)
                            .height(48.dp),
                    )
                },
                loading = {
                    CapaSubstituta(
                        titulo = reserva.livroTitulo,
                        autor = reserva.livroAutor,
                        modifier = Modifier
                            .width(32.dp)
                            .height(48.dp),
                    )
                },
            )
        } else {
            CapaSubstituta(
                titulo = reserva.livroTitulo,
                autor = reserva.livroAutor,
                modifier = Modifier
                    .width(32.dp)
                    .height(48.dp),
            )
        }

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = reserva.livroTitulo,
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.onSurface,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(Space.s1),
            ) {
                Icon(
                    imageVector = iconeStatus,
                    contentDescription = null,
                    tint = corStatus,
                    modifier = Modifier.size(16.dp),
                )
                Text(
                    text = textoStatus,
                    style = MaterialTheme.typography.bodySmall,
                    color = corStatus,
                )
            }
        }
    }
}

@Preview(name = "Reservas - Claro", showBackground = true)
@Composable
private fun ReservasScreenLightPreview() {
    AcerviaTheme(darkTheme = false) {
        ReservasContent(
            uiState = ReservasUiState(
                isLoading = false,
                reservasAtivas = listOf(
                    Reserva(
                        id = "res_1",
                        usuarioId = "u1",
                        volumeId = "v1",
                        livroTitulo = "A Biblioteca da Meia-Noite",
                        livroAutor = "Matt Haig",
                        capaUrl = null,
                        codigoRetirada = "482716",
                        status = StatusReserva.ATIVA,
                        reservadoEm = System.currentTimeMillis(),
                        expiraEm = System.currentTimeMillis() + 86400000L,
                        localBiblioteca = "Central",
                        statusBiblioteca = "Aberta",
                    ),
                ),
                historicoReservas = listOf(
                    Reserva(
                        id = "res_2",
                        usuarioId = "u1",
                        volumeId = "v2",
                        livroTitulo = "1984",
                        livroAutor = "George Orwell",
                        capaUrl = null,
                        codigoRetirada = "109823",
                        status = StatusReserva.RETIRADA,
                        reservadoEm = System.currentTimeMillis() - 864000000L,
                        expiraEm = System.currentTimeMillis() - 500000000L,
                        localBiblioteca = "Central",
                        statusBiblioteca = "Aberta",
                    ),
                ),
                historicoExibido = listOf(
                    Reserva(
                        id = "res_2",
                        usuarioId = "u1",
                        volumeId = "v2",
                        livroTitulo = "1984",
                        livroAutor = "George Orwell",
                        capaUrl = null,
                        codigoRetirada = "109823",
                        status = StatusReserva.RETIRADA,
                        reservadoEm = System.currentTimeMillis() - 864000000L,
                        expiraEm = System.currentTimeMillis() - 500000000L,
                        localBiblioteca = "Central",
                        statusBiblioteca = "Aberta",
                    ),
                ),
            ),
            onReservaClick = {},
            onExplorarCatalogoClick = {},
            onCarregarMaisHistorico = {},
            onTentarNovamente = {},
            formatarPrazoTextual = { "Retire até amanhã, 10/10 · 18h" },
            ehPrazoUrgente = { true },
        )
    }
}

@Preview(name = "Reservas - Escuro", uiMode = Configuration.UI_MODE_NIGHT_YES, showBackground = true)
@Composable
private fun ReservasScreenDarkPreview() {
    AcerviaTheme(darkTheme = true) {
        ReservasContent(
            uiState = ReservasUiState(
                isLoading = false,
                reservasAtivas = listOf(
                    Reserva(
                        id = "res_1",
                        usuarioId = "u1",
                        volumeId = "v1",
                        livroTitulo = "A Biblioteca da Meia-Noite",
                        livroAutor = "Matt Haig",
                        capaUrl = null,
                        codigoRetirada = "482716",
                        status = StatusReserva.ATIVA,
                        reservadoEm = System.currentTimeMillis(),
                        expiraEm = System.currentTimeMillis() + 86400000L,
                        localBiblioteca = "Central",
                        statusBiblioteca = "Aberta",
                    ),
                ),
                historicoReservas = emptyList(),
                historicoExibido = emptyList(),
            ),
            onReservaClick = {},
            onExplorarCatalogoClick = {},
            onCarregarMaisHistorico = {},
            onTentarNovamente = {},
            formatarPrazoTextual = { "Retire até amanhã, 10/10 · 18h" },
            ehPrazoUrgente = { true },
        )
    }
}
