package space.dector.ghosty

import android.os.Bundle
import android.util.Log
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import space.dector.ghosty.DeviceController.Status.Off
import space.dector.ghosty.DeviceController.Status.On
import space.dector.ghosty.DeviceController.Status.Unknown
import space.dector.ghosty.ui.theme.AppTheme
import space.dector.tuyalib.Bulb
import space.dector.tuyalib.IpAddress


class MainActivity : AppCompatActivity() {

    private val controller = object : DeviceController {

        private val TAG = "DeviceController"

        private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

        private val device = Bulb(
            ip = BuildConfig.DEVICE_IP,
            deviceId = BuildConfig.DEVICE_ID,
            localKey = BuildConfig.DEVICE_LOCAL_KEY,
        )

        override val ip = device.ip

        private val _enabledStatusFlow = MutableStateFlow(Unknown)
        override val enabledStatusFlow: StateFlow<DeviceController.Status> = _enabledStatusFlow

        init {
            updateStatusAsync()
        }

        override fun turnOn() = execute(
            onFailure = ::showError,
        ) {
            device.turnOn()

            updateStatusAsync()
        }

        override fun turnOff() = execute(
            onFailure = ::showError,
        ) {
            device.turnOff()

            updateStatusAsync()
        }

        private fun execute(
            onFailure: (Throwable) -> Unit = {},
            action: () -> Unit,
        ) {
            scope.launch {
                runCatching {
                    action()
                }.onFailure {
                    Log.e(TAG, "Action failed", it)
                    executeOnMain { onFailure(it) }
                }
            }
        }

        private fun updateStatusAsync() {
            scope.launch {
                delay(100)

                runCatching { device.status() }
                    .onSuccess { status ->
                        _enabledStatusFlow.value = if (status.isOn()) On else Off
                    }
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            AppTheme {
                MainScreen(controller = controller)
            }
        }
    }

    private fun showError(throwable: Throwable) {
        Snackbar.make(
            window.decorView,/*.findViewById<ViewGroup>(android.R.id.content)*/
            "Error: ${throwable.message}",
            Snackbar.LENGTH_SHORT,
        ).show()
    }
}

interface DeviceController {
    val ip: IpAddress

    val enabledStatusFlow: StateFlow<Status>

    fun turnOn()
    fun turnOff()

    enum class Status {
        On, Off, Unknown,
    }
}

@Composable
private fun MainScreen(
    controller: DeviceController,
) {
    val enabledStatus: State<DeviceController.Status> =
        controller.enabledStatusFlow.collectAsState(Unknown)

    Surface(
        color = Color.Transparent,
        contentColor = Color.White,
        modifier = Modifier.fillMaxSize(),
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.Center,
        ) {
            BulbView(
                isOn = when (enabledStatus.value) {
                    On -> true
                    Off -> false
                    Unknown -> null
                },
            )
            Spacer(Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
            ) {
                Text("IP: ${controller.ip}")
            }

            // On/Off buttons
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp),
                horizontalArrangement = Arrangement.Center,
            ) {
                Button(
                    colors = ButtonDefaults.buttonColors(
                        backgroundColor = Color(0xFF8BC34A),
                        contentColor = Color.Black,
                    ),
                    onClick = { controller.turnOn() },
                ) {
                    Text("ON")
                }
                Spacer(modifier = Modifier.width(16.dp))
                Button(
                    colors = ButtonDefaults.buttonColors(
                        backgroundColor = Color(0xFFE91E63),
                        contentColor = Color.Black,
                    ),
                    onClick = { controller.turnOff() },
                ) {
                    Text("OFF")
                }
            }

            // Brightness
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                TextButton(
                    onClick = {},
                    modifier = Modifier.size(32.dp),
                ) {
                    Text("-")
                }
                Text("100%")
                TextButton(
                    onClick = {},
                    modifier = Modifier.size(32.dp),
                ) {
                    Text("+")
                }
            }
        }
    }
}

@Preview(name = "Main Screen", showBackground = true, widthDp = 300, heightDp = 150)
@Composable
private fun DefaultMainScreen() {
    MainScreen(controller = MockedController())
}

@Preview(group = "Screen", device = Devices.PIXEL_4)
@Composable
private fun DefaultMainScreen_FullScreen() {
    MainScreen(controller = MockedController())
}

private class MockedController : DeviceController {
    override val ip get() = "192.168.0.1"
    override fun turnOn() = TODO()
    override fun turnOff() = TODO()

    override val enabledStatusFlow = MutableStateFlow(Unknown)
}

@Preview(group = "Components")
@Composable
fun BulbView(
    isOn: Boolean? = null,
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(1f),
    ) {
        Image(
            painter = painterResource(R.drawable.ic_light_bulb),
            contentDescription = "",
            modifier = Modifier
                .fillMaxSize(),
            alpha = when (isOn) {
                null -> 0.1f
                true -> 1f
                false -> 0.35f
            },
        )
        Box(
            modifier = Modifier
                .fillMaxSize(),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                text = when (isOn) {
                    null -> "n/a"
                    true -> "ON"
                    false -> "OFF"
                },
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier
                    .background(
                        when (isOn) {
                            null -> Color(0xFF7E777A)
                            true -> Color(0xFF8BC34A)
                            false -> Color(0xFFE91E63)
                        })
                    .padding(horizontal = 8.dp, vertical = 3.dp),
            )
        }
    }
}

suspend fun executeOnMain(action: suspend CoroutineScope.() -> Unit) = withContext(Dispatchers.Main, action)
