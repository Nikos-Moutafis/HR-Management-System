package gr.aueb.cf.springapp.authentication;

import gr.aueb.cf.springapp.dao.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
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
     * then validates these credentials against the UserRepository.
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
        String password = authentication.getCredentials().toString();

        if (!userRepository.isUserValid(username,password)){
            throw new BadCredentialsException(accessor.getMessage("badCredentials"));
        }

        return new UsernamePasswordAuthenticationToken(username, password, Collections.<GrantedAuthority>emptyList());
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
