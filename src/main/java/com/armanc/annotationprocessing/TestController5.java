package com.armanc.annotationprocessing;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/asd/test5")
@PreAuthorize("hasAuthority('asd')")
public class TestController5 {

    @GetMapping("/test")
    public ResponseEntity<String> test() {
        return ResponseEntity.ok("Success!");
    }

    @GetMapping("/test2")
    @PreAuthorize("hasAuthority('saad')")
    public ResponseEntity<String> test2() {
        return ResponseEntity.ok("Success!");
    }
}