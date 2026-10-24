package com.example.acervia.domain

import com.example.acervia.domain.usecase.CalcularPrazoReserva
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import java.util.Calendar

class CalcularPrazoReservaUnitTest {

    private lateinit var calcularPrazoReserva: CalcularPrazoReserva

    @Before
    fun setup() {
        calcularPrazoReserva = CalcularPrazoReserva()
    }

    @Test
    fun reservaEmSextaFeira_deveExpirarEmTercaFeiraAs18h() {
        // Sexta-feira às 17:00 -> +3 dias úteis (Seg, Ter, Qua) -> Quarta-feira às 18:00
        val calSexta = Calendar.getInstance().apply {
            set(2026, Calendar.OCTOBER, 16, 17, 0, 0) // 16/10/2026 é sexta-feira
            set(Calendar.MILLISECOND, 0)
        }

        val expiraEm = calcularPrazoReserva(calSexta.timeInMillis)

        val calResultado = Calendar.getInstance().apply { timeInMillis = expiraEm }
        assertEquals(Calendar.WEDNESDAY, calResultado.get(Calendar.DAY_OF_WEEK))
        assertEquals(21, calResultado.get(Calendar.DAY_OF_MONTH)) // Quarta 21/10
        assertEquals(18, calResultado.get(Calendar.HOUR_OF_DAY))
        assertEquals(0, calResultado.get(Calendar.MINUTE))
    }

    @Test
    fun reservaEmSabado_deveExpirarEmTercaFeiraAs18h() {
        // Sábado às 10:00 (dia fechado) -> +3 dias úteis (Seg, Ter, Qua) -> Quarta-feira às 18:00
        val calSabado = Calendar.getInstance().apply {
            set(2026, Calendar.OCTOBER, 17, 10, 0, 0) // 17/10/2026 é sábado
            set(Calendar.MILLISECOND, 0)
        }

        val expiraEm = calcularPrazoReserva(calSabado.timeInMillis)

        val calResultado = Calendar.getInstance().apply { timeInMillis = expiraEm }
        assertEquals(Calendar.WEDNESDAY, calResultado.get(Calendar.DAY_OF_WEEK))
        assertEquals(21, calResultado.get(Calendar.DAY_OF_MONTH)) // Quarta 21/10
        assertEquals(18, calResultado.get(Calendar.HOUR_OF_DAY))
    }

    @Test
    fun reservaComFeriadoNaSegunda_devePularOFeriadoEExpirarNaQuartaAs18h() {
        // Sexta-feira com feriado na segunda (19/10/2026) -> +3 dias úteis (Ter, Qua, Qui) -> Quinta-feira às 18:00
        val calSexta = Calendar.getInstance().apply {
            set(2026, Calendar.OCTOBER, 16, 17, 0, 0)
            set(Calendar.MILLISECOND, 0)
        }
        val feriados = setOf("2026-10-19")

        val expiraEm = calcularPrazoReserva(
            agoraMillis = calSexta.timeInMillis,
            feriados = feriados,
        )

        val calResultado = Calendar.getInstance().apply { timeInMillis = expiraEm }
        assertEquals(Calendar.THURSDAY, calResultado.get(Calendar.DAY_OF_WEEK))
        assertEquals(22, calResultado.get(Calendar.DAY_OF_MONTH)) // Quinta 22/10
        assertEquals(18, calResultado.get(Calendar.HOUR_OF_DAY))
    }

    @Test
    fun formatarPrazoTextual_mesmoDia_deveExibirHoje() {
        val calHoje = Calendar.getInstance().apply { set(2026, Calendar.OCTOBER, 16, 10, 0, 0) }
        val calExpiraHoje = Calendar.getInstance().apply { set(2026, Calendar.OCTOBER, 16, 18, 0, 0) }

        val texto = calcularPrazoReserva.formatarPrazoTextual(
            expiraEmMillis = calExpiraHoje.timeInMillis,
            agoraMillis = calHoje.timeInMillis,
        )

        assertEquals("Retire até hoje · 18h", texto)
    }

    @Test
    fun formatarPrazoTextual_proximoDia_deveExibirAmanha() {
        val calHoje = Calendar.getInstance().apply { set(2026, Calendar.OCTOBER, 16, 10, 0, 0) }
        val calExpiraAmanha = Calendar.getInstance().apply { set(2026, Calendar.OCTOBER, 17, 18, 0, 0) }

        val texto = calcularPrazoReserva.formatarPrazoTextual(
            expiraEmMillis = calExpiraAmanha.timeInMillis,
            agoraMillis = calHoje.timeInMillis,
        )

        assertTrue(texto.contains("Retire até amanhã"))
        assertTrue(texto.contains("18h"))
    }
}
