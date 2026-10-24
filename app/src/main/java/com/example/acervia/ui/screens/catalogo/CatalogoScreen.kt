package com.example.acervia.ui.screens.catalogo

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
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Check
import androidx.compose.material.icons.outlined.Clear
import androidx.compose.material.icons.outlined.FilterList
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.semantics.LiveRegionMode
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.liveRegion
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.SubcomposeAsyncImage
import coil.request.ImageRequest
import com.example.acervia.domain.model.LivroResumo
import com.example.acervia.domain.repository.Ordem
import com.example.acervia.ui.components.CapaSubstituta
import com.example.acervia.ui.components.SeloDisponibilidade
import com.example.acervia.ui.theme.AcerviaTheme
import com.example.acervia.ui.theme.AppTextStyles
import com.example.acervia.ui.theme.Space

@Composable
fun CatalogoScreen(
    viewModel: CatalogoViewModel,
    onLivroClick: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    val uiState by viewModel.uiState.collectAsState()

    CatalogoContent(
        uiState = uiState,
        onQueryChange = viewModel::onQueryChange,
        onLimparQuery = viewModel::onLimparQuery,
        onSoDisponiveisToggle = viewModel::onSoDisponiveisToggle,
        onOrdemSelecionada = viewModel::onOrdemSelecionada,
        onMostrarBottomSheetOrdenacao = viewModel::onMostrarBottomSheetOrdenacao,
        onLimparFiltros = viewModel::onLimparFiltros,
        onTentarNovamente = viewModel::carregarLivros,
        onLivroClick = onLivroClick,
        modifier = modifier,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CatalogoContent(
    uiState: CatalogoUiState,
    onQueryChange: (String) -> Unit,
    onLimparQuery: () -> Unit,
    onSoDisponiveisToggle: (Boolean) -> Unit,
    onOrdemSelecionada: (Ordem) -> Unit,
    onMostrarBottomSheetOrdenacao: (Boolean) -> Unit,
    onLimparFiltros: () -> Unit,
    onTentarNovamente: () -> Unit,
    onLivroClick: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    val keyboardController = LocalSoftwareKeyboardController.current

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

            // 1. Título
            Text(
                text = "Catálogo",
                style = MaterialTheme.typography.headlineMedium,
                color = MaterialTheme.colorScheme.onBackground,
                modifier = Modifier.semantics { heading() },
            )

            // 2. Campo de Busca
            OutlinedTextField(
                value = uiState.query,
                onValueChange = onQueryChange,
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = 56.dp),
                placeholder = {
                    Text(
                        text = "Buscar título, autor ou ISBN",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Outlined.Search,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(24.dp),
                    )
                },
                trailingIcon = {
                    if (uiState.query.isNotEmpty()) {
                        IconButton(onClick = onLimparQuery) {
                            Icon(
                                imageVector = Icons.Outlined.Clear,
                                contentDescription = "Limpar busca",
                                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                            )
                        }
                    }
                },
                singleLine = true,
                shape = MaterialTheme.shapes.medium,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                    unfocusedBorderColor = MaterialTheme.colorScheme.outline,
                    focusedContainerColor = MaterialTheme.colorScheme.surface,
                    unfocusedContainerColor = MaterialTheme.colorScheme.surface,
                ),
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                keyboardActions = KeyboardActions(onSearch = { keyboardController?.hide() }),
            )

            // 3. Linha de Filtros
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(Space.s2),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                FilterChip(
                    selected = uiState.soDisponiveis,
                    onClick = { onSoDisponiveisToggle(!uiState.soDisponiveis) },
                    label = { Text("Só disponíveis") },
                    leadingIcon = if (uiState.soDisponiveis) {
                        {
                            Icon(
                                imageVector = Icons.Outlined.Check,
                                contentDescription = null,
                                modifier = Modifier.size(18.dp),
                            )
                        }
                    } else null,
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                        selectedLabelColor = MaterialTheme.colorScheme.onPrimaryContainer,
                    ),
                )

                FilterChip(
                    selected = uiState.ordem != Ordem.TITULO,
                    onClick = { onMostrarBottomSheetOrdenacao(true) },
                    label = { Text("Ordenar: ${uiState.ordem.rotuloFormatado}") },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Outlined.FilterList,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp),
                        )
                    },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                        selectedLabelColor = MaterialTheme.colorScheme.onPrimaryContainer,
                    ),
                )
            }

            // 4. Contagem de Resultados
            if (!uiState.isLoading && uiState.erro == null) {
                val textoContagem = if (uiState.totalItens == 1) {
                    "1 livro"
                } else {
                    "${uiState.totalItens} livros"
                }
                Text(
                    text = textoContagem,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.semantics { liveRegion = LiveRegionMode.Polite },
                )
            }

            // 5. Lista / Estados
            Box(
                modifier = Modifier.fillMaxSize(),
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

                    uiState.livros.isEmpty() -> {
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(Space.s4),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center,
                        ) {
                            val mensagem = if (uiState.query.isNotBlank()) {
                                "Nenhum livro encontrado para \"${uiState.query}\"."
                            } else {
                                "Nenhum livro encontrado."
                            }
                            Text(
                                text = mensagem,
                                style = MaterialTheme.typography.bodyLarge,
                                color = MaterialTheme.colorScheme.onSurface,
                            )
                            Spacer(modifier = Modifier.height(Space.s1))
                            Text(
                                text = "Confira a escrita ou busque pelo autor.",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                            )
                            if (uiState.soDisponiveis || uiState.query.isNotBlank()) {
                                Spacer(modifier = Modifier.height(Space.s4))
                                OutlinedButton(onClick = onLimparFiltros) {
                                    Text("Limpar filtros")
                                }
                            }
                        }
                    }

                    else -> {
                        LazyColumn(
                            modifier = Modifier.fillMaxSize(),
                            verticalArrangement = Arrangement.spacedBy(Space.s3),
                        ) {
                            items(
                                items = uiState.livros,
                                key = { it.volumeId },
                            ) { livro ->
                                ItemLivroCatalogo(
                                    livro = livro,
                                    onLivroClick = onLivroClick,
                                )
                                HorizontalDivider(
                                    color = MaterialTheme.colorScheme.outlineVariant,
                                    modifier = Modifier.padding(top = Space.s3),
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    // Modal BottomSheet de Ordenação
    if (uiState.mostrarBottomSheetOrdenacao) {
        ModalBottomSheet(
            onDismissRequest = { onMostrarBottomSheetOrdenacao(false) },
            sheetState = rememberModalBottomSheetState(),
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(Space.s5)
                    .selectableGroup(),
                verticalArrangement = Arrangement.spacedBy(Space.s2),
            ) {
                Text(
                    text = "Ordenar por",
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.onSurface,
                )
                Spacer(modifier = Modifier.height(Space.s2))

                Ordem.entries.forEach { opcaoOrdem ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(min = 48.dp)
                            .clickable { onOrdemSelecionada(opcaoOrdem) },
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(Space.s3),
                    ) {
                        RadioButton(
                            selected = uiState.ordem == opcaoOrdem,
                            onClick = null,
                            colors = RadioButtonDefaults.colors(selectedColor = MaterialTheme.colorScheme.primary),
                        )
                        Text(
                            text = opcaoOrdem.rotuloFormatado,
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onSurface,
                        )
                    }
                }
                Spacer(modifier = Modifier.height(Space.s4))
            }
        }
    }
}

@Composable
private fun ItemLivroCatalogo(
    livro: LivroResumo,
    onLivroClick: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clickable { onLivroClick(livro.volumeId) }
            .padding(vertical = Space.s2),
        horizontalArrangement = Arrangement.spacedBy(Space.s3),
        verticalAlignment = Alignment.CenterVertically,
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
                    .width(52.dp)
                    .height(78.dp)
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
                            .width(52.dp)
                            .height(78.dp),
                    )
                },
                loading = {
                    CapaSubstituta(
                        titulo = livro.titulo,
                        autor = livro.autores.joinToString(", "),
                        modifier = Modifier
                            .width(52.dp)
                            .height(78.dp),
                    )
                },
            )
        } else {
            CapaSubstituta(
                titulo = livro.titulo,
                autor = livro.autores.joinToString(", "),
                modifier = Modifier
                    .width(52.dp)
                    .height(78.dp),
            )
        }

        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(Space.s1),
        ) {
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
            Spacer(modifier = Modifier.height(Space.s1))
            SeloDisponibilidade(
                disponivel = livro.disponivel,
                textoPersonalizado = if (livro.disponivel) "${livro.exemplaresDisponiveis} disponíveis" else "Indisponível",
            )
        }
    }
}

private val Ordem.rotuloFormatado: String
    get() = when (this) {
        Ordem.TITULO -> "A–Z"
        Ordem.MAIS_RESERVADOS -> "Mais reservados"
        Ordem.MAIS_RECENTES -> "Mais recentes"
        Ordem.RELEVANCIA -> "Relevância"
    }

@Preview(name = "Catálogo - Claro", showBackground = true)
@Composable
private fun CatalogoScreenLightPreview() {
    AcerviaTheme(darkTheme = false) {
        CatalogoContent(
            uiState = CatalogoUiState(
                isLoading = false,
                livros = listOf(
                    LivroResumo("1", "123", "A Biblioteca da Meia-Noite", listOf("Matt Haig"), null, 3, 3),
                    LivroResumo("2", "456", "1984", listOf("George Orwell"), null, 5, 2),
                    LivroResumo("3", "789", "O Sol É Para Todos", listOf("Harper Lee"), null, 2, 0),
                ),
                totalItens = 3,
            ),
            onQueryChange = {},
            onLimparQuery = {},
            onSoDisponiveisToggle = {},
            onOrdemSelecionada = {},
            onMostrarBottomSheetOrdenacao = {},
            onLimparFiltros = {},
            onTentarNovamente = {},
            onLivroClick = {},
        )
    }
}

@Preview(name = "Catálogo - Escuro", uiMode = Configuration.UI_MODE_NIGHT_YES, showBackground = true)
@Composable
private fun CatalogoScreenDarkPreview() {
    AcerviaTheme(darkTheme = true) {
        CatalogoContent(
            uiState = CatalogoUiState(
                isLoading = false,
                livros = listOf(
                    LivroResumo("1", "123", "A Biblioteca da Meia-Noite", listOf("Matt Haig"), null, 3, 3),
                    LivroResumo("2", "456", "1984", listOf("George Orwell"), null, 5, 2),
                    LivroResumo("3", "789", "O Sol É Para Todos", listOf("Harper Lee"), null, 2, 0),
                ),
                totalItens = 3,
            ),
            onQueryChange = {},
            onLimparQuery = {},
            onSoDisponiveisToggle = {},
            onOrdemSelecionada = {},
            onMostrarBottomSheetOrdenacao = {},
            onLimparFiltros = {},
            onTentarNovamente = {},
            onLivroClick = {},
        )
    }
}
