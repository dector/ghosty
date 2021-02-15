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
    Surface(
        color = MaterialTheme.colors.background,
        modifier = Modifier.padding(8.dp),
    ) {
        Column {
            Row {
                // TODO add to library ability to get ip
                val ip = BuildConfig.DEVICE_IP

                Text("IP: $ip")
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
            ) {
                Button(onClick = { /*device.turnOn()*/ }) {
                    Text("ON")
                }
                Spacer(modifier = Modifier.width(4.dp))
                Button(onClick = { /*device.turnOff()*/ }) {
                    Text("OFF")
                }
            }
        }
    }
}
