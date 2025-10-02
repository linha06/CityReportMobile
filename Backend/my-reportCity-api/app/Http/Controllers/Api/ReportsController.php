<?php

namespace App\Http\Controllers\Api;

use App\Http\Controllers\Controller;
use App\Models\Reports;
use Carbon\Carbon;
use Illuminate\Http\Request;

class ReportsController extends Controller
{
    /**
     * Display a listing of the resource.
     */
    public function index(Request $request)
    {
    $limit = $request->limit ?? 10;

    return Reports::with('user:id,name')
        ->orderBy('created_at', 'desc')
        ->paginate($limit);
    }

    public function getReportsOrderByStatus(Request $request)
    {
    $limit = $request->limit ?? 10;
    $orderBy = $request->orderByStatus ?? 'asc'; // Default sorting ke 'asc'

    // Memastikan nilai orderBy valid
    if (!in_array(strtolower($orderBy), ['asc', 'desc'])) {
        $orderBy = 'asc';
    }

    $query = Reports::with('user:id,name')
        ->orderBy('status', $orderBy);

    return $query->paginate($limit);
    }

    public function showReportByStatus(Request $request)
    {
    $limit = $request->limit ?? 10;

    return Reports::with('user:id,name')
        ->where('status', $request->status)
        ->paginate($limit);
    }

    public function getReportUsername($userId)
    {
        $usernames = Reports::select('users.name as user_name')
            ->join('users', 'reports.userId', '=', 'users.id')
            ->where('reports.userId', $userId)
            ->distinct()
            ->get();
    
        return response()->json($usernames, 200);
    }

    /**
     * Store a newly created resource in storage.
     */
    public function store(Request $request)
    {
        $report = Reports::create($request->all());
        return response()->json($report, 201);
    }

    /**
     * Display the specified resource.
     */
    public function show(string $id)
    {
    $report = Reports::find($id);

    if (is_null($report)) {
        return response()->json(['message' => 'Report not found'], 404);
    }

    return response()->json($report, 200);
    }

    public function showByUserId(string $userId)
    {
    $reports = Reports::with('user:id,name')
        ->where('userId', $userId)
        ->get();

    if ($reports->isEmpty()) {
        return response()->json(['message' => 'No reports found for this user'], 404);
    }

    return response()->json($reports, 200);
    }

    public function countByStatus($status) 
    {
        $currentYear = Carbon::now()->year;

        $reports = Reports::where('status', $status)
            ->whereYear('created_at', $currentYear)
            ->count('status');

        return response()->json(['count_status'=>$reports , 'message'=>'Berhasil dapat count status'], 200);
    }

    public function countStatusByMonth($status)
    {
    $results = []; 

    for ($x = 1; $x <= 12; $x++) {
        $reports = Reports::where('status', $status)
            ->whereMonth('created_at', $x)
            ->count('status');
        $results[] = $reports; 
    }


    return response()->json([
        'count_month_status' => $results,
        'message' => "Berhasil dapat count status per bulan"
    ], 200);
    }

    /**
     * Update the specified resource in storage.
     */
    public function update(Request $request, string $id)
    {
    $report = Reports::find($id);

    if (is_null($report)) {
        return response()->json(['message' => 'Report not found'], 404);
    }

    $report->update($request->all());

    return response()->json($report, 200);
    }

    public function updateStatus(Request $request, string $id)
    {
        $report = Reports::find($id);

        if (is_null($report)) {
            return response()->json(['message' => 'Report not found'], 404);
        }

        $report->update([
            'status' => $request->status
        ]);

        return response()->json([
            'message' => 'Status laporan berhasil diperbarui',
            'report' => $report
        ], 200);
    }

    /**
     * Remove the specified resource from storage.
     */
    public function destroy(string $id)
    {
        Reports::delete($id);
    }
}
