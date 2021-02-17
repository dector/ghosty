package space.dector.ghosty

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
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
        color = Color.Transparent,
        contentColor = Color.White,
        modifier = Modifier.fillMaxSize(),
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.Center,
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
            ) {
                Text("IP: ${controller.ip}")
            }

            // On/Off buttons
            Row(
                modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
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
                modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
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
    MainScreen(controller = object : DeviceController {
        override val ip get() = "192.168.0.1"
        override fun turnOn() = TODO()
        override fun turnOff() = TODO()
    })
}
