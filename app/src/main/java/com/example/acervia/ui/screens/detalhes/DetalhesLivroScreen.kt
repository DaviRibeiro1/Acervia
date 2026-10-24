package com.example.acervia.ui.screens.detalhes

import android.content.res.Configuration
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import com.example.acervia.domain.model.Livro
import com.example.acervia.ui.components.CapaSubstituta
import com.example.acervia.ui.components.SeloDisponibilidade
import com.example.acervia.ui.theme.AcerviaTheme
import com.example.acervia.ui.theme.AppTextStyles
import com.example.acervia.ui.theme.Space

@Composable
fun DetalhesLivroScreen(
    viewModel: DetalhesLivroViewModel,
    onVoltarClick: () -> Unit,
    onReservaConfirmada: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    val uiState by viewModel.uiState.collectAsState()

    DetalhesLivroContent(
        uiState = uiState,
        onVoltarClick = onVoltarClick,
        onToggleDescricao = viewModel::toggleDescricaoExpandida,
        onTentarNovamente = viewModel::carregarDetalhes,
        onAbrirConfirmacaoReserva = viewModel::onAbrirBottomSheetConfirmacao,
        onFecharConfirmacaoReserva = viewModel::onFecharBottomSheetConfirmacao,
        onConfirmarReserva = { viewModel.onConfirmarReserva(onReservaConfirmada) },
        modifier = modifier,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetalhesLivroContent(
    uiState: DetalhesLivroUiState,
    onVoltarClick: () -> Unit,
    onToggleDescricao: () -> Unit,
    onTentarNovamente: () -> Unit,
    onAbrirConfirmacaoReserva: () -> Unit,
    onFecharConfirmacaoReserva: () -> Unit,
    onConfirmarReserva: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Detalhes do livro",
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
        bottomBar = {
            if (uiState.livro != null) {
                BarraFixaReserva(
                    disponivel = uiState.livro.disponivel,
                    botaoHabilitado = uiState.livro.disponivel,
                    onReservarClick = onAbrirConfirmacaoReserva,
                )
            }
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

                uiState.livro != null -> {
                    val livro = uiState.livro
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .verticalScroll(rememberScrollState())
                            .padding(horizontal = Space.s5, vertical = Space.s3),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(Space.s4),
                    ) {
                        // 1. Capa Centralizada (160x240 dp)
                        Box(
                            modifier = Modifier.fillMaxWidth(),
                            contentAlignment = Alignment.Center,
                        ) {
                            if (!livro.capaUrl.isNullOrBlank()) {
                                SubcomposeAsyncImage(
                                    model = ImageRequest.Builder(LocalContext.current)
                                        .data(livro.capaUrl)
                                        .crossfade(enable = true)
                                        .build(),
                                    contentDescription = null,
                                    contentScale = ContentScale.Crop,
                                    modifier = Modifier
                                        .width(160.dp)
                                        .height(240.dp)
                                        .clip(MaterialTheme.shapes.small)
                                        .border(
                                            width = 1.dp,
                                            color = MaterialTheme.colorScheme.outlineVariant,
                                            shape = MaterialTheme.shapes.small,
                                        ),
                                    error = {
                                        CapaSubstituta(
                                            titulo = livro.titulo,
                                            autor = livro.autores.joinToString(", "),
                                            modifier = Modifier
                                                .width(160.dp)
                                                .height(240.dp),
                                        )
                                    },
                                    loading = {
                                        CapaSubstituta(
                                            titulo = livro.titulo,
                                            autor = livro.autores.joinToString(", "),
                                            modifier = Modifier
                                                .width(160.dp)
                                                .height(240.dp),
                                        )
                                    },
                                )
                            } else {
                                CapaSubstituta(
                                    titulo = livro.titulo,
                                    autor = livro.autores.joinToString(", "),
                                    modifier = Modifier
                                        .width(160.dp)
                                        .height(240.dp),
                                )
                            }
                        }

                        // 2. Categoria, Título e Autor
                        Column(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalAlignment = Alignment.Start,
                            verticalArrangement = Arrangement.spacedBy(Space.s1),
                        ) {
                            if (!livro.categoria.isNullOrBlank()) {
                                Text(
                                    text = livro.categoria.uppercase(),
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                )
                            }

                            // Hífen sem quebra (U+2011) para evitar quebra de palavra irregular
                            val tituloFormatado = livro.titulo.replace("-", "\u2011")

                            Text(
                                text = tituloFormatado,
                                style = MaterialTheme.typography.headlineMedium,
                                color = MaterialTheme.colorScheme.onBackground,
                                modifier = Modifier.semantics { heading() },
                            )

                            Text(
                                text = livro.autores.joinToString(", "),
                                style = MaterialTheme.typography.bodyLarge,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                            )

                            Spacer(modifier = Modifier.height(Space.s1))

                            // 3. Selo de Disponibilidade
                            SeloDisponibilidade(
                                disponivel = livro.disponivel,
                                textoPersonalizado = if (livro.disponivel) "${livro.exemplaresDisponiveis} exemplares disponíveis" else "Indisponível",
                            )
                        }

                        HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)

                        // 4. Sobre o Livro (Descrição)
                        if (!livro.descricao.isNullOrBlank()) {
                            Column(
                                modifier = Modifier.fillMaxWidth(),
                                verticalArrangement = Arrangement.spacedBy(Space.s2),
                            ) {
                                Text(
                                    text = "Sobre o livro",
                                    style = MaterialTheme.typography.titleLarge,
                                    color = MaterialTheme.colorScheme.onBackground,
                                    modifier = Modifier.semantics { heading() },
                                )

                                Text(
                                    text = livro.descricao,
                                    style = MaterialTheme.typography.bodyLarge,
                                    color = MaterialTheme.colorScheme.onBackground,
                                    maxLines = if (uiState.descricaoExpandida) Int.MAX_VALUE else 5,
                                    overflow = TextOverflow.Ellipsis,
                                )

                                TextButton(
                                    onClick = onToggleDescricao,
                                    modifier = Modifier.align(Alignment.End),
                                ) {
                                    Text(
                                        text = if (uiState.descricaoExpandida) "Ler menos" else "Ler mais",
                                        style = AppTextStyles.button,
                                        color = MaterialTheme.colorScheme.primary,
                                    )
                                }
                            }

                            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
                        }

                        // 5. Ficha Técnica
                        SecaoFichaTecnica(livro = livro)

                        Spacer(modifier = Modifier.height(Space.s6))
                    }
                }
            }
        }
    }

    // Modal BottomSheet de Confirmação da Reserva
    if (uiState.mostrarBottomSheetConfirmacao && uiState.livro != null) {
        val livro = uiState.livro
        ModalBottomSheet(
            onDismissRequest = onFecharConfirmacaoReserva,
            sheetState = rememberModalBottomSheetState(),
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(Space.s5),
                verticalArrangement = Arrangement.spacedBy(Space.s4),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Text(
                    text = "Confirmar reserva",
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.align(Alignment.Start),
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(Space.s3),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    CapaSubstituta(
                        titulo = livro.titulo,
                        autor = livro.autores.joinToString(", "),
                        modifier = Modifier
                            .width(52.dp)
                            .height(78.dp),
                    )
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = livro.titulo,
                            style = AppTextStyles.button,
                            color = MaterialTheme.colorScheme.onSurface,
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis,
                        )
                        Text(
                            text = livro.autores.joinToString(", "),
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                        )
                    }
                }

                Text(
                    text = "Você terá até às 18h de ${uiState.prazoTextualFormatted.lowercase()} para retirar na Biblioteca Central.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )

                if (uiState.erroReserva != null) {
                    Text(
                        text = uiState.erroReserva,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.error,
                    )
                }

                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(Space.s2),
                ) {
                    Button(
                        onClick = onConfirmarReserva,
                        enabled = !uiState.isReservando,
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(min = 56.dp),
                        shape = MaterialTheme.shapes.medium,
                    ) {
                        if (uiState.isReservando) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(24.dp),
                                color = MaterialTheme.colorScheme.onPrimary,
                            )
                        } else {
                            Text("Confirmar reserva", style = AppTextStyles.button)
                        }
                    }

                    OutlinedButton(
                        onClick = onFecharConfirmacaoReserva,
                        enabled = !uiState.isReservando,
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(min = 56.dp),
                        shape = MaterialTheme.shapes.medium,
                    ) {
                        Text("Cancelar", style = AppTextStyles.button)
                    }
                }
                Spacer(modifier = Modifier.height(Space.s4))
            }
        }
    }
}

@Composable
private fun SecaoFichaTecnica(
    livro: Livro,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(Space.s2),
    ) {
        Text(
            text = "Ficha técnica",
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.onBackground,
            modifier = Modifier.semantics { heading() },
        )

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
                verticalArrangement = Arrangement.spacedBy(Space.s3),
            ) {
                if (livro.paginas != null) {
                    ItemFichaTecnica(rotulo = "Páginas", valor = "${livro.paginas} páginas")
                }
                if (!livro.editora.isNullOrBlank()) {
                    ItemFichaTecnica(rotulo = "Editora", valor = livro.editora)
                }
                if (!livro.isbn13.isNullOrBlank()) {
                    ItemFichaTecnica(rotulo = "ISBN", valor = livro.isbn13)
                }
            }
        }
    }
}

@Composable
private fun ItemFichaTecnica(
    rotulo: String,
    valor: String,
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = rotulo,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        Text(
            text = valor,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurface,
        )
    }
}

@Composable
private fun BarraFixaReserva(
    disponivel: Boolean,
    botaoHabilitado: Boolean,
    onReservarClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        color = MaterialTheme.colorScheme.surface,
        tonalElevation = 0.dp,
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.surface)
                .border(
                    width = 1.dp,
                    color = MaterialTheme.colorScheme.outlineVariant,
                )
                .padding(Space.s4),
            verticalArrangement = Arrangement.spacedBy(Space.s2),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(Space.s1),
            ) {
                Icon(
                    imageVector = Icons.Outlined.LocationOn,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.size(20.dp),
                )
                Text(
                    text = "Retirada na Biblioteca Central",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }

            Button(
                onClick = onReservarClick,
                enabled = botaoHabilitado,
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = 56.dp),
                shape = MaterialTheme.shapes.medium,
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary,
                    disabledContainerColor = MaterialTheme.colorScheme.outlineVariant,
                    disabledContentColor = MaterialTheme.colorScheme.onSurfaceVariant,
                ),
            ) {
                Text(
                    text = if (disponivel) "Reservar livro" else "Indisponível",
                    style = AppTextStyles.button,
                )
            }
        }
    }
}

@Preview(name = "Detalhes do Livro - Claro", showBackground = true)
@Composable
private fun DetalhesLivroScreenLightPreview() {
    AcerviaTheme(darkTheme = false) {
        DetalhesLivroContent(
            uiState = DetalhesLivroUiState(
                isLoading = false,
                livro = Livro(
                    volumeId = "1",
                    isbn13 = "9786555600292",
                    titulo = "A Biblioteca da Meia-Noite",
                    autores = listOf("Matt Haig"),
                    descricao = "Nora Seed é uma mulher de 35 anos cheia de arrepimentos. Entre a vida e a morte, ela chega à Biblioteca da Meia-Noite, onde cada livro oferece a chance de vivenciar outra versão de sua vida.",
                    categoria = "Ficção",
                    paginas = 308,
                    editora = "Bertrand Brasil",
                    capaUrl = null,
                    exemplaresTotal = 3,
                    exemplaresDisponiveis = 3,
                ),
            ),
            onVoltarClick = {},
            onToggleDescricao = {},
            onTentarNovamente = {},
            onAbrirConfirmacaoReserva = {},
            onFecharConfirmacaoReserva = {},
            onConfirmarReserva = {},
        )
    }
}

@Preview(name = "Detalhes do Livro - Escuro", uiMode = Configuration.UI_MODE_NIGHT_YES, showBackground = true)
@Composable
private fun DetalhesLivroScreenDarkPreview() {
    AcerviaTheme(darkTheme = true) {
        DetalhesLivroContent(
            uiState = DetalhesLivroUiState(
                isLoading = false,
                livro = Livro(
                    volumeId = "1",
                    isbn13 = "9786555600292",
                    titulo = "A Biblioteca da Meia-Noite",
                    autores = listOf("Matt Haig"),
                    descricao = "Nora Seed é uma mulher de 35 anos cheia de arrepimentos. Entre a vida e a morte, ela chega à Biblioteca da Meia-Noite, onde cada livro oferece a chance de vivenciar outra versão de sua vida.",
                    categoria = "Ficção",
                    paginas = 308,
                    editora = "Bertrand Brasil",
                    capaUrl = null,
                    exemplaresTotal = 3,
                    exemplaresDisponiveis = 3,
                ),
            ),
            onVoltarClick = {},
            onToggleDescricao = {},
            onTentarNovamente = {},
            onAbrirConfirmacaoReserva = {},
            onFecharConfirmacaoReserva = {},
            onConfirmarReserva = {},
        )
    }
}
