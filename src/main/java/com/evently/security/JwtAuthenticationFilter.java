package com.evently.security;
// Clear authentication context on error
SecurityContextHolder.clearContext();
}


filterChain.doFilter(request, response);
}


/*
Extract JWT token 
*/
private String getJwtFromRequest(HttpServletRequest request) {
String bearerToken = request.getHeader("Authorization");
if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
return bearerToken.substring(7);
}
return null;
}


/**
* Skip filter for public endpoints and CORS preflight (OPTIONS)
*/
@Override
protected boolean shouldNotFilter(HttpServletRequest request) {
String path = request.getRequestURI();
if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
return true; // allow CORS preflight through
}
return path.equals("/")
|| path.startsWith("/health")
|| path.startsWith("/actuator/health")
|| path.startsWith("/api/auth/")
|| path.startsWith("/swagger-ui/")
|| path.startsWith("/v3/api-docs/")
|| path.equals("/favicon.ico");
}
}
