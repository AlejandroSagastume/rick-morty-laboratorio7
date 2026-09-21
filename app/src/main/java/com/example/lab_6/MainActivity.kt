package com.example.lab_6

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.lab_6.ui.theme.LAB_6Theme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            LAB_6Theme {
                CounterScreen()
            }
        }
    }
}

data class HistoryItem(val value: Int, val isIncrement: Boolean)

@Composable
fun CounterScreen() {
    var count by remember { mutableIntStateOf(0) }
    var totalIncrements by remember { mutableIntStateOf(0) }
    var totalDecrements by remember { mutableIntStateOf(0) }
    var maxValue by remember { mutableIntStateOf(0) }
    var minValue by remember { mutableIntStateOf(0) }
    var totalChanges by remember { mutableIntStateOf(0) }
    var history by remember { mutableStateOf(listOf<HistoryItem>()) }

    fun onIncrement() {
        val newValue = count + 1
        count = newValue
        totalIncrements++
        totalChanges++
        if (newValue > maxValue) maxValue = newValue
        history = history + HistoryItem(newValue, true)
    }

    fun onDecrement() {
        val newValue = count - 1
        count = newValue
        totalDecrements++
        totalChanges++
        if (newValue < minValue) minValue = newValue
        history = history + HistoryItem(newValue, false)
    }

    fun onReset() {
        count = 0
        totalIncrements = 0
        totalDecrements = 0
        maxValue = 0
        minValue = 0
        totalChanges = 0
        history = emptyList()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .systemBarsPadding()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        LazyColumn(
            modifier = Modifier.weight(1f),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            item {
                Text(
                    text = "Alejandro Sagastume",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(vertical = 16.dp)
                )

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center,
                    modifier = Modifier.padding(vertical = 24.dp)
                ) {
                    IconButton(
                        onClick = { onDecrement() },
                        modifier = Modifier.size(48.dp),
                        colors = IconButtonDefaults.iconButtonColors(
                            containerColor = Color(0xFF3F51B5),
                            contentColor = Color.White
                        )
                    ) {
                        Text("-", fontSize = 24.sp, fontWeight = FontWeight.Bold)
                    }

                    Text(
                        text = count.toString(),
                        style = MaterialTheme.typography.displayLarge,
                        modifier = Modifier.padding(horizontal = 32.dp),
                        fontSize = 64.sp
                    )

                    IconButton(
                        onClick = { onIncrement() },
                        modifier = Modifier.size(48.dp),
                        colors = IconButtonDefaults.iconButtonColors(
                            containerColor = Color(0xFF3F51B5),
                            contentColor = Color.White
                        )
                    ) {
                        Text("+", fontSize = 24.sp, fontWeight = FontWeight.Bold)
                    }
                }

                HorizontalDivider(modifier = Modifier.padding(vertical = 16.dp))

                Column(modifier = Modifier.fillMaxWidth()) {
                    StatRow("Total incrementos:", totalIncrements.toString())
                    StatRow("Total decrementos:", totalDecrements.toString())
                    StatRow("Valor máximo:", maxValue.toString())
                    StatRow("Valor mínimo:", minValue.toString())
                    StatRow("Total cambios:", totalChanges.toString())
                }

                Spacer(modifier = Modifier.height(24.dp))

                Text(
                    text = "Historial:",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 8.dp).fillMaxWidth()
                )
            }


            val historyRows = history.chunked(5)
            items(historyRows) { rowItems ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Start
                ) {
                    rowItems.forEach { item ->
                        HistoryItemView(item)
                    }
                }
            }
        }

        Button(
            onClick = { onReset() },
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp),
            shape = RoundedCornerShape(8.dp)
        ) {
            Text(text = "Reiniciar")
        }
    }
}

@Composable
fun StatRow(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = label, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.bodyLarge)
        Text(text = value, style = MaterialTheme.typography.bodyLarge)
    }
}

@Composable
fun HistoryItemView(item: HistoryItem) {
    Surface(
        color = if (item.isIncrement) Color(0xFF2E7D32) else Color(0xFFC62828),
        shape = RoundedCornerShape(8.dp),
        modifier = Modifier
            .padding(4.dp)
            .size(50.dp)
    ) {
        Box(contentAlignment = Alignment.Center) {
            Text(
                text = item.value.toString(),
                color = Color.White,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun CounterScreenPreview() {
    LAB_6Theme {
        CounterScreen()
    }
}
