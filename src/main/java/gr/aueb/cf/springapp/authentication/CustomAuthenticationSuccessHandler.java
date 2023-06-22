package gr.aueb.cf.springapp.authentication;

import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * This is a custom implementation of an AuthenticationSuccessHandler, which extends SimpleUrlAuthenticationSuccessHandler.
 * It is responsible for handling what happens after a successful authentication, specifically,
 * it decides where to redirect the user after successful login.
 */
public class CustomAuthenticationSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {
    public static final String REDIRECT_URL_SECTION_ATTRIBUTE_NAME = "REDIRECT_URL";

    /**
     * This method is invoked when a user is successfully authenticated.
     * If a redirect URL is stored in the session, it sets that as the default target URL.
     * If no redirect URL is found, it defaults to "/employees/list".
     * After setting the URL, it removes the redirect URL from the session,
     * then invokes the superclass onAuthenticationSuccess method.
     *
     * @param request the request which caused the successful authentication
     * @param response the response
     * @param authentication the <tt>Authentication</tt> object which was created during
     * the authentication process.
     * @throws IOException
     * @throws ServletException
     */
    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException, ServletException{

        Object redirectURLObject = request.getSession().getAttribute(REDIRECT_URL_SECTION_ATTRIBUTE_NAME);

        if (redirectURLObject != null) {
            setDefaultTargetUrl(redirectURLObject.toString());
        }else {
            setDefaultTargetUrl("/employees/list");
        }

        request.getSession().removeAttribute(REDIRECT_URL_SECTION_ATTRIBUTE_NAME);
        super.onAuthenticationSuccess(request, response, authentication);
    }
}
