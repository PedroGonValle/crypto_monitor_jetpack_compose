package pedrogonvalle.com.github.crypto_monitor_jetpack_compose.service

import pedrogonvalle.com.github.crypto_monitor_jetpack_compose.model.TickerResponse
import retrofit2.Response
import retrofit2.http.GET

interface MercadoBitcoinService {
    @GET("api/BTC/ticker/")
    suspend fun getTicker(): Response<TickerResponse>
}