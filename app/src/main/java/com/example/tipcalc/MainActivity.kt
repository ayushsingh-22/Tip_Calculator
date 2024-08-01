package com.example.tipcalc

import android.os.Bundle
import android.widget.Space
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.tipcalc.ui.theme.TipCalcTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            TipCalcTheme {
                Box(modifier = Modifier
                    .fillMaxSize()
                    .size(2.dp)) {

                    Column {
                        var amount by remember { mutableStateOf("") }
                        var splitValue by remember { mutableIntStateOf(1) }
                        var tipPercentage by remember { mutableFloatStateOf(0f) }

                        Calculation(
                            amount = amount,
                            amountChanged = { newValue -> amount = newValue },
                            splitValue = splitValue,
                            splitChanged = { newSplit -> splitValue = newSplit },
                            tipPercentage = tipPercentage,
                            tipChanged = { newTip -> tipPercentage = newTip }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun Calculation(
    amount: String,
    amountChanged: (String) -> Unit,
    splitValue: Int,
    splitChanged: (Int) -> Unit,
    tipPercentage: Float,
    tipChanged: (Float) -> Unit
) {
    val keyboard = LocalSoftwareKeyboardController.current
    var totalAmount by remember { mutableFloatStateOf(0f) }
    var totalPerPerson by remember { mutableFloatStateOf(0f) }

    LaunchedEffect(amount, splitValue, tipPercentage) {
        val amountValue = amount.toFloatOrNull() ?: 0f
        val tipValue = amountValue * (tipPercentage / 100)
        totalAmount = amountValue + tipValue
        totalPerPerson = if (splitValue > 0) totalAmount / splitValue else totalAmount
    }

    Calculator(totalPerPerson)

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(5.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 10.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White)
    ) {

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp),

            ) {
            OutlinedTextField(
                value = amount,
                onValueChange = amountChanged,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                placeholder = { Text(text = "Enter your amount",
                    fontWeight = FontWeight.Bold,) },
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Number,
                    imeAction = ImeAction.Done
                ),
                keyboardActions = KeyboardActions(onDone = {
                    keyboard?.hide()
                })
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Split",
                    fontSize = 20.sp
                )

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    CustomButton(
                        onClick = { if (splitValue > 1) splitChanged(splitValue - 1) },
                        text = "-"
                    )

                    Text(fontSize = 24.sp, text = "$splitValue")

                    CustomButton(
                        onClick = { splitChanged(splitValue + 1) },
                        text = "+"
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Tip",
                    fontSize = 20.sp
                )

                Text(text = "₹ ${(amount.toFloatOrNull() ?: 0f) * (tipPercentage / 100)}")
            }

            Column(
                modifier = Modifier.padding(horizontal = 16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(text = "Tip Percentage: ${tipPercentage.toInt()}%")
                Slider(
                    value = tipPercentage,
                    colors = SliderDefaults.colors(
                        thumbColor = Color.Red,
                        activeTrackColor = Color.Blue,
                        inactiveTrackColor = Color.Gray,
                        activeTickColor = Color.Green,
                        inactiveTickColor = Color.Yellow
                    ),
                    onValueChange = tipChanged,
                    valueRange = 1f..100f,
                    steps = 10,
                    modifier = Modifier.fillMaxWidth()

                )
            }

        }
    }
}

@Composable
fun Calculator(amountPerPerson: Float) {
    Card(
        shape = RectangleShape,
        modifier = Modifier
            .fillMaxWidth()
            .padding(10.dp)
            .padding(top = 40.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 10.dp),

        ) {
        Column {
            Card(
                colors = CardDefaults.cardColors(
                    containerColor = Color.White,),
                        elevation = CardDefaults.cardElevation(defaultElevation = 10.dp),
            ) {
                Text(
                    text = "Total per person",
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                        .align(Alignment.CenterHorizontally),
                    style = MaterialTheme.typography.bodyMedium,
                    fontSize = 20.sp,
                    color = Color.Black
                )

                Text(
                    text = "₹ $amountPerPerson",
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                        .align(Alignment.CenterHorizontally),
                    style = MaterialTheme.typography.displayLarge,
                    fontSize = 55.sp,
                    color = Color.Black
                )
            }
        }
    }
}

@Composable
fun CustomButton(
    onClick: () -> Unit,
    text: String
) {
    Button(
        onClick = onClick,
        colors = ButtonDefaults.buttonColors(containerColor = Color.Gray),
        shape = RoundedCornerShape(8.dp),
        modifier = Modifier
            .size(width = 50.dp, height = 50.dp)
    ) {
        Text(text = text, color = Color.White, fontSize = 24.sp)
    }
}
