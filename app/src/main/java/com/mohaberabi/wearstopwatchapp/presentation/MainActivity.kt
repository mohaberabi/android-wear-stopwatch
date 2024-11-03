package com.mohaberabi.wearstopwatchapp.presentation

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.wear.compose.material.Button
import androidx.wear.compose.material.Icon
import androidx.wear.compose.material.MaterialTheme
import androidx.wear.compose.material.Text
import androidx.wear.compose.material.TimeText
import com.mohaberabi.wearstopwatchapp.R
import com.mohaberabi.wearstopwatchapp.presentation.theme.WearStopWatchAppTheme
import java.sql.Time

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()

        super.onCreate(savedInstanceState)

        setTheme(android.R.style.Theme_DeviceDefault)
        val viewmodel by viewModels<StopWatchViewModel>()

        setContent {
            val stopwatch by viewmodel.stopWatch.collectAsState("00:00:00:00")
            val timerSate by viewmodel.timerState.collectAsState()
            WearApp(
                onSetTimerState = viewmodel::setTimerState,
                stopWatch = stopwatch,
                currnetState = timerSate
            )
        }
    }
}

@Composable
fun WearApp(
    onSetTimerState: (TimerState) -> Unit = { TimerState.Reset },
    stopWatch: String = "",
    currnetState: TimerState = TimerState.Reset
) {
    WearStopWatchAppTheme {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colors.background),
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center,
                color = MaterialTheme.colors.primary,
                text = stopWatch,
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(6.dp),
                horizontalArrangement = Arrangement.Center,
            ) {
                Button(onClick = {
                    onSetTimerState(TimerState.Running)
                })
                {
                    Text(text = "Start")
                }
                Spacer(modifier = Modifier.width(4.dp))
                Button(
                    onClick = {
                        val state =
                            if (currnetState == TimerState.Paused)
                                TimerState.Reset else TimerState.Paused
                        onSetTimerState(state)
                    },
                ) {
                    val text = if (currnetState == TimerState.Paused)
                        "Rest" else "Pause"
                    Text(text = text)

                }
            }

        }
    }
}


@Preview(
    device = Devices.WEAR_OS_SMALL_ROUND,
    showSystemUi = true,
)
@Composable
fun DefaultPreview() {
    WearApp()
}