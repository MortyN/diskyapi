package com.disky.api.config;

import com.disky.api.util.DatabaseConnection;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

@Configuration
@EnableWebSecurity
@Order(1)
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    @Value("${diskyapi.http.auth-token-header-name}")
    private String principalRequestHeader;

    @Value("${diskyapi.http.auth-token}")
    private String principalAdminRequestValue;


    @Override
    protected void configure(HttpSecurity httpSecurity) throws Exception {
        APIKeyAuthFilter filter = new APIKeyAuthFilter(principalRequestHeader);
        filter.setAuthenticationManager(new AuthenticationManager() {

            @Override
            public Authentication authenticate(Authentication authentication) throws AuthenticationException {
                List<String> principalRequestValue = new ArrayList<>();

                try {
                    Connection con = DatabaseConnection.getConnection();

                    Logger log = Logger.getLogger(String.valueOf(SecurityConfig.class));

                    String sql = "SELECT API_KEY FROM users";
                    PreparedStatement ps = con.prepareStatement(sql);
                    ResultSet rs = ps.executeQuery();

                    while (rs.next()) {
                        principalRequestValue.add(rs.getString("API_KEY"));
                    }
                } catch (SQLException e) {
                    throw new SecurityException("Could not find any api keys");
                }

                String principal = (String) authentication.getPrincipal();
                if (principalRequestValue.contains(principal) || principalAdminRequestValue.equals(principal)) {
                    authentication.setAuthenticated(true);
                    return authentication;

                } else {
                    throw new BadCredentialsException("The api key was not found");
                }
            }
        });
        httpSecurity
                .antMatcher("/api/fiks/dette/hans")
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and().addFilter(filter).authorizeRequests().anyRequest().authenticated();
    }
}
