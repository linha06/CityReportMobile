package com.linha.myreportcity.screen

import androidx.compose.foundation.background
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
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.linha.myreportcity.viewmodel.ReportViewModel
import com.linha.myreportcity.viewmodel.UserViewModel

@Composable
fun HomeScreen(
    modifier: Modifier = Modifier,
    reportVM: ReportViewModel,
    usersVM: UserViewModel
) {
    var counterUp by remember { mutableIntStateOf(0) }
    var counterDown by remember { mutableIntStateOf(0) }

    val listState = rememberLazyListState()

    val isLoading by reportVM.isLoading.collectAsState()
    val canPaginate by reportVM.canPaginate.collectAsState()

    val token by usersVM.tempToken.collectAsState()
    val reports by reportVM.getReports.observeAsState(emptyList())

    // Memuat data pertama kali
    LaunchedEffect(key1 = token) {
        if (!token.isNullOrEmpty()) {
            reportVM.getAllReports(token.toString())
        }
    }

    // Memuat halaman selanjutnya jika sudah mencapai akhir list dan bisa paginate
    LaunchedEffect(listState.isScrollInProgress) {
        val lastVisibleItemIndex = listState.layoutInfo.visibleItemsInfo.lastOrNull()?.index ?: 0
        if (lastVisibleItemIndex >= reports.size - 1 && reports.isNotEmpty() && !isLoading && canPaginate) {
            reportVM.getNextReportPage(token.toString())
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color.White),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(modifier = Modifier.fillMaxWidth()) {

        }
        LazyColumn(
            state = listState,
            modifier = Modifier.fillMaxSize()
        ) {
            items(reports.size) { report ->
                val item = reports[report]
                val status = when (item.status) {
                    1 -> "Pending"
                    2 -> "on progress"
                    else -> "complete"
                }

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
                            Column {
                                Text(text = item.user?.name ?: "Username", fontSize = 12.sp)
                                Text(text = status, fontSize = 12.sp)
                            }
                        }
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
}
