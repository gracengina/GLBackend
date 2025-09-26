# 🚀 Render Deployment Configuration

## Environment Variables for CORS

When deploying to Render, make sure to set the following environment variable in your Render dashboard:

### **CORS Configuration**
```
CORS_ALLOWED_ORIGINS=http://127.0.0.1:5500,http://localhost:5500,https://your-frontend-domain.com
```

### **How to Set Environment Variables in Render:**

1. Go to your Render dashboard
2. Select your backend service (evently-avc4)
3. Go to "Environment" tab
4. Add the following environment variable:
   - **Key**: `CORS_ALLOWED_ORIGINS`
   - **Value**: `http://127.0.0.1:5500,http://localhost:5500,https://your-frontend-domain.com`
5. Click "Save Changes"
6. Render will automatically redeploy your service

### **Alternative: Use Wildcard (Less Secure)**
For development/testing, you can use:
```
CORS_ALLOWED_ORIGINS=*
```
**⚠️ Warning**: Only use wildcard (*) for development. In production, always specify exact origins.

### **Current Default CORS Origins**
The application.properties file includes these default origins:
- `http://localhost:3000,5173,8080`
- `http://127.0.0.1:3000,5500,5501,5502,8000,8080`
- `http://192.168.*:*` (local network)
- `https://*.onrender.com,*.netlify.app,*.vercel.app` (deployment platforms)

### **Testing CORS After Deployment**

Test with curl:
```bash
curl -X OPTIONS -H "Origin: http://127.0.0.1:5500" -H "Access-Control-Request-Method: POST" -H "Access-Control-Request-Headers: Content-Type,Authorization" -v https://evently-avc4.onrender.com/auth/register
```

Look for these headers in response:
- `Access-Control-Allow-Origin: http://127.0.0.1:5500`
- `Access-Control-Allow-Methods: GET,POST,PUT,DELETE,OPTIONS`
- `Access-Control-Allow-Headers: Content-Type, Authorization`
- `Access-Control-Allow-Credentials: true`

### **Frontend Update**
Update your frontend API base URL to point to your deployed backend:
```javascript
const API_BASE_URL = 'https://evently-avc4.onrender.com';
```