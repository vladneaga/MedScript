package group11.medScriptAPI.config;

import group11.medScriptAPI.security.AuthProviderImpl;
import group11.medScriptAPI.service.AccountDetailsService;
import group11.medScriptAPI.util.MyPasswordEncoderFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

/**
 * Security configuration for the application, defining authentication and authorization rules.
 * This class configures the security filter chain, CORS settings, and password encoding.
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Autowired
    private AccountDetailsService accountDetailsService;
    @Autowired
    private JWTAuthFIlter jwtAuthFIlter;
    @Autowired
    private AuthProviderImpl authenticationProvider;

    @Autowired
    private MyPasswordEncoderFactory myPasswordEncoderFactory;
    /**
     * Configures the security filter chain for HTTP requests.
     *
     * @param httpSecurity the HttpSecurity object to customize
     * @return the configured SecurityFilterChain
     * @throws Exception if an error occurs during configuration
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {
        httpSecurity
                .csrf(AbstractHttpConfigurer::disable)
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .authorizeHttpRequests(request -> request.requestMatchers("/auth/**", "/public/**").permitAll()
                        .requestMatchers("/admin/**").hasRole("ADMIN")
                        .requestMatchers("/physician/**").hasRole("PHYSICIAN")
                        .requestMatchers("/patient/**").hasRole("PATIENT")
                        .requestMatchers("/adminuser/**").hasAnyAuthority("USER", "ADMIN", "PHYSICIAN")
                        .anyRequest().authenticated())
                .sessionManagement(manager -> manager.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authenticationProvider(authenticationProvider()).addFilterBefore(
                        jwtAuthFIlter, UsernamePasswordAuthenticationFilter.class
                );
        return httpSecurity.build();
    }

    /**
     * Provides an AuthenticationProvider bean for managing authentication.
     *
     * @return the configured AuthenticationProvider
     */
  @Bean
  public AuthenticationProvider authenticationProvider() {
      AuthProviderImpl authProvider = new AuthProviderImpl(accountDetailsService, myPasswordEncoderFactory);
      return authProvider;
  }
    /**
     * Provides a PasswordEncoder bean for encoding passwords.
     *
     * @return the BCryptPasswordEncoder instance
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
    /**
     * Provides an AuthenticationManager bean for authenticating users.
     *
     * @param http the HttpSecurity object for building the AuthenticationManager
     * @return the configured AuthenticationManager
     * @throws Exception if an error occurs during configuration
     */
    @Bean
     public AuthenticationManager authManager(HttpSecurity http) throws Exception {
      AuthenticationManagerBuilder authenticationManagerBuilder =
              http.getSharedObject(AuthenticationManagerBuilder.class);
      authenticationManagerBuilder.authenticationProvider(authenticationProvider).userDetailsService(accountDetailsService);
      return authenticationManagerBuilder.build();
  }
    /**
     * Configures CORS settings for the application.
     *
     * @return the CorsConfigurationSource for handling CORS requests
     */
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(List.of("http://localhost:5173"));
        configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(List.of("*"));
        configuration.setAllowCredentials(true);
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}
