package com.linha.myreportcity.screen

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet
import com.linha.myreportcity.viewmodel.ReportViewModel
import com.linha.myreportcity.viewmodel.UserViewModel

@Composable
fun SolarReportScreen(
    userVM: UserViewModel,
    reportVM: ReportViewModel
) {
    val token by userVM.tempToken.collectAsState()

    val monthStatusCount by reportVM.monthStatusCount.collectAsState()

    LaunchedEffect(token) {
        if (token != null) {
            reportVM.getCountMonthStatus(token.toString(), 1)
            reportVM.getCountMonthStatus(token.toString(), 2)
            reportVM.getCountMonthStatus(token.toString(), 3)
        }
    }

    val pendingData: List<Float> = monthStatusCount[1]
        ?.filterNotNull() // pengecekan jika ada null
        ?: emptyList()

    val onProgressData: List<Float> = monthStatusCount[2]
        ?.filterNotNull()
        ?: emptyList()

    val completeData: List<Float> = monthStatusCount[3]
        ?.filterNotNull()
        ?: emptyList()

    val lineChartData: Map<String, List<Float>> = mapOf(
        "PENDING" to pendingData,
        "ON PROGRESS" to onProgressData,
        "COMPLETE" to completeData
    )
    val months = listOf("Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec")

    Column(modifier = Modifier.padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = "STATISTIK LAPORAN STATUS",
            fontWeight = FontWeight.Bold,
            fontSize = 18.sp
        )
        Text(
            text = "PER TAHUN SEKARANG",
            fontSize = 14.sp
        )
        Spacer(Modifier.height(24.dp))

        MultiLineChart(chartData = lineChartData, xLabels = months)
    }
}

@Composable
fun MultiLineChart(
    chartData: Map<String, List<Float>>,
    xLabels: List<String>
) {
    AndroidView(
        modifier = Modifier
            .fillMaxWidth()
            .height(350.dp),
        factory = { context ->
            LineChart(context)
        },
        update = { chart ->
            val dataSets = mutableListOf<LineDataSet>()

            // Definisikan warna untuk setiap seri data (sesuai contoh)
            val colors = listOf(
                Color(0xFF04D4E3).toArgb(), // Biru (PENDING)
                Color(0xFFC7E102).toArgb(), // Kuning (ON PROGRESS)
                Color(0xFF03D729).toArgb()  // Hijau (COMPLETE)
            )

            // Loop melalui setiap periode (dataset)
            chartData.entries.forEachIndexed { setIndex, entry ->
                val label = entry.key
                val values = entry.value

                // Konversi List<Float> ke List<Entry>
                val entries = values.mapIndexed { index, value ->
                    Entry(index.toFloat(), value)
                }

                // Buat DataSet untuk satu garis
                val dataSet = LineDataSet(entries, label).apply {
                    color = colors[setIndex % colors.size]
                    setCircleColor(colors[setIndex % colors.size])
                    lineWidth = 1f
                    circleRadius = 4f
                    setDrawCircleHole(false)
                    mode = LineDataSet.Mode.CUBIC_BEZIER // Untuk garis yang lebih halus
                    setDrawValues(false) // Jangan tampilkan angka di atas titik
                }
                dataSets.add(dataSet)
            }

            val lineData = LineData(dataSets as List<ILineDataSet>)
            chart.data = lineData

            // --- Konfigurasi Sumbu X (Bulan) ---
            chart.xAxis.apply {
                valueFormatter = IndexAxisValueFormatter(xLabels)
                position = XAxis.XAxisPosition.BOTTOM
                granularity = 1f // Jarak antar bulan harus 1
                setDrawGridLines(false)
                textSize = 10f
            }

            // --- Konfigurasi Sumbu Y Kiri ---
            chart.axisLeft.apply {
                axisMinimum = 0.0f // jumlah nilai status di kiri
                axisMaximum = 12.0f // maximum nilai status
                granularity = 1.0f
                setDrawGridLines(true)
            }

            // --- Konfigurasi Umum ---
            chart.axisRight.isEnabled = false // Sumbu kanan dinonaktifkan
            chart.description.isEnabled = false // Hapus deskripsi default

            // Legenda di bawah, Horizontal
            chart.legend.apply {
                form = com.github.mikephil.charting.components.Legend.LegendForm.CIRCLE
                orientation = com.github.mikephil.charting.components.Legend.LegendOrientation.HORIZONTAL
                verticalAlignment = com.github.mikephil.charting.components.Legend.LegendVerticalAlignment.BOTTOM
                horizontalAlignment = com.github.mikephil.charting.components.Legend.LegendHorizontalAlignment.CENTER
                setDrawInside(false)
            }

            chart.extraBottomOffset = 15f

            chart.invalidate() // Gambar ulang chart
        }
    )
}