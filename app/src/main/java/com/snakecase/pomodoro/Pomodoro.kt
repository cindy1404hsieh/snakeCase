package com.snakecase.pomodoro

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue

import com.snakecase.DataBaseManager
data class Pomodoro(private var tipoTimer: TipoTimer) {
    private var ciclos = 0
    var minutosRestantes = tipoTimer.minutos
    var segundosRestantes = 0
    private var pausa = true

    var cicloConteo by mutableStateOf(4)
    var estudioTime by mutableStateOf(25)
    var descansoTime by mutableStateOf(5)
    var descansoLargoTime by mutableStateOf(20)

    fun obtenerTipoTimer(): TipoTimer {
        return tipoTimer
    }

    fun actualizarTipoTimer() {
        if (ciclos < cicloConteo) {
            if (tipoTimer == TipoTimer.ESTUDIO) {
                incrementarCiclo()
                tipoTimer = TipoTimer.DESCANSOCORTO
            } else {
                tipoTimer = TipoTimer.ESTUDIO
            }
        } else {
            tipoTimer = TipoTimer.DESCANSOLARGO
            ciclos = 0
        }
        actualizarMinutosRestantes()
    }

    fun incrementarCiclo() {
        ciclos = ciclos + 1
        val DBManager = DataBaseManager("usuario")
        DBManager.incrementarCiclos()
    }

    fun pasa1Segundo() {
        if (segundosRestantes > 0) {
            segundosRestantes -= 1
        } else if (minutosRestantes > 0) {
            minutosRestantes -= 1
            segundosRestantes = 59
        } else {
            actualizarTipoTimer()
        }
    }

    fun pausar() {
        this.pausa = false
    }

    fun reanudar() {
        this.pausa = true
    }

    fun reiniciar() {
        this.segundosRestantes = 0
        actualizarMinutosRestantes()
    }

    fun enPausa(): Boolean {
        return pausa
    }

    fun obtenerMinutos(): Int {
        return this.minutosRestantes
    }

    fun obtenerSegundos(): Int {
        return this.segundosRestantes
    }

    fun setearMinutos(minutos: Int) {
        this.minutosRestantes = minutos
    }

    fun updateFocusCount(nuevoConteo: Int) {
        if (nuevoConteo in 1..12) {
            cicloConteo = nuevoConteo
        }
    }

    fun updateFocusTime(nuevoTime: Int) {
        if (nuevoTime in 5..120) {
            estudioTime = nuevoTime
            if (tipoTimer == TipoTimer.ESTUDIO) actualizarMinutosRestantes()
        }
    }

    fun updateBreakTime(nuevoTime: Int) {
        if (nuevoTime in 5..60) {
            descansoTime = nuevoTime
            if (tipoTimer == TipoTimer.DESCANSOCORTO) actualizarMinutosRestantes()
        }
    }

    fun updateLongBreakTime(nuevoTime: Int) {
        if (nuevoTime in 5..60) {
            descansoLargoTime = nuevoTime
            if (tipoTimer == TipoTimer.DESCANSOLARGO) actualizarMinutosRestantes()
        }
    }

    private fun actualizarMinutosRestantes() {
        minutosRestantes = when (tipoTimer) {
            TipoTimer.ESTUDIO -> estudioTime
            TipoTimer.DESCANSOCORTO -> descansoTime
            TipoTimer.DESCANSOLARGO -> descansoLargoTime
        }
    }
}