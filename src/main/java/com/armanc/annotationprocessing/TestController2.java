package com.armanc.annotationprocessing;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(path = "/public/test2")
public class TestController2 {

    @GetMapping("/test")
    public ResponseEntity<String> test() {
        return ResponseEntity.ok("Success!");
    }

    @GetMapping("/test2")
    public ResponseEntity<String> test2() {
        return ResponseEntity.ok("Success!");
    }
}
