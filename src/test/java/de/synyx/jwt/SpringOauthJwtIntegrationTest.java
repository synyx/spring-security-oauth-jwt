package de.synyx.jwt;

import com.jayway.restassured.RestAssured;
import com.jayway.restassured.response.Header;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.IntegrationTest;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import java.util.HashMap;
import java.util.Map;

import static com.jayway.restassured.RestAssured.*;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Application.class)
@WebAppConfiguration
@IntegrationTest("server.port:0")
public class SpringOauthJwtIntegrationTest {

    @Value("${local.server.port}")
    private int port;

    @Before
    public void setUp() {
        RestAssured.port = this.port;
    }

    @Test
    public void foobarRequiresAuthorization() {
        when().
                get("/foobar").
                then().
                statusCode(HttpStatus.UNAUTHORIZED.value());
    }

    @Test
    public void accessTokenRequiresClientCredentialsParameters() {
        when().
                get("/oauth/token").
                then().
                statusCode(HttpStatus.UNAUTHORIZED.value());
    }

    @Test
    public void accessTokenRequiresOAuthParameters() {
        given().
                header(new Header("Authorization", "Basic bXlfY2xpZW50X3VzZXJuYW1lOm15X2NsaWVudF9wYXNzd29yZA==")).
                when().
                get("/oauth/token").
                then().
                statusCode(HttpStatus.BAD_REQUEST.value());
    }

    @Test
    public void grantsAccessToken() {
        given().
                header(new Header("Authorization", "Basic bXlfY2xpZW50X3VzZXJuYW1lOm15X2NsaWVudF9wYXNzd29yZA==")).
                queryParam("username", "hdampf").
                queryParam("password", "wert123$").
                queryParam("client_id", "my_client_username").
                queryParam("grant_type", "password").
                queryParam("scope", "foobar_scope").
                when().
                post("/oauth/token").
                then().
                statusCode(HttpStatus.OK.value()).log().all();
    }
}
