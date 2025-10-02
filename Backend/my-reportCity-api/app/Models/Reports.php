<?php

namespace App\Models;

use Illuminate\Database\Eloquent\Factories\HasFactory;
use Illuminate\Database\Eloquent\Model;

class Reports extends Model
{
    use HasFactory;

    public const STATUS_PENDING = 1;
    public const STATUS_IN_PROGRESS = 2;
    public const STATUS_COMPLETE = 3;

    protected $table = 'reports';

    protected $fillable = [
        'userId',
        'urlFoto',
        'deskripsi',
        'status',
        'LAT',
        'DAT',
        'upVote'
    ];

    public function user()
    {
        // Relasi: Sebuah laporan dimiliki oleh satu user.
        return $this->belongsTo(User::class, 'userId');
    }
}
