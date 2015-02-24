package client.potlach.com.potlachandroid.util;

import client.potlach.com.potlachandroid.exception.UnauthorizedException;
import retrofit.ErrorHandler;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by thiago on 11/9/14.
 */
public class RetrofitErrorHandler implements ErrorHandler {
    @Override
    public Throwable handleError(RetrofitError cause) {
        Response r = cause.getResponse();
        if (r != null && r.getStatus() == 401) {
            return new UnauthorizedException(cause);
        }
        return cause;
    }
}
