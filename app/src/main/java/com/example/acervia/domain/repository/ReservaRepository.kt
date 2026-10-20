package com.example.acervia.domain.repository

import com.example.acervia.domain.model.Reserva
import com.example.acervia.domain.model.ResultadoReserva

interface ReservaRepository {
    suspend fun reservar(livroId: String): ResultadoReserva
    suspend fun listar(): List<Reserva>
    suspend fun reserva(id: String): Reserva?
    suspend fun cancelar(id: String): Boolean
}
