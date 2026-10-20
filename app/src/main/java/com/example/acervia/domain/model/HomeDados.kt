package com.example.acervia.domain.model

data class HomeDados(
    val nomeUsuario: String,
    val reservaAtivaMaisProxima: Reserva?,
    val quantidadeReservasAtivas: Int,
    val destaques: List<LivroResumo>,
)
