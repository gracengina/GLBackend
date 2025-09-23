@echo off
REM Comprehensive Evently API Testing Script
REM Tests all endpoints with GET and POST requests where applicable
REM Shows response body, headers, and HTTP status codes

echo ================================================
echo EVENTLY API COMPREHENSIVE ENDPOINT TESTING
echo ================================================
echo.

REM Set base URL
set BASE_URL=http://localhost:8080

REM Function to make curl request with full output
REM Usage: call :make_request METHOD URL [DATA] [AUTH_HEADER]

echo ================================================
echo 1. HEALTH ENDPOINTS
echo ================================================

echo.
echo --- GET /health ---
curl -w "\n\nHTTP Status: %%{http_code}\nTotal Time: %%{time_total}s\n" -v %BASE_URL%/health 2>&1
echo.

echo.
echo --- GET /health/detailed ---
curl -w "\n\nHTTP Status: %%{http_code}\nTotal Time: %%{time_total}s\n" -v %BASE_URL%/health/detailed 2>&1
echo.

echo.
echo --- GET /health/ping ---
curl -w "\n\nHTTP Status: %%{http_code}\nTotal Time: %%{time_total}s\n" -v %BASE_URL%/health/ping 2>&1
echo.

echo ================================================
echo 2. API DOCUMENTATION ENDPOINTS
echo ================================================

echo.
echo --- GET /api/docs ---
curl -w "\n\nHTTP Status: %%{http_code}\nTotal Time: %%{time_total}s\n" -v %BASE_URL%/api/docs 2>&1
echo.

echo.
echo --- GET /api/endpoints ---
curl -w "\n\nHTTP Status: %%{http_code}\nTotal Time: %%{time_total}s\n" -v %BASE_URL%/api/endpoints 2>&1
echo.

echo ================================================
echo 3. AUTHENTICATION ENDPOINTS
echo ================================================

echo.
echo --- POST /auth/register (New User) ---
curl -w "\n\nHTTP Status: %%{http_code}\nTotal Time: %%{time_total}s\n" -v ^
  -X POST %BASE_URL%/auth/register ^
  -H "Content-Type: application/json" ^
  -d "{\"username\":\"testuser%RANDOM%\",\"password\":\"password123\",\"confirmPassword\":\"password123\",\"email\":\"test%RANDOM%@example.com\",\"firstName\":\"Test\",\"lastName\":\"User\"}" ^
  2>&1
echo.

echo.
echo --- POST /auth/login (Test User) ---
curl -w "\n\nHTTP Status: %%{http_code}\nTotal Time: %%{time_total}s\n" -v ^
  -X POST %BASE_URL%/auth/login ^
  -H "Content-Type: application/json" ^
  -d "{\"username\":\"testuser\",\"password\":\"password123\"}" ^
  2>&1 > temp_login.txt

REM Extract token from login response
for /f "tokens=2 delims=:" %%a in ('findstr "token" temp_login.txt') do set TOKEN_RAW=%%a
for /f "tokens=1 delims=," %%b in ("%TOKEN_RAW%") do set TOKEN=%%b
set TOKEN=%TOKEN:~1,-1%
type temp_login.txt
del temp_login.txt
echo.
echo Token extracted for authenticated requests: %TOKEN:~0,50%...
echo.

echo.
echo --- GET /auth/profile (With Auth) ---
curl -w "\n\nHTTP Status: %%{http_code}\nTotal Time: %%{time_total}s\n" -v ^
  -H "Authorization: Bearer %TOKEN%" ^
  %BASE_URL%/auth/profile 2>&1
echo.

echo ================================================
echo 4. VENDOR ENDPOINTS (PUBLIC)
echo ================================================

echo.
echo --- GET /api/vendors ---
curl -w "\n\nHTTP Status: %%{http_code}\nTotal Time: %%{time_total}s\n" -v %BASE_URL%/api/vendors 2>&1
echo.

echo.
echo --- GET /api/vendors/1 ---
curl -w "\n\nHTTP Status: %%{http_code}\nTotal Time: %%{time_total}s\n" -v %BASE_URL%/api/vendors/1 2>&1
echo.

echo.
echo --- GET /api/vendors/search?query=photography ---
curl -w "\n\nHTTP Status: %%{http_code}\nTotal Time: %%{time_total}s\n" -v "%BASE_URL%/api/vendors/search?query=photography" 2>&1
echo.

echo.
echo --- GET /api/vendors/search?query=catering ---
curl -w "\n\nHTTP Status: %%{http_code}\nTotal Time: %%{time_total}s\n" -v "%BASE_URL%/api/vendors/search?query=catering" 2>&1
echo.

echo.
echo --- GET /api/vendors/search?query=nonexistent ---
curl -w "\n\nHTTP Status: %%{http_code}\nTotal Time: %%{time_total}s\n" -v "%BASE_URL%/api/vendors/search?query=nonexistent" 2>&1
echo.

echo.
echo --- GET /api/vendors/location?location=New%%20York ---
curl -w "\n\nHTTP Status: %%{http_code}\nTotal Time: %%{time_total}s\n" -v "%BASE_URL%/api/vendors/location?location=New%%20York" 2>&1
echo.

echo ================================================
echo 5. USER ENDPOINTS (WITH AUTH)
echo ================================================

echo.
echo --- GET /api/users/me ---
curl -w "\n\nHTTP Status: %%{http_code}\nTotal Time: %%{time_total}s\n" -v ^
  -H "Authorization: Bearer %TOKEN%" ^
  %BASE_URL%/api/users/me 2>&1
echo.

echo.
echo --- GET /api/users ---
curl -w "\n\nHTTP Status: %%{http_code}\nTotal Time: %%{time_total}s\n" -v ^
  -H "Authorization: Bearer %TOKEN%" ^
  %BASE_URL%/api/users 2>&1
echo.

echo.
echo --- GET /api/users/7 ---
curl -w "\n\nHTTP Status: %%{http_code}\nTotal Time: %%{time_total}s\n" -v ^
  -H "Authorization: Bearer %TOKEN%" ^
  %BASE_URL%/api/users/7 2>&1
echo.

echo.
echo --- GET /api/users/search?query=test ---
curl -w "\n\nHTTP Status: %%{http_code}\nTotal Time: %%{time_total}s\n" -v ^
  -H "Authorization: Bearer %TOKEN%" ^
  "%BASE_URL%/api/users/search?query=test" 2>&1
echo.

echo.
echo --- GET /api/users/planners ---
curl -w "\n\nHTTP Status: %%{http_code}\nTotal Time: %%{time_total}s\n" -v ^
  -H "Authorization: Bearer %TOKEN%" ^
  %BASE_URL%/api/users/planners 2>&1
echo.

echo.
echo --- GET /api/users/vendors ---
curl -w "\n\nHTTP Status: %%{http_code}\nTotal Time: %%{time_total}s\n" -v ^
  -H "Authorization: Bearer %TOKEN%" ^
  %BASE_URL%/api/users/vendors 2>&1
echo.

echo.
echo --- GET /api/users/active ---
curl -w "\n\nHTTP Status: %%{http_code}\nTotal Time: %%{time_total}s\n" -v ^
  -H "Authorization: Bearer %TOKEN%" ^
  %BASE_URL%/api/users/active 2>&1
echo.

echo ================================================
echo 6. PROTECTED ENDPOINTS (SHOULD RETURN 401/403)
echo ================================================

echo.
echo --- GET /api/users (No Auth) ---
curl -w "\n\nHTTP Status: %%{http_code}\nTotal Time: %%{time_total}s\n" -v %BASE_URL%/api/users 2>&1
echo.

echo.
echo --- POST /api/vendors/profile (No Auth) ---
curl -w "\n\nHTTP Status: %%{http_code}\nTotal Time: %%{time_total}s\n" -v ^
  -X POST %BASE_URL%/api/vendors/profile ^
  -H "Content-Type: application/json" ^
  -d "{\"businessName\":\"Test Business\",\"description\":\"Test Description\"}" ^
  2>&1
echo.

echo.
echo --- GET /api/events (No Auth) ---
curl -w "\n\nHTTP Status: %%{http_code}\nTotal Time: %%{time_total}s\n" -v %BASE_URL%/api/events 2>&1
echo.

echo.
echo --- POST /api/events (No Auth) ---
curl -w "\n\nHTTP Status: %%{http_code}\nTotal Time: %%{time_total}s\n" -v ^
  -X POST %BASE_URL%/api/events ^
  -H "Content-Type: application/json" ^
  -d "{\"title\":\"Test Event\",\"description\":\"Test Description\",\"location\":\"Test Location\",\"date\":\"2025-12-25T10:00:00\"}" ^
  2>&1
echo.

echo.
echo --- GET /api/bookings (No Auth) ---
curl -w "\n\nHTTP Status: %%{http_code}\nTotal Time: %%{time_total}s\n" -v %BASE_URL%/api/bookings 2>&1
echo.

echo.
echo --- POST /api/bookings (No Auth) ---
curl -w "\n\nHTTP Status: %%{http_code}\nTotal Time: %%{time_total}s\n" -v ^
  -X POST %BASE_URL%/api/bookings ^
  -H "Content-Type: application/json" ^
  -d "{\"eventId\":1,\"vendorId\":1,\"serviceId\":1}" ^
  2>&1
echo.

echo ================================================
echo 7. EDGE CASES AND ERROR TESTING
echo ================================================

echo.
echo --- GET /api/vendors/999 (Non-existent ID) ---
curl -w "\n\nHTTP Status: %%{http_code}\nTotal Time: %%{time_total}s\n" -v %BASE_URL%/api/vendors/999 2>&1
echo.

echo.
echo --- GET /api/users/999 (Non-existent ID with Auth) ---
curl -w "\n\nHTTP Status: %%{http_code}\nTotal Time: %%{time_total}s\n" -v ^
  -H "Authorization: Bearer %TOKEN%" ^
  %BASE_URL%/api/users/999 2>&1
echo.

echo.
echo --- POST /auth/login (Invalid Credentials) ---
curl -w "\n\nHTTP Status: %%{http_code}\nTotal Time: %%{time_total}s\n" -v ^
  -X POST %BASE_URL%/auth/login ^
  -H "Content-Type: application/json" ^
  -d "{\"username\":\"invaliduser\",\"password\":\"wrongpassword\"}" ^
  2>&1
echo.

echo.
echo --- POST /auth/register (Duplicate User) ---
curl -w "\n\nHTTP Status: %%{http_code}\nTotal Time: %%{time_total}s\n" -v ^
  -X POST %BASE_URL%/auth/register ^
  -H "Content-Type: application/json" ^
  -d "{\"username\":\"testuser\",\"password\":\"password123\",\"confirmPassword\":\"password123\",\"email\":\"duplicate@example.com\",\"firstName\":\"Test\",\"lastName\":\"User\"}" ^
  2>&1
echo.

echo.
echo --- GET /nonexistent-endpoint ---
curl -w "\n\nHTTP Status: %%{http_code}\nTotal Time: %%{time_total}s\n" -v %BASE_URL%/nonexistent-endpoint 2>&1
echo.

echo ================================================
echo TESTING COMPLETE
echo ================================================
echo.
echo Summary of tested endpoints:
echo - Health endpoints: 3 tested
echo - Authentication endpoints: 3 tested
echo - Vendor endpoints: 6 tested
echo - User endpoints: 7 tested
echo - Protected endpoints: 5 tested (401/403 expected)
echo - Error cases: 5 tested
echo.
echo Total: 29 endpoint tests completed
echo.
echo Check the output above for:
echo - HTTP Status codes
echo - Response bodies
echo - Request/Response headers
echo - Performance timing
echo.
pause