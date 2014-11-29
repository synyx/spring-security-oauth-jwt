package de.synyx.jwt;

import com.jayway.restassured.RestAssured;
import com.jayway.restassured.response.Header;
import com.jayway.restassured.response.Response;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.IntegrationTest;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import java.util.Base64;

import static com.jayway.restassured.RestAssured.*;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Application.class)
@WebAppConfiguration
@IntegrationTest("server.port:0")
public class SpringOauthJwtIntegrationTest {

    @Value("${local.server.port}")
    private int port;

    private String clientBasicAuthCredentials;

    @Before
    public void setUp() {
        RestAssured.port = this.port;
        this.clientBasicAuthCredentials =
                Base64.getEncoder().encodeToString("my_client_username:my_client_password".getBytes());
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
            header(new Header("Authorization", "Basic " + this.clientBasicAuthCredentials)).
        when().
            get("/oauth/token").
        then().
            statusCode(HttpStatus.BAD_REQUEST.value());
    }

    @Test
    public void grantsAccessToken() {
        Response response =
            given().
                header(new Header("Authorization", "Basic " + this.clientBasicAuthCredentials)).
                queryParam("username", "hdampf").
                queryParam("password", "wert123$").
                queryParam("client_id", "my_client_username").
                queryParam("grant_type", "password").
                queryParam("scope", "foobar_scope").
            when().
                post("/oauth/token").
            then().
                statusCode(HttpStatus.OK.value()).
                extract().response();

        Assert.assertEquals("bearer", response.getBody().jsonPath().getString("token_type"));
        Assert.assertEquals("foobar_scope", response.getBody().jsonPath().getString("scope"));
        Assert.assertEquals("eyJhbGciOiJIUzI1NiJ9",
                response.getBody().jsonPath().getString("access_token").split("[.]")[0]);
    }

    @Test
    public void foobarIsAccessibleWithAccessToken() {
        Response tokenResponse =
            given().
                header(new Header("Authorization", "Basic " + this.clientBasicAuthCredentials)).
                queryParam("username", "hdampf").
                queryParam("password", "wert123$").
                queryParam("client_id", "my_client_username").
                queryParam("grant_type", "password").
                queryParam("scope", "foobar_scope").
            when().
                post("/oauth/token").
            then().
                statusCode(HttpStatus.OK.value()).
                extract().response();

        String token = tokenResponse.getBody().jsonPath().getString("access_token");

        Response foobarResponse =
            given().
                header(new Header("Authorization", "Bearer " + token)).
            when().
                get("/foobar").
            then().
                statusCode(HttpStatus.OK.value()).
                extract().response();

        Assert.assertEquals("hello OAuth2!", foobarResponse.getBody().print());
    }

    @Test
    public void accessTokenAreInvalidatedAfterTimeout() throws InterruptedException {
        Response tokenResponse =
            given().
                header(new Header("Authorization", "Basic " + this.clientBasicAuthCredentials)).
                queryParam("username", "hdampf").
                queryParam("password", "wert123$").
                queryParam("client_id", "my_client_username").
                queryParam("grant_type", "password").
                queryParam("scope", "foobar_scope").
            when().
                post("/oauth/token").
            then().
                statusCode(HttpStatus.OK.value()).
                extract().response();

        String token = tokenResponse.getBody().jsonPath().getString("access_token");

        Thread.sleep(2000);

        Response foobarResponse =
            given().
                header(new Header("Authorization", "Bearer " + token)).
            when().
                get("/foobar").
            then().
                statusCode(HttpStatus.UNAUTHORIZED.value()).
                extract().response();

        Assert.assertEquals("invalid_token", foobarResponse.getBody().jsonPath().getString("error"));
        Assert.assertThat(foobarResponse.getBody().jsonPath().getString("error_description"),
                CoreMatchers.startsWith("Access token expired:"));

    }
}
