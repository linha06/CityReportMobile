<?php

use Illuminate\Http\Request;
use Illuminate\Support\Facades\Route;
use App\Http\Controllers\Api\ReportsController;
use App\Http\Controllers\Api\UserController;
use App\Http\Controllers\AuthController;
use App\Http\Controllers\CloudinaryUploadController;

/*
|--------------------------------------------------------------------------
| API Routes
|--------------------------------------------------------------------------
|
| Here is where you can register API routes for your application. These
| routes are loaded by the RouteServiceProvider and all of them will
| be assigned to the "api" middleware group. Make something great!
|
*/

Route::middleware('auth:sanctum')->get('/user', function (Request $request) {
    return $request->user();
});

// default endpoint reports
// Route::apiResource('reports', ReportsController::class);

// Rute yang dapat diakses publik (tanpa token)
Route::controller(AuthController::class)->group(function () {
    Route::post('/register', 'register');
    Route::post('/login', 'login');
})->middleware('auth:guest');


// JWT auth
// Route untuk autentikasi
// Rute yang dilindungi oleh JWT middleware
Route::middleware('checkJWT')->group(function () {

    Route::controller(AuthController::class)->group(function () {
        Route::post('/logout','logout');
        Route::post('/refresh', 'refresh');
        Route::get('/me', 'me');
    });

    // mindahin rute diatas rute default ini agar tidak bentrok dengan endpoint dibawah
    // Route::get('/reports/user/{userId}', [ReportsController::class, 'showByUserId']);
    // atau diganti nama endpoint nya, contoh begini : 
    Route::get('/reports-by-user/{userId}', [ReportsController::class, 'showByUserId']);

    Route::get('/reports-username/{userId}', [ReportsController::class, 'getReportUsername']);

    Route::get('/reports-orderby-status', [ReportsController::class, 'getReportsOrderByStatus']);

    Route::get('/reports-by-status', [ReportsController::class, 'showReportByStatus']);

    Route::get('/show-count-status/{status}', [ReportsController::class, 'countByStatus']);

    Route::get('/count-month-status/{status}', [ReportsController::class, 'countStatusByMonth']);

    Route::get('/search-username', [UserController::class, 'searchByUsername']);

    Route::post('/update-status/{id}', [ReportsController::class, 'updateStatus']);

    // rute default get post put delete
    Route::apiResource('reports', ReportsController::class);
    
    Route::post('/upload-file', [CloudinaryUploadController::class, 'store']);

});