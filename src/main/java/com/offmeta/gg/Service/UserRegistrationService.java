//package com.offmeta.gg.Service;
//
//import java.io.IOException;
//import java.net.URI;
//import java.net.http.HttpClient;
//import java.net.http.HttpRequest;
//import java.net.http.HttpResponse;
//
//import javax.management.RuntimeErrorException;
//
//import com.offmeta.gg.DTO.UserDTO;
//import com.offmeta.gg.Entity.UserEntity;
//import com.offmeta.gg.Repository.UserRepository;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.stereotype.Service;
//import com.fasterxml.jackson.databind.DeserializationFeature;
//import com.fasterxml.jackson.databind.ObjectMapper;
//
//import lombok.RequiredArgsConstructor;
//
//@Service
//@RequiredArgsConstructor
//public class UserRegistrationService {
//    @Value("${auth0.userinfoEndpoint}")
//    private String userInfoEndpoint;
//
//    private final UserRepository userRepository;
//
//    public void registerUser(String tokenValue) {
//
//        HttpRequest httpRequest = HttpRequest.newBuilder()
//                .GET()
//                .uri(URI.create(userInfoEndpoint))
//                .setHeader("Authorization", String.format("Bearer %s", tokenValue))
//                .build();
//
//        HttpClient httpClient = HttpClient.newBuilder()
//                .version(HttpClient.Version.HTTP_2)
//                .build();
//
//        try {
//            HttpResponse<String> responseString = httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString());
//            String body = responseString.body();
//
//            ObjectMapper objectMapper = new ObjectMapper();
//            objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
//            UserDTO userInfoDTO = objectMapper.readValue(body, UserDTO.class);
//
//            UserEntity user = new UserEntity();
//            user.setFirstName(userInfoDTO.getGivenName());
//            user.setLastName(userInfoDTO.getFamilyName());
//            user.setFullName(userInfoDTO.getName());
//            user.setEmailAddress(userInfoDTO.getEmail());
//            user.setSub(userInfoDTO.getSub());
//
//            userRepository.save(user);
//
//        } catch (IOException | InterruptedException e) {
//            throw new RuntimeErrorException(new Error(e), "Exception occurred while registering user");
//        }
//    }
//}
