# Comprehensive Evently API Testing Script (PowerShell)
# Tests all endpoints with GET and POST requests where applicable
# Shows response body, headers, and HTTP status codes

Write-Host "================================================" -ForegroundColor Green
Write-Host "EVENTLY API COMPREHENSIVE ENDPOINT TESTING" -ForegroundColor Green
Write-Host "================================================" -ForegroundColor Green
Write-Host

# Set base URL
$baseUrl = "http://localhost:8080"

# Function to make REST request with full output
function Invoke-ApiTest {
    param(
        [string]$Method,
        [string]$Uri,
        [string]$Body = $null,
        [hashtable]$Headers = @{}
    )
    
    Write-Host "--- $Method $Uri ---" -ForegroundColor Yellow
    
    try {
        $stopwatch = [System.Diagnostics.Stopwatch]::StartNew()
        
        $params = @{
            Uri = $Uri
            Method = $Method
            Headers = $Headers
            UseBasicParsing = $true
        }
        
        if ($Body) {
            $params.Body = $Body
            $params.ContentType = "application/json"
        }
        
        $response = Invoke-WebRequest @params
        $stopwatch.Stop()
        
        Write-Host "Response Headers:" -ForegroundColor Cyan
        $response.Headers | ForEach-Object { Write-Host "  $($_.Key): $($_.Value)" }
        Write-Host
        Write-Host "Response Body:" -ForegroundColor Cyan
        Write-Host $response.Content
        Write-Host
        Write-Host "HTTP Status: $($response.StatusCode)" -ForegroundColor Green
        Write-Host "Total Time: $($stopwatch.ElapsedMilliseconds)ms" -ForegroundColor Green
        
    } catch {
        $stopwatch.Stop()
        Write-Host "Error occurred:" -ForegroundColor Red
        Write-Host $_.Exception.Message -ForegroundColor Red
        if ($_.Exception.Response) {
            Write-Host "HTTP Status: $($_.Exception.Response.StatusCode.value__)" -ForegroundColor Red
            $reader = New-Object System.IO.StreamReader($_.Exception.Response.GetResponseStream())
            $responseBody = $reader.ReadToEnd()
            Write-Host "Response Body: $responseBody" -ForegroundColor Red
        }
        Write-Host "Total Time: $($stopwatch.ElapsedMilliseconds)ms" -ForegroundColor Red
    }
    Write-Host
}

Write-Host "================================================" -ForegroundColor Blue
Write-Host "1. HEALTH ENDPOINTS" -ForegroundColor Blue
Write-Host "================================================" -ForegroundColor Blue

Invoke-ApiTest "GET" "$baseUrl/health"
Invoke-ApiTest "GET" "$baseUrl/health/detailed"
Invoke-ApiTest "GET" "$baseUrl/health/ping"

Write-Host "================================================" -ForegroundColor Blue
Write-Host "2. API DOCUMENTATION ENDPOINTS" -ForegroundColor Blue
Write-Host "================================================" -ForegroundColor Blue

Invoke-ApiTest "GET" "$baseUrl/api/docs"
Invoke-ApiTest "GET" "$baseUrl/api/endpoints"

Write-Host "================================================" -ForegroundColor Blue
Write-Host "3. AUTHENTICATION ENDPOINTS" -ForegroundColor Blue
Write-Host "================================================" -ForegroundColor Blue

# Register a new user
$randomNum = Get-Random -Minimum 1000 -Maximum 9999
$registerData = @{
    username = "testuser$randomNum"
    password = "password123"
    confirmPassword = "password123"
    email = "test$randomNum@example.com"
    firstName = "Test"
    lastName = "User"
} | ConvertTo-Json

Invoke-ApiTest "POST" "$baseUrl/auth/register" $registerData

# Login with existing user
$loginData = @{
    username = "testuser"
    password = "password123"
} | ConvertTo-Json

Write-Host "--- POST /auth/login (Test User) ---" -ForegroundColor Yellow
try {
    $loginResponse = Invoke-RestMethod -Uri "$baseUrl/auth/login" -Method POST -Body $loginData -ContentType "application/json"
    Write-Host "Login Response:" -ForegroundColor Cyan
    Write-Host ($loginResponse | ConvertTo-Json -Depth 3)
    $token = $loginResponse.token
    Write-Host "Token extracted: $($token.Substring(0, [Math]::Min(50, $token.Length)))..." -ForegroundColor Green
} catch {
    Write-Host "Login failed: $($_.Exception.Message)" -ForegroundColor Red
    $token = $null
}
Write-Host

# Test profile endpoint with auth
if ($token) {
    $authHeaders = @{ "Authorization" = "Bearer $token" }
    Invoke-ApiTest "GET" "$baseUrl/auth/profile" -Headers $authHeaders
}

Write-Host "================================================" -ForegroundColor Blue
Write-Host "4. VENDOR ENDPOINTS (PUBLIC)" -ForegroundColor Blue
Write-Host "================================================" -ForegroundColor Blue

Invoke-ApiTest "GET" "$baseUrl/api/vendors"
Invoke-ApiTest "GET" "$baseUrl/api/vendors/1"
Invoke-ApiTest "GET" "$baseUrl/api/vendors/search?query=photography"
Invoke-ApiTest "GET" "$baseUrl/api/vendors/search?query=catering"
Invoke-ApiTest "GET" "$baseUrl/api/vendors/search?query=nonexistent"
Invoke-ApiTest "GET" "$baseUrl/api/vendors/location?location=New%20York"

Write-Host "================================================" -ForegroundColor Blue
Write-Host "5. USER ENDPOINTS (WITH AUTH)" -ForegroundColor Blue
Write-Host "================================================" -ForegroundColor Blue

if ($token) {
    $authHeaders = @{ "Authorization" = "Bearer $token" }
    
    Invoke-ApiTest "GET" "$baseUrl/api/users/me" -Headers $authHeaders
    Invoke-ApiTest "GET" "$baseUrl/api/users" -Headers $authHeaders
    Invoke-ApiTest "GET" "$baseUrl/api/users/7" -Headers $authHeaders
    Invoke-ApiTest "GET" "$baseUrl/api/users/search?query=test" -Headers $authHeaders
    Invoke-ApiTest "GET" "$baseUrl/api/users/planners" -Headers $authHeaders
    Invoke-ApiTest "GET" "$baseUrl/api/users/vendors" -Headers $authHeaders
    Invoke-ApiTest "GET" "$baseUrl/api/users/active" -Headers $authHeaders
} else {
    Write-Host "Skipping authenticated user endpoints - no valid token" -ForegroundColor Yellow
}

Write-Host "================================================" -ForegroundColor Blue
Write-Host "6. PROTECTED ENDPOINTS (SHOULD RETURN 401/403)" -ForegroundColor Blue
Write-Host "================================================" -ForegroundColor Blue

Invoke-ApiTest "GET" "$baseUrl/api/users"

$vendorProfileData = @{
    businessName = "Test Business"
    description = "Test Description"
} | ConvertTo-Json

Invoke-ApiTest "POST" "$baseUrl/api/vendors/profile" $vendorProfileData
Invoke-ApiTest "GET" "$baseUrl/api/events"

$eventData = @{
    title = "Test Event"
    description = "Test Description"
    location = "Test Location"
    date = "2025-12-25T10:00:00"
} | ConvertTo-Json

Invoke-ApiTest "POST" "$baseUrl/api/events" $eventData
Invoke-ApiTest "GET" "$baseUrl/api/bookings"

$bookingData = @{
    eventId = 1
    vendorId = 1
    serviceId = 1
} | ConvertTo-Json

Invoke-ApiTest "POST" "$baseUrl/api/bookings" $bookingData

Write-Host "================================================" -ForegroundColor Blue
Write-Host "7. EDGE CASES AND ERROR TESTING" -ForegroundColor Blue
Write-Host "================================================" -ForegroundColor Blue

Invoke-ApiTest "GET" "$baseUrl/api/vendors/999"

if ($token) {
    $authHeaders = @{ "Authorization" = "Bearer $token" }
    Invoke-ApiTest "GET" "$baseUrl/api/users/999" -Headers $authHeaders
}

$invalidLoginData = @{
    username = "invaliduser"
    password = "wrongpassword"
} | ConvertTo-Json

Invoke-ApiTest "POST" "$baseUrl/auth/login" $invalidLoginData

$duplicateUserData = @{
    username = "testuser"
    password = "password123"
    confirmPassword = "password123"
    email = "duplicate@example.com"
    firstName = "Test"
    lastName = "User"
} | ConvertTo-Json

Invoke-ApiTest "POST" "$baseUrl/auth/register" $duplicateUserData
Invoke-ApiTest "GET" "$baseUrl/nonexistent-endpoint"

Write-Host "================================================" -ForegroundColor Green
Write-Host "TESTING COMPLETE" -ForegroundColor Green
Write-Host "================================================" -ForegroundColor Green
Write-Host
Write-Host "Summary of tested endpoints:" -ForegroundColor Cyan
Write-Host "- Health endpoints: 3 tested" -ForegroundColor White
Write-Host "- Authentication endpoints: 3 tested" -ForegroundColor White
Write-Host "- Vendor endpoints: 6 tested" -ForegroundColor White
Write-Host "- User endpoints: 7 tested" -ForegroundColor White
Write-Host "- Protected endpoints: 5 tested (401/403 expected)" -ForegroundColor White
Write-Host "- Error cases: 5 tested" -ForegroundColor White
Write-Host
Write-Host "Total: 29 endpoint tests completed" -ForegroundColor Green
Write-Host
Write-Host "Check the output above for:" -ForegroundColor Cyan
Write-Host "- HTTP Status codes" -ForegroundColor White
Write-Host "- Response bodies" -ForegroundColor White
Write-Host "- Request/Response headers" -ForegroundColor White
Write-Host "- Performance timing" -ForegroundColor White