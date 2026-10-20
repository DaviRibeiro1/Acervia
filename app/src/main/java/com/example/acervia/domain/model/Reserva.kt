package com.example.acervia.domain.model

enum class StatusReserva {
    ATIVA,
    RETIRADA,
    EXPIRADA,
    CANCELADA,
}

data class Reserva(
    val id: String,
    val usuarioId: String,
    val volumeId: String,
    val livroTitulo: String,
    val livroAutor: String,
    val capaUrl: String?,
    val codigoRetirada: String,
    val status: StatusReserva,
    val reservadoEm: Long,
    val expiraEm: Long,
    val localBiblioteca: String,
    val statusBiblioteca: String,
)

sealed interface ResultadoReserva {
    data class Ok(val reserva: Reserva) : ResultadoReserva
    data object Esgotado : ResultadoReserva
    data class JaReservado(val reserva: Reserva) : ResultadoReserva
}
