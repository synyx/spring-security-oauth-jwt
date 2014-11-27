package de.synyx.jwt;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.oauth2.config.annotation.configurers.ClientDetailsServiceConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerEndpointsConfigurer;
import org.springframework.security.oauth2.provider.token.store.JwtAccessTokenConverter;

/**
 * Provides "/oauth/token" endpoint, which supplies JWT-encoded OAuth2 authentication tokens
 *
 * To acquire a token, POST to "/oauth/token", providing credentials for a registered client app as basic auth
 * and an OAuth2-compliant parameter set
 *
 * Example:
 *
 * curl -X 'POST'
 *  -u my_client_username:my_client_password (1)
 *  --data "username=hdampf (2)
 *      &password=wert123$ (3)
 *      &client_id=my_client_username (4)
 *      &grant_type=password (5)
 *      &scope=foobar_scope" (6)
 *  localhost:8080/oauth/token (7)
 *
 * (1) username and password for this request are transmitted via HTTP basic auth,
 *      they are static for a client application (see configure(ClientDetailsServiceConfigurer clients))
 * (2) in this case, the payload is an OAuth2 request for grant type 'password'
 *      (see http://aaronparecki.com/articles/2012/07/29/1/oauth2-simplified#others)
 * (2/3) the user credentials are the ones that are authenticated against the AuthenticationManager instance
 *      e.g., the actual credentials of the human user using the client application
 * (4) the ID of the client that the user wants to a token for. must be the same as the basic auth username in (1)
 * (5) grant type 'password' means that actual user credentials are supplied
 * (6) the scope in which the token will be valid. this is an arbitrary string that needs to be configured along with
 *      the client (see configure(ClientDetailsServiceConfigurer clients))
 * (7) the url of the endpoint
 *
 */
@EnableAuthorizationServer
@Configuration
public class AuthorizationServer extends AuthorizationServerConfigurerAdapter {

    /**
     * An AuthenticationManager instance is required to enable OAuth2 grant type 'password'
     */
    @Autowired
    private AuthenticationManager authenticationManager;

    /**
     * Supplies an AccessTokenConverter implementation to be used by this endpoint
     *
     * Also sets the 'secret' used to sign the JWT, in this case to 'foobar'
     *
     * @return A JwtAccessTokenConverter instance
     */
    @Bean
    public JwtAccessTokenConverter accessTokenConverter() {
        JwtAccessTokenConverter jwtAccessTokenConverter = new JwtAccessTokenConverter();
        jwtAccessTokenConverter.setSigningKey("foobar");
        return jwtAccessTokenConverter;
    }

    /**
     * Sets up this authorization endpoint
     *
     * @param endpoints
     * @throws Exception
     */
    @Override
    public void configure(AuthorizationServerEndpointsConfigurer endpoints) throws Exception {
        endpoints.authenticationManager(authenticationManager)
                .accessTokenConverter(accessTokenConverter());
    }

    /**
     * Configures a static client application that can request access tokens
     *
     * @param clients
     * @throws Exception
     */
    @Override
    public void configure(ClientDetailsServiceConfigurer clients) throws Exception {
        clients.inMemory()
                .withClient("my_client_username")
                .authorities("ROLE_ADMIN")
                .resourceIds("my_resource_id")
                .scopes("foobar_scope")
                .authorizedGrantTypes("password")
                .secret("my_client_password");
    }
}
