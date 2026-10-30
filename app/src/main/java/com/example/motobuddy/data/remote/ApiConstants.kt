package com.example.motobuddy.data.remote

object ApiConstants {
    /**
     * Set [USE_EMULATOR] to false when testing on a physical Android phone via Wi-Fi/Wireless Debugging.
     * Set [USE_EMULATOR] to true when testing inside the Android Studio Emulator.
     */
    const val USE_EMULATOR = false

    /**
     * REPLACE [PC_IPV4_ADDRESS] with your PC's actual local Wi-Fi IPv4 address (e.g. "192.168.1.15").
     * Find your IP address by running 'ipconfig' in Windows Command Prompt/PowerShell.
     */
    const val PC_IPV4_ADDRESS = "YOUR_PC_IPV4_ADDRESS"

    // Base URL endpoints
    const val EMULATOR_BASE_URL = "http://10.0.2.2/motobuddy_api/"
    val PHYSICAL_DEVICE_BASE_URL: String
        get() = "http://$PC_IPV4_ADDRESS/motobuddy_api/"

    // Active Base URL used by Retrofit
    val BASE_URL: String
        get() = if (USE_EMULATOR) EMULATOR_BASE_URL else PHYSICAL_DEVICE_BASE_URL
}
