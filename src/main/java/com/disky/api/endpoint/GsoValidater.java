package com.disky.api.endpoint;
import com.disky.api.Exceptions.GetUserException;
import com.disky.api.controller.UserController;
import com.disky.api.filter.UserFilter;
import com.disky.api.model.User;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import org.springframework.web.bind.annotation.*;

import java.io.*;
import java.security.GeneralSecurityException;
import java.util.*;

@RequestMapping("/auth/validategso")
@RestController
@CrossOrigin
public class GsoValidater {
    GoogleIdTokenVerifier verifier = new GoogleIdTokenVerifier.Builder(new NetHttpTransport(), new GsonFactory())
            .setAudience(Arrays.asList("331251032035-3t6a82lqs6k1n1ecbr010qfks35lt3sv.apps.googleusercontent.com", "331251032035-q46uh4q3psjrimko8sbpibit08997nnn.apps.googleusercontent.com"))
            .build();

    @PostMapping
    public User validategso(@RequestParam String id_token) throws GeneralSecurityException, IOException, GetUserException {
        User user = null;

        GoogleIdToken idToken = verifier.verify(id_token);
        if (idToken != null) {
            GoogleIdToken.Payload payload = idToken.getPayload();

            UserFilter filter = new UserFilter();
            List<String> emails = new ArrayList<>();
            emails.add(payload.getEmail());
            filter.setUserNames(emails);
            user = UserController.getOne(filter);

            if (user == null) {
                UserController.save(
                        new User(null, payload.getEmail(), (String) payload.get("name"), "", "", null, UUID.randomUUID().toString(), null),
                        null
                );

                user = UserController.getOne(filter);
            }

        }
        return user;
    }

}
