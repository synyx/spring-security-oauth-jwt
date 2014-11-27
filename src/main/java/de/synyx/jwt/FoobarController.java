package de.synyx.jwt;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Provides very simple controller method mapped to "/foobar"
 */
@RestController
public class FoobarController {

    @RequestMapping("/foobar")
    public String foobar() {
        return "hello OAuth2!";
    }
}
