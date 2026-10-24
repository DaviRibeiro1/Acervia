package com.example.acervia.domain.usecase

import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

class CalcularPrazoReserva {

    operator fun invoke(
        agoraMillis: Long = System.currentTimeMillis(),
        diasFuncionamento: Int = 3,
        feriados: Set<String> = emptySet(),
    ): Long {
        val calendar = Calendar.getInstance().apply {
            timeInMillis = agoraMillis
        }

        var diasAdicionados = 0
        while (diasAdicionados < diasFuncionamento) {
            calendar.add(Calendar.DAY_OF_MONTH, 1)
            val dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK)
            val dataFormatada = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(calendar.time)

            val ehDiaUtil = dayOfWeek != Calendar.SATURDAY &&
                    dayOfWeek != Calendar.SUNDAY &&
                    !feriados.contains(dataFormatada)

            if (ehDiaUtil) {
                diasAdicionados++
            }
        }

        calendar.set(Calendar.HOUR_OF_DAY, 18)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MILLISECOND, 0)

        return calendar.timeInMillis
    }

    fun formatarPrazoTextual(
        expiraEmMillis: Long,
        agoraMillis: Long = System.currentTimeMillis(),
    ): String {
        val calAgora = Calendar.getInstance().apply { timeInMillis = agoraMillis }
        val calExpira = Calendar.getInstance().apply { timeInMillis = expiraEmMillis }

        val diaAgora = calAgora.get(Calendar.DAY_OF_YEAR)
        val anoAgora = calAgora.get(Calendar.YEAR)

        val diaExpira = calExpira.get(Calendar.DAY_OF_YEAR)
        val anoExpira = calExpira.get(Calendar.YEAR)

        val mesDiaFormatado = SimpleDateFormat("dd/MM", Locale.getDefault()).format(Date(expiraEmMillis))
        val diaSemanaFormatado = SimpleDateFormat("EEE", Locale("pt", "BR")).format(Date(expiraEmMillis)).replace(".", "")

        return when {
            anoAgora == anoExpira && diaAgora == diaExpira -> {
                "Retire até hoje · 18h"
            }
            anoAgora == anoExpira && diaExpira == diaAgora + 1 -> {
                "Retire até amanhã, $mesDiaFormatado · 18h"
            }
            else -> {
                "Retire até $diaSemanaFormatado, $mesDiaFormatado · 18h"
            }
        }
    }
}
