package de.thb.ue.android.custom;

import android.app.Application;

import com.octo.android.robospice.SpringAndroidSpiceService;
import com.octo.android.robospice.persistence.CacheManager;
import com.octo.android.robospice.persistence.exception.CacheCreationException;
import com.octo.android.robospice.persistence.springandroid.json.jackson2.Jackson2ObjectPersisterFactory;

import org.springframework.http.converter.FormHttpMessageConverter;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import java.util.List;

/**
 * Created by Admin on 01.06.2015.
 */
public class CustomJsonSpiceService extends SpringAndroidSpiceService {
    @Override
    public CacheManager createCacheManager(Application application) throws CacheCreationException {
        CacheManager cacheManager = new CacheManager();
        cacheManager.addPersister(new Jackson2ObjectPersisterFactory(application));
        return cacheManager;
    }

    @Override
    public RestTemplate createRestTemplate() {
        RestTemplate restTemplate = new RestTemplate();

        // web services support json responses
        MappingJackson2HttpMessageConverter jsonConverter = new MappingJackson2HttpMessageConverter();
//        StringHttpMessageConverter stringHttpMessageConverter = new StringHttpMessageConverter();
        FormHttpMessageConverter formHttpMessageConverter = new FormHttpMessageConverter();
        final List<HttpMessageConverter<?>> listHttpMessageConverters = restTemplate.getMessageConverters();

        listHttpMessageConverters.add(jsonConverter);
//        listHttpMessageConverters.add(formHttpMessageConverter);
        restTemplate.setMessageConverters(listHttpMessageConverters);
        return restTemplate;
    }
}