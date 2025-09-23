#!/bin/bash

# Comprehensive Evently API Testing Script
# Tests all endpoints with GET and POST requests where applicable
# Shows response body, headers, and HTTP status codes

echo "================================================"
echo "EVENTLY API COMPREHENSIVE ENDPOINT TESTING"
echo "================================================"
echo

# Set base URL
BASE_URL="http://localhost:8080"

# Function to make curl request with full output
make_request() {
    local method=$1
    local url=$2
    local data=$3
    local auth_header=$4
    
    echo "--- $method $url ---"
    if [ -n "$data" ] && [ -n "$auth_header" ]; then
        curl -w "\n\nHTTP Status: %{http_code}\nTotal Time: %{time_total}s\n" -v \
             -X "$method" "$url" \
             -H "Content-Type: application/json" \
             -H "$auth_header" \
             -d "$data" 2>&1
    elif [ -n "$data" ]; then
        curl -w "\n\nHTTP Status: %{http_code}\nTotal Time: %{time_total}s\n" -v \
             -X "$method" "$url" \
             -H "Content-Type: application/json" \
             -d "$data" 2>&1
    elif [ -n "$auth_header" ]; then
        curl -w "\n\nHTTP Status: %{http_code}\nTotal Time: %{time_total}s\n" -v \
             -H "$auth_header" \
             "$url" 2>&1
    else
        curl -w "\n\nHTTP Status: %{http_code}\nTotal Time: %{time_total}s\n" -v "$url" 2>&1
    fi
    echo
}

echo "================================================"
echo "1. HEALTH ENDPOINTS"
echo "================================================"

make_request "GET" "$BASE_URL/health"
make_request "GET" "$BASE_URL/health/detailed"
make_request "GET" "$BASE_URL/health/ping"

echo "================================================"
echo "2. API DOCUMENTATION ENDPOINTS"
echo "================================================"

make_request "GET" "$BASE_URL/api/docs"
make_request "GET" "$BASE_URL/api/endpoints"

echo "================================================"
echo "3. AUTHENTICATION ENDPOINTS"
echo "================================================"

# Register a new user
echo "--- POST /auth/register (New User) ---"
REGISTER_DATA="{\"username\":\"testuser$RANDOM\",\"password\":\"password123\",\"confirmPassword\":\"password123\",\"email\":\"test$RANDOM@example.com\",\"firstName\":\"Test\",\"lastName\":\"User\"}"
make_request "POST" "$BASE_URL/auth/register" "$REGISTER_DATA"

# Login with existing user and extract token
echo "--- POST /auth/login (Test User) ---"
LOGIN_DATA="{\"username\":\"testuser\",\"password\":\"password123\"}"
LOGIN_RESPONSE=$(curl -s -X POST "$BASE_URL/auth/login" \
                     -H "Content-Type: application/json" \
                     -d "$LOGIN_DATA")
echo "$LOGIN_RESPONSE"

# Extract token (simple extraction - may need adjustment based on exact response format)
TOKEN=$(echo "$LOGIN_RESPONSE" | grep -o '"token":"[^"]*"' | cut -d'"' -f4)
echo "Token extracted: ${TOKEN:0:50}..."
echo

# Test profile endpoint with auth
make_request "GET" "$BASE_URL/auth/profile" "" "Authorization: Bearer $TOKEN"

echo "================================================"
echo "4. VENDOR ENDPOINTS (PUBLIC)"
echo "================================================"

make_request "GET" "$BASE_URL/api/vendors"
make_request "GET" "$BASE_URL/api/vendors/1"
make_request "GET" "$BASE_URL/api/vendors/search?query=photography"
make_request "GET" "$BASE_URL/api/vendors/search?query=catering"
make_request "GET" "$BASE_URL/api/vendors/search?query=nonexistent"
make_request "GET" "$BASE_URL/api/vendors/location?location=New%20York"

echo "================================================"
echo "5. USER ENDPOINTS (WITH AUTH)"
echo "================================================"

make_request "GET" "$BASE_URL/api/users/me" "" "Authorization: Bearer $TOKEN"
make_request "GET" "$BASE_URL/api/users" "" "Authorization: Bearer $TOKEN"
make_request "GET" "$BASE_URL/api/users/7" "" "Authorization: Bearer $TOKEN"
make_request "GET" "$BASE_URL/api/users/search?query=test" "" "Authorization: Bearer $TOKEN"
make_request "GET" "$BASE_URL/api/users/planners" "" "Authorization: Bearer $TOKEN"
make_request "GET" "$BASE_URL/api/users/vendors" "" "Authorization: Bearer $TOKEN"
make_request "GET" "$BASE_URL/api/users/active" "" "Authorization: Bearer $TOKEN"

echo "================================================"
echo "6. PROTECTED ENDPOINTS (SHOULD RETURN 401/403)"
echo "================================================"

make_request "GET" "$BASE_URL/api/users"
make_request "POST" "$BASE_URL/api/vendors/profile" "{\"businessName\":\"Test Business\",\"description\":\"Test Description\"}"
make_request "GET" "$BASE_URL/api/events"
make_request "POST" "$BASE_URL/api/events" "{\"title\":\"Test Event\",\"description\":\"Test Description\",\"location\":\"Test Location\",\"date\":\"2025-12-25T10:00:00\"}"
make_request "GET" "$BASE_URL/api/bookings"
make_request "POST" "$BASE_URL/api/bookings" "{\"eventId\":1,\"vendorId\":1,\"serviceId\":1}"

echo "================================================"
echo "7. EDGE CASES AND ERROR TESTING"
echo "================================================"

make_request "GET" "$BASE_URL/api/vendors/999"
make_request "GET" "$BASE_URL/api/users/999" "" "Authorization: Bearer $TOKEN"
make_request "POST" "$BASE_URL/auth/login" "{\"username\":\"invaliduser\",\"password\":\"wrongpassword\"}"
make_request "POST" "$BASE_URL/auth/register" "{\"username\":\"testuser\",\"password\":\"password123\",\"confirmPassword\":\"password123\",\"email\":\"duplicate@example.com\",\"firstName\":\"Test\",\"lastName\":\"User\"}"
make_request "GET" "$BASE_URL/nonexistent-endpoint"

echo "================================================"
echo "TESTING COMPLETE"
echo "================================================"
echo
echo "Summary of tested endpoints:"
echo "- Health endpoints: 3 tested"
echo "- Authentication endpoints: 3 tested"
echo "- Vendor endpoints: 6 tested"
echo "- User endpoints: 7 tested"
echo "- Protected endpoints: 5 tested (401/403 expected)"
echo "- Error cases: 5 tested"
echo
echo "Total: 29 endpoint tests completed"
echo
echo "Check the output above for:"
echo "- HTTP Status codes"
echo "- Response bodies"
echo "- Request/Response headers"
echo "- Performance timing"
echo