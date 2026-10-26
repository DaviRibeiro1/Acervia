package com.example.acervia.ui.screens.ajustes

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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.KeyboardArrowRight
import androidx.compose.material.icons.automirrored.outlined.Logout
import androidx.compose.material.icons.outlined.BugReport
import androidx.compose.material.icons.outlined.DarkMode
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.Language
import androidx.compose.material.icons.outlined.WifiOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.acervia.ui.theme.AcerviaTheme
import com.example.acervia.ui.theme.AppTextStyles
import com.example.acervia.ui.theme.Space

@Composable
fun AjustesScreen(
    viewModel: AjustesViewModel,
    onLogoutConcluido: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val uiState by viewModel.uiState.collectAsState()

    AjustesContent(
        uiState = uiState,
        onSimularErroToggle = viewModel::onSimularErroToggle,
        onSimularSemConexaoToggle = viewModel::onSimularSemConexaoToggle,
        onMostrarBottomSheetAparencia = viewModel::onMostrarBottomSheetAparencia,
        onTemaSelecionado = viewModel::onTemaSelecionado,
        onMostrarBottomSheetSair = viewModel::onMostrarBottomSheetSair,
        onConfirmarSairClick = { viewModel.onConfirmarSairClick(onLogoutConcluido) },
        modifier = modifier,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AjustesContent(
    uiState: AjustesUiState,
    onSimularErroToggle: (Boolean) -> Unit,
    onSimularSemConexaoToggle: (Boolean) -> Unit,
    onMostrarBottomSheetAparencia: (Boolean) -> Unit,
    onTemaSelecionado: (String) -> Unit,
    onMostrarBottomSheetSair: (Boolean) -> Unit,
    onConfirmarSairClick: () -> Unit,
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
                .padding(horizontal = Space.s5)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(Space.s4),
        ) {
            Spacer(modifier = Modifier.height(Space.s2))

            // Título
            Text(
                text = "Ajustes",
                style = MaterialTheme.typography.headlineMedium,
                color = MaterialTheme.colorScheme.onBackground,
                modifier = Modifier.semantics { heading() },
            )

            // 1. Cartão da Conta
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
                Row(
                    modifier = Modifier.padding(Space.s4),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(Space.s3),
                ) {
                    Surface(
                        modifier = Modifier.size(56.dp),
                        shape = CircleShape,
                        color = MaterialTheme.colorScheme.primaryContainer,
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Text(
                                text = uiState.iniciais,
                                style = MaterialTheme.typography.titleLarge,
                                color = MaterialTheme.colorScheme.onPrimaryContainer,
                            )
                        }
                    }

                    Column(
                        modifier = Modifier.weight(1f),
                        verticalArrangement = Arrangement.spacedBy(Space.s1),
                    ) {
                        Text(
                            text = uiState.nomeUsuario,
                            style = AppTextStyles.button,
                            color = MaterialTheme.colorScheme.onSurface,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                        )
                        Text(
                            text = uiState.emailUsuario,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                        )
                    }
                }
            }

            // 2. Seção Aplicativo
            Column(verticalArrangement = Arrangement.spacedBy(Space.s2)) {
                Text(
                    text = "Aplicativo",
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
                    Column {
                        ItemOpcaoAjustes(
                            icone = Icons.Outlined.DarkMode,
                            titulo = "Aparência",
                            subtitulo = uiState.temaAtual,
                            onClick = { onMostrarBottomSheetAparencia(true) },
                        )
                        HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
                        ItemOpcaoAjustes(
                            icone = Icons.Outlined.Language,
                            titulo = "Idioma",
                            subtitulo = "Português (Brasil)",
                            onClick = {},
                        )
                        HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
                        ItemOpcaoAjustes(
                            icone = Icons.Outlined.Info,
                            titulo = "Sobre o Acervia",
                            subtitulo = "Versão 0.1.0 · Projeto Acadêmico",
                            onClick = {},
                        )
                    }
                }
            }

            // 3. Seção Modo de Demonstração
            Column(verticalArrangement = Arrangement.spacedBy(Space.s2)) {
                Text(
                    text = "Modo de Demonstração",
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
                    Column {
                        ItemSwitchAjustes(
                            icone = Icons.Outlined.BugReport,
                            titulo = "Simular erro de servidor",
                            ativo = uiState.simularErro,
                            onToggle = onSimularErroToggle,
                        )
                        HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
                        ItemSwitchAjustes(
                            icone = Icons.Outlined.WifiOff,
                            titulo = "Simular sem conexão",
                            ativo = uiState.simularSemConexao,
                            onToggle = onSimularSemConexaoToggle,
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(Space.s2))

            // 4. Botão Sair da Conta
            TextButton(
                onClick = { onMostrarBottomSheetSair(true) },
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = 48.dp),
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Outlined.Logout,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.error,
                    modifier = Modifier.size(20.dp),
                )
                Spacer(modifier = Modifier.width(Space.s2))
                Text(
                    text = "Sair da conta",
                    style = AppTextStyles.button,
                    color = MaterialTheme.colorScheme.error,
                )
            }

            Spacer(modifier = Modifier.height(Space.s6))
        }
    }

    // Modal BottomSheet de Aparência
    if (uiState.mostrarBottomSheetAparencia) {
        ModalBottomSheet(
            onDismissRequest = { onMostrarBottomSheetAparencia(false) },
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
                    text = "Escolha a aparência",
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.onSurface,
                )
                Spacer(modifier = Modifier.height(Space.s2))

                listOf("Claro", "Escuro", "Sistema").forEach { opcao ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(min = 48.dp)
                            .clickable { onTemaSelecionado(opcao) },
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(Space.s3),
                    ) {
                        RadioButton(
                            selected = uiState.temaAtual == opcao,
                            onClick = null,
                            colors = RadioButtonDefaults.colors(selectedColor = MaterialTheme.colorScheme.primary),
                        )
                        Text(
                            text = opcao,
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onSurface,
                        )
                    }
                }
                Spacer(modifier = Modifier.height(Space.s4))
            }
        }
    }

    // Modal BottomSheet de Confirmação de Logout
    if (uiState.mostrarBottomSheetSair) {
        ModalBottomSheet(
            onDismissRequest = { if (!uiState.isSaindo) onMostrarBottomSheetSair(false) },
            sheetState = rememberModalBottomSheetState(),
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(Space.s5),
                verticalArrangement = Arrangement.spacedBy(Space.s4),
            ) {
                Text(
                    text = "Sair da conta?",
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.onSurface,
                )

                Text(
                    text = "Você precisará entrar de novo para acessar suas reservas e seu perfil.",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )

                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(Space.s2),
                ) {
                    Button(
                        onClick = { if (!uiState.isSaindo) onMostrarBottomSheetSair(false) },
                        enabled = !uiState.isSaindo,
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(min = 56.dp),
                        shape = MaterialTheme.shapes.medium,
                    ) {
                        Text(text = "Permanecer conectado", style = AppTextStyles.button)
                    }

                    OutlinedButton(
                        onClick = onConfirmarSairClick,
                        enabled = !uiState.isSaindo,
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(min = 56.dp),
                        shape = MaterialTheme.shapes.medium,
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = MaterialTheme.colorScheme.error,
                        ),
                    ) {
                        if (uiState.isSaindo) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(24.dp),
                                color = MaterialTheme.colorScheme.error,
                            )
                        } else {
                            Text(
                                text = "Sair da conta",
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
private fun ItemOpcaoAjustes(
    icone: ImageVector,
    titulo: String,
    subtitulo: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(Space.s4),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(Space.s3),
    ) {
        Icon(
            imageVector = icone,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(24.dp),
        )
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = titulo,
                style = AppTextStyles.button,
                color = MaterialTheme.colorScheme.onSurface,
            )
            Text(
                text = subtitulo,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
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

@Composable
private fun ItemSwitchAjustes(
    icone: ImageVector,
    titulo: String,
    ativo: Boolean,
    onToggle: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = Space.s4, vertical = Space.s2),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(Space.s3),
    ) {
        Icon(
            imageVector = icone,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(24.dp),
        )
        Text(
            text = titulo,
            style = AppTextStyles.button,
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.weight(1f),
        )
        Switch(
            checked = ativo,
            onCheckedChange = onToggle,
        )
    }
}

@Preview(name = "Ajustes - Claro", showBackground = true)
@Composable
private fun AjustesScreenLightPreview() {
    AcerviaTheme(darkTheme = false) {
        AjustesContent(
            uiState = AjustesUiState(),
            onSimularErroToggle = {},
            onSimularSemConexaoToggle = {},
            onMostrarBottomSheetAparencia = {},
            onTemaSelecionado = {},
            onMostrarBottomSheetSair = {},
            onConfirmarSairClick = {},
        )
    }
}

@Preview(name = "Ajustes - Escuro", uiMode = Configuration.UI_MODE_NIGHT_YES, showBackground = true)
@Composable
private fun AjustesScreenDarkPreview() {
    AcerviaTheme(darkTheme = true) {
        AjustesContent(
            uiState = AjustesUiState(),
            onSimularErroToggle = {},
            onSimularSemConexaoToggle = {},
            onMostrarBottomSheetAparencia = {},
            onTemaSelecionado = {},
            onMostrarBottomSheetSair = {},
            onConfirmarSairClick = {},
        )
    }
}
