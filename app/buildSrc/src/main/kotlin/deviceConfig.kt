import java.util.Properties


fun applyDeviceConfig() {
    val file = File("device.local.properties")
    require(file.exists()) { "Create 'device.local.properties' with your device configuration" }

    val props = Properties()
    // TODO
}
