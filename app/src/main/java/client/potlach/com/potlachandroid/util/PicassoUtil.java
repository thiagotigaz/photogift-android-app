package client.potlach.com.potlachandroid.util;

import android.app.Activity;
import android.net.Uri;
import android.util.Log;

import com.squareup.okhttp.OkHttpClient;
import com.squareup.picasso.OkHttpDownloader;
import com.squareup.picasso.Picasso;


import java.io.IOException;
import java.net.HttpURLConnection;
import java.security.cert.X509Certificate;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import client.potlach.com.potlachandroid.oauth.PhotoGiftRestBuilder;

/**
 * Created by thiago on 10/23/14.
 */
public class PicassoUtil {

    public static final String AUTHORIZATION_HEADER = "Authorization";

    public static Picasso getPicassoClient(Activity activity){
        Picasso.Builder builder = new Picasso.Builder(activity);
        Picasso picasso =  builder.downloader(new OkHttpDownloader(activity) {

            @Override
            protected OkHttpClient getClient() {
                OkHttpClient client = super.getClient();
                try {
                    //Connect with SSL
                    // Create a trust manager that does not validate certificate chains
                    TrustManager[] trustAllCerts = new TrustManager[] {new X509TrustManager() {
                        public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                            return new X509Certificate[0];
                        }

                        @Override
                        public void checkClientTrusted(X509Certificate[] certs, String authType) {
                        }

                        @Override
                        public void checkServerTrusted(X509Certificate[] certs, String authType) {
                        }
                    }
                    };

                    // Install the all-trusting trust manager
                    SSLContext sc = SSLContext.getInstance("SSL");
                    sc.init(null, trustAllCerts, new java.security.SecureRandom());


                    SSLSocketFactory sslsocketfactory = sc.getSocketFactory();
                    client.setSslSocketFactory(sslsocketfactory);
                    client.setHostnameVerifier(new HostnameVerifier() {
                        @Override
                        public boolean verify(String hostname, SSLSession session) {
                            return true;
                        }
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                }
//                HostnameVerifier hostnameVerifier = org.apache.http.conn.ssl.SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER;
//                client.setHostnameVerifier(hostnameVerifier);
                return client;
            }

            @Override
            protected HttpURLConnection openConnection(Uri uri) throws IOException {
                HttpURLConnection connection = super.openConnection(uri);
                Log.d("Picasso", "URI: " + uri.toString() + " token: " + PhotoGiftRestBuilder.getAccessToken());
                connection.setRequestProperty(AUTHORIZATION_HEADER, "Bearer " + PhotoGiftRestBuilder.getAccessToken());
                return connection;
            }
        }).listener(new Picasso.Listener() {
            @Override
            public void onImageLoadFailed(Picasso picasso, Uri uri, Exception exception) {
                Log.d("Picasso", "URI: "+uri.toString()+"  Picasso: "+ picasso.toString());
                exception.printStackTrace();
            }
        }).build();

        return picasso;
    }
}
