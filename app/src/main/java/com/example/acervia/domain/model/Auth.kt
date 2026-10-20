package com.example.acervia.domain.model

data class Perfil(
    val nome: String,
    val sobrenome: String,
    val dataNascimento: String,
    val termosAceitos: Boolean,
)

data class Sessao(
    val usuarioId: String,
    val email: String,
    val perfil: Perfil?,
)

sealed interface ResultadoLogin {
    data class Sucesso(val sessao: Sessao, val contaNova: Boolean) : ResultadoLogin
    data class Erro(val mensagem: String) : ResultadoLogin
}
