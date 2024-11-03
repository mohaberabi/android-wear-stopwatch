package com.mohaberabi.wearstopwatchapp.presentation

import androidx.compose.runtime.MutableState
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import java.time.LocalTime
import java.time.format.DateTimeFormatter


enum class TimerState {
    Running,
    Paused,
    Reset,
}

class StopWatchViewModel : ViewModel() {
    private val _elapsedTime = MutableStateFlow(0L)
    private val _timerState = MutableStateFlow(TimerState.Reset)
    val timerState = _timerState.asStateFlow()
    private fun getTimerFlow(
        isRunning: Boolean,
    ): Flow<Long> {
        return flow {
            var startMillis = System.currentTimeMillis()
            while (isRunning) {
                val currentMillis = System.currentTimeMillis()
                val timeDiff = if (currentMillis > startMillis)
                    currentMillis - startMillis else 0L
                emit(timeDiff)
                startMillis = System.currentTimeMillis()
                delay(10L)
            }
        }
    }

    private val formatter = DateTimeFormatter.ofPattern("HH:mm:ss:SSS")
    val stopWatch = _elapsedTime.map { millis ->
        LocalTime.ofNanoOfDay(millis * 1000 * 1000).format(formatter)
    }.onStart { initTimer() }.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5_000),
        "00:00:00:00"
    )

    fun setTimerState(state: TimerState) {
        if (state == TimerState.Reset) {
            _elapsedTime.update { 0L }
        }
        _timerState.update { state }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    private fun initTimer() {
        _timerState.flatMapLatest { state ->
            getTimerFlow(state == TimerState.Running)
        }.onEach { diff ->
            _elapsedTime.update { it + diff }
        }.launchIn(viewModelScope)
    }
}