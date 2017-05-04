package de.thb.ue.android.utility.customized_classes;

import com.franmontiel.persistentcookiejar.PersistentCookieJar;
import com.franmontiel.persistentcookiejar.cache.CookieCache;
import com.franmontiel.persistentcookiejar.persistence.CookiePersistor;

import okhttp3.Cookie;

/**
 * Created by Sebastian Müller on 12.01.2017.
 */

public class CheckableCookieJar extends PersistentCookieJar {

    private CookieCache cache;
    private CookiePersistor persistor;

    public CheckableCookieJar(CookieCache cache, CookiePersistor persistor) {
        super(cache, persistor);

        this.cache = cache;
        this.persistor = persistor;
    }

    public boolean isSessionCookieExpired(){
        for (Cookie currentCookie : cache) {
            if (currentCookie.name().equals("PLAY_SESSION") && isCookieExpired(currentCookie)) {
                return true;
            } else if (currentCookie.name().equals("PLAY_SESSION") && !isCookieExpired(currentCookie)) {
                return false;
            }
        }
        return true; // no cookie is an expired one
    }

    private static boolean isCookieExpired(Cookie cookie) {
        return cookie.expiresAt() < System.currentTimeMillis();
    }
}
