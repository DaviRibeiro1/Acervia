package com.example.acervia.domain.usecase

import com.example.acervia.domain.model.Reserva
import com.example.acervia.domain.model.StatusReserva

class CalcularDisponibilidade {

    operator fun invoke(
        exemplaresTotal: Int,
        reservas: List<Reserva>,
        agora: Long = System.currentTimeMillis(),
    ): Int {
        val reservasAtivasEValidas = reservas.count {
            it.status == StatusReserva.ATIVA && it.expiraEm > agora
        }
        return maxOf(0, exemplaresTotal - reservasAtivasEValidas)
    }
}
