package com.linha.myreportcity.utils

import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.graphics.Bitmap
import android.net.Uri
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.DividerDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.DialogProperties
import coil.compose.AsyncImage
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody

fun uriToMultipartBodyPart(context: Context, uri: Uri, userId: Int, nama: String): MultipartBody.Part? {
    // Gunakan ContentResolver untuk mendapatkan input stream dari Uri
    val inputStream = context.contentResolver.openInputStream(uri) ?: return null
    val fileName = "image_report_${userId}_${nama}.jpg"
    val fileExtension = context.contentResolver.getType(uri)?.toMediaTypeOrNull()

    // Baca byte dari input stream
    val bytes = inputStream.readBytes()
    val requestBody = bytes.toRequestBody(fileExtension, 0, bytes.size)

    // Buat MultipartBody.Part
    return MultipartBody.Part.createFormData("file", fileName, requestBody)
}

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun DisplayImageFromUri(modifier: Modifier = Modifier, imageUri: Uri?) {
    if (imageUri != null) {
        AsyncImage(
            model = imageUri,
            contentDescription = "Gambar dari galeri atau kamera",
            modifier = modifier
                .fillMaxWidth()
                .height(300.dp),
            contentScale = ContentScale.Crop
        )
    }
}

fun saveImageToInternalStorage(context: Context, bitMap: Bitmap, username: String): Uri? {
    val filename = "${username}_laporan_foto_${System.currentTimeMillis()}.jpg"
    val outputStream = context.openFileOutput(filename, MODE_PRIVATE)
    bitMap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream)
    outputStream.close()
    return Uri.fromFile(context.getFileStreamPath(filename))
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DialogGambar(
    onDismissRequest: () -> Unit,
    onKameraClick: () -> Unit,
    onGaleriClick: () -> Unit,
    cameraPermissionIsGranted: Boolean,
    onPermissionRequest: () -> Unit
) {
    BasicAlertDialog(
        onDismissRequest = onDismissRequest,
        modifier = Modifier
            .background(Color.White)
            .size(width = 300.dp, height = 200.dp)
            .border(BorderStroke(1.dp, Color.Black))
            .padding(20.dp),
        properties = DialogProperties(),
        content = {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Column(verticalArrangement = Arrangement.Center) {
                    Text(text = "Ambil foto dari")
                }
                Spacer(modifier = Modifier.height(8.dp))
                HorizontalDivider(Modifier, DividerDefaults.Thickness, DividerDefaults.color)
                Spacer(modifier = Modifier.height(16.dp))
                Column(verticalArrangement = Arrangement.Center) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp)
                            .clickable(onClick = {
                                if (cameraPermissionIsGranted) {
                                    onKameraClick()
                                } else {
                                    onPermissionRequest()
                                }
                            }),
                        contentAlignment = Alignment.Center
                    ) {
                        if (!cameraPermissionIsGranted) {
                            Text(text = "Camera Permission is required", color = Color.Red)
                        } else {
                            Text(text = "Kamera")
                        }
                    }
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp)
                            .clickable(onClick = onGaleriClick),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(text = "Galeri")
                    }
                }
            }
        }
    )
}