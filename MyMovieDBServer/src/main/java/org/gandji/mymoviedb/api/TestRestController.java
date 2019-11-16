package org.gandji.mymoviedb.api;

import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

/**
 * Created by gandji on 20/10/2019.
 */
@RestController
public class TestRestController {

    @GetMapping(value = "/api/image/carousel")
    public ResponseEntity<InputStreamResource> getImage(@RequestParam("background") String background) throws IOException {

        ClassPathResource imgFile;

        switch(background){
            case "back":
                imgFile = new ClassPathResource("image/jsa_about_img_black_background.png");
                break;
            case "green":
                imgFile = new ClassPathResource("image/jsa_about_img_green_background.png");
                break;
            case "blue":
                imgFile = new ClassPathResource("image/jsa_about_img_blue_background.png");
                break;
            default:
                imgFile = new ClassPathResource("image/jsa_about_img_white_background.png");
        }

        return ResponseEntity
                .ok()
                .contentType(MediaType.IMAGE_PNG)
                .body(new InputStreamResource(imgFile.getInputStream()));
    }
}
