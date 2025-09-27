package com.linha.myreportcity.screen

import android.content.Context
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.linha.myreportcity.viewmodel.ReportViewModel
import com.linha.myreportcity.viewmodel.UserViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminScreen(
    modifier: Modifier = Modifier,
    userVM: UserViewModel,
    reportVM: ReportViewModel
) {
    val context = LocalContext.current

    val listState = rememberLazyListState()
    var counterUp by remember { mutableIntStateOf(0) }
    var counterDown by remember { mutableIntStateOf(0) }

    val token by userVM.tempToken.collectAsState()
    val reports by reportVM.getReports.observeAsState(emptyList())
    val countStatus by reportVM.statusCounts.collectAsState()
    val isLoading by reportVM.isLoading.collectAsState()
    val canPaginate by reportVM.canPaginate.collectAsState()

    val colorList = listOf(Color(0xFFB2F4F9), Color(0xFFEDFE71), Color(0xFF83FE99))
    val statusList = listOf("Pending", "On Progress", "Complete")

    var status by remember { mutableIntStateOf(1) }
    val menuItems = listOf("Pending", "On Progress", "Complete")

    var expanded by remember { mutableStateOf(false) }


    LaunchedEffect(token) {
        if (token != null) {
            reportVM.getCountStatus(token.toString(), 1)
            reportVM.getCountStatus(token.toString(), 2)
            reportVM.getCountStatus(token.toString(), 3)

            reportVM.getReportByStatus(token.toString(), status)
        }
    }

    LaunchedEffect(listState.isScrollInProgress) {
        val lastVisibleItemIndex = listState.layoutInfo.visibleItemsInfo.lastOrNull()?.index ?: 0
        if (lastVisibleItemIndex >= reports.size - 1 && reports.isNotEmpty() && !isLoading && canPaginate) {
            reportVM.getNextPageReportByStatus(token.toString(), status)
        }
    }


    LaunchedEffect(key1 = status) {
        if (token != null) {
            reportVM.getReportByStatus(token.toString(), status)
        }
    }

    LazyColumn(
        modifier = modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {

        // --- 1. LazyRow untuk Card Status ---
        item {
            LazyRow(
                modifier = Modifier.padding(10.dp),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                items(3) { index ->
                    val statusId = index + 1
                    val count = countStatus[statusId]

                    CardStatusBox(
                        color = colorList[index],
                        textStatus = statusList[index],
                        totalLaporan = count
                    )
                }
            }
        }

        // --- 2. SolarReportScreen (Chart) ---
        item {
            SolarReportScreen(
                userVM = userVM,
                reportVM = reportVM
            )
        }

        // -- 3. Header Daftar Laporan --
        item {
            Text(
                text = "DAFTAR SEMUA LAPORAN",
                modifier = Modifier.padding(top = 16.dp, bottom = 8.dp)
            )

            // --- Dropdown Menu Status ---
            ExposedDropdownMenuBox(
                expanded = expanded,
                onExpandedChange = { expanded = !expanded }
            ) {
                TextField(
                    modifier = Modifier
                        .fillMaxWidth()
                        .menuAnchor(),
                    value = status.toString(),
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Pilih Laporan Berdasarkan :") },
                    trailingIcon = {
                        ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
                    },
                    colors = ExposedDropdownMenuDefaults.textFieldColors()
                )

                ExposedDropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false }
                ) {
                    menuItems.forEach { item ->
                        DropdownMenuItem(
                            text = { Text(item) },
                            onClick = {
                                when (item) {
                                    "Pending" -> status = 1
                                    "On Progress" -> status = 2
                                    "Complete" -> status = 3
                                }
                                expanded = false
                            }
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
        }


        // 4 --- List Laporan By Status ---
        items(reports.size) { report ->
            val item = reports[report]

            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Person,
                            contentDescription = "Foto Profil",
                            modifier = Modifier.size(40.dp)
                        )
                        Spacer(Modifier.width(8.dp))
                        Text(text = item.user?.name ?: "Username", fontSize = 12.sp)
                    }
                    StatusDropdownButton(
                        context = context,
                        reportViewModel = reportVM,
                        userViewModel = userVM,
                        id = item.id,
                        currentStatus = item.status
                    )
                    Column(
                        horizontalAlignment = Alignment.End
                    ) {
                        Icon(
                            imageVector = Icons.Default.MoreVert,
                            contentDescription = "Setting Laporan",
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
                AsyncImage(
                    model = item.urlFoto,
                    contentDescription = "Foto Laporan",
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(300.dp),
                    contentScale = ContentScale.Crop
                )
                Row(
                    modifier = Modifier.padding(10.dp),
                    horizontalArrangement = Arrangement.Start
                ) {
                    Icon(
                        imageVector = Icons.Default.KeyboardArrowUp,
                        contentDescription = "Upvote",
                        modifier = Modifier.clickable(
                            onClick = { counterUp++ }
                        )
                    )
                    Text(text = counterUp.toString())
                    Spacer(Modifier.width(16.dp))
                    Icon(
                        imageVector = Icons.Default.KeyboardArrowDown,
                        contentDescription = "DownVote",
                        modifier = Modifier.clickable(
                            onClick = { counterDown++ }
                        )
                    )
                    Text(text = counterDown.toString())
                    Spacer(Modifier.width(16.dp))
                    Icon(
                        imageVector = Icons.Default.LocationOn,
                        contentDescription = "Lokasi Laporan",
                        modifier = Modifier.clickable(
                            onClick = {
                                // goto map
                            }
                        )
                    )
                }
                Text(text = item.deskripsi, modifier = Modifier.padding(start = 10.dp))
                Spacer(modifier = Modifier.height(32.dp))
            }
        }

        if (isLoading && canPaginate) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                    Spacer(modifier = Modifier.height(32.dp))
                }
            }
        }
    }
}

@Composable
fun StatusDropdownButton(
    context: Context,
    reportViewModel: ReportViewModel,
    userViewModel: UserViewModel,
    id: Int,
    currentStatus: Int
) {
    var expanded by remember { mutableStateOf(false) }

    val token by userViewModel.tempToken.collectAsState()

    val statusMap = mapOf(
        1 to "Pending",
        2 to "On Progress",
        3 to "Complete"
    )

    val currentStatusText = statusMap[currentStatus] ?: "Error"

    val buttonBackgroundColor = Color.White
    val buttonBorderColor = Color.Gray.copy(alpha = 0.7f)
    val contentColor = Color.Black

    Box(
        modifier = Modifier
            .clickable { expanded = !expanded }
            .border(
                border = BorderStroke(1.dp, buttonBorderColor),
                shape = RoundedCornerShape(8.dp)
            )
            .background(buttonBackgroundColor, shape = RoundedCornerShape(8.dp))
            .padding(horizontal = 12.dp, vertical = 8.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.width(120.dp)
        ) {
            Text(
                text = currentStatusText,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = contentColor
            )
            Icon(
                imageVector = Icons.Default.KeyboardArrowDown,
                contentDescription = "Ubah Status",
                modifier = Modifier.size(20.dp),
                tint = contentColor
            )
        }

        // Dropdown Menu
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            statusMap.forEach { (statusId, statusText) ->
                DropdownMenuItem(
                    text = { Text(statusText) },
                    onClick = {
                        if (token != null) {
                            reportViewModel.updateReportStatus(
                                token = token.toString(),
                                id = id,
                                status = statusId,
                                context = context
                            )
                        }
                        expanded = false
                    },
                    enabled = statusId != currentStatus // Nonaktifkan opsi status yang sudah aktif
                )
            }
        }
    }
}

@Composable
fun CardStatusBox(color: Color, textStatus: String, totalLaporan: Int?) {

    val cornerRadius = 8.dp

    Column(
        modifier = Modifier
            .padding(10.dp)
            .border(
                border = BorderStroke(1.dp, Color.Black),
                shape = RoundedCornerShape(cornerRadius)
            )
            .background(
                color = color,
                shape = RoundedCornerShape(cornerRadius)
            )
            .padding(10.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = textStatus)
        Spacer(modifier = Modifier.height(16.dp))
        Text(text = "Total : $totalLaporan")
    }
}