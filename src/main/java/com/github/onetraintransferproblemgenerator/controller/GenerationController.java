package com.github.onetraintransferproblemgenerator.controller;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("generation")
public class GenerationController {

    public GenerationController() {

    }

    @PostMapping("generateinstances")
    String generateInstances(@RequestBody GenerationParameters generationParameters) {


        System.out.println(generationParameters.toString());
        return "Test";
    }
}
