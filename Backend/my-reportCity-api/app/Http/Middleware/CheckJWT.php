<?php

namespace App\Http\Middleware;

use Closure;
use Illuminate\Http\Request;
use Symfony\Component\HttpFoundation\Response;
use Tymon\JWTAuth\Exceptions\JWTException as ExceptionsJWTException;
use Tymon\JWTAuth\Exceptions\TokenExpiredException as ExceptionsTokenExpiredException;
use Tymon\JWTAuth\Exceptions\TokenInvalidException as ExceptionsTokenInvalidException;
use Tymon\JWTAuth\Facades\JWTAuth as FacadesJWTAuth;

class CheckJWT
{
    /**
     * Handle an incoming request.
     *
     * @param  \Closure(\Illuminate\Http\Request): (\Symfony\Component\HttpFoundation\Response)  $next
     */
    public function handle(Request $request, Closure $next): Response
    {
        try {
            $jwt = $request->header('Authorization');

            if (!$jwt) {
                return response()->json([
                    'status' => 401,
                    'message' => 'Unauthorized, please provide token!'
                ], 401);
            }
            
            $user = FacadesJWTAuth::parseToken()->authenticate();

            if (!$user) {
                return response()->json([
                    'status' => 401,
                    'message' => 'Unauthorized, user not found!'
                ], 401);
            }

        } catch (ExceptionsTokenExpiredException $e) {
            return response()->json([
                'status' => 401,
                'message' => 'Token expired!'
            ], 401);
        } catch (ExceptionsTokenInvalidException $e) {
            return response()->json([
                'status' => 401,
                'message' => 'Token invalid!'
            ], 401);
        } catch (ExceptionsJWTException $e) {
            return response()->json([
                'status' => 401,
                'message' => 'Token not found or not provided!'
            ], 401);
        }

        return $next($request);
    }
}