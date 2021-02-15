package space.dector.ghosty

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.setContent
import space.dector.ghosty.ui.theme.AppTheme
import space.dector.tuyalib.Bulb


class MainActivity : AppCompatActivity() {

    private val device = Bulb(
        ip = BuildConfig.DEVICE_IP,
        deviceId = BuildConfig.DEVICE_ID,
        localKey = BuildConfig.DEVICE_LOCAL_KEY,
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            AppTheme {
                MainScreen(device = device)
            }
        }
    }
}

@Composable
private fun MainScreen(
    device: Bulb,
) {
    Surface(color = MaterialTheme.colors.background) {
        Column {
            Row {
                Button(onClick = { /*device.turnOn()*/ }) {
                    Text("ON")
                }
                Button(onClick = { /*device.turnOff()*/ }) {
                    Text("OFF")
                }
            }
        }
    }
}
