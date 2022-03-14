package com.armanc.annotationprocessing;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/asd/test8")
public class TestController8 {

    @GetMapping("/test")
    public ResponseEntity<String> test() {
        return ResponseEntity.ok("Success!");
    }

}