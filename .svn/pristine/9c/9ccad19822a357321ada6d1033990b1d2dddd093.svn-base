/* 
 **
 ** Copyright 2014, Jules White
 **
 ** 
 */
package client.potlach.com.potlachandroid.service;


import client.potlach.com.potlachandroid.activity.LoginActivity;
import client.potlach.com.potlachandroid.exception.UnauthorizedException;
import client.potlach.com.potlachandroid.oauth.PhotoGiftRestBuilder;
import client.potlach.com.potlachandroid.unsafe.EasyHttpClient;
import retrofit.ErrorHandler;
import retrofit.RestAdapter.LogLevel;
import retrofit.RetrofitError;
import retrofit.client.ApacheClient;
import android.content.Context;
import android.content.Intent;

public class UserSvc {

	public static final String CLIENT_ID = "mobile";

	private static UserSvcApi userSvc;

	public static synchronized UserSvcApi getOrShowLogin(Context ctx) {
		if (userSvc != null) {
			return userSvc;
		} else {
			Intent i = new Intent(ctx, LoginActivity.class);
			ctx.startActivity(i);
			return null;
		}
	}

	public static synchronized UserSvcApi init(String user,
			String pass){
		userSvc = new PhotoGiftRestBuilder()
				.setLoginEndpoint(PhotoGiftRestBuilder.SERVER_URL + PhotoGiftRestBuilder.TOKEN_PATH)
				.setUsername(user)
				.setPassword(pass)
				.setClientId(CLIENT_ID)
                .setErrorHandler(new ErrorHandler() {
                    @Override
                    public Throwable handleError(RetrofitError cause) {
                        return new UnauthorizedException(cause);
                    }
                })
				.setClient(
                        new ApacheClient(new EasyHttpClient()))
				.setEndpoint(PhotoGiftRestBuilder.SERVER_URL).setLogLevel(LogLevel.FULL).build()
				.create(UserSvcApi.class);
		return userSvc;
	}

    public static synchronized UserSvcApi initNoLogin(){
        return new PhotoGiftRestBuilder()
                .setSecure(false)
                .setClient(new ApacheClient(new EasyHttpClient()))
                .setEndpoint(PhotoGiftRestBuilder.SERVER_URL).setLogLevel(LogLevel.FULL).build()
                .create(UserSvcApi.class);
    }

    public static void reset() {
        userSvc = null;
    }
}
