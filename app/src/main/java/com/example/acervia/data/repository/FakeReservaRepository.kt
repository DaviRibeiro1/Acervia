package com.example.acervia.data.repository

import com.example.acervia.data.DemoConfig
import com.example.acervia.domain.model.Reserva
import com.example.acervia.domain.model.ResultadoReserva
import com.example.acervia.domain.model.StatusReserva
import com.example.acervia.domain.repository.ReservaRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FakeReservaRepository @Inject constructor(
    private val demoConfig: DemoConfig,
) : ReservaRepository {

    private val reservas = mutableListOf(
        Reserva(
            id = "res_101",
            usuarioId = "usr_1",
            volumeId = "vol_1",
            livroTitulo = "A Biblioteca da Meia-Noite",
            livroAutor = "Matt Haig",
            capaUrl = null,
            codigoRetirada = "482716",
            status = StatusReserva.ATIVA,
            reservadoEm = System.currentTimeMillis() - (3600 * 1000 * 5),
            expiraEm = System.currentTimeMillis() + (3600 * 1000 * 20),
            localBiblioteca = "Biblioteca Central",
            statusBiblioteca = "Aberta agora, fecha às 18h",
        ),
        Reserva(
            id = "res_100",
            usuarioId = "usr_1",
            volumeId = "vol_2",
            livroTitulo = "1984",
            livroAutor = "George Orwell",
            capaUrl = null,
            codigoRetirada = "109823",
            status = StatusReserva.RETIRADA,
            reservadoEm = System.currentTimeMillis() - (86400 * 1000 * 10),
            expiraEm = System.currentTimeMillis() - (86400 * 1000 * 7),
            localBiblioteca = "Biblioteca Central",
            statusBiblioteca = "Aberta agora, fecha às 18h",
        ),
        Reserva(
            id = "res_099",
            usuarioId = "usr_1",
            volumeId = "vol_3",
            livroTitulo = "Duna",
            livroAutor = "Frank Herbert",
            capaUrl = null,
            codigoRetirada = "742109",
            status = StatusReserva.EXPIRADA,
            reservadoEm = System.currentTimeMillis() - (86400 * 1000 * 20),
            expiraEm = System.currentTimeMillis() - (86400 * 1000 * 17),
            localBiblioteca = "Biblioteca Central",
            statusBiblioteca = "Aberta agora, fecha às 18h",
        ),
    )

    override suspend fun reservar(livroId: String): ResultadoReserva {
        demoConfig.simularLatenciaEFalhas()
        val jaExiste = reservas.find { it.volumeId == livroId && it.status == StatusReserva.ATIVA }
        if (jaExiste != null) {
            return ResultadoReserva.JaReservado(jaExiste)
        }

        val novaReserva = Reserva(
            id = "res_${System.currentTimeMillis().toString().takeLast(4)}",
            usuarioId = "usr_1",
            volumeId = livroId,
            livroTitulo = "Livro Reservado",
            livroAutor = "Autor do Livro",
            capaUrl = null,
            codigoRetirada = (100000..999999).random().toString(),
            status = StatusReserva.ATIVA,
            reservadoEm = System.currentTimeMillis(),
            expiraEm = System.currentTimeMillis() + (3 * 86400 * 1000L),
            localBiblioteca = "Biblioteca Central",
            statusBiblioteca = "Aberta agora, fecha às 18h",
        )
        reservas.add(0, novaReserva)
        return ResultadoReserva.Ok(novaReserva)
    }

    override suspend fun listar(): List<Reserva> {
        demoConfig.simularLatenciaEFalhas()
        return reservas.toList()
    }

    override suspend fun reserva(id: String): Reserva? {
        demoConfig.simularLatenciaEFalhas()
        return reservas.find { it.id == id }
    }

    override suspend fun cancelar(id: String): Boolean {
        demoConfig.simularLatenciaEFalhas()
        val index = reservas.indexOfFirst { it.id == id }
        if (index != -1) {
            val existente = reservas[index]
            reservas[index] = existente.copy(status = StatusReserva.CANCELADA)
            return true
        }
        return false
    }
}
