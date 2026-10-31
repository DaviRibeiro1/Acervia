package com.example.acervia.ui.screens.auth

import android.content.res.Configuration
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.ExperimentalTextApi
import androidx.compose.ui.text.VerbatimTtsAnnotation
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withAnnotation
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.acervia.ui.theme.AcerviaTheme
import com.example.acervia.ui.theme.AppTextStyles
import com.example.acervia.ui.theme.Space

@Composable
fun CodigoAcessoScreen(
    viewModel: CodigoAcessoViewModel,
    onVoltarClick: () -> Unit,
    onLoginSucesso: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
) {
    val uiState by viewModel.uiState.collectAsState()

    CodigoAcessoContent(
        uiState = uiState,
        onCodigoChange = { novo -> viewModel.onCodigoChange(novo, onLoginSucesso) },
        onEntrarClick = { viewModel.onEntrarClick(onLoginSucesso) },
        onReenviarCodigoClick = viewModel::onReenviarCodigoClick,
        onVoltarClick = onVoltarClick,
        modifier = modifier,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CodigoAcessoContent(
    uiState: CodigoAcessoUiState,
    onCodigoChange: (String) -> Unit,
    onEntrarClick: () -> Unit,
    onReenviarCodigoClick: () -> Unit,
    onVoltarClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
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
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Spacer(modifier = Modifier.height(Space.s2))

            // 1. Título e E-mail
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(Space.s2),
            ) {
                Text(
                    text = "Digite o código",
                    style = MaterialTheme.typography.headlineMedium,
                    color = MaterialTheme.colorScheme.onBackground,
                    modifier = Modifier.semantics { heading() },
                )
                Text(
                    text = "Enviamos um código de 6 dígitos para ${uiState.email.ifBlank { "seu e-mail" }}.",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
                TextButton(
                    onClick = onVoltarClick,
                    modifier = Modifier.align(Alignment.Start),
                ) {
                    Text(text = "Usar outro e-mail", style = AppTextStyles.button)
                }
            }

            Spacer(modifier = Modifier.height(Space.s2))

            // 2. Caixas de OTP
            CaixasOtpInput(
                codigo = uiState.codigo,
                onCodigoChange = onCodigoChange,
                isError = uiState.erro != null,
            )

            if (uiState.erro != null) {
                Text(
                    text = uiState.erro,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.error,
                    textAlign = TextAlign.Center,
                )
            }

            // 3. Contador de Reenvio
            if (uiState.podeReenviar) {
                TextButton(onClick = onReenviarCodigoClick) {
                    Text(text = "Reenviar código", style = AppTextStyles.button)
                }
            } else {
                val segFmt = String.format(java.util.Locale.getDefault(), "%02d", uiState.tempoReenvioRestante)
                Text(
                    text = "Reenviar código em 0:$segFmt",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }

            Spacer(modifier = Modifier.height(Space.s2))

            // 4. Botão Entrar
            Button(
                onClick = onEntrarClick,
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
                    Text(text = "Entrar", style = AppTextStyles.button)
                }
            }

            Spacer(modifier = Modifier.height(Space.s4))

            // 5. Ajuda
            Text(
                text = "Não chegou? Veja também a caixa de spam.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center,
            )

            Spacer(modifier = Modifier.height(Space.s6))
        }
    }
}

@Composable
private fun CaixasOtpInput(
    codigo: String,
    onCodigoChange: (String) -> Unit,
    isError: Boolean,
    modifier: Modifier = Modifier,
) {
    BasicTextField(
        value = codigo,
        onValueChange = onCodigoChange,
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
        decorationBox = {
            Row(
                modifier = modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(Space.s2, Alignment.CenterHorizontally),
            ) {
                for (i in 0 until 6) {
                    val char = codigo.getOrNull(i)?.toString() ?: ""
                    val emFoco = codigo.length == i || (codigo.length == 6 && i == 5)

                    val borderColor = when {
                        isError -> MaterialTheme.colorScheme.error
                        emFoco -> MaterialTheme.colorScheme.primary
                        else -> MaterialTheme.colorScheme.outline
                    }

                    Surface(
                        modifier = Modifier
                            .weight(1f)
                            .height(56.dp)
                            .clip(MaterialTheme.shapes.medium)
                            .border(
                                width = if (emFoco || isError) 2.dp else 1.dp,
                                color = borderColor,
                                shape = MaterialTheme.shapes.medium,
                            ),
                        color = MaterialTheme.colorScheme.surface,
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            val charTts = buildAnnotatedString {
                                @OptIn(ExperimentalTextApi::class)
                                withAnnotation(VerbatimTtsAnnotation(verbatim = char)) {
                                    append(char)
                                }
                            }
                            Text(
                                text = charTts,
                                style = AppTextStyles.otp,
                                color = MaterialTheme.colorScheme.onSurface,
                            )
                        }
                    }
                }
            }
        },
    )
}

@Preview(name = "Código Acesso - Claro", showBackground = true)
@Composable
private fun CodigoAcessoScreenLightPreview() {
    AcerviaTheme(darkTheme = false) {
        CodigoAcessoContent(
            uiState = CodigoAcessoUiState(email = "marina@exemplo.com", codigo = "1234"),
            onCodigoChange = {},
            onEntrarClick = {},
            onReenviarCodigoClick = {},
            onVoltarClick = {},
        )
    }
}

@Preview(name = "Código Acesso - Escuro", uiMode = Configuration.UI_MODE_NIGHT_YES, showBackground = true)
@Composable
private fun CodigoAcessoScreenDarkPreview() {
    AcerviaTheme(darkTheme = true) {
        CodigoAcessoContent(
            uiState = CodigoAcessoUiState(email = "marina@exemplo.com", codigo = "123456"),
            onCodigoChange = {},
            onEntrarClick = {},
            onReenviarCodigoClick = {},
            onVoltarClick = {},
        )
    }
}
