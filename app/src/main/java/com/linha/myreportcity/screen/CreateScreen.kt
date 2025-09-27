package com.linha.myreportcity.screen

import android.Manifest
import android.app.Activity.RESULT_OK
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.linha.myreportcity.R
import com.linha.myreportcity.model.report.PostReport
import com.linha.myreportcity.utils.DialogGambar
import com.linha.myreportcity.utils.DisplayImageFromUri
import com.linha.myreportcity.utils.saveImageToInternalStorage
import com.linha.myreportcity.utils.uriToMultipartBodyPart
import com.linha.myreportcity.viewmodel.CloudinaryViewModel
import com.linha.myreportcity.viewmodel.ReportViewModel
import com.linha.myreportcity.viewmodel.UserViewModel

@OptIn(ExperimentalMaterial3Api::class, ExperimentalPermissionsApi::class)
@Composable
fun CreateReportScreen(
    modifier: Modifier = Modifier,
    reportVM: ReportViewModel,
    userVM: UserViewModel,
    cloudinaryVM: CloudinaryViewModel
) {
    val context = LocalContext.current

    val token by userVM.tempToken.collectAsState()

    LaunchedEffect(key1 = token) {
        if (!token.isNullOrEmpty()) {
            userVM.getUserProfiles(token.toString(), context)
        }
    }

    val user by userVM.user.observeAsState(null)
    val urlFoto by cloudinaryVM.urlResult.observeAsState()

    var imageUri by remember { mutableStateOf<Uri?>(null) }
    var dialog by remember { mutableStateOf(false) }

    var deskripsiLaporan by remember { mutableStateOf("") }
    var lat by remember { mutableDoubleStateOf(0.0) }
    var dat by remember { mutableDoubleStateOf(0.0) }

    val isReportLoading by reportVM.isLoading.collectAsState()
    val isCloudinaryLoading by cloudinaryVM.isLoading.collectAsState(initial = false)
    val isTotalLoading = isReportLoading || isCloudinaryLoading

// ...

    val cameraLauncher =
        rememberLauncherForActivityResult(contract = ActivityResultContracts.TakePicturePreview()) {
            it?.let {
                val uri = saveImageToInternalStorage(context, it, "username")
                imageUri = uri
                dialog = false
            }
        }
    val cameraPermissionState = rememberPermissionState(permission = Manifest.permission.CAMERA)

    val galleryLauncher =
        rememberLauncherForActivityResult(contract = ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == RESULT_OK) {
                val data: Intent? = result.data
                imageUri = data?.data
                dialog = false
            }
        }

    Box(
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = modifier
                .background(Color.White)
                .fillMaxSize()
                .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            if (imageUri != null) {
                DisplayImageFromUri(
                    imageUri = imageUri,
                    modifier = Modifier.clickable(onClick = {
                        dialog = true
                    })
                )
            } else {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(300.dp)
                        .clickable(
                            onClick = {
                                dialog = true
                            }
                        ),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Image(
                        painter = painterResource(R.drawable.baseline_camera_alt_24),
                        contentDescription = "Foto Laporan"
                    )
                    Text(text = "Ambil Foto")
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
            OutlinedTextField(
                value = deskripsiLaporan,
                onValueChange = {
                    deskripsiLaporan = it
                },
                maxLines = 5,
                placeholder = {
                    Text(text = "Tuliskan Deskripsi Laporan disini")
                },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(text = "Ambil lokasi laporan saat ini : ")
            if (lat != 0.0 && dat != 0.0) {
                Text(text = "Posisi kamu : $lat, $dat")
            } else {
                Button(onClick = {

                }) {
                    Column {
                        Icon(
                            imageVector = Icons.Default.LocationOn,
                            contentDescription = "Lokasi"
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.height(32.dp))
            Button(
                onClick = {
                    val currentToken = token
                    val currentUser = user

                    if (imageUri != null && deskripsiLaporan.isNotEmpty() && currentToken != null && currentUser != null) {
                        val imagePart = uriToMultipartBodyPart(
                            context = context,
                            uri = imageUri!!, // Smart cast
                            userId = currentUser.id, // Smart cast
                            nama = currentUser.name
                        )
                        if (imagePart != null) {
                            cloudinaryVM.uploadImage(token = currentToken, image = imagePart)
                        } else {
                            Toast.makeText(context, "Gagal memproses gambar.", Toast.LENGTH_SHORT).show()
                        }
                    } else {
                        Toast.makeText(context, "Mohon lengkapi semua data laporan.", Toast.LENGTH_SHORT).show()
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(text = "Upload")
            }
        }

        if (isTotalLoading) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Gray.copy(alpha = 0.5f))
                    .clickable(enabled = false, onClick = {})
            ) {
                CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.Center)
                )
            }
        }
    }

    LaunchedEffect(key1 = urlFoto) {
        val currentToken = token
        val currentUser = user

        if (urlFoto != null && currentToken != null && currentUser != null) {
            reportVM.uploadReport(
                token = currentToken,
                PostReport(
                    userId = currentUser.id,
                    urlFoto = urlFoto!!,
                    deskripsi = deskripsiLaporan,
                    lat = lat,
                    dat = dat,
                ),
                context = context,
            )
            cloudinaryVM.resetUrlResult()
        }
    }

    if (dialog) {
        DialogGambar(
            onDismissRequest = { dialog = false },
            onKameraClick = {
                if (cameraPermissionState.status.isGranted) {
                    cameraLauncher.launch(null)
                } else {
                    cameraPermissionState.launchPermissionRequest()
                }
            },
            onGaleriClick = {
                val intent = Intent(Intent.ACTION_PICK).apply { type = "image/*" }
                galleryLauncher.launch(intent)
            },
            cameraPermissionIsGranted = cameraPermissionState.status.isGranted,
            onPermissionRequest = {
                cameraPermissionState.launchPermissionRequest()
            }
        )
    }
}