package com.example.acervia.domain

import com.example.acervia.domain.model.Reserva
import com.example.acervia.domain.model.StatusReserva
import com.example.acervia.domain.usecase.CalcularDisponibilidade
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class DisponibilidadeUnitTest {

    private lateinit var calcularDisponibilidade: CalcularDisponibilidade
    private val agora = 1000000L

    @Before
    fun setup() {
        calcularDisponibilidade = CalcularDisponibilidade()
    }

    private fun criarReservaFicticia(
        id: String,
        status: StatusReserva,
        expiraEm: Long,
    ): Reserva {
        return Reserva(
            id = id,
            usuarioId = "usr_1",
            volumeId = "vol_1",
            livroTitulo = "Livro Teste",
            livroAutor = "Autor Teste",
            capaUrl = null,
            codigoRetirada = "123456",
            status = status,
            reservadoEm = agora - 100000L,
            expiraEm = expiraEm,
            localBiblioteca = "Central",
            statusBiblioteca = "Aberta",
        )
    }

    @Test
    fun quandoSemReservas_deveRetornarTotalDeExemplares() {
        val total = 5
        val reservas = emptyList<Reserva>()

        val disponiveis = calcularDisponibilidade(total, reservas, agora)

        assertEquals(5, disponiveis)
        assertTrue(disponiveis > 0)
    }

    @Test
    fun quandoHouverReservasAtivasValidas_deveDescontarDoEstoque() {
        val total = 5
        val reservas = listOf(
            criarReservaFicticia("res_1", StatusReserva.ATIVA, expiraEm = agora + 50000L),
            criarReservaFicticia("res_2", StatusReserva.ATIVA, expiraEm = agora + 80000L),
        )

        val disponiveis = calcularDisponibilidade(total, reservas, agora)

        assertEquals(3, disponiveis)
        assertTrue(disponiveis > 0)
    }

    @Test
    fun quandoReservasIgualAExemplares_deveRetornarZeroDisponivel() {
        val total = 2
        val reservas = listOf(
            criarReservaFicticia("res_1", StatusReserva.ATIVA, expiraEm = agora + 50000L),
            criarReservaFicticia("res_2", StatusReserva.ATIVA, expiraEm = agora + 80000L),
        )

        val disponiveis = calcularDisponibilidade(total, reservas, agora)

        assertEquals(0, disponiveis)
        assertFalse(disponiveis > 0)
    }

    @Test
    fun quandoExcederExemplares_deveRetornarZeroENuncaNegativo() {
        val total = 1
        val reservas = listOf(
            criarReservaFicticia("res_1", StatusReserva.ATIVA, expiraEm = agora + 50000L),
            criarReservaFicticia("res_2", StatusReserva.ATIVA, expiraEm = agora + 80000L),
            criarReservaFicticia("res_3", StatusReserva.ATIVA, expiraEm = agora + 90000L),
        )

        val disponiveis = calcularDisponibilidade(total, reservas, agora)

        assertEquals(0, disponiveis)
        assertFalse(disponiveis > 0)
    }

    @Test
    fun quandoReservasEstiveremExpiradasCanceladasOuRetiradas_naoDeveDescontarDoEstoque() {
        val total = 4
        val reservas = listOf(
            criarReservaFicticia("res_1", StatusReserva.RETIRADA, expiraEm = agora + 50000L),
            criarReservaFicticia("res_2", StatusReserva.CANCELADA, expiraEm = agora + 50000L),
            criarReservaFicticia("res_3", StatusReserva.EXPIRADA, expiraEm = agora + 50000L),
        )

        val disponiveis = calcularDisponibilidade(total, reservas, agora)

        assertEquals(4, disponiveis)
    }

    @Test
    fun quandoReservaTiverExpiraEmNoPassado_deveConsiderarExpiradaENaoDescontar() {
        val total = 3
        val reservas = listOf(
            criarReservaFicticia("res_1", StatusReserva.ATIVA, expiraEm = agora - 1000L),
            criarReservaFicticia("res_2", StatusReserva.ATIVA, expiraEm = agora + 50000L),
        )

        val disponiveis = calcularDisponibilidade(total, reservas, agora)

        assertEquals(2, disponiveis)
    }
}
