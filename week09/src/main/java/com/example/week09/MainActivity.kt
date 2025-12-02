package com.example.w09

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlin.random.Random

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MaterialTheme(
                colorScheme = lightColorScheme()
            ) {
                GuessNumberGame()
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GuessNumberGame() {
    var randomNumber by remember { mutableStateOf(Random.nextInt(1, 100)) }
    var userInput by remember { mutableStateOf("") }
    var message by remember { mutableStateOf("1부터 100 사이 숫자를 맞혀보세요!") }
    var attemptCount by remember { mutableStateOf(0) }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("🎯 숫자 맞히기 게임") }
            )
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(
                    text = message,
                    fontSize = 20.sp,
                    textAlign = TextAlign.Center,
                    fontWeight = FontWeight.Bold
                )

                OutlinedTextField(
                    value = userInput,
                    onValueChange = { userInput = it },
                    label = { Text("숫자 입력") },
                    singleLine = true
                )

                Button(
                    onClick = {
                        val guess = userInput.toIntOrNull()
                        if (guess == null) {
                            message = "숫자를 입력해주세요!"
                        } else {
                            attemptCount++
                            when {
                                guess < randomNumber -> message = "UP! 🔼 더 큰 수예요."
                                guess > randomNumber -> message = "DOWN! 🔽 더 작은 수예요."
                                else -> message = "🎉 정답! (${attemptCount}회 시도)"
                            }
                        }
                    }
                ) {
                    Text("확인")
                }

                Button(
                    onClick = {
                        randomNumber = Random.nextInt(1, 100)
                        userInput = ""
                        attemptCount = 0
                        message = "게임이 새로 시작되었습니다!"
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.secondaryContainer
                    )
                ) {
                    Text("다시 시작")
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewGuessNumberGame() {
    MaterialTheme {
        GuessNumberGame()
    }
}
