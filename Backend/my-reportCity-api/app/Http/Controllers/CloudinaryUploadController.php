<?php

namespace App\Http\Controllers;

use CloudinaryLabs\CloudinaryLaravel\Facades\Cloudinary;
use Illuminate\Http\Request;
use Illuminate\Http\Response;

class CloudinaryUploadController extends Controller
{
    public function store(Request $request)
    {
        // Validasi permintaan untuk memastikan ada file yang diunggah
        $request->validate([
            'file' => 'required|file|mimes:jpeg,png,jpg,gif,svg,mp4,mov,avi|max:20480',
        ]);

        // Cek apakah file benar-benar ada dan valid
        if (!$request->hasFile('file') || !$request->file('file')->isValid()) {
            return response()->json([
                'success' => false,
                'message' => 'No valid file was uploaded.'
            ], Response::HTTP_BAD_REQUEST);
        }

        try {
            // Upload file ke Cloudinary
            $uploadedFile = $request->file('file');
            
            // Menggunakan method 'upload' dari facade Cloudinary
            $uploadedFileUrl = Cloudinary::upload($uploadedFile->getRealPath())->getSecurePath();

            // Kembalikan respons sukses dengan URL file
            return response()->json([
                'success' => true,
                'message' => 'File uploaded successfully!',
                'url' => $uploadedFileUrl
            ], Response::HTTP_CREATED, [], JSON_UNESCAPED_SLASHES);

        } catch (\Exception $e) {
            // Tangani error jika terjadi masalah saat mengunggah
            return response()->json([
                'success' => false,
                'message' => 'Failed to upload file.',
                'error_detail' => $e->getMessage()
            ], Response::HTTP_INTERNAL_SERVER_ERROR);
        }
    }
}