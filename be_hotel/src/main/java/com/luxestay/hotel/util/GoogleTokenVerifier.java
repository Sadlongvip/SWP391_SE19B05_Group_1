package com.luxestay.hotel.util;

import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken.Payload;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import org.springframework.stereotype.Component;

import java.util.Collections;

@Component
public class GoogleTokenVerifier {
    private final String clientId = "your-google-client-id"; // Replace with actual client ID

    public GoogleUserInfo verify(String idTokenString) throws Exception {
        GoogleIdTokenVerifier verifier = new GoogleIdTokenVerifier.Builder(new NetHttpTransport(), new GsonFactory())
            .setAudience(Collections.singletonList(clientId))
            .build();

        GoogleIdToken idToken = verifier.verify(idTokenString);
        if (idToken != null) {
            Payload payload = idToken.getPayload();
            return new GoogleUserInfo(
                payload.getSubject(),
                (String) payload.get("name"),
                payload.getEmail(),
                (String) payload.get("picture")
            );
        }
        throw new Exception("Invalid Google token");
    }
}

class GoogleUserInfo {
    private String id;
    private String name;
    private String email;
    private String picture;

    public GoogleUserInfo(String id, String name, String email, String picture) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.picture = picture;
    }

    public String getId() { return id; }
    public String getName() { return name; }
    public String getEmail() { return email; }
    public String getPicture() { return picture; }
}