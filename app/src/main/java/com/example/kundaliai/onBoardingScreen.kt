// kotlin
package com.example.kundaliai.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.tooling.preview.Preview
import com.example.kundaliai.ui.theme.DeepPurple
import com.example.kundaliai.ui.theme.DeepSlate
import com.example.kundaliai.ui.theme.RichGold

@Composable
fun OnboardingScreen(navController: NavController) {


    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Brush.verticalGradient(listOf(DeepSlate, DeepPurple)))
            .padding(horizontal = 24.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(Modifier.height(48.dp))

            Box(
                modifier = Modifier
                    .size(160.dp),
                contentAlignment = Alignment.Center
            ) {
                Canvas(modifier = Modifier.matchParentSize()) {
                    val stroke = 2.dp.toPx()
                    drawCircle(
                        color = Color(0x22333333),
                        radius = size.minDimension / 2,
                        style = androidx.compose.ui.graphics.drawscope.Stroke(width = stroke)
                    )
                    drawCircle(
                        color = RichGold,
                        radius = size.minDimension / 2 - 8.dp.toPx(),
                        style = androidx.compose.ui.graphics.drawscope.Stroke(
                            width = 1.8f,
                            pathEffect = PathEffect.dashPathEffect(floatArrayOf(6f, 6f), 0f),
                            cap = StrokeCap.Round
                        )
                    )
                }

                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "✦",
                        color = RichGold,
                        fontSize = 44.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                    Spacer(Modifier.height(4.dp))
                }
            }

            Spacer(Modifier.height(28.dp))

            Text(
                text = "DRISHTI",
                color = RichGold,
                fontSize = 38.sp,
                fontWeight = FontWeight.ExtraBold,
                letterSpacing = 4.sp
            )

            Text(
                text = "VEDIC ORACLE",
                color = RichGold.copy(alpha = 0.85f),
                fontSize = 14.sp,
                letterSpacing = 3.sp,
                modifier = Modifier.padding(top = 6.dp)
            )

            Spacer(Modifier.height(28.dp))

            Box(
                modifier = Modifier
                    .width(48.dp)
                    .height(1.dp)
                    .background(RichGold.copy(alpha = 0.25f))
            )

            Spacer(Modifier.height(28.dp))

            Text(
                text = "\"Om Bhur Bhuva Swaha\"",
                color = Color(0xFFBBC7D2),
                fontSize = 16.sp,
                textAlign = TextAlign.Center
            )

            Spacer(Modifier.height(12.dp))

            Text(
                text = "Align your Karma with the Stars. Your destiny is written in the movement of the Grahas.",
                color = Color(0xFF8FA1AD),
                fontSize = 14.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(horizontal = 16.dp)
            )

            Spacer(Modifier.height(36.dp))

            Surface(
                shape = RoundedCornerShape(8.dp),
                border = BorderStroke(1.dp, RichGold),
                color = Color(0x0AFFFFFF),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
                    .clickable { navController.navigate("liveSession") }
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Text(
                        text = "Star your journey ",
                        color = RichGold,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }

            Spacer(Modifier.height(48.dp))
        }
    }
}
@Preview(showBackground = true)
@Composable
fun OnboardingScreenPreview() {
    OnboardingScreen(navController = androidx.navigation.compose.rememberNavController())
}

