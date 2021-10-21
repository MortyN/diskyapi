package com.disky.api.endpoint;

import com.disky.api.filter.testFilter;
import org.springframework.web.bind.annotation.*;

@RequestMapping("/api/v1/test")
@RestController
@CrossOrigin
public class TestEndpoint {

    //GET http://localhost:8080/api/v1/test?id=10000 required default: true, can be false.
    @GetMapping
    public static testFilter getTest(@RequestParam(required = true) int id) {
        return new testFilter(id, "I returned the gotten id");
    }

    //Se facebook. Bildet fra insomnia
    @PostMapping(consumes = "application/json")
    public static testFilter getTestPost(@RequestBody testFilter testFilter) {
        return testFilter;
    }

}
