# 📚 Evently API Documentation & Integration Guide

## 🌟 **Overview**
Evently is a comprehensive event management platform with vendor services, built with Spring Boot 3.5.6. This guide provides complete API documentation and step-by-step integration instructions for frontend developers.

### **Base Information**
- **API Version**: 1.0.0
- **Base URL**: `http://your-backend-ip:8080`
- **Authentication**: JWT Bearer Token
- **Content Type**: `application/json`

---

## 🔐 **Authentication System**

### **JWT Token Flow**
1. Register or login to receive JWT token
2. Include token in Authorization header: `Bearer <token>`
3. Tokens expire after 24 hours
4. Refresh by re-authenticating

---

## 📋 **API Endpoints Documentation**

## 🟢 **1. Health & Documentation Endpoints**

### **GET /health** - Basic Health Check
```http
GET /health
```
**Response:**
```json
{
  "database": "UP",
  "application": "evently-backend",
  "version": "1.0.0",
  "status": "UP",
  "timestamp": "2025-09-22T22:27:09.169062"
}
```

### **GET /health/detailed** - Detailed Health Information
```http
GET /health/detailed
```

### **GET /health/ping** - Simple Ping
```http
GET /health/ping
```
**Response:** `"pong"`

### **GET /api/docs** - API Documentation
```http
GET /api/docs
```

### **GET /api/endpoints** - Available Endpoints List
```http
GET /api/endpoints
```

---

## 🔑 **2. Authentication Endpoints**

### **POST /auth/register** - User Registration
```http
POST /auth/register
Content-Type: application/json

{
  "username": "johndoe",
  "password": "password123",
  "confirmPassword": "password123",
  "email": "john@example.com",
  "firstName": "John",
  "lastName": "Doe"
}
```

**Response:**
```json
{
  "token": "eyJhbGciOiJIUzUxMiJ9...",
  "type": "Bearer",
  "id": 1,
  "username": "johndoe",
  "email": "john@example.com",
  "authorities": "[ROLE_USER]",
  "message": "Registration successful"
}
```

### **POST /auth/login** - User Login
```http
POST /auth/login
Content-Type: application/json

{
  "username": "johndoe",
  "password": "password123"
}
```

**Response:**
```json
{
  "token": "eyJhbGciOiJIUzUxMiJ9...",
  "type": "Bearer",
  "id": 1,
  "username": "johndoe",
  "email": "john@example.com",
  "authorities": "[ROLE_USER]",
  "message": "Login successful"
}
```

### **GET /auth/profile** - Get Current User Profile
```http
GET /auth/profile
Authorization: Bearer <token>
```

---

## 👥 **3. User Management Endpoints**

### **GET /api/users** - Get All Users (Protected)
```http
GET /api/users
Authorization: Bearer <token>
```

### **GET /api/users/me** - Get Current User
```http
GET /api/users/me
Authorization: Bearer <token>
```

### **GET /api/users/{id}** - Get User by ID
```http
GET /api/users/1
Authorization: Bearer <token>
```

### **PUT /api/users/{id}** - Update User
```http
PUT /api/users/1
Authorization: Bearer <token>
Content-Type: application/json

{
  "firstName": "John",
  "lastName": "Smith",
  "email": "johnsmith@example.com"
}
```

### **GET /api/users/search** - Search Users
```http
GET /api/users/search?query=john
Authorization: Bearer <token>
```

### **GET /api/users/planners** - Get All Planners
```http
GET /api/users/planners
Authorization: Bearer <token>
```

### **GET /api/users/vendors** - Get All Vendors
```http
GET /api/users/vendors
Authorization: Bearer <token>
```

### **GET /api/users/active** - Get Active Users
```http
GET /api/users/active
Authorization: Bearer <token>
```

---

## 🏢 **4. Vendor Management Endpoints**

### **GET /api/vendors** - Get All Vendors (Public)
```http
GET /api/vendors?page=0&size=20
```

**Response:**
```json
{
  "content": [
    {
      "id": 1,
      "userId": 3,
      "username": "photographer",
      "fullName": "John Smith",
      "email": "photo@vendor.com",
      "businessName": "Smith Photography Studio",
      "description": "Professional wedding and event photography",
      "location": "New York, NY",
      "contactInfo": "555-0123",
      "isVerified": true,
      "servicesCount": 2,
      "portfolioCount": 2,
      "reviewsCount": 2,
      "averageRating": 4.5
    }
  ],
  "totalElements": 2,
  "totalPages": 1
}
```

### **GET /api/vendors/{id}** - Get Vendor by ID (Public)
```http
GET /api/vendors/1
```

### **GET /api/vendors/search** - Search Vendors (Public)
```http
GET /api/vendors/search?query=photography
GET /api/vendors/search?query=catering
```

### **GET /api/vendors/location** - Get Vendors by Location (Public)
```http
GET /api/vendors/location?location=New%20York
```

### **POST /api/vendors/profile** - Create Vendor Profile (Protected)
```http
POST /api/vendors/profile
Authorization: Bearer <token>
Content-Type: application/json

{
  "businessName": "Amazing Catering Co",
  "description": "Premium catering services",
  "location": "Los Angeles, CA",
  "contactInfo": "555-0789"
}
```

### **PUT /api/vendors/{id}** - Update Vendor Profile (Protected)
```http
PUT /api/vendors/1
Authorization: Bearer <token>
Content-Type: application/json

{
  "businessName": "Updated Business Name",
  "description": "Updated description",
  "location": "Updated location"
}
```

### **GET /api/vendors/my-profile** - Get Current Vendor Profile (Protected)
```http
GET /api/vendors/my-profile
Authorization: Bearer <token>
```

---

## 🛍️ **5. Vendor Services Endpoints**

### **GET /api/vendors/{vendorId}/services** - Get Services by Vendor
```http
GET /api/vendors/1/services
```

### **POST /api/vendors/{vendorId}/services** - Add Service (Protected)
```http
POST /api/vendors/1/services
Authorization: Bearer <token>
Content-Type: application/json

{
  "name": "Wedding Photography",
  "description": "Complete wedding photo coverage",
  "price": 1500.00,
  "duration": "8 hours"
}
```

### **PUT /api/vendors/services/{serviceId}** - Update Service (Protected)
```http
PUT /api/vendors/services/1
Authorization: Bearer <token>
Content-Type: application/json

{
  "name": "Updated Service Name",
  "price": 1800.00
}
```

### **DELETE /api/vendors/services/{serviceId}** - Delete Service (Protected)
```http
DELETE /api/vendors/services/1
Authorization: Bearer <token>
```

---

## 🖼️ **6. Vendor Portfolio Endpoints**

### **GET /api/vendors/{vendorId}/portfolio** - Get Portfolio Items
```http
GET /api/vendors/1/portfolio
```

### **POST /api/vendors/{vendorId}/portfolio** - Add Portfolio Item (Protected)
```http
POST /api/vendors/1/portfolio
Authorization: Bearer <token>
Content-Type: application/json

{
  "title": "Beautiful Wedding",
  "description": "A gorgeous outdoor wedding ceremony",
  "imageUrl": "https://example.com/image.jpg"
}
```

### **PUT /api/vendors/portfolio/{itemId}** - Update Portfolio Item (Protected)
```http
PUT /api/vendors/portfolio/1
Authorization: Bearer <token>
```

### **DELETE /api/vendors/portfolio/{itemId}** - Delete Portfolio Item (Protected)
```http
DELETE /api/vendors/portfolio/1
Authorization: Bearer <token>
```

---

## ⭐ **7. Vendor Reviews Endpoints**

### **GET /api/vendors/{vendorId}/reviews** - Get Reviews by Vendor
```http
GET /api/vendors/1/reviews
```

### **POST /api/vendors/{vendorId}/reviews** - Add Review (Protected)
```http
POST /api/vendors/1/reviews
Authorization: Bearer <token>
Content-Type: application/json

{
  "rating": 5,
  "comment": "Excellent service!",
  "eventDate": "2025-08-15"
}
```

### **PUT /api/vendors/reviews/{reviewId}** - Update Review (Protected)
```http
PUT /api/vendors/reviews/1
Authorization: Bearer <token>
```

### **DELETE /api/vendors/reviews/{reviewId}** - Delete Review (Protected)
```http
DELETE /api/vendors/reviews/1
Authorization: Bearer <token>
```

---

## 🎉 **8. Event Management Endpoints**

### **GET /api/events** - Get All Events (Protected)
```http
GET /api/events
Authorization: Bearer <token>
```

### **GET /api/events/{id}** - Get Event by ID (Public)
```http
GET /api/events/1
```

### **POST /api/events** - Create Event (Protected)
```http
POST /api/events
Authorization: Bearer <token>
Content-Type: application/json

{
  "title": "Wedding Reception",
  "description": "Beautiful wedding celebration",
  "date": "2025-12-25T18:00:00",
  "location": "Grand Ballroom, NYC",
  "budget": 10000.00
}
```

### **PUT /api/events/{id}** - Update Event (Protected)
```http
PUT /api/events/1
Authorization: Bearer <token>
Content-Type: application/json

{
  "title": "Updated Event Title",
  "budget": 12000.00
}
```

### **DELETE /api/events/{id}** - Delete Event (Protected)
```http
DELETE /api/events/1
Authorization: Bearer <token>
```

### **GET /api/events/my-events** - Get User's Events (Protected)
```http
GET /api/events/my-events
Authorization: Bearer <token>
```

### **GET /api/events/upcoming** - Get Upcoming Events
```http
GET /api/events/upcoming
```

### **GET /api/events/search** - Search Events
```http
GET /api/events/search?query=wedding
```

---

## 👥 **9. Event Guest Management**

### **POST /api/events/{eventId}/guests** - Add Guest to Event (Protected)
```http
POST /api/events/1/guests
Authorization: Bearer <token>
Content-Type: application/json

{
  "name": "Jane Doe",
  "email": "jane@example.com",
  "rsvpStatus": "PENDING"
}
```

### **GET /api/events/{eventId}/guests** - Get Event Guests (Protected)
```http
GET /api/events/1/guests
Authorization: Bearer <token>
```

### **PUT /api/events/guests/{guestId}** - Update Guest (Protected)
```http
PUT /api/events/guests/1
Authorization: Bearer <token>
```

### **DELETE /api/events/guests/{guestId}** - Remove Guest (Protected)
```http
DELETE /api/events/guests/1
Authorization: Bearer <token>
```

### **PUT /api/events/guests/{guestId}/rsvp/{status}** - Update RSVP Status
```http
PUT /api/events/guests/1/rsvp/ACCEPTED
```

---

## 📅 **10. Booking Management Endpoints**

### **GET /api/bookings** - Get All Bookings (Protected)
```http
GET /api/bookings
Authorization: Bearer <token>
```

### **GET /api/bookings/{id}** - Get Booking by ID (Protected)
```http
GET /api/bookings/1
Authorization: Bearer <token>
```

### **POST /api/bookings** - Create Booking (Protected)
```http
POST /api/bookings
Authorization: Bearer <token>
Content-Type: application/json

{
  "eventId": 1,
  "vendorId": 1,
  "serviceId": 1,
  "bookingDate": "2025-12-25T10:00:00",
  "notes": "Special requirements"
}
```

### **PUT /api/bookings/{id}** - Update Booking (Protected)
```http
PUT /api/bookings/1
Authorization: Bearer <token>
```

### **DELETE /api/bookings/{id}** - Cancel Booking (Protected)
```http
DELETE /api/bookings/1
Authorization: Bearer <token>
```

### **GET /api/bookings/my-bookings** - Get User's Bookings (Protected)
```http
GET /api/bookings/my-bookings
Authorization: Bearer <token>
```

### **GET /api/bookings/event/{eventId}** - Get Bookings by Event (Protected)
```http
GET /api/bookings/event/1
Authorization: Bearer <token>
```

### **GET /api/bookings/vendor/{vendorId}** - Get Bookings by Vendor (Protected)
```http
GET /api/bookings/vendor/1
Authorization: Bearer <token>
```

---

## 📊 **11. Statistics Endpoints**

### **GET /api/events/{eventId}/stats** - Get Event Statistics (Protected)
```http
GET /api/events/1/stats
Authorization: Bearer <token>
```

### **GET /api/vendors/{vendorId}/stats** - Get Vendor Statistics (Protected)
```http
GET /api/vendors/1/stats
Authorization: Bearer <token>
```

---

# 🚀 **Frontend Integration Guide**

## **Step 1: Environment Setup**

### **Backend Configuration**
Ensure your backend is running with cross-origin support:
- Backend URL: `http://192.168.100.7:8080` (or your backend IP)
- CORS enabled for your frontend domain
- JWT authentication configured

### **Frontend Environment Variables**
Create `.env` file in your frontend project:
```env
# React/Next.js
REACT_APP_API_BASE_URL=http://192.168.100.7:8080
REACT_APP_API_VERSION=v1

# Vue/Nuxt
VUE_APP_API_BASE_URL=http://192.168.100.7:8080

# Vite
VITE_API_BASE_URL=http://192.168.100.7:8080
```

## **Step 2: API Client Setup**

### **Option A: Axios Setup**
```javascript
// api/client.js
import axios from 'axios';

const API_BASE_URL = process.env.REACT_APP_API_BASE_URL || 'http://192.168.100.7:8080';

const apiClient = axios.create({
  baseURL: API_BASE_URL,
  headers: {
    'Content-Type': 'application/json',
  },
  withCredentials: true,
});

// Request interceptor for JWT token
apiClient.interceptors.request.use(
  (config) => {
    const token = localStorage.getItem('authToken');
    if (token) {
      config.headers.Authorization = `Bearer ${token}`;
    }
    return config;
  },
  (error) => Promise.reject(error)
);

// Response interceptor for error handling
apiClient.interceptors.response.use(
  (response) => response,
  (error) => {
    if (error.response?.status === 401) {
      // Handle unauthorized access
      localStorage.removeItem('authToken');
      window.location.href = '/login';
    }
    return Promise.reject(error);
  }
);

export default apiClient;
```

### **Option B: Fetch API Setup**
```javascript
// api/client.js
const API_BASE_URL = 'http://192.168.100.7:8080';

class ApiClient {
  constructor() {
    this.baseURL = API_BASE_URL;
  }

  async request(endpoint, options = {}) {
    const token = localStorage.getItem('authToken');
    
    const config = {
      headers: {
        'Content-Type': 'application/json',
        ...(token && { Authorization: `Bearer ${token}` }),
        ...options.headers,
      },
      credentials: 'include',
      ...options,
    };

    if (options.body && typeof options.body === 'object') {
      config.body = JSON.stringify(options.body);
    }

    const response = await fetch(`${this.baseURL}${endpoint}`, config);
    
    if (response.status === 401) {
      localStorage.removeItem('authToken');
      window.location.href = '/login';
      return;
    }

    if (!response.ok) {
      throw new Error(`HTTP error! status: ${response.status}`);
    }

    const contentType = response.headers.get('content-type');
    if (contentType && contentType.includes('application/json')) {
      return response.json();
    }
    
    return response.text();
  }

  // Convenience methods
  get(endpoint) {
    return this.request(endpoint, { method: 'GET' });
  }

  post(endpoint, data) {
    return this.request(endpoint, { method: 'POST', body: data });
  }

  put(endpoint, data) {
    return this.request(endpoint, { method: 'PUT', body: data });
  }

  delete(endpoint) {
    return this.request(endpoint, { method: 'DELETE' });
  }
}

export default new ApiClient();
```

## **Step 3: Authentication Service**

```javascript
// services/authService.js
import apiClient from '../api/client';

class AuthService {
  async register(userData) {
    try {
      const response = await apiClient.post('/auth/register', userData);
      
      if (response.token) {
        localStorage.setItem('authToken', response.token);
        localStorage.setItem('user', JSON.stringify({
          id: response.id,
          username: response.username,
          email: response.email
        }));
      }
      
      return response;
    } catch (error) {
      throw new Error(error.response?.data?.message || 'Registration failed');
    }
  }

  async login(credentials) {
    try {
      const response = await apiClient.post('/auth/login', credentials);
      
      if (response.token) {
        localStorage.setItem('authToken', response.token);
        localStorage.setItem('user', JSON.stringify({
          id: response.id,
          username: response.username,
          email: response.email
        }));
      }
      
      return response;
    } catch (error) {
      throw new Error(error.response?.data?.message || 'Login failed');
    }
  }

  logout() {
    localStorage.removeItem('authToken');
    localStorage.removeItem('user');
    window.location.href = '/login';
  }

  getCurrentUser() {
    const user = localStorage.getItem('user');
    return user ? JSON.parse(user) : null;
  }

  isAuthenticated() {
    return !!localStorage.getItem('authToken');
  }

  async getProfile() {
    return apiClient.get('/auth/profile');
  }
}

export default new AuthService();
```

## **Step 4: Service Classes Examples**

### **Vendor Service**
```javascript
// services/vendorService.js
import apiClient from '../api/client';

class VendorService {
  async getAllVendors(page = 0, size = 20) {
    return apiClient.get(`/api/vendors?page=${page}&size=${size}`);
  }

  async getVendorById(id) {
    return apiClient.get(`/api/vendors/${id}`);
  }

  async searchVendors(query) {
    return apiClient.get(`/api/vendors/search?query=${encodeURIComponent(query)}`);
  }

  async getVendorsByLocation(location) {
    return apiClient.get(`/api/vendors/location?location=${encodeURIComponent(location)}`);
  }

  async createVendorProfile(profileData) {
    return apiClient.post('/api/vendors/profile', profileData);
  }

  async updateVendorProfile(id, profileData) {
    return apiClient.put(`/api/vendors/${id}`, profileData);
  }

  async getMyProfile() {
    return apiClient.get('/api/vendors/my-profile');
  }

  async getVendorServices(vendorId) {
    return apiClient.get(`/api/vendors/${vendorId}/services`);
  }

  async addService(vendorId, serviceData) {
    return apiClient.post(`/api/vendors/${vendorId}/services`, serviceData);
  }

  async updateService(serviceId, serviceData) {
    return apiClient.put(`/api/vendors/services/${serviceId}`, serviceData);
  }

  async deleteService(serviceId) {
    return apiClient.delete(`/api/vendors/services/${serviceId}`);
  }
}

export default new VendorService();
```

### **Event Service**
```javascript
// services/eventService.js
import apiClient from '../api/client';

class EventService {
  async getAllEvents() {
    return apiClient.get('/api/events');
  }

  async getEventById(id) {
    return apiClient.get(`/api/events/${id}`);
  }

  async createEvent(eventData) {
    return apiClient.post('/api/events', eventData);
  }

  async updateEvent(id, eventData) {
    return apiClient.put(`/api/events/${id}`, eventData);
  }

  async deleteEvent(id) {
    return apiClient.delete(`/api/events/${id}`);
  }

  async getMyEvents() {
    return apiClient.get('/api/events/my-events');
  }

  async getUpcomingEvents() {
    return apiClient.get('/api/events/upcoming');
  }

  async searchEvents(query) {
    return apiClient.get(`/api/events/search?query=${encodeURIComponent(query)}`);
  }

  async addGuest(eventId, guestData) {
    return apiClient.post(`/api/events/${eventId}/guests`, guestData);
  }

  async getEventGuests(eventId) {
    return apiClient.get(`/api/events/${eventId}/guests`);
  }

  async updateGuestRSVP(guestId, status) {
    return apiClient.put(`/api/events/guests/${guestId}/rsvp/${status}`);
  }
}

export default new EventService();
```

## **Step 5: React Component Examples**

### **Login Component**
```jsx
// components/Login.jsx
import React, { useState } from 'react';
import authService from '../services/authService';

const Login = ({ onLogin }) => {
  const [credentials, setCredentials] = useState({
    username: '',
    password: ''
  });
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState('');

  const handleSubmit = async (e) => {
    e.preventDefault();
    setLoading(true);
    setError('');

    try {
      await authService.login(credentials);
      onLogin();
    } catch (error) {
      setError(error.message);
    } finally {
      setLoading(false);
    }
  };

  return (
    <form onSubmit={handleSubmit}>
      <div>
        <label>Username:</label>
        <input
          type="text"
          value={credentials.username}
          onChange={(e) => setCredentials({
            ...credentials,
            username: e.target.value
          })}
          required
        />
      </div>
      <div>
        <label>Password:</label>
        <input
          type="password"
          value={credentials.password}
          onChange={(e) => setCredentials({
            ...credentials,
            password: e.target.value
          })}
          required
        />
      </div>
      {error && <div className="error">{error}</div>}
      <button type="submit" disabled={loading}>
        {loading ? 'Logging in...' : 'Login'}
      </button>
    </form>
  );
};

export default Login;
```

### **Vendor List Component**
```jsx
// components/VendorList.jsx
import React, { useState, useEffect } from 'react';
import vendorService from '../services/vendorService';

const VendorList = () => {
  const [vendors, setVendors] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');
  const [searchQuery, setSearchQuery] = useState('');

  useEffect(() => {
    loadVendors();
  }, []);

  const loadVendors = async () => {
    try {
      const response = await vendorService.getAllVendors();
      setVendors(response.content || response);
    } catch (error) {
      setError('Failed to load vendors');
      console.error(error);
    } finally {
      setLoading(false);
    }
  };

  const handleSearch = async (e) => {
    e.preventDefault();
    if (!searchQuery.trim()) {
      loadVendors();
      return;
    }

    setLoading(true);
    try {
      const results = await vendorService.searchVendors(searchQuery);
      setVendors(results);
    } catch (error) {
      setError('Search failed');
    } finally {
      setLoading(false);
    }
  };

  if (loading) return <div>Loading vendors...</div>;
  if (error) return <div className="error">{error}</div>;

  return (
    <div>
      <form onSubmit={handleSearch}>
        <input
          type="text"
          placeholder="Search vendors..."
          value={searchQuery}
          onChange={(e) => setSearchQuery(e.target.value)}
        />
        <button type="submit">Search</button>
      </form>

      <div className="vendor-grid">
        {vendors.map(vendor => (
          <div key={vendor.id} className="vendor-card">
            <h3>{vendor.businessName}</h3>
            <p>{vendor.description}</p>
            <p><strong>Location:</strong> {vendor.location}</p>
            <p><strong>Rating:</strong> {vendor.averageRating}/5</p>
            <p><strong>Services:</strong> {vendor.servicesCount}</p>
            <button onClick={() => window.location.href = `/vendors/${vendor.id}`}>
              View Details
            </button>
          </div>
        ))}
      </div>
    </div>
  );
};

export default VendorList;
```

## **Step 6: Error Handling**

### **Global Error Handler**
```javascript
// utils/errorHandler.js
export const handleApiError = (error) => {
  if (error.response) {
    // Server responded with error status
    const { status, data } = error.response;
    
    switch (status) {
      case 400:
        return data.message || 'Bad request. Please check your input.';
      case 401:
        return 'You are not authorized. Please login again.';
      case 403:
        return 'You do not have permission to perform this action.';
      case 404:
        return 'The requested resource was not found.';
      case 500:
        return 'Server error. Please try again later.';
      default:
        return data.message || 'An unexpected error occurred.';
    }
  } else if (error.request) {
    // Network error
    return 'Network error. Please check your connection.';
  } else {
    // Other error
    return error.message || 'An unexpected error occurred.';
  }
};
```

## **Step 7: Protected Routes (React Router)**

```jsx
// components/ProtectedRoute.jsx
import React from 'react';
import { Navigate } from 'react-router-dom';
import authService from '../services/authService';

const ProtectedRoute = ({ children }) => {
  return authService.isAuthenticated() ? children : <Navigate to="/login" />;
};

export default ProtectedRoute;
```

## **Step 8: Complete React App Example**

```jsx
// App.jsx
import React from 'react';
import { BrowserRouter as Router, Routes, Route } from 'react-router-dom';
import Login from './components/Login';
import VendorList from './components/VendorList';
import ProtectedRoute from './components/ProtectedRoute';
import './App.css';

function App() {
  return (
    <Router>
      <div className="App">
        <Routes>
          <Route path="/login" element={<Login />} />
          <Route path="/vendors" element={<VendorList />} />
          <Route path="/dashboard" element={
            <ProtectedRoute>
              <Dashboard />
            </ProtectedRoute>
          } />
          <Route path="/" element={<VendorList />} />
        </Routes>
      </div>
    </Router>
  );
}

export default App;
```

---

## 🔧 **Testing Your Integration**

### **1. Health Check Test**
```javascript
// Test backend connectivity
fetch('http://192.168.100.7:8080/health')
  .then(response => response.json())
  .then(data => console.log('Backend is running:', data))
  .catch(error => console.error('Backend connection failed:', error));
```

### **2. Authentication Test**
```javascript
// Test user registration and login
const testAuth = async () => {
  try {
    // Register
    const registerResponse = await authService.register({
      username: 'testuser',
      password: 'password123',
      confirmPassword: 'password123',
      email: 'test@example.com',
      firstName: 'Test',
      lastName: 'User'
    });
    console.log('Registration successful:', registerResponse);

    // Login
    const loginResponse = await authService.login({
      username: 'testuser',
      password: 'password123'
    });
    console.log('Login successful:', loginResponse);
  } catch (error) {
    console.error('Auth test failed:', error);
  }
};
```

### **3. API Endpoints Test**
```javascript
// Test vendor endpoints
const testVendorEndpoints = async () => {
  try {
    // Get all vendors
    const vendors = await vendorService.getAllVendors();
    console.log('Vendors loaded:', vendors);

    // Search vendors
    const searchResults = await vendorService.searchVendors('photography');
    console.log('Search results:', searchResults);
  } catch (error) {
    console.error('Vendor API test failed:', error);
  }
};
```

---

## 🛠️ **Troubleshooting**

### **Common Issues and Solutions**

1. **CORS Errors**
   - Ensure backend CORS is configured for your frontend URL
   - Check that `withCredentials: true` is set in your HTTP client

2. **Authentication Issues**
   - Verify JWT token is being stored and sent correctly
   - Check token expiration (24 hours)
   - Ensure Authorization header format: `Bearer <token>`

3. **Network Connectivity**
   - Verify backend IP address and port
   - Check firewall settings
   - Test with curl: `curl http://192.168.100.7:8080/health`

4. **API Response Issues**
   - Check Content-Type headers
   - Verify request/response body format
   - Use browser developer tools to inspect network requests

---

## 📱 **Mobile Integration**

### **React Native Example**
```javascript
// services/apiClient.js for React Native
const API_BASE_URL = 'http://192.168.100.7:8080';

export const apiRequest = async (endpoint, options = {}) => {
  const token = await AsyncStorage.getItem('authToken');
  
  const config = {
    headers: {
      'Content-Type': 'application/json',
      ...(token && { Authorization: `Bearer ${token}` }),
      ...options.headers,
    },
    ...options,
  };

  const response = await fetch(`${API_BASE_URL}${endpoint}`, config);
  
  if (!response.ok) {
    throw new Error(`HTTP error! status: ${response.status}`);
  }
  
  return response.json();
};
```

---

## 🚀 **Performance Tips**

1. **Caching**: Implement response caching for frequently accessed data
2. **Pagination**: Use pagination for large data sets
3. **Debouncing**: Implement search debouncing to reduce API calls
4. **Error Retry**: Implement automatic retry for failed requests
5. **Connection Pooling**: Reuse HTTP connections when possible

---

## 📋 **Quick Reference**

### **Authentication Headers**
```
Authorization: Bearer eyJhbGciOiJIUzUxMiJ9...
Content-Type: application/json
```

### **Common Response Codes**
- `200` - Success
- `201` - Created
- `400` - Bad Request
- `401` - Unauthorized
- `403` - Forbidden
- `404` - Not Found
- `500` - Internal Server Error

### **Base URLs for Different Environments**
- **Development**: `http://192.168.100.7:8080`
- **Local Testing**: `http://localhost:8080`
- **Production**: `https://your-domain.com`

---

**🎉 Your Evently API integration is now complete!**

This comprehensive guide covers all endpoints, authentication, error handling, and provides ready-to-use code examples. Your frontend should now be able to communicate seamlessly with the Spring Boot backend.