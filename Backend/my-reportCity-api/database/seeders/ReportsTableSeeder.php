<?php

namespace Database\Seeders;

use App\Models\Reports;
use Illuminate\Database\Seeder;
use Illuminate\Support\Facades\DB;
use Illuminate\Support\Str;

class ReportsTableSeeder extends Seeder
{
    /**
     * Run the database seeds.
     */
    public function run(): void
    {
        // Hapus data yang sudah ada agar tidak duplikat saat dijalankan
        DB::table('reports')->truncate();

        // Data dummy untuk deskripsi
        $descriptions = [
            'Laporan kerusakan fasilitas umum.',
            'Laporan tentang kebersihan lingkungan.',
            'Laporan vandalisme di taman kota.',
            'Laporan penerangan jalan yang mati.',
            'Laporan tumpukan sampah liar.',
            'Laporan genangan air di jalan.',
            'Laporan kemacetan lalu lintas.',
            'Laporan fasilitas olahraga yang rusak.',
            'Laporan jalan berlubang.',
            'Laporan tentang fasilitas pejalan kaki yang tidak layak.'
        ];
        
        // Loop untuk membuat 30 data dummy
        for ($i = 0; $i < 30; $i++) {
            Reports::create([
                'userId' => rand(1, 10), // User ID acak antara 1 dan 10
                'urlFoto' => 'https://placehold.co/600x400/292524/white?text=' . Str::random(5),
                'deskripsi' => $descriptions[array_rand($descriptions)],
                'status' => rand(Reports::STATUS_PENDING, Reports::STATUS_COMPLETE), // Status acak
                'LAT' => null, // Dibuat null sesuai migrasi
                'DAT' => null, // Dibuat null sesuai migrasi
                'upVote' => 0,
            ]);
        }
    }
}

