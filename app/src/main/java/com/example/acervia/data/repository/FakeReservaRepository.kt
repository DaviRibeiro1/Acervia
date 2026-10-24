package com.example.acervia.data.repository

import com.example.acervia.data.DemoConfig
import com.example.acervia.domain.model.Reserva
import com.example.acervia.domain.model.ResultadoReserva
import com.example.acervia.domain.model.StatusReserva
import com.example.acervia.domain.repository.ReservaRepository
import com.example.acervia.domain.usecase.CalcularPrazoReserva
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FakeReservaRepository @Inject constructor(
    private val demoConfig: DemoConfig,
    private val livroRepository: FakeLivroRepository,
) : ReservaRepository {

    private val calcularPrazoReserva = CalcularPrazoReserva()

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

        // 1. Verificar reserva duplicada para o mesmo livro
        val jaExiste = reservas.find { it.volumeId == livroId && it.status == StatusReserva.ATIVA && it.expiraEm > System.currentTimeMillis() }
        if (jaExiste != null) {
            return ResultadoReserva.JaReservado(jaExiste)
        }

        // 2. Obter dados do livro e verificar estoque disponível
        val livroObj = try {
            livroRepository.livro(livroId)
        } catch (e: Exception) {
            null
        }

        if (livroObj == null || livroObj.exemplaresDisponiveis <= 0) {
            return ResultadoReserva.Esgotado
        }

        // 3. Gerar código único de 6 dígitos
        var codigoUnico: String
        do {
            codigoUnico = (100000..999999).random().toString()
        } while (reservas.any { it.codigoRetirada == codigoUnico && it.status == StatusReserva.ATIVA })

        val agora = System.currentTimeMillis()
        val expiraEm = calcularPrazoReserva(agora)

        val novaReserva = Reserva(
            id = "res_${agora.toString().takeLast(4)}",
            usuarioId = "usr_1",
            volumeId = livroId,
            livroTitulo = livroObj.titulo,
            livroAutor = livroObj.autores.joinToString(", "),
            capaUrl = livroObj.capaUrl,
            codigoRetirada = codigoUnico,
            status = StatusReserva.ATIVA,
            reservadoEm = agora,
            expiraEm = expiraEm,
            localBiblioteca = "Biblioteca Central",
            statusBiblioteca = "Aberta agora, fecha às 18h",
        )

        reservas.add(0, novaReserva)
        return ResultadoReserva.Ok(novaReserva)
    }

    override suspend fun listar(): List<Reserva> {
        demoConfig.simularLatenciaEFalhas()
        val agora = System.currentTimeMillis()
        // Atualizar reservas expiradas no tempo
        return reservas.map { reserva ->
            if (reserva.status == StatusReserva.ATIVA && reserva.expiraEm <= agora) {
                reserva.copy(status = StatusReserva.EXPIRADA)
            } else {
                reserva
            }
        }
    }

    override suspend fun reserva(id: String): Reserva? {
        demoConfig.simularLatenciaEFalhas()
        return listar().find { it.id == id }
    }

    override suspend fun cancelar(id: String): Boolean {
        demoConfig.simularLatenciaEFalhas()
        val index = reservas.indexOfFirst { it.id == id && it.status == StatusReserva.ATIVA }
        if (index != -1) {
            val existente = reservas[index]
            reservas[index] = existente.copy(status = StatusReserva.CANCELADA)
            return true
        }
        return false
    }

    fun limparReservas() {
        reservas.clear()
    }
}
