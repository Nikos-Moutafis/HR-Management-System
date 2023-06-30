package gr.aueb.cf.springapp.authentication;

import gr.aueb.cf.springapp.dao.UserRepository;
import gr.aueb.cf.springapp.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.Collections;
import java.util.Locale;

/**
 * This component is a custom implementation of an AuthenticationProvider.
 * It uses a UserRepository to authenticate users and uses a MessageSource to provide user-friendly error messages.
 * It implements the AuthenticationProvider interface provided by Spring Security.
 */
@Component
public class CustomAuthenticationProvider implements AuthenticationProvider {
    private final UserRepository userRepository;
    private final MessageSource messageSource;

    @Autowired
    public CustomAuthenticationProvider(UserRepository userRepository, MessageSource messageSource) {
        this.userRepository = userRepository;
        this.messageSource = messageSource;
    }

    private MessageSourceAccessor accessor;

    /**
     * This method Initializes the MessageSourceAccessor.
     */
    @PostConstruct
    private void init(){
        accessor = new MessageSourceAccessor(messageSource, Locale.getDefault());
    }

    /**
     * This method is responsible for authenticating users.
     * It receives an Authentication object, retrieves the username and password from it,
     * then validates these credentials against the UserRepository. It compares hashed password
     * stored in database versus the password user has provided after it encodes it with bcrypt.
     * If the credentials are valid, it creates a new UsernamePasswordAuthenticationToken and returns it.
     * If the credentials are invalid, it throws a BadCredentialsException.
     *
     * @param authentication the Authentication object
     * @return a fully authenticated object including credentials
     * @throws AuthenticationException if authentication fails
     */
    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        String username = authentication.getName();
        String providedPassword = authentication.getCredentials().toString();

        User user = userRepository.findByUsernameEquals(username);
        if (user == null) {
            throw new BadCredentialsException(accessor.getMessage("badCredentials"));
        }
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        if (!passwordEncoder.matches(providedPassword, user.getPassword())) {
            throw new BadCredentialsException(accessor.getMessage("badCredentials"));
        }
        return new UsernamePasswordAuthenticationToken(username, user.getPassword(), Collections.<GrantedAuthority>emptyList());
    }

    /**
     * This method is used by the AuthenticationManager to determine if this AuthenticationProvider
     * supports the type of Authentication object passed into it.
     *
     * @param authentication the class of the Authentication object
     * @return true if this AuthenticationProvider supports the passed Authentication object, false otherwise
     */
    @Override
    public boolean supports(Class<?> authentication) {
        return (UsernamePasswordAuthenticationToken.class.isAssignableFrom(authentication));
    }
}
