package com.example.acervia.ui.screens.auth

import android.content.res.Configuration
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.acervia.ui.theme.AcerviaTheme
import com.example.acervia.ui.theme.AppTextStyles
import com.example.acervia.ui.theme.Space

@Composable
fun FaltaPoucoScreen(
    viewModel: FaltaPoucoViewModel,
    onVoltarClick: () -> Unit,
    onCadastroConcluido: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val uiState by viewModel.uiState.collectAsState()

    FaltaPoucoContent(
        uiState = uiState,
        onNomeChange = viewModel::onNomeChange,
        onSobrenomeChange = viewModel::onSobrenomeChange,
        onDataNascimentoChange = viewModel::onDataNascimentoChange,
        onTermosAceitosChange = viewModel::onTermosAceitosChange,
        onConcluirClick = { viewModel.onConcluirClick(onCadastroConcluido) },
        onVoltarClick = onVoltarClick,
        modifier = modifier,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FaltaPoucoContent(
    uiState: FaltaPoucoUiState,
    onNomeChange: (String) -> Unit,
    onSobrenomeChange: (String) -> Unit,
    onDataNascimentoChange: (String) -> Unit,
    onTermosAceitosChange: (Boolean) -> Unit,
    onConcluirClick: () -> Unit,
    onVoltarClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val keyboardController = LocalSoftwareKeyboardController.current

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = {},
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
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = Space.s5)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(Space.s4),
            horizontalAlignment = Alignment.Start,
        ) {
            Spacer(modifier = Modifier.height(Space.s2))

            // 1. Título e E-mail
            Column(verticalArrangement = Arrangement.spacedBy(Space.s2)) {
                Text(
                    text = "Falta pouco",
                    style = MaterialTheme.typography.headlineMedium,
                    color = MaterialTheme.colorScheme.onBackground,
                    modifier = Modifier.semantics { heading() },
                )
                Text(
                    text = "Sua conta é ${uiState.email.ifBlank { "seu e-mail" }}.",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
                TextButton(onClick = onVoltarClick) {
                    Text(text = "Usar outra conta", style = AppTextStyles.button)
                }
            }

            Spacer(modifier = Modifier.height(Space.s2))

            // 2. Campo Nome
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(Space.s1),
            ) {
                Text(
                    text = "Nome",
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.onSurface,
                )
                OutlinedTextField(
                    value = uiState.nome,
                    onValueChange = onNomeChange,
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(min = 56.dp),
                    placeholder = { Text("Ex: Marina") },
                    isError = uiState.erroNome != null,
                    singleLine = true,
                    shape = MaterialTheme.shapes.medium,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = MaterialTheme.colorScheme.primary,
                        unfocusedBorderColor = MaterialTheme.colorScheme.outline,
                        focusedContainerColor = MaterialTheme.colorScheme.surface,
                        unfocusedContainerColor = MaterialTheme.colorScheme.surface,
                    ),
                    keyboardOptions = KeyboardOptions(
                        capitalization = KeyboardCapitalization.Words,
                        imeAction = ImeAction.Next,
                    ),
                )
                if (uiState.erroNome != null) {
                    Text(
                        text = uiState.erroNome,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.error,
                    )
                } else {
                    Text(
                        text = "É como vamos te chamar no app.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            }

            // 3. Campo Sobrenome
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(Space.s1),
            ) {
                Text(
                    text = "Sobrenome",
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.onSurface,
                )
                OutlinedTextField(
                    value = uiState.sobrenome,
                    onValueChange = onSobrenomeChange,
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(min = 56.dp),
                    placeholder = { Text("Ex: Silva") },
                    isError = uiState.erroSobrenome != null,
                    singleLine = true,
                    shape = MaterialTheme.shapes.medium,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = MaterialTheme.colorScheme.primary,
                        unfocusedBorderColor = MaterialTheme.colorScheme.outline,
                        focusedContainerColor = MaterialTheme.colorScheme.surface,
                        unfocusedContainerColor = MaterialTheme.colorScheme.surface,
                    ),
                    keyboardOptions = KeyboardOptions(
                        capitalization = KeyboardCapitalization.Words,
                        imeAction = ImeAction.Next,
                    ),
                )
                if (uiState.erroSobrenome != null) {
                    Text(
                        text = uiState.erroSobrenome,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.error,
                    )
                }
            }

            // 4. Campo Data de Nascimento
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(Space.s1),
            ) {
                Text(
                    text = "Data de nascimento",
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.onSurface,
                )
                OutlinedTextField(
                    value = uiState.dataNascimento,
                    onValueChange = onDataNascimentoChange,
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(min = 56.dp),
                    placeholder = { Text("DD/MM/AAAA") },
                    isError = uiState.erroDataNascimento != null,
                    singleLine = true,
                    shape = MaterialTheme.shapes.medium,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = MaterialTheme.colorScheme.primary,
                        unfocusedBorderColor = MaterialTheme.colorScheme.outline,
                        focusedContainerColor = MaterialTheme.colorScheme.surface,
                        unfocusedContainerColor = MaterialTheme.colorScheme.surface,
                    ),
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Number,
                        imeAction = ImeAction.Done,
                    ),
                    keyboardActions = KeyboardActions(onDone = {
                        keyboardController?.hide()
                        onConcluirClick()
                    }),
                )
                if (uiState.erroDataNascimento != null) {
                    Text(
                        text = uiState.erroDataNascimento,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.error,
                    )
                } else {
                    Text(
                        text = "Necessário para validar a idade mínima (14 anos).",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            }

            // 5. Aceite de Termos
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onTermosAceitosChange(!uiState.termosAceitos) },
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(Space.s2),
            ) {
                Checkbox(
                    checked = uiState.termosAceitos,
                    onCheckedChange = onTermosAceitosChange,
                )
                Text(
                    text = "Li e aceito os Termos de Uso e a Política de Privacidade.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface,
                )
            }
            if (uiState.erroTermos != null) {
                Text(
                    text = uiState.erroTermos,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.error,
                )
            }

            if (uiState.erroGeral != null) {
                Text(
                    text = uiState.erroGeral,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.error,
                )
            }

            Spacer(modifier = Modifier.height(Space.s2))

            // 6. Botão Concluir
            Button(
                onClick = onConcluirClick,
                enabled = !uiState.isLoading,
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = 56.dp),
                shape = MaterialTheme.shapes.medium,
            ) {
                if (uiState.isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        color = MaterialTheme.colorScheme.onPrimary,
                    )
                } else {
                    Text(text = "Concluir", style = AppTextStyles.button)
                }
            }

            Spacer(modifier = Modifier.height(Space.s6))
        }
    }
}

@Preview(name = "Falta Pouco - Claro", showBackground = true)
@Composable
private fun FaltaPoucoScreenLightPreview() {
    AcerviaTheme(darkTheme = false) {
        FaltaPoucoContent(
            uiState = FaltaPoucoUiState(email = "marina@exemplo.com"),
            onNomeChange = {},
            onSobrenomeChange = {},
            onDataNascimentoChange = {},
            onTermosAceitosChange = {},
            onConcluirClick = {},
            onVoltarClick = {},
        )
    }
}

@Preview(name = "Falta Pouco - Escuro", uiMode = Configuration.UI_MODE_NIGHT_YES, showBackground = true)
@Composable
private fun FaltaPoucoScreenDarkPreview() {
    AcerviaTheme(darkTheme = true) {
        FaltaPoucoContent(
            uiState = FaltaPoucoUiState(email = "marina@exemplo.com"),
            onNomeChange = {},
            onSobrenomeChange = {},
            onDataNascimentoChange = {},
            onTermosAceitosChange = {},
            onConcluirClick = {},
            onVoltarClick = {},
        )
    }
}
