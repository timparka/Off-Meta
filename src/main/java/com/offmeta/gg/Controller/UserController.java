package com.offmeta.gg.Controller;

import com.offmeta.gg.Service.UserRegistrationService;
import com.offmeta.gg.Service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.security.core.Authentication;
//import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class UserController {

    @Autowired
    private UserRegistrationService userRegistrationService;
    @Autowired
    private UserService userService;

//    @GetMapping("/register")
//    @ResponseStatus(HttpStatus.OK)
//    public String register(Authentication authentication){
//        Jwt jwt = (Jwt) authentication.getPrincipal();
//
//        userRegistrationService.registerUser(jwt.getTokenValue());
//        return "User Registration successful";
//    }

    @GetMapping("/fetch")
    @ResponseStatus(HttpStatus.OK)
    public String fetchPatchData() {
        userService.fetchData();
        return "Successfully fetched data!";
    }

    @DeleteMapping("/newpatch")
    @ResponseStatus(HttpStatus.OK)
    public String newPatchData() {
        userService.newPatchData();
        return "Successfully cleared data!";
    }
}
