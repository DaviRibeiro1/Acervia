package com.example.acervia.ui.screens.home

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.res.Configuration
import android.widget.Toast
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.MenuBook
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.SubcomposeAsyncImage
import coil.request.ImageRequest
import com.example.acervia.domain.model.LivroResumo
import com.example.acervia.domain.model.Reserva
import com.example.acervia.domain.model.StatusReserva
import com.example.acervia.ui.components.CapaSubstituta
import com.example.acervia.ui.components.CartaoReserva
import com.example.acervia.ui.components.SeloDisponibilidade
import com.example.acervia.ui.theme.AcerviaTheme
import com.example.acervia.ui.theme.AppTextStyles
import com.example.acervia.ui.theme.Space

@Composable
fun HomeScreen(
    viewModel: HomeViewModel,
    onAtalhoBuscaClick: () -> Unit,
    onVerTodasReservasClick: () -> Unit,
    onReservaClick: (String) -> Unit,
    onLivroClick: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.carregarHome()
    }

    HomeContent(
        uiState = uiState,
        onRefresh = viewModel::atualizarHome,
        onAtalhoBuscaClick = onAtalhoBuscaClick,
        onVerTodasReservasClick = onVerTodasReservasClick,
        onReservaClick = onReservaClick,
        onLivroClick = onLivroClick,
        onTentarNovamente = viewModel::carregarHome,
        modifier = modifier,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeContent(
    uiState: HomeUiState,
    onRefresh: () -> Unit,
    onAtalhoBuscaClick: () -> Unit,
    onVerTodasReservasClick: () -> Unit,
    onReservaClick: (String) -> Unit,
    onLivroClick: (String) -> Unit,
    onTentarNovamente: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = MaterialTheme.colorScheme.background,
    ) { innerPadding ->
        PullToRefreshBox(
            isRefreshing = uiState.isRefreshing,
            onRefresh = onRefresh,
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
        ) {
            Box(modifier = Modifier.fillMaxSize()) {
                when {
                    uiState.isLoading && !uiState.isRefreshing -> {
                        HomeSkeletonLoading()
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

                    uiState.reservaAtiva == null && uiState.livrosDestaque.isEmpty() -> {
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(Space.s5),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center,
                        ) {
                            Text(
                                text = "Nenhum destaque disponível no momento.",
                                style = MaterialTheme.typography.bodyLarge,
                                color = MaterialTheme.colorScheme.onSurface,
                            )
                            Spacer(modifier = Modifier.height(Space.s4))
                            Button(onClick = onAtalhoBuscaClick) {
                                Text("Explorar catálogo", style = AppTextStyles.button)
                            }
                        }
                    }

                    else -> {
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .verticalScroll(rememberScrollState())
                                .padding(vertical = Space.s3),
                            verticalArrangement = Arrangement.spacedBy(Space.s5),
                        ) {
                            // 1. Cabeçalho e Saudação
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = Space.s5),
                                verticalArrangement = Arrangement.spacedBy(Space.s2),
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(Space.s2),
                                ) {
                                    Icon(
                                        imageVector = Icons.AutoMirrored.Outlined.MenuBook,
                                        contentDescription = null,
                                        tint = MaterialTheme.colorScheme.primary,
                                        modifier = Modifier.size(24.dp),
                                    )
                                    Text(
                                        text = "Acervia",
                                        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                                        color = MaterialTheme.colorScheme.primary,
                                    )
                                }

                                val saudacao = if (uiState.nomeUsuario.isNotBlank()) {
                                    "Olá, ${uiState.nomeUsuario}. Qual será a próxima leitura?"
                                } else {
                                    "Olá! Qual será a próxima leitura?"
                                }

                                Text(
                                    text = saudacao,
                                    style = MaterialTheme.typography.headlineMedium,
                                    color = MaterialTheme.colorScheme.onBackground,
                                    modifier = Modifier.semantics { heading() },
                                )
                            }

                            // 2. Atalho de Busca
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = Space.s5),
                            ) {
                                Surface(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(56.dp)
                                        .clip(MaterialTheme.shapes.medium)
                                        .border(
                                            width = 1.dp,
                                            color = MaterialTheme.colorScheme.outline,
                                            shape = MaterialTheme.shapes.medium,
                                        )
                                        .clickable(onClick = onAtalhoBuscaClick),
                                    color = MaterialTheme.colorScheme.surface,
                                ) {
                                    Row(
                                        modifier = Modifier.padding(horizontal = Space.s4),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(Space.s3),
                                    ) {
                                        Icon(
                                            imageVector = Icons.Outlined.Search,
                                            contentDescription = null,
                                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                            modifier = Modifier.size(24.dp),
                                        )
                                        Text(
                                            text = "Buscar título, autor ou ISBN",
                                            style = MaterialTheme.typography.bodyLarge,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                                        )
                                    }
                                }
                            }

                            // 3. Seção Reserva Ativa
                            if (uiState.reservaAtiva != null) {
                                val res = uiState.reservaAtiva
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(horizontal = Space.s5),
                                    verticalArrangement = Arrangement.spacedBy(Space.s2),
                                ) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically,
                                    ) {
                                        Text(
                                            text = "Sua reserva ativa",
                                            style = MaterialTheme.typography.titleLarge,
                                            color = MaterialTheme.colorScheme.onBackground,
                                            modifier = Modifier.semantics { heading() },
                                        )
                                        TextButton(onClick = onVerTodasReservasClick) {
                                            Text(
                                                text = "Ver todas",
                                                style = AppTextStyles.button,
                                                color = MaterialTheme.colorScheme.primary,
                                            )
                                        }
                                    }

                                    CartaoReserva(
                                        prazoTexto = uiState.prazoTextoReserva,
                                        prazoUrgente = uiState.prazoUrgenteReserva,
                                        tituloLivro = res.livroTitulo,
                                        autorLivro = res.livroAutor,
                                        codigoRetirada = res.codigoRetirada,
                                        localBiblioteca = res.localBiblioteca,
                                        statusBiblioteca = res.statusBiblioteca,
                                        capaUrl = res.capaUrl,
                                        expirada = false,
                                        copiarHabilitado = true,
                                        onCartaoClick = { onReservaClick(res.id) },
                                        onCopiarCodigoClick = { cod ->
                                            val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                                            val clip = ClipData.newPlainText("Código de Retirada", cod)
                                            clipboard.setPrimaryClip(clip)
                                            Toast.makeText(context, "Código copiado.", Toast.LENGTH_SHORT).show()
                                        },
                                    )
                                }
                            }

                            // 4. Seção Livros em Destaque
                            if (uiState.livrosDestaque.isNotEmpty()) {
                                Column(
                                    modifier = Modifier.fillMaxWidth(),
                                    verticalArrangement = Arrangement.spacedBy(Space.s3),
                                ) {
                                    Text(
                                        text = "Livros em destaque",
                                        style = MaterialTheme.typography.titleLarge,
                                        color = MaterialTheme.colorScheme.onBackground,
                                        modifier = Modifier
                                            .padding(horizontal = Space.s5)
                                            .semantics { heading() },
                                    )

                                    LazyRow(
                                        contentPadding = PaddingValues(horizontal = Space.s5),
                                        horizontalArrangement = Arrangement.spacedBy(Space.s4),
                                    ) {
                                        items(
                                            items = uiState.livrosDestaque,
                                            key = { "dest_${it.volumeId}" },
                                        ) { livro ->
                                            CartaoDestaqueItem(
                                                livro = livro,
                                                onLivroClick = { onLivroClick(livro.volumeId) },
                                            )
                                        }
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(Space.s6))
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun HomeSkeletonLoading(modifier: Modifier = Modifier) {
    val transition = rememberInfiniteTransition(label = "shimmer")
    val translateAnim by transition.animateFloat(
        initialValue = 0f,
        targetValue = 1000f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1200, easing = LinearEasing),
            repeatMode = RepeatMode.Restart,
        ),
        label = "shimmer_float",
    )

    val shimmerBrush = Brush.linearGradient(
        colors = listOf(
            MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f),
            MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.2f),
            MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f),
        ),
        start = Offset(translateAnim - 500f, translateAnim - 500f),
        end = Offset(translateAnim, translateAnim),
    )

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = Space.s5, vertical = Space.s3),
        verticalArrangement = Arrangement.spacedBy(Space.s5),
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(Space.s2)) {
            Box(
                modifier = Modifier
                    .width(120.dp)
                    .height(24.dp)
                    .clip(MaterialTheme.shapes.small)
                    .background(shimmerBrush),
            )
            Box(
                modifier = Modifier
                    .fillMaxWidth(0.8f)
                    .height(32.dp)
                    .clip(MaterialTheme.shapes.small)
                    .background(shimmerBrush),
            )
        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
                .clip(MaterialTheme.shapes.medium)
                .background(shimmerBrush),
        )

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(180.dp)
                .clip(MaterialTheme.shapes.medium)
                .background(shimmerBrush),
        )

        Column(verticalArrangement = Arrangement.spacedBy(Space.s3)) {
            Box(
                modifier = Modifier
                    .width(180.dp)
                    .height(24.dp)
                    .clip(MaterialTheme.shapes.small)
                    .background(shimmerBrush),
            )
            Row(horizontalArrangement = Arrangement.spacedBy(Space.s4)) {
                repeat(3) {
                    Column(
                        modifier = Modifier.width(132.dp),
                        verticalArrangement = Arrangement.spacedBy(Space.s2),
                    ) {
                        Box(
                            modifier = Modifier
                                .width(132.dp)
                                .height(198.dp)
                                .clip(MaterialTheme.shapes.medium)
                                .background(shimmerBrush),
                        )
                        Box(
                            modifier = Modifier
                                .fillMaxWidth(0.8f)
                                .height(16.dp)
                                .clip(MaterialTheme.shapes.small)
                                .background(shimmerBrush),
                        )
                        Box(
                            modifier = Modifier
                                .fillMaxWidth(0.5f)
                                .height(14.dp)
                                .clip(MaterialTheme.shapes.small)
                                .background(shimmerBrush),
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun CartaoDestaqueItem(
    livro: LivroResumo,
    onLivroClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .width(132.dp)
            .clickable(onClick = onLivroClick),
        verticalArrangement = Arrangement.spacedBy(Space.s2),
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
                    .width(132.dp)
                    .height(198.dp)
                    .clip(MaterialTheme.shapes.medium)
                    .border(
                        width = 1.dp,
                        color = MaterialTheme.colorScheme.outlineVariant,
                        shape = MaterialTheme.shapes.medium,
                    ),
                error = {
                    CapaSubstituta(
                        titulo = livro.titulo,
                        autor = livro.autores.joinToString(", "),
                        modifier = Modifier
                            .width(132.dp)
                            .height(198.dp),
                    )
                },
                loading = {
                    CapaSubstituta(
                        titulo = livro.titulo,
                        autor = livro.autores.joinToString(", "),
                        modifier = Modifier
                            .width(132.dp)
                            .height(198.dp),
                    )
                },
            )
        } else {
            CapaSubstituta(
                titulo = livro.titulo,
                autor = livro.autores.joinToString(", "),
                modifier = Modifier
                    .width(132.dp)
                    .height(198.dp),
            )
        }

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

        SeloDisponibilidade(
            disponivel = livro.disponivel,
            textoPersonalizado = if (livro.disponivel) "${livro.exemplaresDisponiveis} disponíveis" else "Indisponível",
        )
    }
}

@Preview(name = "Home Loading Skeleton - Claro", showBackground = true)
@Composable
private fun HomeSkeletonLoadingLightPreview() {
    AcerviaTheme(darkTheme = false) {
        HomeContent(
            uiState = HomeUiState(isLoading = true),
            onRefresh = {},
            onAtalhoBuscaClick = {},
            onVerTodasReservasClick = {},
            onReservaClick = {},
            onLivroClick = {},
            onTentarNovamente = {},
        )
    }
}

@Preview(name = "Home - Claro", showBackground = true)
@Composable
private fun HomeScreenLightPreview() {
    AcerviaTheme(darkTheme = false) {
        HomeContent(
            uiState = HomeUiState(
                isLoading = false,
                nomeUsuario = "Marina",
                reservaAtiva = Reserva(
                    id = "r1",
                    usuarioId = "u1",
                    volumeId = "v1",
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
                prazoTextoReserva = "Retire até amanhã, 10/10 · 18h",
                prazoUrgenteReserva = true,
                livrosDestaque = listOf(
                    LivroResumo("1", "123", "A Biblioteca da Meia-Noite", listOf("Matt Haig"), null, 3, 3),
                    LivroResumo("2", "456", "1984", listOf("George Orwell"), null, 5, 2),
                ),
            ),
            onRefresh = {},
            onAtalhoBuscaClick = {},
            onVerTodasReservasClick = {},
            onReservaClick = {},
            onLivroClick = {},
            onTentarNovamente = {},
        )
    }
}

@Preview(name = "Home - Escuro", uiMode = Configuration.UI_MODE_NIGHT_YES, showBackground = true)
@Composable
private fun HomeScreenDarkPreview() {
    AcerviaTheme(darkTheme = true) {
        HomeContent(
            uiState = HomeUiState(
                isLoading = false,
                nomeUsuario = "Marina",
                reservaAtiva = null,
                livrosDestaque = listOf(
                    LivroResumo("1", "123", "A Biblioteca da Meia-Noite", listOf("Matt Haig"), null, 3, 3),
                    LivroResumo("2", "456", "1984", listOf("George Orwell"), null, 5, 2),
                ),
            ),
            onRefresh = {},
            onAtalhoBuscaClick = {},
            onVerTodasReservasClick = {},
            onReservaClick = {},
            onLivroClick = {},
            onTentarNovamente = {},
        )
    }
}
