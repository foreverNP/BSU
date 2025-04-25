package com.mockcrypto.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import android.graphics.Paint
import android.graphics.Typeface
import androidx.compose.ui.unit.sp
import com.mockcrypto.ui.theme.PositiveChange
import com.mockcrypto.ui.theme.NegativeChange
import java.math.BigDecimal
import java.text.DecimalFormat

@Composable
fun SparklineChart(
    sparklineData: List<BigDecimal>,
    modifier: Modifier = Modifier,
    lineColor: Color? = null,
    lineWidth: Float = 2f
) {
    if (sparklineData.isEmpty()) return
    
    val color = lineColor ?: if (sparklineData.first() <= sparklineData.last()) {
        PositiveChange
    } else {
        NegativeChange
    }
    
    Column(modifier = modifier) {
        // Title for the chart
        Text(
            text = "7-day Price Chart",
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Medium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        
        // Add extra padding on the left for price labels
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(180.dp)
                .padding(start = 40.dp) // Add padding for price labels
        ) {
            Canvas(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp)
            ) {
                val dataPoints = sparklineData.map { it.toFloat() }
                
                // Find min and max for scaling
                val minValue = dataPoints.minOrNull() ?: 0f
                val maxValue = dataPoints.maxOrNull() ?: 0f
                val valueRange = maxValue - minValue
                
                // Avoid division by zero
                if (valueRange <= 0f) return@Canvas
                
                // Create a Paint object for text
                val textPaint = Paint().apply {
                    this.color = android.graphics.Color.GRAY
                    textSize = 10.sp.toPx()
                    typeface = Typeface.DEFAULT
                    textAlign = Paint.Align.RIGHT
                }
                
                // Create a decimal formatter for price values
                val decimalFormat = DecimalFormat("$#,##0.00")
                
                // Draw horizontal grid lines (5 lines)
                val gridLineCount = 5
                val gridColor = Color.Gray.copy(alpha = 0.2f)
                val dashPathEffect = androidx.compose.ui.graphics.PathEffect.dashPathEffect(floatArrayOf(10f, 10f), 0f)
                
                for (i in 0..gridLineCount) {
                    val ratio = i.toFloat() / gridLineCount
                    val y = size.height - (ratio * size.height)
                    
                    // Calculate price at this grid line
                    val price = minValue + (valueRange * ratio)
                    val priceText = decimalFormat.format(price)
                    
                    // Draw grid line
                    drawLine(
                        color = gridColor,
                        start = Offset(0f, y),
                        end = Offset(size.width, y),
                        strokeWidth = 1f,
                        pathEffect = dashPathEffect
                    )
                    
                    // Draw price label
                    drawIntoCanvas {
                        it.nativeCanvas.drawText(
                            priceText,
                            -8f, // Position text to the left of the chart
                            y + (textPaint.textSize / 3), // Center text vertically with line
                            textPaint
                        )
                    }
                }
                
                // Draw vertical grid lines (7 lines for days)
                val dayCount = 7
                for (i in 0..dayCount) {
                    val x = i.toFloat() / dayCount * size.width
                    drawLine(
                        color = gridColor,
                        start = Offset(x, 0f),
                        end = Offset(x, size.height),
                        strokeWidth = 1f,
                        pathEffect = dashPathEffect
                    )
                }
                
                // Create path
                val path = Path()
                val xStep = size.width / (dataPoints.size - 1)
                
                dataPoints.forEachIndexed { index, value ->
                    // Scale value to available height
                    // We invert Y because in canvas Y grows downward
                    val x = index * xStep
                    val y = size.height - ((value - minValue) / valueRange * size.height)
                    
                    if (index == 0) {
                        path.moveTo(x, y)
                    } else {
                        path.lineTo(x, y)
                    }
                }
                
                // Draw the path
                drawPath(
                    path = path,
                    color = color,
                    style = Stroke(width = lineWidth)
                )
            }
        }
    }
} 