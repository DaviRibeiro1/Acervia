package com.example.acervia.data

import com.example.acervia.data.repository.FakeLivroRepository
import com.example.acervia.data.repository.FakeReservaRepository
import com.example.acervia.domain.model.ResultadoReserva
import com.example.acervia.domain.model.StatusReserva
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class ReservaRepositoryUnitTest {

    private lateinit var demoConfig: DemoConfig
    private lateinit var livroRepository: FakeLivroRepository
    private lateinit var reservaRepository: FakeReservaRepository

    @Before
    fun setup() {
        demoConfig = DemoConfig().apply {
            latenciaMinMs = 0
            latenciaMaxMs = 0
        }
        livroRepository = FakeLivroRepository(demoConfig)
        reservaRepository = FakeReservaRepository(demoConfig, livroRepository)
    }

    @Test
    fun reservarLivroDisponivel_deveRetornarOkComCodigoDe6Digitos() = runTest {
        val resultado = reservaRepository.reservar("vol_5") // Dom Casmurro (disponível)

        assertTrue(resultado is ResultadoReserva.Ok)
        val reserva = (resultado as ResultadoReserva.Ok).reserva
        assertEquals("vol_5", reserva.volumeId)
        assertEquals(6, reserva.codigoRetirada.length)
        assertEquals(StatusReserva.ATIVA, reserva.status)
    }

    @Test
    fun reservarLivroJaReservadoAtivo_deveRetornarJaReservado() = runTest {
        // "vol_1" (A Biblioteca da Meia-Noite) já possui reserva ativa ("res_101")
        val resultado = reservaRepository.reservar("vol_1")

        assertTrue(resultado is ResultadoReserva.JaReservado)
        val reserva = (resultado as ResultadoReserva.JaReservado).reserva
        assertEquals("res_101", reserva.id)
    }

    @Test
    fun reservarLivroEsgotado_deveRetornarEsgotado() = runTest {
        // "vol_4" (O Sol É Para Todos) possui 0 exemplares disponíveis
        val resultado = reservaRepository.reservar("vol_4")

        assertTrue(resultado is ResultadoReserva.Esgotado)
    }

    @Test
    fun cancelarReservaAtiva_deveRetornarTrueEAtualizarStatus() = runTest {
        val sucesso = reservaRepository.cancelar("res_101")
        assertTrue(sucesso)

        val reservaCancelada = reservaRepository.reserva("res_101")
        assertEquals(StatusReserva.CANCELADA, reservaCancelada?.status)
    }

    @Test
    fun listarReservasExpiradas_deveAtualizarStatusParaExpirada() = runTest {
        val reservas = reservaRepository.listar()
        val expirada = reservas.find { it.id == "res_099" }

        assertEquals(StatusReserva.EXPIRADA, expirada?.status)
    }
}
