package gr.aueb.cf.springapp.authentication;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.BeanIds;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

/**
 * The SecurityConfig class provides a central location for application security
 * configuration.  It also enables Spring Security's web security support.
 */

@Configuration
@EnableWebSecurity
public class SecurityConfig {
    private final CustomAuthenticationProvider authProvider;

    @Autowired
    public SecurityConfig(CustomAuthenticationProvider authProvider) {
        this.authProvider = authProvider;
    }

    @Autowired
    public void configure(AuthenticationManagerBuilder auth) {
        auth.authenticationProvider(authProvider);
    }

    /**
     * Builds the SecurityFilterChain which is used by Spring Security to apply
     * security configurations. The method is marked as @Bean to indicate that it
     * should be used to create a Bean in the Spring context.
     *
     * @param http an HttpSecurity instance.
     * @return a SecurityFilterChain instance.
     * @throws Exception if there is an issue configuring the HttpSecurity instance.
     */
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.cors().and().csrf().disable()
//        http.cors().disable()
                .authorizeRequests().antMatchers("/login").permitAll()
                .and()
                .authorizeRequests().antMatchers("/home").permitAll()
                .and()
                .authorizeRequests().antMatchers("/register").permitAll()
                .and()
                .authorizeRequests().antMatchers("/employees/**").authenticated()
                .anyRequest().authenticated().and().formLogin()
                .loginPage("/login").defaultSuccessUrl("/home").permitAll()
                .and().httpBasic()
                .and()
                .logout().logoutRequestMatcher(new AntPathRequestMatcher("/logout")).logoutSuccessUrl("/login");
        return http.build();
    }

    /**
     * Configures Spring Security to ignore certain requests. The method is marked
     * as @Bean to indicate that it should be used to create a Bean in the Spring context.
     *
     * @return a WebSecurityCustomizer instance.
     */
    @Bean
    public WebSecurityCustomizer webSecurityCustomizer(){
        return web -> web.ignoring().antMatchers("/styles/**", "/img/**", "/js/**");
    }

    /**
     * Returns the AuthenticationManager instance created by Spring Security.
     *
     * @param authenticationConfiguration an AuthenticationConfiguration instance.
     * @return an AuthenticationManager instance.
     * @throws Exception if there is an issue getting the AuthenticationManager instance.
     */
    @Bean(name = BeanIds.AUTHENTICATION_MANAGER)
    public AuthenticationManager authenticationManagerBean(AuthenticationConfiguration authenticationConfiguration)
            throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }
}
