/* 
 **
 ** Copyright 2014, Jules White
 **
 ** 
 */
package client.potlach.com.potlachandroid.service;


import android.app.Activity;
import android.content.Intent;

import client.potlach.com.potlachandroid.PhotoGiftApplication;
import client.potlach.com.potlachandroid.activity.LoginActivity;
import client.potlach.com.potlachandroid.model.User;
import client.potlach.com.potlachandroid.oauth.PhotoGiftRestBuilder;
import client.potlach.com.potlachandroid.unsafe.EasyHttpClient;
import retrofit.RestAdapter.LogLevel;
import retrofit.client.ApacheClient;

public class GiftSvc {

	public static final String CLIENT_ID = "mobile";

	private static GiftSvcApi giftSvc;

	public static synchronized GiftSvcApi getOrShowLogin(Activity activity) {
		if (giftSvc != null) {
			return giftSvc;
		} else {
            User loggedIn = ((PhotoGiftApplication)activity.getApplication()).getLoggedUser();
            if(loggedIn==null){
                activity.startActivity(new Intent(activity, LoginActivity.class));
                return null;
            }
            else
                return init(loggedIn.getUsername(),loggedIn.getPassword());
		}
	}

	private static synchronized GiftSvcApi init(String user,
                                                String pass) {

		giftSvc = new PhotoGiftRestBuilder()
				.setLoginEndpoint(PhotoGiftRestBuilder.SERVER_URL + PhotoGiftRestBuilder.TOKEN_PATH)
				.setUsername(user)
				.setPassword(pass)
				.setClientId(CLIENT_ID)
				.setClient(
						new ApacheClient(new EasyHttpClient()))
				.setEndpoint(PhotoGiftRestBuilder.SERVER_URL).setLogLevel(LogLevel.FULL).build()
				.create(GiftSvcApi.class);

		return giftSvc;
	}

    public static void reset() {
        giftSvc = null;
    }
}
