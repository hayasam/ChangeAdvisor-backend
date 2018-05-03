package ch.uzh.ifi.seal.changeadvisor.web;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Simple controller to check server status from client.
 */
@RestController
public class ServerStatusResource {

    @GetMapping("/is-up")
    public boolean isServerUp() {
        return true;
    }
}
