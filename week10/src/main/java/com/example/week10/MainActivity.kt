package com.example.week10

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.*
import com.google.accompanist.pager.*

data class Movie(
    val title: String,
    val rating: String,
    val desc: String,
    val runtime: String,
    val director: String,
    val cast: String,
    val imageRes: Int
)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MovieApp()
        }
    }
}

@OptIn(ExperimentalPagerApi::class)
@Composable
fun MovieApp() {
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = "home") {
        composable("home") { HomeScreen(navController) }
        composable("detail/{movieIndex}") {
            val index = it.arguments?.getString("movieIndex")?.toInt() ?: 0
            DetailScreen(index, navController)
        }
        composable("timetable/{movieIndex}") {
            val index = it.arguments?.getString("movieIndex")?.toInt() ?: 0
            TimeTableScreen(index, navController)
        }
    }
}

@OptIn(ExperimentalPagerApi::class)
@Composable
fun HomeScreen(navController: NavController) {

    val movies = listOf(
        Movie(
            "나우 유 씨 미 3",
            "8.5/10",
            "세계 최고의 마술사들이 다시 뭉쳐 거대한 음모에 맞선다.",
            "121분",
            "존 M. 추",
            "제시 아이젠버그, 우디 해럴슨, 데이브 프랑코",
            R.drawable.nowyouseeme3
        ),
        Movie(
            "주토피아 2",
            "7.8/10",
            "주디와 닉이 새로운 사건을 해결하는 코믹 수사 액션.",
            "118분",
            "바이런 하워드",
            "지니퍼 굿윈, 제이슨 베이트먼",
            R.drawable.zootofia2
        ),
        Movie(
            "아바타3 불과 재",
            "9.0/10",
            "판도라의 새로운 부족과 불의 세력에 맞서는 전쟁.",
            "192분",
            "제임스 카메론",
            "샘 워싱턴, 조 샐다나, 시고니 위버",
            R.drawable.avatar
        )
    )

    val pagerState = rememberPagerState()

    var expanded by remember { mutableStateOf(false) }

    val recommendedMovies = listOf(
        "존윅 시리즈",
        "인셉션",
        "F1",
        "죽은 시인의 사회",
        "노트북"
    )

    Scaffold(
        bottomBar = {
            BottomAppBar(
                containerColor = Color.White,
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp)
            ) {
                Text("☰ 메뉴", modifier = Modifier.weight(1f).clickable {}, fontSize = 16.sp, textAlign = TextAlign.Center)
                Box(
                    modifier = Modifier.size(60.dp).clip(CircleShape).background(Color(0xFFE50914)).clickable {},
                    contentAlignment = Alignment.Center
                ) {
                    Text("예매✨예약", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                }
                Text("🍿 팝콘 구매", modifier = Modifier.weight(1f).clickable {}, fontSize = 16.sp, textAlign = TextAlign.Center)
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier.padding(padding).fillMaxSize().background(Color.White).padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Text("Movie 예매", fontSize = 24.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(bottom = 16.dp))

            HorizontalPager(
                count = movies.size,
                state = pagerState,
                modifier = Modifier.fillMaxWidth().height(350.dp)
            ) { page ->
                Image(
                    painter = painterResource(id = movies[page].imageRes),
                    contentDescription = movies[page].title,
                    modifier = Modifier.width(250.dp).height(330.dp).clip(RoundedCornerShape(16.dp))
                        .clickable { navController.navigate("detail/$page") }
                )
            }

            Spacer(Modifier.height(16.dp))

            val m = movies[pagerState.currentPage]
            Text(m.title, fontSize = 20.sp, fontWeight = FontWeight.Bold)
            Text("평점: ${m.rating}", fontSize = 16.sp)

            Spacer(Modifier.height(16.dp))

            Button(
                onClick = { navController.navigate("timetable/${pagerState.currentPage}") },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE50914)),
                modifier = Modifier.fillMaxWidth(0.6f).height(48.dp)
            ) {
                Text("예매하기", color = Color.White, fontWeight = FontWeight.Bold)
            }

            Spacer(Modifier.height(12.dp))

            Button(
                onClick = { expanded = !expanded },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1976D2)),
                modifier = Modifier.height(40.dp)
            ) {
                Text("영화 추천! 😎", color = Color.White, fontWeight = FontWeight.Bold)
            }

            if (expanded) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 12.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color(0xFFF0F0F0))
                        .padding(16.dp)
                ) {
                    Column {
                        Text("저는 이것을 추천해요!", fontSize = 18.sp, fontWeight = FontWeight.Bold)
                        Spacer(Modifier.height(8.dp))
                        recommendedMovies.forEach {
                            Text("• $it", fontSize = 16.sp)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun DetailScreen(movieIndex: Int, navController: NavController) {

    val movies = listOf(
        Movie("나우 유 씨 미 3","8.5/10","세계 최고의 마술사들이 다시 뭉쳐 거대한 음모에 맞선다.","121분","존 M. 추","제시 아이젠버그, 우디 해럴슨, 데이브 프랑코",R.drawable.nowyouseeme3),
        Movie("주토피아 2","7.8/10","주디와 닉이 새로운 사건을 해결하는 코믹 수사 액션.","118분","바이런 하워드","지니퍼 굿윈, 제이슨 베이트먼",R.drawable.zootofia2),
        Movie("아바타3 불과 재","9.0/10","판도라의 새로운 부족과 불의 세력에 맞서는 전쟁.","192분","제임스 카메론","샘 워싱턴, 조 샐다나, 시고니 위버",R.drawable.avatar)
    )

    val movie = movies[movieIndex]

    Column(
        modifier = Modifier.fillMaxSize().background(Color.White).padding(16.dp)
    ) {

        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(bottom = 12.dp)) {
            Text("←", fontSize = 30.sp, modifier = Modifier.clickable { navController.popBackStack() }.padding(end = 12.dp))
            Text(movie.title, fontSize = 22.sp, fontWeight = FontWeight.Bold)
        }

        Image(
            painter = painterResource(id = movie.imageRes),
            contentDescription = movie.title,
            modifier = Modifier.size(width = 220.dp, height = 320.dp).clip(RoundedCornerShape(16.dp))
        )

        Spacer(Modifier.height(16.dp))

        Text("평점: ${movie.rating}", fontSize = 18.sp, fontWeight = FontWeight.Bold)
        Text("러닝타임: ${movie.runtime}", fontSize = 16.sp)
        Text("감독: ${movie.director}", fontSize = 16.sp)
        Text("출연: ${movie.cast}", fontSize = 16.sp)

        Spacer(Modifier.height(12.dp))
        Text(movie.desc, fontSize = 16.sp)

        Spacer(Modifier.height(24.dp))

        Button(
            onClick = { navController.navigate("timetable/$movieIndex") },
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE50914)),
            modifier = Modifier.fillMaxWidth().height(48.dp)
        ) {
            Text("예매하기", color = Color.White, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
fun TimeTableScreen(movieIndex: Int, navController: NavController) {

    val movies = listOf(
        Movie("나우 유 씨 미 3","8.5/10","세계 최고의 마술사들이 다시 뭉쳐 거대한 음모에 맞선다.","121분","존 M. 추","제시 아이젠버그, 우디 해럴슨, 데이브 프랑코",R.drawable.nowyouseeme3),
        Movie("주토피아 2","7.8/10","주디와 닉이 새로운 사건을 해결하는 코믹 수사 액션.","118분","바이런 하워드","지니퍼 굿윈, 제이슨 베이트먼",R.drawable.zootofia2),
        Movie("아바타3 불과 재","9.0/10","판도라의 새로운 부족과 불의 세력에 맞서는 전쟁.","192분","제임스 카메론","샘 워싱턴, 조 샐다나, 시고니 위버",R.drawable.avatar)
    )

    val movie = movies[movieIndex]
    val branchList = listOf("경기도", "서울", "경북", "대전", "강원도")

    val dateList = listOf("오늘", "내일", "모레")
    var selectedDate by remember { mutableStateOf(0) }

    val timeData = mapOf(
        0 to listOf("12:00", "14:30", "18:00"),
        1 to listOf("11:30", "16:00", "20:10"),
        2 to listOf("10:00", "13:45", "19:00", "22:20")
    )

    Column(
        modifier = Modifier.fillMaxSize().background(Color.White).padding(16.dp)
    ) {

        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(bottom = 12.dp)) {
            Text("←", fontSize = 30.sp, modifier = Modifier.clickable { navController.popBackStack() }.padding(end = 12.dp))
            Text("${movie.title} 예매", fontSize = 22.sp, fontWeight = FontWeight.Bold)
        }

        Image(
            painter = painterResource(id = movie.imageRes),
            contentDescription = movie.title,
            modifier = Modifier.size(width = 120.dp, height = 180.dp).clip(RoundedCornerShape(12.dp)).padding(bottom = 16.dp)
        )

        Text("날짜 선택", fontSize = 20.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(bottom = 8.dp))

        Row(horizontalArrangement = Arrangement.spacedBy(12.dp), modifier = Modifier.fillMaxWidth()) {
            dateList.forEachIndexed { index, label ->
                Box(
                    modifier = Modifier.weight(1f).height(45.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(if (selectedDate == index) Color(0xFFE50914) else Color(0xFFE0E0E0))
                        .clickable { selectedDate = index },
                    contentAlignment = Alignment.Center
                ) {
                    Text(label, color = if (selectedDate == index) Color.White else Color.Black, fontSize = 16.sp)
                }
            }
        }

        Spacer(Modifier.height(16.dp))

        Text("지점 선택", fontSize = 20.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(bottom = 8.dp))

        Row(horizontalArrangement = Arrangement.spacedBy(12.dp), modifier = Modifier.fillMaxWidth()) {
            branchList.forEach { b ->
                Box(
                    modifier = Modifier.weight(1f).height(50.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(Color(0xFF1976D2))
                        .clickable {},
                    contentAlignment = Alignment.Center
                ) {
                    Text(b, color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                }
            }
        }

        Spacer(Modifier.height(16.dp))

        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(timeData[selectedDate] ?: emptyList()) { time ->
                Box(
                    modifier = Modifier.height(70.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color(0xFFE50914))
                        .clickable {},
                    contentAlignment = Alignment.Center
                ) {
                    Text(time, color = Color.White, fontSize = 20.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewMovieApp() {
    MovieApp()
}
