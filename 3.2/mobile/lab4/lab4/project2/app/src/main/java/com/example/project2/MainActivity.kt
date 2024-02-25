package com.example.project2

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.layout.*
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.consumeAllChanges
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.text.style.TextAlign


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            GestureDemoScreen()
        }
    }
    @Composable
    fun GestureDemoScreen() {
        var tapText by remember { mutableStateOf("Ждем Tap жест...") }

        var offsetX by remember { mutableStateOf(0f) }
        var offsetY by remember { mutableStateOf(0f) }

        var scale by remember { mutableStateOf(1f) }
        var rotation by remember { mutableStateOf(0f) }

        Column(modifier = Modifier.fillMaxSize()) {
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .background(Color.LightGray)
                    .pointerInput(Unit) {
                        detectTapGestures(
                            onTap = { offset ->
                                tapText = "Одиночное нажатие: (${offset.x.toInt()}, ${offset.y.toInt()})"
                            },
                            onDoubleTap = { offset ->
                                tapText = "Двойное нажатие: (${offset.x.toInt()}, ${offset.y.toInt()})"
                            },
                            onLongPress = { offset ->
                                tapText = "Долгое нажатие: (${offset.x.toInt()}, ${offset.y.toInt()})"
                            }
                        )
                    },
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = tapText,
                    fontSize = 18.sp,
                    textAlign = TextAlign.Center
                )
            }

            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .background(Color.Magenta)
                    .pointerInput(Unit) {
                        detectTransformGestures { centroid, pan, zoom, rotationChange ->
                            scale *= zoom
                            rotation += rotationChange
                        }
                    },
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Масштаб: ${"%.2f".format(scale)}\nВращение: ${"%.2f".format(rotation)}°",
                    fontSize = 18.sp,
                    textAlign = TextAlign.Center
                )
            }

            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .background(Color.Cyan)
                    .pointerInput(Unit) {
                        detectDragGestures(
                            onDrag = { change, dragAmount ->
                                change.consumeAllChanges()
                                offsetX += dragAmount.x
                                offsetY += dragAmount.y
                            }
                        )
                    },
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Перемещение: (${offsetX.toInt()}, ${offsetY.toInt()})",
                    fontSize = 18.sp,
                    textAlign = TextAlign.Center
                )
            }


        }
    }

}
