package com.example.demo.filter;
import com.example.demo.service.PersonService;

import io.jsonwebtoken.ExpiredJwtException;

import com.example.demo.service.JwtService;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.lang.NonNull;

import java.io.IOException;
 
/**
 * JwtAuthFilter is a custom filter used in the authentication process.
 * It intercepts incoming requests to extract the JWT token from the request's "Authorization" header, 
 * validates the token, and sets the authentication in the security context if the token is valid.
 */
@Component
public class JwtAuthFilter extends OncePerRequestFilter{
    
    @Autowired
    private JwtService jwtService;

    @Autowired
    private PersonService personService;

    //This excludes specific urls from the filtering, to allow access without a JWT token
    //Link: https://www.baeldung.com/spring-exclude-filter 
    @Override
    /**
     * Specifies which requests should not be filtered.
     * The endpoint "/auth/generateToken" is excluded from authentication checks.
     *
     * @param request the incoming HTTP request.
     * @throws ServletException if an error occurs.
     * @return true if the request should not be filtered, false otherwise.
     */
    protected boolean shouldNotFilter(@NonNull HttpServletRequest request)
      throws ServletException {
        String path = request.getRequestURI();
        return "/auth/generateToken".equals(path);
    }

    /**
     * Filters the incoming HTTP request to authenticate the user based on a JWT token.
     * If the token is valid, the user is set in the security context.
     * It also handles expired tokens and other authentication-related errors.
     * 
     * @param request the HTTP request to process
     * @param response the HTTP response to send
     * @param filterChain the filter chain to pass the request and response to the next filter
     * @throws ServletException if there is an error processing the request
     * @throws IOException if there is an IO error during request/response processing
     */
    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response,@NonNull FilterChain filterChain) throws ServletException, IOException{
        String authHeader = request.getHeader("Authorization");
        String token = null;
        String username = null;

        try {
            
        if(authHeader != null && authHeader.startsWith("Bearer ")){
            token = authHeader.substring(7);
            username = jwtService.extractSubject(token);
        }

        if(username != null && (SecurityContextHolder.getContext().getAuthentication()==null||SecurityContextHolder.getContext().getAuthentication().getPrincipal().toString().equals("anonymousUser"))){
            UserDetails userDetails = personService.loadUserByUsername(username);
            if(jwtService.validateToken(token, userDetails)){
                UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(userDetails,null,userDetails.getAuthorities());
                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authToken);
                System.out.println("Token validated, user has the authorities "+userDetails.getAuthorities().toString());
            }
        }
        else
        {
            if (SecurityContextHolder.getContext().getAuthentication()!=null) {
                System.out.println(SecurityContextHolder.getContext().getAuthentication().toString());
            }
        }

        filterChain.doFilter(request,response);
    
        } 
        //Handling for expired token
        // https://stackoverflow.com/questions/73052974/why-is-the-expiredjwtexception-not-being-caught-and-handled 
        catch (ExpiredJwtException e) {
            response.setStatus(HttpStatus.UNAUTHORIZED.value());
            response.getWriter().write("{\"error\":\"JWT token expired, please refresh it\"}");
            response.setContentType(MediaType.APPLICATION_JSON_VALUE);
            return;
        }
        /* 
        catch(Exception e)
        {
            response.setStatus(HttpStatus.UNAUTHORIZED.value());
            response.getWriter().write("Unknown error occured while inspecting jwt token :" + e.getMessage());
            response.setContentType(MediaType.APPLICATION_JSON_VALUE);
            return;
        }*/
    } 
} 
