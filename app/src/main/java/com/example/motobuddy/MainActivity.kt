package com.example.motobuddy

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.motobuddy.navigation.AppNavigation
import com.example.motobuddy.navigation.Screen
import com.example.motobuddy.ui.theme.*

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MotoBuddyTheme {
                val navController = rememberNavController()

                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    AppNavigation(navController)
                }
            }
        }
    }
}

@Composable
fun MotoBuddyScreen(navController: NavController? = null) {
    val backgroundColor = MaterialTheme.colorScheme.background
    val primaryCyan = MaterialTheme.colorScheme.primary
    val secondaryOrange = MaterialTheme.colorScheme.secondary
    val textWhite = MaterialTheme.colorScheme.onBackground
    val textGrey = MaterialTheme.colorScheme.onSurfaceVariant

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(backgroundColor)
            .padding(horizontal = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Spacer(modifier = Modifier.height(48.dp))

        // Top Kicker Text
        Text(
            text = "YOUR NEXT CHAPTER STARTS HERE",
            color = primaryCyan,
            fontSize = 10.sp,
            letterSpacing = 1.5.sp,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.padding(bottom = 32.dp)
        )

        // Main Image Card with Overlapping Button
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(420.dp)
        ) {
            Image(
                painter = painterResource(id = R.drawable.moto_landscape),
                contentDescription = "Motorcycle Landscape",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxSize()
                    .clip(RoundedCornerShape(32.dp))
                    .background(CardBackground)
            )

            // Overlapping Action Button
            Box(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .offset(y = 32.dp)
                    .size(72.dp)
                    .background(secondaryOrange, RoundedCornerShape(24.dp))
                    .clickable {
                        navController?.navigate(Screen.MainHost.route)
                    },
                contentAlignment = Alignment.Center
            ) {
                // Creative Helmet & Wrench Logo
                Canvas(modifier = Modifier.size(36.dp).align(Alignment.Center)) {
                    val w = size.width
                    val h = size.height
                    val cX = w / 2
                    val cY = h / 2

                    // Tool: Wrench
                    rotate(45f, pivot = Offset(cX, cY)) {
                        drawRoundRect(
                            color = MidnightNavy,
                            topLeft = Offset(w * 0.4f, h * 0.15f),
                            size = Size(w * 0.2f, h * 0.7f),
                            cornerRadius = CornerRadius(w * 0.1f, w * 0.1f)
                        )
                        drawArc(
                            color = MidnightNavy,
                            startAngle = 135f,
                            sweepAngle = 270f,
                            useCenter = false,
                            topLeft = Offset(w * 0.25f, h * 0.05f),
                            size = Size(w * 0.5f, h * 0.3f),
                            style = Stroke(width = w * 0.12f, cap = StrokeCap.Round)
                        )
                        drawCircle(
                            color = MidnightNavy,
                            radius = w * 0.2f,
                            center = Offset(w * 0.5f, h * 0.85f),
                            style = Stroke(width = w * 0.12f)
                        )
                    }

                    // Helmet (Overlayed)
                    val hR = w * 0.35f
                    
                    drawArc(
                        color = Color.White,
                        startAngle = 150f,
                        sweepAngle = 220f,
                        useCenter = true,
                        topLeft = Offset(cX - hR * 1.1f, cY - hR * 0.8f),
                        size = Size(hR * 2.2f, hR * 2.2f)
                    )
                    
                    drawRoundRect(
                        color = Color.White,
                        topLeft = Offset(cX - hR * 1.1f, cY + hR * 0.2f),
                        size = Size(hR * 1.8f, hR * 0.6f),
                        cornerRadius = CornerRadius(hR * 0.3f, hR * 0.3f)
                    )

                    drawRoundRect(
                        color = MidnightNavy,
                        topLeft = Offset(cX - hR * 0.1f, cY - hR * 0.3f),
                        size = Size(hR * 1.4f, hR * 0.7f),
                        cornerRadius = CornerRadius(hR * 0.2f, hR * 0.4f)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(64.dp))

        // Main App Title
        Text(
            text = "MotoBuddy",
            color = textWhite,
            fontSize = 42.sp,
            fontWeight = FontWeight.ExtraBold,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        // Subtitle
        Text(
            text = "Ride Smarter. Explore Further.",
            color = textGrey,
            fontSize = 16.sp,
            modifier = Modifier.padding(bottom = 24.dp)
        )

        // Category Text
        Text(
            text = "PLAN | RIDE | CARE",
            color = primaryCyan,
            fontSize = 12.sp,
            letterSpacing = 1.2.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 48.dp)
        )

        // Stylized Route Line
        Canvas(
            modifier = Modifier
                .width(180.dp)
                .height(40.dp)
        ) {
            val path = Path().apply {
                moveTo(0f, size.height * 0.9f)
                lineTo(size.width * 0.3f, size.height * 0.9f)
                cubicTo(
                    size.width * 0.4f, size.height * 0.9f,
                    size.width * 0.45f, 0f,
                    size.width * 0.55f, 0f
                )
                cubicTo(
                    size.width * 0.6f, 0f,
                    size.width * 0.65f, size.height * 0.4f,
                    size.width * 0.75f, size.height * 0.4f
                )
                cubicTo(
                    size.width * 0.8f, size.height * 0.4f,
                    size.width * 0.85f, 0f,
                    size.width * 0.9f, 0f
                )
                lineTo(size.width, 0f)
            }

            drawPath(
                path = path,
                color = secondaryOrange,
                style = Stroke(
                    width = 2.dp.toPx(),
                    cap = StrokeCap.Round,
                    join = StrokeJoin.Round
                )
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewMotoBuddyScreen() {
    MotoBuddyTheme {
        MotoBuddyScreen()
    }
}
