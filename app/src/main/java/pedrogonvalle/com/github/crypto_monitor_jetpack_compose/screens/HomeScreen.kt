package pedrogonvalle.com.github.crypto_monitor_jetpack_compose.screens

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.launch
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import androidx.compose.material3.ExperimentalMaterial3Api
import pedrogonvalle.com.github.crypto_monitor_jetpack_compose.service.MercadoBitcoinServiceFactory

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(modifier: Modifier = Modifier) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    var isDarkTheme by remember { mutableStateOf(false) }

    var valueText by remember { mutableStateOf("R$ 0,00") }
    var dateText by remember { mutableStateOf("dd/mm/yyyy hh:mm:ss") }
    var loading by remember { mutableStateOf(false) }

    fun fetchTicker() {
        scope.launch {
            loading = true
            try {
                val service = MercadoBitcoinServiceFactory().create()
                val response = service.getTicker()

                if (response.isSuccessful) {
                    val body = response.body()
                    val lastStr = body?.ticker?.last
                    val dateSec = body?.ticker?.date

                    val lastValue = lastStr?.toDoubleOrNull()
                    if (lastValue != null) {
                        val nf = NumberFormat.getCurrencyInstance(Locale("pt", "BR"))
                        valueText = nf.format(lastValue)
                    } else {
                        valueText = "R$ 0,00"
                    }

                    // formata data
                    if (dateSec != null) {
                        val date = Date(dateSec * 1000L)
                        val sdf = SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.getDefault())
                        dateText = sdf.format(date)
                    } else {
                        dateText = "--/--/---- --:--:--"
                    }

                } else {
                    val errorMessage = when (response.code()) {
                        400 -> "Bad Request"
                        401 -> "Unauthorized"
                        403 -> "Forbidden"
                        404 -> "Not Found"
                        else -> "Unknown error: ${response.code()}"
                    }
                    Toast.makeText(context, errorMessage, Toast.LENGTH_LONG).show()
                }
            } catch (e: Exception) {
                Toast.makeText(context, "Falha na chamada: ${e.message}", Toast.LENGTH_LONG).show()
            } finally {
                loading = false
            }
        }
    }

    LaunchedEffect(Unit) {
        fetchTicker()
    }

    val backgroundColor = if (isDarkTheme) Color(0xFF121212) else Color.White
    val textColor = if (isDarkTheme) Color.White else Color.Black

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(backgroundColor)
    ) {
        CenterAlignedTopAppBar(
            modifier = Modifier.height(58.dp),
            title = {
                Text(
                    text = "Seja bem-vindo ao Android Crypto Monitor!",
                    color = Color.White,
                    fontSize = 16.sp,
                    maxLines = 1
                )
            },
            colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                containerColor = Color(0xFF1976D2) // azul
            )
        )

        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(32.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Cotação - BITCOIN",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = textColor,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(12.dp))

                if (loading) {
                    CircularProgressIndicator(modifier = Modifier.size(48.dp))
                } else {
                    Text(
                        text = valueText,
                        fontSize = 32.sp,
                        fontWeight = FontWeight.Bold,
                        color = textColor
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = dateText,
                    fontSize = 12.sp,
                    color = textColor,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(20.dp))

                Button(
                    onClick = { isDarkTheme = !isDarkTheme },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (isDarkTheme) Color(0xFF455A64) else Color(0xFF607D8B),
                        contentColor = Color.White
                    ),
                    modifier = Modifier
                        .wrapContentWidth()
                        .height(44.dp)
                ) {
                    Text(
                        text = if (isDarkTheme) "Modo Claro" else "Modo Escuro",
                        fontSize = 14.sp
                    )
                }


                Button(
                    onClick = { fetchTicker() },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF2E7D32),
                        contentColor = Color.White
                    ),
                    modifier = Modifier
                        .wrapContentWidth()
                        .height(44.dp)
                ) {
                    Text(text = "ATUALIZAR", fontSize = 14.sp)
                }
            }
        }
    }
}