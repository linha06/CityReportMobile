<?php

use App\Models\Reports;
use Illuminate\Database\Migrations\Migration;
use Illuminate\Database\Schema\Blueprint;
use Illuminate\Support\Facades\Schema;

return new class extends Migration
{
    /**
     * Run the migrations.
     */
    public function up(): void
    {
        Schema::create('reports', function (Blueprint $table) {
            $table->id();
            $table->integer('userId')->nullable();
            $table->string('urlFoto');
            $table->text('deskripsi');
            $table->integer('status')->default(Reports::STATUS_PENDING);
            $table->double('LAT')->nullable();
            $table->double('DAT')->nullable();
            $table->integer('upVote')->default(0);
            $table->timestamps();
        });
    }

    /**
     * Reverse the migrations.
     */
    public function down(): void
    {
        Schema::dropIfExists('reports');
    }
};
