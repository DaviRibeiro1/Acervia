package com.example.acervia.ui.screens.detalhes

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.res.Configuration
import android.widget.Toast
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.ContentCopy
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.LocationOn
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
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
fun SuaReservaScreen(
    viewModel: SuaReservaViewModel,
    onVoltarClick: () -> Unit,
    onVerMinhasReservasClick: () -> Unit,
    onLivroClick: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    val uiState by viewModel.uiState.collectAsState()

    SuaReservaContent(
        uiState = uiState,
        onVoltarClick = onVoltarClick,
        onVerMinhasReservasClick = onVerMinhasReservasClick,
        onLivroClick = onLivroClick,
        onMostrarDialogoCancelamento = viewModel::onMostrarDialogoCancelamento,
        onConfirmarCancelamento = { viewModel.onConfirmarCancelamento(onVerMinhasReservasClick) },
        onTentarNovamente = viewModel::carregarReserva,
        modifier = modifier,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SuaReservaContent(
    uiState: SuaReservaUiState,
    onVoltarClick: () -> Unit,
    onVerMinhasReservasClick: () -> Unit,
    onLivroClick: (String) -> Unit,
    onMostrarDialogoCancelamento: (Boolean) -> Unit,
    onConfirmarCancelamento: () -> Unit,
    onTentarNovamente: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Sua reserva",
                        style = MaterialTheme.typography.headlineMedium,
                        color = MaterialTheme.colorScheme.onBackground,
                        modifier = Modifier.semantics { heading() },
                    )
                },
                navigationIcon = {
                    IconButton(
                        onClick = onVoltarClick,
                        modifier = Modifier.size(48.dp),
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Outlined.ArrowBack,
                            contentDescription = "Voltar",
                            tint = MaterialTheme.colorScheme.primary,
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background,
                ),
            )
        },
        containerColor = MaterialTheme.colorScheme.background,
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
        ) {
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
                            .padding(Space.s5),
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

                uiState.reserva != null -> {
                    val reserva = uiState.reserva
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .verticalScroll(rememberScrollState())
                            .padding(horizontal = Space.s5, vertical = Space.s3),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(Space.s4),
                    ) {
                        // 1. Topo da Confirmação (se recém-reservado)
                        if (uiState.isConfirmacao) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.spacedBy(Space.s2),
                                modifier = Modifier.padding(vertical = Space.s2),
                            ) {
                                Surface(
                                    modifier = Modifier.size(56.dp),
                                    shape = MaterialTheme.shapes.large,
                                    color = AppTheme.extra.successContainer,
                                ) {
                                    Box(contentAlignment = Alignment.Center) {
                                        Icon(
                                            imageVector = Icons.Outlined.CheckCircle,
                                            contentDescription = null,
                                            tint = AppTheme.extra.success,
                                            modifier = Modifier.size(36.dp),
                                        )
                                    }
                                }
                                Text(
                                    text = "Reserva feita",
                                    style = MaterialTheme.typography.headlineMedium,
                                    color = MaterialTheme.colorScheme.onBackground,
                                    modifier = Modifier.semantics { heading() },
                                )
                                Text(
                                    text = "Apresente o código no balcão de retirada.",
                                    style = MaterialTheme.typography.bodyLarge,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                )
                            }
                        }

                        // 2. Cartão do Código de Retirada
                        CartaoDoCodigo(
                            codigo = reserva.codigoRetirada,
                            prazoTexto = uiState.prazoTexto,
                            prazoUrgente = uiState.prazoUrgente,
                            status = reserva.status,
                            expiraEm = reserva.expiraEm,
                            onCopiarCodigo = { cod ->
                                val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                                val clip = ClipData.newPlainText("Código de Retirada", cod)
                                clipboard.setPrimaryClip(clip)
                                Toast.makeText(context, "Código copiado.", Toast.LENGTH_SHORT).show()
                            },
                        )

                        // 3. Informações do Livro
                        ItemLivroReserva(
                            reserva = reserva,
                            onLivroClick = { onLivroClick(reserva.volumeId) },
                        )

                        // 4. Cartão de Local e Instruções
                        CartaoLocalRetirada(
                            local = reserva.localBiblioteca,
                            status = reserva.statusBiblioteca,
                        )

                        Spacer(modifier = Modifier.height(Space.s2))

                        // 5. Botão Principal (Apenas na Confirmação)
                        if (uiState.isConfirmacao) {
                            Button(
                                onClick = onVerMinhasReservasClick,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .heightIn(min = 56.dp),
                                shape = MaterialTheme.shapes.medium,
                            ) {
                                Text(text = "Ver minhas reservas", style = AppTextStyles.button)
                            }
                        }

                        // 6. Ação de Cancelar Reserva (se ativa)
                        if (reserva.status == StatusReserva.ATIVA) {
                            TextButton(
                                onClick = { onMostrarDialogoCancelamento(true) },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .heightIn(min = 48.dp),
                            ) {
                                Text(
                                    text = "Cancelar reserva",
                                    style = AppTextStyles.button,
                                    color = MaterialTheme.colorScheme.error,
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(Space.s6))
                    }
                }
            }
        }
    }

    // Modal BottomSheet de Confirmação de Cancelamento
    if (uiState.mostrarDialogoCancelamento) {
        ModalBottomSheet(
            onDismissRequest = { if (!uiState.isCancelando) onMostrarDialogoCancelamento(false) },
            sheetState = rememberModalBottomSheetState(),
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(Space.s5),
                verticalArrangement = Arrangement.spacedBy(Space.s4),
                horizontalAlignment = Alignment.Start,
            ) {
                Text(
                    text = "Cancelar esta reserva?",
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.onSurface,
                )

                Text(
                    text = "O exemplar volta ao estoque e outra pessoa pode reservá-lo.",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )

                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(Space.s2),
                ) {
                    Button(
                        onClick = { if (!uiState.isCancelando) onMostrarDialogoCancelamento(false) },
                        enabled = !uiState.isCancelando,
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(min = 56.dp),
                        shape = MaterialTheme.shapes.medium,
                    ) {
                        Text(text = "Manter reserva", style = AppTextStyles.button)
                    }

                    OutlinedButton(
                        onClick = onConfirmarCancelamento,
                        enabled = !uiState.isCancelando,
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(min = 56.dp),
                        shape = MaterialTheme.shapes.medium,
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = MaterialTheme.colorScheme.error,
                        ),
                    ) {
                        if (uiState.isCancelando) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(24.dp),
                                color = MaterialTheme.colorScheme.error,
                            )
                        } else {
                            Text(
                                text = "Cancelar reserva",
                                style = AppTextStyles.button,
                                color = MaterialTheme.colorScheme.error,
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.height(Space.s4))
            }
        }
    }
}

@Composable
private fun CartaoDoCodigo(
    codigo: String,
    prazoTexto: String,
    prazoUrgente: Boolean,
    status: StatusReserva,
    expiraEm: Long,
    onCopiarCodigo: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    val codigoFormatado = if (codigo.length == 6) {
        "${codigo.substring(0, 3)} ${codigo.substring(3)}"
    } else {
        codigo
    }

    val ehAtiva = status == StatusReserva.ATIVA
    val ehExpirada = status == StatusReserva.EXPIRADA

    val textoSelo = when {
        ehExpirada -> {
            val dataExpirada = SimpleDateFormat("dd/MM", Locale.getDefault()).format(Date(expiraEm))
            "Reserva expirada em $dataExpirada"
        }
        status == StatusReserva.CANCELADA -> "Reserva cancelada"
        status == StatusReserva.RETIRADA -> "Reserva retirada"
        else -> prazoTexto
    }

    Surface(
        modifier = modifier
            .fillMaxWidth()
            .clip(MaterialTheme.shapes.large)
            .border(
                width = 1.dp,
                color = MaterialTheme.colorScheme.outlineVariant,
                shape = MaterialTheme.shapes.large,
            ),
        color = MaterialTheme.colorScheme.surface,
    ) {
        Column(
            modifier = Modifier.padding(Space.s4),
            verticalArrangement = Arrangement.spacedBy(Space.s3),
        ) {
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
                    Text(
                        text = codigoFormatado,
                        style = AppTextStyles.otp,
                        color = if (ehAtiva) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }

                OutlinedButton(
                    onClick = { onCopiarCodigo(codigo) },
                    enabled = ehAtiva,
                    modifier = Modifier.heightIn(min = 48.dp),
                    shape = MaterialTheme.shapes.medium,
                ) {
                    Icon(
                        imageVector = Icons.Outlined.ContentCopy,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp),
                    )
                    Spacer(modifier = Modifier.width(Space.s2))
                    Text(text = "Copiar", style = AppTextStyles.button)
                }
            }

            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)

            SeloPrazo(
                texto = textoSelo,
                urgente = prazoUrgente,
                expirada = ehExpirada,
            )
        }
    }
}

@Composable
private fun ItemLivroReserva(
    reserva: Reserva,
    onLivroClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val dataReservado = SimpleDateFormat("dd/MM", Locale.getDefault()).format(Date(reserva.reservadoEm))

    Surface(
        modifier = modifier
            .fillMaxWidth()
            .clip(MaterialTheme.shapes.medium)
            .border(
                width = 1.dp,
                color = MaterialTheme.colorScheme.outlineVariant,
                shape = MaterialTheme.shapes.medium,
            )
            .clickable(onClick = onLivroClick),
        color = MaterialTheme.colorScheme.surface,
    ) {
        Row(
            modifier = Modifier.padding(Space.s4),
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
                        .width(56.dp)
                        .height(84.dp)
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
                                .width(56.dp)
                                .height(84.dp),
                        )
                    },
                    loading = {
                        CapaSubstituta(
                            titulo = reserva.livroTitulo,
                            autor = reserva.livroAutor,
                            modifier = Modifier
                                .width(56.dp)
                                .height(84.dp),
                        )
                    },
                )
            } else {
                CapaSubstituta(
                    titulo = reserva.livroTitulo,
                    autor = reserva.livroAutor,
                    modifier = Modifier
                        .width(56.dp)
                        .height(84.dp),
                )
            }

            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(Space.s1),
            ) {
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
                Spacer(modifier = Modifier.height(Space.s1))
                Text(
                    text = "Reservado em $dataReservado",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }
    }
}

@Composable
private fun CartaoLocalRetirada(
    local: String,
    status: String,
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
            ),
        color = MaterialTheme.colorScheme.surface,
    ) {
        Column(
            modifier = Modifier.padding(Space.s4),
            verticalArrangement = Arrangement.spacedBy(Space.s2),
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(Space.s2),
            ) {
                Icon(
                    imageVector = Icons.Outlined.LocationOn,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(20.dp),
                )
                Column {
                    Text(
                        text = local,
                        style = AppTextStyles.button,
                        color = MaterialTheme.colorScheme.onSurface,
                    )
                    Text(
                        text = status,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            }

            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(Space.s2),
            ) {
                Icon(
                    imageVector = Icons.Outlined.Info,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.size(18.dp),
                )
                Text(
                    text = "Para retirar, basta o código.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }
    }
}

@Preview(name = "Sua Reserva Confirmação - Claro", showBackground = true)
@Composable
private fun SuaReservaConfirmacaoLightPreview() {
    AcerviaTheme(darkTheme = false) {
        SuaReservaContent(
            uiState = SuaReservaUiState(
                isLoading = false,
                isConfirmacao = true,
                prazoTexto = "Retire até amanhã, 10/10 · 18h",
                prazoUrgente = true,
                reserva = Reserva(
                    id = "res_101",
                    usuarioId = "usr_1",
                    volumeId = "vol_1",
                    livroTitulo = "A Biblioteca da Meia-Noite",
                    livroAutor = "Matt Haig",
                    capaUrl = null,
                    codigoRetirada = "482716",
                    status = StatusReserva.ATIVA,
                    reservadoEm = System.currentTimeMillis(),
                    expiraEm = System.currentTimeMillis() + 86400000L,
                    localBiblioteca = "Biblioteca Central",
                    statusBiblioteca = "Aberta agora, fecha às 18h",
                ),
            ),
            onVoltarClick = {},
            onVerMinhasReservasClick = {},
            onLivroClick = {},
            onMostrarDialogoCancelamento = {},
            onConfirmarCancelamento = {},
            onTentarNovamente = {},
        )
    }
}

@Preview(name = "Sua Reserva Detalhe - Escuro", uiMode = Configuration.UI_MODE_NIGHT_YES, showBackground = true)
@Composable
private fun SuaReservaDetalheDarkPreview() {
    AcerviaTheme(darkTheme = true) {
        SuaReservaContent(
            uiState = SuaReservaUiState(
                isLoading = false,
                isConfirmacao = false,
                prazoTexto = "Retire até qui, 15/10 · 18h",
                prazoUrgente = false,
                reserva = Reserva(
                    id = "res_101",
                    usuarioId = "usr_1",
                    volumeId = "vol_1",
                    livroTitulo = "A Biblioteca da Meia-Noite",
                    livroAutor = "Matt Haig",
                    capaUrl = null,
                    codigoRetirada = "482716",
                    status = StatusReserva.ATIVA,
                    reservadoEm = System.currentTimeMillis(),
                    expiraEm = System.currentTimeMillis() + 86400000L,
                    localBiblioteca = "Biblioteca Central",
                    statusBiblioteca = "Aberta agora, fecha às 18h",
                ),
            ),
            onVoltarClick = {},
            onVerMinhasReservasClick = {},
            onLivroClick = {},
            onMostrarDialogoCancelamento = {},
            onConfirmarCancelamento = {},
            onTentarNovamente = {},
        )
    }
}
