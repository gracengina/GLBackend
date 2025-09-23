# 🌐 Frontend Integration Guide for Different Laptop Setup

This guide explains how to connect your frontend application running on a **different laptop** to this Spring Boot backend.

## 📋 **Current Backend Configuration**

### Network Information
- **Backend Server IP Addresses:**
  - Wi-Fi: `192.168.100.7` (Primary)
  - Ethernet: `192.168.56.1` (Secondary)
- **Port:** `8080`
- **Protocol:** `HTTP` (Development mode)

### Base URLs for Frontend
```javascript
// Use the appropriate IP based on your network setup
const API_BASE_URL = 'http://192.168.100.7:8080';
// OR
const API_BASE_URL = 'http://192.168.56.1:8080';
```

## 🔧 **Backend Configuration Applied**

### 1. CORS Configuration ✅
The backend now accepts requests from:
- `http://localhost:3000` (React default)
- `http://localhost:5173` (Vite default)
- `http://192.168.*:*` (Local network range)
- `http://10.0.*:*` (Private network range)
- `http://172.16.*:*` (Corporate network range)

### 2. Server Binding ✅
- **Server Address:** `0.0.0.0` (All network interfaces)
- **Port:** `8080`
- **Accessible from:** Any device on the same network

### 3. Security Headers ✅
- CORS preflight requests handled
- Credentials allowed for authentication
- Proper security headers configured

## 🚀 **Frontend Setup Instructions**

### Step 1: Determine Backend IP Address
From your frontend laptop, you need to reach the backend laptop. Use one of these methods:

#### Method A: Ping Test
```bash
# Test connectivity to backend laptop
ping 192.168.100.7
ping 192.168.56.1
```

#### Method B: Direct API Test
```bash
# Test API accessibility
curl http://192.168.100.7:8080/health
curl http://192.168.56.1:8080/health
```

### Step 2: Configure Frontend Environment

#### For React Applications
Create or update `.env` file:
```env
# .env
REACT_APP_API_BASE_URL=http://192.168.100.7:8080
REACT_APP_API_VERSION=v1
```

#### For Vue/Nuxt Applications
```javascript
// nuxt.config.js or vue.config.js
export default {
  env: {
    API_BASE_URL: 'http://192.168.100.7:8080'
  }
}
```

#### For Vite Applications
```javascript
// vite.config.js
export default {
  define: {
    __API_BASE_URL__: '"http://192.168.100.7:8080"'
  }
}
```

### Step 3: Update API Client Configuration

#### Example: Axios Configuration
```javascript
// api/client.js
import axios from 'axios';

const API_BASE_URL = process.env.REACT_APP_API_BASE_URL || 'http://192.168.100.7:8080';

const apiClient = axios.create({
  baseURL: API_BASE_URL,
  headers: {
    'Content-Type': 'application/json',
  },
  withCredentials: true, // Important for JWT cookies
});

// Add request interceptor for JWT token
apiClient.interceptors.request.use((config) => {
  const token = localStorage.getItem('token');
  if (token) {
    config.headers.Authorization = `Bearer ${token}`;
  }
  return config;
});

export default apiClient;
```

#### Example: Fetch Configuration
```javascript
// api/client.js
const API_BASE_URL = 'http://192.168.100.7:8080';

export const apiRequest = async (endpoint, options = {}) => {
  const token = localStorage.getItem('token');
  
  const config = {
    headers: {
      'Content-Type': 'application/json',
      ...(token && { Authorization: `Bearer ${token}` }),
      ...options.headers,
    },
    credentials: 'include',
    ...options,
  };

  const response = await fetch(`${API_BASE_URL}${endpoint}`, config);
  
  if (!response.ok) {
    throw new Error(`HTTP error! status: ${response.status}`);
  }
  
  return response.json();
};
```

## 🔗 **Available API Endpoints**

### Public Endpoints (No Authentication Required)
```javascript
// Health Check
GET http://192.168.100.7:8080/health

// API Documentation
GET http://192.168.100.7:8080/api/docs
GET http://192.168.100.7:8080/api/endpoints

// Authentication
POST http://192.168.100.7:8080/auth/register
POST http://192.168.100.7:8080/auth/login

// Vendor Search (Public)
GET http://192.168.100.7:8080/api/vendors
GET http://192.168.100.7:8080/api/vendors/search?query=photography
GET http://192.168.100.7:8080/api/vendors/location?location=New%20York
```

### Protected Endpoints (Require JWT Token)
```javascript
// User Management
GET http://192.168.100.7:8080/api/users/me
GET http://192.168.100.7:8080/api/users
PUT http://192.168.100.7:8080/api/users/{id}

// Event Management
GET http://192.168.100.7:8080/api/events
POST http://192.168.100.7:8080/api/events
PUT http://192.168.100.7:8080/api/events/{id}

// Booking Management
GET http://192.168.100.7:8080/api/bookings
POST http://192.168.100.7:8080/api/bookings
```

## 🔐 **Authentication Flow Example**

### Step 1: User Registration
```javascript
const registerUser = async (userData) => {
  const response = await apiRequest('/auth/register', {
    method: 'POST',
    body: JSON.stringify(userData),
  });
  
  // Store JWT token
  localStorage.setItem('token', response.token);
  return response;
};
```

### Step 2: User Login
```javascript
const loginUser = async (credentials) => {
  const response = await apiRequest('/auth/login', {
    method: 'POST',
    body: JSON.stringify(credentials),
  });
  
  // Store JWT token
  localStorage.setItem('token', response.token);
  return response;
};
```

### Step 3: Authenticated Requests
```javascript
const getUserProfile = async () => {
  // Token automatically added by interceptor
  return await apiRequest('/api/users/me');
};
```

## 🚨 **Troubleshooting**

### Issue 1: CORS Errors
**Symptoms:** "Access-Control-Allow-Origin" errors in browser console

**Solutions:**
1. Verify your frontend URL is allowed in CORS configuration
2. Add your specific frontend URL to the CORS settings:

```bash
# Set environment variable before starting backend
set CORS_ALLOWED_ORIGINS=http://localhost:3000,http://YOUR_FRONTEND_IP:PORT
mvn spring-boot:run
```

### Issue 2: Network Connectivity
**Symptoms:** "ERR_NETWORK" or timeout errors

**Solutions:**
1. Ensure both laptops are on the same network
2. Check Windows Firewall settings on backend laptop:
   ```cmd
   # Allow port 8080 through firewall
   netsh advfirewall firewall add rule name="Spring Boot" dir=in action=allow protocol=TCP localport=8080
   ```
3. Verify backend is listening on all interfaces:
   ```cmd
   netstat -an | findstr :8080
   ```

### Issue 3: Authentication Issues
**Symptoms:** 401 Unauthorized errors

**Solutions:**
1. Verify JWT token is being sent in requests
2. Check token expiration (tokens expire after 24 hours)
3. Ensure `withCredentials: true` is set for cookie-based auth

### Issue 4: Backend Not Accessible
**Symptoms:** Connection refused or timeout

**Solutions:**
1. Start backend with explicit network binding:
   ```cmd
   mvn spring-boot:run -Dspring-boot.run.arguments="--server.address=0.0.0.0"
   ```
2. Check if backend is running:
   ```cmd
   curl http://localhost:8080/health
   ```

## 📱 **Mobile Development**

### React Native
```javascript
// Use the backend laptop's IP
const API_BASE_URL = 'http://192.168.100.7:8080';

// Note: Use real device or ensure emulator has network access
```

### Flutter
```dart
// lib/config/api_config.dart
class ApiConfig {
  static const String baseUrl = 'http://192.168.100.7:8080';
  static const String apiVersion = 'v1';
}
```

## 🔄 **Auto-Detection Script**

Create this utility to automatically detect the best backend URL:

```javascript
// utils/detectBackendUrl.js
const POSSIBLE_BACKENDS = [
  'http://192.168.100.7:8080',
  'http://192.168.56.1:8080',
  'http://localhost:8080',
];

export const detectBackendUrl = async () => {
  for (const url of POSSIBLE_BACKENDS) {
    try {
      const response = await fetch(`${url}/health`, { 
        method: 'GET',
        timeout: 3000 
      });
      if (response.ok) {
        console.log(`Backend detected at: ${url}`);
        return url;
      }
    } catch (error) {
      console.log(`Backend not reachable at: ${url}`);
    }
  }
  throw new Error('No backend server found');
};
```

## 📝 **Quick Start Checklist**

- [ ] Backend laptop: Start Spring Boot with `mvn spring-boot:run`
- [ ] Frontend laptop: Update API base URL to backend's IP
- [ ] Test connectivity: `curl http://192.168.100.7:8080/health`
- [ ] Configure CORS if needed with specific frontend URL
- [ ] Test authentication flow
- [ ] Verify protected endpoints work with JWT token

## 💡 **Production Considerations**

For production deployment:
1. Use HTTPS instead of HTTP
2. Configure proper domain names instead of IP addresses
3. Implement proper CORS restrictions
4. Use environment-specific configuration files
5. Set up proper network security and firewalls

---

**✅ Your backend is now configured for cross-laptop development!**

The Spring Boot application supports:
- ✅ Cross-origin requests from different laptops
- ✅ Network accessibility from other devices
- ✅ Comprehensive API endpoints for event management
- ✅ JWT-based authentication
- ✅ Proper security headers and CORS configuration

Happy coding! 🎉