package space.dector.ghosty

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import space.dector.ghosty.ui.theme.AppTheme
import space.dector.tuyalib.Bulb
import space.dector.tuyalib.IpAddress


class MainActivity : AppCompatActivity() {

    private val controller = object : DeviceController {

        private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

        private val device = Bulb(
            ip = BuildConfig.DEVICE_IP,
            deviceId = BuildConfig.DEVICE_ID,
            localKey = BuildConfig.DEVICE_LOCAL_KEY,
        )

        override val ip = device.ip

        override fun turnOn() = execute {
            device.turnOn()
        }

        override fun turnOff() = execute {
            device.turnOff()
        }

        private fun execute(action: () -> Unit) {
            scope.launch {
                action()
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
}

interface DeviceController {
    val ip: IpAddress

    fun turnOn()
    fun turnOff()
}

@Composable
private fun MainScreen(
    controller: DeviceController,
) {
    Surface(
        color = MaterialTheme.colors.background,
        modifier = Modifier.padding(8.dp),
    ) {
        Column {
            Row {
                Text("IP: ${controller.ip}")
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
            ) {
                Button(onClick = { controller.turnOn() }) {
                    Text("ON")
                }
                Spacer(modifier = Modifier.width(4.dp))
                Button(onClick = { controller.turnOff() }) {
                    Text("OFF")
                }
            }
        }
    }
}
