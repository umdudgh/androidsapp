package com.example.week06

import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.foundation.layout.ColumnScope
import com.example.week06.ui.theme.AndroidappTheme
import kotlinx.coroutines.delay
import kotlin.random.Random

data class Bubble(
    val id: Int,
    val position: Offset,
    val radius: Float,
    val color: Color,
    val velocityX: Float,
    val velocityY: Float
)

class MainActivity : ComponentActivity() {
    private val PREFS_NAME = "bubble_game_prefs"
    private val HIGH_SCORE_KEY = "high_score"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            AndroidappTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val highScore = getHighScore()
                    BubbleGameScreen(
                        onGameOver = { currentScore ->
                            if (currentScore > highScore) {
                                saveHighScore(currentScore)
                            }
                        },
                        initialHighScore = highScore
                    )
                }
            }
        }
    }

    private fun getHighScore(): Int {
        val prefs = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        return prefs.getInt(HIGH_SCORE_KEY, 0)
    }

    private fun saveHighScore(score: Int) {
        val prefs = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        prefs.edit().putInt(HIGH_SCORE_KEY, score).apply()
    }
}

@Composable
fun BubbleGameScreen(
    onGameOver: (currentScore: Int) -> Unit,
    initialHighScore: Int
) {
    var score by remember { mutableStateOf(0) }
    var timeLeft by remember { mutableStateOf(30) }
    var isGameOver by remember { mutableStateOf(false) }
    var highScore by remember { mutableStateOf(initialHighScore) }

    val density = LocalDensity.current

    var maxWidthPx by remember { mutableStateOf(0f) }
    var maxHeightPx by remember { mutableStateOf(0f) }
    var bubbles by remember { mutableStateOf(listOf<Bubble>()) }

    // 마지막 터치 시간 기억 (초 단위)
    var lastTouchTime by remember { mutableStateOf(System.currentTimeMillis()) }

    // 화면 크기 감지
    val maxWidthDp = remember { mutableStateOf(0.dp) }
    val maxHeightDp = remember { mutableStateOf(0.dp) }

    // 버블 위치 업데이트 (60fps)
    LaunchedEffect(bubbles, maxWidthPx, maxHeightPx, isGameOver) {
        if (!isGameOver && maxWidthPx > 0 && maxHeightPx > 0) {
            while (true) {
                delay(16L)
                bubbles = bubbles.map { bubble ->
                    var newX = bubble.position.x + bubble.velocityX
                    var newY = bubble.position.y + bubble.velocityY
                    var newVx = bubble.velocityX
                    var newVy = bubble.velocityY

                    // 화면 경계 충돌 시 튕기기
                    if (newX - bubble.radius < 0f) {
                        newX = bubble.radius
                        newVx = -newVx
                    }
                    if (newX + bubble.radius > maxWidthPx) {
                        newX = maxWidthPx - bubble.radius
                        newVx = -newVx
                    }
                    if (newY - bubble.radius < 0f) {
                        newY = bubble.radius
                        newVy = -newVy
                    }
                    if (newY + bubble.radius > maxHeightPx) {
                        newY = maxHeightPx - bubble.radius
                        newVy = -newVy
                    }

                    bubble.copy(
                        position = Offset(newX, newY),
                        velocityX = newVx,
                        velocityY = newVy
                    )
                }
            }
        }
    }

    // 3초간 터치 없으면 기존 버블 모두 제거 후 3개 새로 생성
    LaunchedEffect(lastTouchTime, maxWidthPx, maxHeightPx, isGameOver) {
        if (!isGameOver && maxWidthPx > 0 && maxHeightPx > 0) {
            while (!isGameOver) {
                delay(500L) // 자주 체크
                val now = System.currentTimeMillis()
                if (now - lastTouchTime > 3000) {
                    // 3초 동안 터치 없으면 초기화
                    bubbles = List(3) {
                        createRandomBubble(maxWidthPx, maxHeightPx)
                    }
                    lastTouchTime = now // 초기화 후 터치 시간 갱신
                }
            }
        }
    }

    // 게임 시간 타이머
    LaunchedEffect(key1 = isGameOver) {
        if (!isGameOver && timeLeft > 0) {
            while (timeLeft > 0) {
                delay(1000L)
                timeLeft--
            }
            isGameOver = true
            onGameOver(score)
            if (score > highScore) {
                highScore = score
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .onSizeChanged {
                maxWidthDp.value = with(density) { it.width.toDp() }
                maxHeightDp.value = with(density) { it.height.toDp() }
                maxWidthPx = it.width.toFloat()
                maxHeightPx = it.height.toFloat()
                // 최초 화면 사이즈 측정 후 초기 버블 3개 생성
                if (bubbles.isEmpty()) {
                    bubbles = List(3) {
                        createRandomBubble(maxWidthPx, maxHeightPx)
                    }
                }
            }
    ) {
        GameStatusRow(score = score, timeLeft = timeLeft, highScore = highScore)

        if (!isGameOver) {
            bubbles.forEach { bubble ->
                BubbleComposable(bubble = bubble) {
                    // 터치 시 점수 증가, 마지막 터치 시간 업데이트, 클릭한 버블 제거 후 1개 새 버블 생성
                    score++
                    lastTouchTime = System.currentTimeMillis()
                    bubbles = bubbles.filter { it.id != bubble.id } + createRandomBubble(maxWidthPx, maxHeightPx)
                }
            }
        } else {
            Text(
                text = "Game Over!\nScore: $score\nHigh Score: $highScore",
                fontWeight = FontWeight.Bold,
                fontSize = 32.sp,
                modifier = Modifier.padding(16.dp)
            )
        }
    }
}

fun createRandomBubble(maxWidthPx: Float, maxHeightPx: Float): Bubble {
    val radius = Random.nextFloat() * 50 + 50
    return Bubble(
        id = Random.nextInt(),
        position = Offset(
            x = Random.nextFloat() * (maxWidthPx - 2 * radius) + radius,
            y = Random.nextFloat() * (maxHeightPx - 2 * radius) + radius
        ),
        radius = radius,
        color = Color(
            red = Random.nextInt(256) / 255f,
            green = Random.nextInt(256) / 255f,
            blue = Random.nextInt(256) / 255f,
            alpha = 0.8f
        ),
        velocityX = Random.nextFloat() * 8f - 4f,
        velocityY = Random.nextFloat() * 8f - 4f
    )
}

@Composable
fun BubbleComposable(bubble: Bubble, onClick: () -> Unit) {
    val density = LocalDensity.current
    val offsetXDp = with(density) { bubble.position.x.toDp() }
    val offsetYDp = with(density) { bubble.position.y.toDp() }

    Canvas(
        modifier = Modifier
            .size((bubble.radius * 2).dp)
            .offset(x = offsetXDp, y = offsetYDp)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = onClick
            )
    ) {
        drawCircle(
            color = bubble.color,
            radius = size.width / 2,
            center = center
        )
    }
}

@Composable
fun GameStatusRow(score: Int, timeLeft: Int, highScore: Int) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = androidx.compose.ui.Alignment.CenterVertically
    ) {
        Text(text = "Score: $score", fontSize = 24.sp, fontWeight = FontWeight.Bold)
        Text(text = "Time: ${timeLeft}s", fontSize = 24.sp, fontWeight = FontWeight.Bold)
        Text(text = "High Score: $highScore", fontSize = 24.sp, fontWeight = FontWeight.Bold)
    }
}

@Preview(showBackground = true)
@Composable
fun BubbleGamePreview() {
    AndroidappTheme {
        BubbleGameScreen(
            onGameOver = {},
            initialHighScore = 0
        )
    }
}
