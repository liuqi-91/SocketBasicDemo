package com.lx.socketbasicdemo;

import android.content.res.AssetManager;

import java.io.FileInputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.Proxy;
import java.net.Socket;
import java.net.UnknownHostException;
import java.security.KeyStore;
import java.security.SecureRandom;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.Arrays;
import java.util.Random;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509TrustManager;

import okhttp3.Call;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okio.ByteString;

public class OKHttpClient {

    public OKHttpClient (){

    }

    public static void main(String[] args) {
//        String pub = "sha256/MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAvedervYmOr79CQ9zzIb0" +
//                "5CEL6jYmHcvV0gEH4/BxN3KJmhm9LnOeOpYAvZRm/GKWNyC0w9h+ZfVLUZBmFmGG" +
//                "m34BGLHC7ucoAl2QBNEAx2Q7PhDpDHqYUvWhTBjE1uCjeIeH45kq2wl2AMV7eVVZ" +
//                "cvrvIS+DiP0Qg+MJ/Dp6OG9VJMPgB2oStArMnUOVEGPeHLME8/lfFtfFvlk/aoky" +
//                "/SYD6veCaWSUQQr2eYwb8SO2twrWSRBCP36DAwo9o+W30p6qMIZDGVDZDyCzgGX0" +
//                "kxrrZ/X4W56vNj0ypN273B2X0TzxCKU3cPXzBs74g9gawuDhj/y22aKdcLDzJnvu" +
//                "DwIDAQAB";
//
//        final ByteString hash = ByteString.decodeBase64(pub.substring("sha256/".length()));
//        final byte[] keys = ("MIIEvQIBADANBgkqhkiG9w0BAQEFAASCBKcwggSjAgEAAoIBAQC9516u9iY6vv0J" +
//                "D3PMhvTkIQvqNiYdy9XSAQfj8HE3comaGb0uc546lgC9lGb8YpY3ILTD2H5l9UtR" +
//                "kGYWYYabfgEYscLu5ygCXZAE0QDHZDs+EOkMephS9aFMGMTW4KN4h4fjmSrbCXYA" +
//                "xXt5VVly+u8hL4OI/RCD4wn8Ono4b1Ukw+AHahK0CsydQ5UQY94cswTz+V8W18W+" +
//                "WT9qiTL9JgPq94JpZJRBCvZ5jBvxI7a3CtZJEEI/foMDCj2j5bfSnqowhkMZUNkP" +
//                "ILOAZfSTGutn9fhbnq82PTKk3bvcHZfRPPEIpTdw9fMGzviD2BrC4OGP/LbZop1w" +
//                "sPMme+4PAgMBAAECggEAXbpGNyByOehtxv0DcFzjnXzZ/pz/LxGd1OqBVHUly/IS" +
//                "DCbMApM58zx53RfTxJl7/nV8AnVl3Ti2FempGT7FSxVjwSZrSbJ5QsGfBWGKet7z" +
//                "hmyeOXSAneFx3LcU3+E7jY+G+ERurHD89uCBUp9XaLKFN+HVj9UnQ5DZKs5J6HPg" +
//                "5Y2Z3iyiUEo24KjICyPqCF7jJM726r0m9FPASFudTp7mbfsNKsXgRShO41P3L63N" +
//                "7QqEBgwGFpPBsO3C66NO5fr5T51OXkjKv+EFa2b5jutRlHPw/47S6PdUE7Vu9G5i" +
//                "vbYuHuenZ/LtqycPqyDnXjj/8JxeFjrJiOl/f3F+YQKBgQDhLmaX3+LVBtfdDKHc" +
//                "GwiLSmW1hiX9nCp8AVPZlTgu0mKdVEVojkzSHZhzYPSmZoejOwqVciirCrb8U7XN" +
//                "crT7HspSWwiFA60vdDtbJ1paGswbp52EWWdgCQoVmNsuphV+f7arV/vfqok4gXG7" +
//                "zGhXrIVRItsgJN4/S/308xQvtwKBgQDX5Pbr16LXsyV85NP9Rr+/kD2s2nFCiUE2" +
//                "CRrTQdsP9SpHqv4N+EHSvBiepCHMmln+aNQpWFIqTIYegoHB58pJ3qnRC/AC2Sn2" +
//                "Tm2zTAv1uV+SATtVGU/PAxPDmw8laBJk6pD9XAbDJTSKCVrvfHAbOrKjK7VIPN9X" +
//                "i4wA1jaEaQKBgCRMGnV4vVOhWsJi+uRD50isoWB1U0JiHecHP8De8MN7XjIGF3oX" +
//                "FgL4ik5u3oDEHjENn1Mfp7aKb5yU0cVgLBQejnZf/iuSwCPaFIdI/KNxll0O07E1" +
//                "yBgO3PVGQm5ujyBo7cUI1azkDGJzWyXueRNI1+SQPcocp0aEtVb6p9JZAoGAKjZc" +
//                "iTR6ZI5kkAz13hnNOBlt1bRIXOgVVglls1S3DmGzSJwIXJbj195rOemBqk2rtvCs" +
//                "OwFH33li5+hAkrdNeDAPKA6beRMY/03KaTo/RGZgZ13SSgtO7Xz+ikpxJbE5l329" +
//                "p6wsrk9B/JNjslu0V76cWwcaxOsSCD4Cu88mjbECgYEAnIVBtovyUfX/xJDG9XsX" +
//                "gitxo7auvtg1ieyBbJ2ClvCXIw8W1SWXsFHJoLGk7FHCNDWHF/WUH993Drtv9u5i" +
//                "45rJvxhWQsV7e8HBm17pBSNSTYUIz5e4ItmAQYqzY+YUu14f6RN7N7p7rlLxwNao" +
//                "q8auHsBF2nyBxDrnJXD2Ifc=").getBytes();
//        final ByteString key1 = ByteString.of(keys).sha256();
//
//        System.out.println("result = "+key1.equals(hash));

        HostnameVerifier hostnameVerifier = new HostnameVerifier() {
            @Override
            public boolean verify(String hostname, SSLSession session) {
                return true;
            }
        };
        try {

                CertificateFactory certificateFactory = CertificateFactory.getInstance("X.509");
                KeyStore           keyStore           = KeyStore.getInstance(KeyStore.getDefaultType());
                keyStore.load(null);
                String certificateAlias = Integer.toString(0);
                keyStore.setCertificateEntry(certificateAlias, certificateFactory.generateCertificate(new FileInputStream("C:\\Users\\Lenovo\\Desktop\\baidu.cer")));//拷贝好的证书
                SSLContext sslContext = SSLContext.getInstance("TLS");
                final TrustManagerFactory trustManagerFactory =
                        TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
                trustManagerFactory.init(keyStore);
                sslContext.init
                        (
                                null,
                                trustManagerFactory.getTrustManagers(),
                                new SecureRandom()
                        );

//            TrustManagerFactory trustManagerFactory = TrustManagerFactory.getInstance(
//                    TrustManagerFactory.getDefaultAlgorithm());
//            trustManagerFactory.init((KeyStore) null);
//            TrustManager[] trustManagers = trustManagerFactory.getTrustManagers();
//            if (trustManagers.length != 1 || !(trustManagers[0] instanceof X509TrustManager)) {
//                throw new IllegalStateException("Unexpected default trust managers:"
//                        + Arrays.toString(trustManagers));
//            }
//            X509TrustManager trustManager = (X509TrustManager) trustManagers[0];


//            X509TrustManager trustManager = new TrustAllCerts();
//            TrustManager[] trustManagers = new TrustManager[]{trustManager};
//            SSLContext sslContext = SSLContext.getInstance("TLS");
//            sslContext.init(null, trustManagers , new SecureRandom());
            SSLSocketFactory sslSocketFactory = sslContext.getSocketFactory();
            OkHttpClient client = new OkHttpClient.Builder()
                    .sslSocketFactory(sslSocketFactory, (X509TrustManager) (trustManagerFactory.getTrustManagers())[0])
                    .hostnameVerifier(hostnameVerifier)
                    .proxy(Proxy.NO_PROXY)
                    .build();

            Call call = client.newCall(new Request.Builder().url("https://baidu.com").build());
            call.execute();
            System.out.println("request = " + "baidu");


        } catch (Exception e) {
            System.out.println("e = " + e);
        }
    }


    public static class TrustAllCerts implements X509TrustManager {
        @Override
        public void checkClientTrusted(X509Certificate[] chain, String authType) {}

        @Override
        public void checkServerTrusted(X509Certificate[] chain, String authType) {}

        @Override
        public X509Certificate[] getAcceptedIssuers() {return new X509Certificate[0];}
    }
}
