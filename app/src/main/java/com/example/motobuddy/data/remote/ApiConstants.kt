
package com.example.motobuddy.data.remote

object ApiConstants {
    const val USE_EMULATOR = false

    const val PC_IPV4_ADDRESS = "192.168.226.1"

    const val EMULATOR_BASE_URL = "http://10.0.2.2/motobuddy_api/"

    val PHYSICAL_DEVICE_BASE_URL: String
        get() = "http://$PC_IPV4_ADDRESS/motobuddy_api/"

    val BASE_URL: String
        get() = if (USE_EMULATOR) {
            EMULATOR_BASE_URL
        } else {
            PHYSICAL_DEVICE_BASE_URL
        }
}
