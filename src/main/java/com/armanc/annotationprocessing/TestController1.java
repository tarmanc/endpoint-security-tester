package com.armanc.annotationprocessing;


import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/test1")
@PreAuthorize("hasAuthority('asd')")
public class TestController1 {

    @GetMapping("/test")
    public ResponseEntity<String> test() {
        return ResponseEntity.ok("Success!");
    }

}
