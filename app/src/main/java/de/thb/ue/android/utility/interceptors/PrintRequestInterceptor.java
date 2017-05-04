package de.thb.ue.android.utility.interceptors;//package timum.base.utility.interceptors;
//
//import android.util.Log;
//
//import java.io.IOException;
//
//import okhttp3.Interceptor;
//import okhttp3.Request;
//import okhttp3.Response;
//
///**
// * Created by Sebastian Müller on 05.01.2017.
// *
// *
// */
//public class PrintRequestInterceptor implements Interceptor {
//    private static final String TAG = "PRINT INTERCEPTOR";
//
//
//    @Override public Response intercept(Interceptor.Chain chain) throws IOException {
//        Request request = chain.request();
//
//        long t1 = System.nanoTime();
//        Log.i(TAG + " INFO", String.format("\nSending request %s on %s%n%s",
//                request.url(), chain.connection(), request.headers()));
//
//        Response response = chain.proceed(request);
////
////        long t2 = System.nanoTime();
////        Log.i(TAG + " INFO", String.format("\nReceived response for %s in %.1fms%n%sCode: %n",
////                response.request().url(), (t2 - t1) / 1e6d, response.headers(), response.code()));
//
//        return response;
//    }
//}

