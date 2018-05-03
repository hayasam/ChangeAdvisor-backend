package ch.uzh.ifi.seal.changeadvisor.web;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class DocumentClusteringController {

    @PostMapping(path = "cluster/{appName}")
    public void clusterReviews(@PathVariable("appName") String appName) {


    }
}
