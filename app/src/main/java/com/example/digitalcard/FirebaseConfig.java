package com.example.digitalcard;

import android.content.Context;

import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;

public final class FirebaseConfig {
    public static final String API_KEY = "AIzaSyD_xhsinP2XtlwXRRmDS88_MEI4luUSSps";

    public static final String APPLICATION_ID = "1:552780166425:android:8fde41295b816db7ad8188";

    public static final String DATABASE_URL = "https://digital-business-card-ca1d8-default-rtdb.europe-west1.firebasedatabase.app/";

    public static void ensureInitialized(Context context) {
        if (context == null) {
            return;
        }
        Context appContext = context.getApplicationContext();
        if (FirebaseApp.getApps(appContext).isEmpty()) {
            FirebaseOptions options = new FirebaseOptions.Builder()
                    .setApiKey(API_KEY)
                    .setApplicationId(APPLICATION_ID)
                    .setDatabaseUrl(DATABASE_URL)
                    .build();
            FirebaseApp.initializeApp(appContext, options);
        }
    }

    public static boolean isConfigured() {
        return !isBlank(API_KEY)
                && !isBlank(APPLICATION_ID)
                && !isBlank(DATABASE_URL);
    }

    private static boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }
}