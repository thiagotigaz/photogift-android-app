/* 
 **
 ** Copyright 2014, Jules White
 **
 ** 
 */
package client.potlach.com.potlachandroid.oauth;

import com.devsmart.android.StringUtils;
import com.google.common.io.BaseEncoding;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

import org.apache.commons.io.IOUtils;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.Executor;

import retrofit.Endpoint;
import retrofit.ErrorHandler;
import retrofit.Profiler;
import retrofit.RequestInterceptor;
import retrofit.RestAdapter;
import retrofit.RestAdapter.Log;
import retrofit.RestAdapter.LogLevel;
import retrofit.client.Client;
import retrofit.client.Client.Provider;
import retrofit.client.Header;
import retrofit.client.OkClient;
import retrofit.client.Request;
import retrofit.client.Response;
import retrofit.converter.Converter;
import retrofit.converter.GsonConverter;
import retrofit.mime.FormUrlEncodedTypedOutput;

/**
 * A Builder class for a Retrofit REST Adapter. Extends the default implementation by providing logic to
 * handle an OAuth 2.0 password grant login flow. The RestAdapter that it produces uses an interceptor
 * to automatically obtain a bearer token from the authorization server and insert it into all client
 * requests.
 * 
 * You can use it like this:
 * 
  	private VideoSvcApi videoService = new SecuredRestBuilder()
			.setLoginEndpoint(TEST_URL + VideoSvcApi.TOKEN_PATH)
			.setUsername(USERNAME)
			.setPassword(PASSWORD)
			.setClientId(CLIENT_ID)
			.setClient(new ApacheClient(UnsafeHttpsClient.createUnsafeClient()))
			.setEndpoint(TEST_URL).setLogLevel(LogLevel.FULL).build()
			.create(VideoSvcApi.class);
 * 
 * @author Jules, Mitchell
 *
 */
public class PhotoGiftRestBuilder extends RestAdapter.Builder  {

    public static final String TOKEN_PATH = "/oauth/token";
    public static final String SERVER_URL = "https://192.168.0.8:8443";
    public static OAuthHandler hdlr;
    public static boolean loggedIn = false;

	private class OAuthHandler implements RequestInterceptor {

		private boolean loggedIn;
		private Client client;
		private String tokenIssuingEndpoint;
		private String username;
		private String password;
		private String clientId;
		private String clientSecret;
		private String accessToken;

        public String getAccessToken() {
            return accessToken;
        }

        public OAuthHandler(Client client, String tokenIssuingEndpoint, String username,
				String password, String clientId, String clientSecret) {
			super();
			this.client = client;
			this.tokenIssuingEndpoint = tokenIssuingEndpoint;
			this.username = username;
			this.password = password;
			this.clientId = clientId;
			this.clientSecret = clientSecret;
		}

        public boolean isLoggedIn() {
            return loggedIn;
        }

        public void setLoggedIn(boolean loggedIn) {
            this.loggedIn = loggedIn;
        }

        /**
		 * Every time a method on the client interface is invoked, this method is
		 * going to get called. The method checks if the client has previously obtained
		 * an OAuth 2.0 bearer token. If not, the method obtains the bearer token by
		 * sending a password grant request to the server. 
		 * 
		 * Once this method has obtained a bearer token, all future invocations will
		 * automatically insert the bearer token as the "Authorization" header in 
		 * outgoing HTTP requests.
		 * 
		 */
		@Override
		public void intercept(RequestFacade request) throws SecuredRestException{
			// If we're not logged in, login and store the authentication token.
			if (!loggedIn) {
				try {
					// This code below programmatically builds an OAuth 2.0 password
					// grant request and sends it to the server. 
					
					// Encode the username and password into the body of the request.
					FormUrlEncodedTypedOutput to = new FormUrlEncodedTypedOutput();
					to.addField("username", username);
					to.addField("password", password);
					
					// Add the client ID and client secret to the body of the request.
					to.addField("client_id", clientId);
					to.addField("client_secret", clientSecret);
					
					// Indicate that we're using the OAuth Password Grant Flow
					// by adding grant_type=password to the body
					to.addField("grant_type", "password");
					
					// The password grant requires BASIC authentication of the client.
					// In order to do BASIC authentication, we need to concatenate the
					// client_id and client_secret values together with a colon and then
					// Base64 encode them. The final value is added to the request as
					// the "Authorization" header and the value is set to "Basic " 
					// concatenated with the Base64 client_id:client_secret value described
					// above.
					String base64Auth = BaseEncoding.base64().encode(new String(clientId + ":" + clientSecret).getBytes());
					// Add the basic authorization header
					List<Header> headers = new ArrayList<Header>();
					headers.add(new Header("Authorization", "Basic " + base64Auth));

					// Create the actual password grant request using the data above
					Request req = new Request("POST", tokenIssuingEndpoint, headers, to);
					
					// Request the password grant.
					Response resp = client.execute(req);

                    android.util.Log.d("PhotoGift", IOUtils.toString(resp.getBody().in()));

					// Make sure the server responded with 200 OK
					if (resp.getStatus() < 200 || resp.getStatus() > 299) {
						// If not, we probably have bad credentials
						throw new SecuredRestException("Login failure: "
								+ resp.getStatus() + " - " + resp.getReason());
					} else {
						// Extract the string body from the response
				        String body = IOUtils.toString(resp.getBody().in());
						
						// Extract the access_token (bearer token) from the response so that we
				        // can add it to future requests.
						accessToken = new Gson().fromJson(body, JsonObject.class).get("access_token").getAsString();

						// Add the access_token to this request as the "Authorization"
						// header.
						request.addHeader("Authorization", "Bearer " + accessToken);	
						
						// Let future calls know we've already fetched the access token
						loggedIn = true;
                        PhotoGiftRestBuilder.this.loggedIn =true;
					}
				} catch (Exception e) {
					throw new SecuredRestException(e);
				}
			}
			else {
				// Add the access_token that we previously obtained to this request as 
				// the "Authorization" header.
				request.addHeader("Authorization", "Bearer " + accessToken );
			}

		}

	}

    private String username;
	private String password;
	private String loginUrl;
	private String clientId;
	private String clientSecret = "";
	private Client client;
    private Boolean secure = true;
	
	public PhotoGiftRestBuilder setLoginEndpoint(String endpoint){
		loginUrl = endpoint;
		return this;
	}

	@Override
	public PhotoGiftRestBuilder setEndpoint(String endpoint) {
		return (PhotoGiftRestBuilder) super.setEndpoint(endpoint);
	}

	@Override
	public PhotoGiftRestBuilder setEndpoint(Endpoint endpoint) {
		return (PhotoGiftRestBuilder) super.setEndpoint(endpoint);
	}

	@Override
	public PhotoGiftRestBuilder setClient(Client client) {
		this.client = client;
		return (PhotoGiftRestBuilder) super.setClient(client);
	}

	@Override
	public PhotoGiftRestBuilder setClient(Provider clientProvider) {
		client = clientProvider.get();
		return (PhotoGiftRestBuilder) super.setClient(clientProvider);
	}

	@Override
	public PhotoGiftRestBuilder setErrorHandler(ErrorHandler errorHandler) {
		return (PhotoGiftRestBuilder) super.setErrorHandler(errorHandler);
	}

	@Override
	public PhotoGiftRestBuilder setExecutors(Executor httpExecutor,
			Executor callbackExecutor) {

		return (PhotoGiftRestBuilder) super.setExecutors(httpExecutor,
				callbackExecutor);
	}

	@Override
	public PhotoGiftRestBuilder setRequestInterceptor(
			RequestInterceptor requestInterceptor) {

		return (PhotoGiftRestBuilder) super
				.setRequestInterceptor(requestInterceptor);
	}

	@Override
	public PhotoGiftRestBuilder setConverter(Converter converter) {

		return (PhotoGiftRestBuilder) super.setConverter(converter);
	}

	@Override
	public PhotoGiftRestBuilder setProfiler(@SuppressWarnings("rawtypes") Profiler profiler) {

		return (PhotoGiftRestBuilder) super.setProfiler(profiler);
	}

	@Override
	public PhotoGiftRestBuilder setLog(Log log) {

		return (PhotoGiftRestBuilder) super.setLog(log);
	}

	@Override
	public PhotoGiftRestBuilder setLogLevel(LogLevel logLevel) {

		return (PhotoGiftRestBuilder) super.setLogLevel(logLevel);
	}

	public PhotoGiftRestBuilder setUsername(String username) {
		this.username = username;
		return this;
	}

	public PhotoGiftRestBuilder setPassword(String password) {
		this.password = password;
		return this;
	}

    public PhotoGiftRestBuilder setSecure(Boolean secure) {
        this.secure = secure;
        return this;
    }

    public PhotoGiftRestBuilder setClientId(String clientId) {
		this.clientId = clientId;
		return this;
	}
	
	public PhotoGiftRestBuilder setClientSecret(String clientSecret) {
		this.clientSecret = clientSecret;
		return this;
	}
	
	@Override
	public RestAdapter build() throws SecuredRestException{
		if(secure){
            if (StringUtils.isEmptyString(username) || StringUtils.isEmptyString(password)) {
                throw new SecuredRestException(
                        "You must specify both a username and password for a "
                                + "SecuredRestBuilder before calling the build() method.");
            }



            if(hdlr==null || hdlr.loggedIn==false){
                hdlr = new OAuthHandler(client, loginUrl, username, password, clientId, clientSecret);
                android.util.Log.d("PhotoGift", "Created new OAuthHandler");
            }

            setRequestInterceptor(hdlr);
        }

        if (client == null) {
            client = new OkClient();
        }

        /*Gson gson = new GsonBuilder()
                .setDateFormat("yyyy-MM-dd'T'HH:mm:ss")
                .create();*/

        /*// Creates the json object which will manage the information received
        GsonBuilder builder = new GsonBuilder();

        // Register an adapter to manage the date types as long values
        builder.registerTypeAdapter(Date.class, new JsonDeserializer<Date>() {
            public Date deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
                return new Date(json.getAsJsonPrimitive().getAsLong());
            }
        });

        Gson gson = builder.create();*/


        JsonSerializer<Date> ser = new JsonSerializer<Date>() {
            @Override
            public JsonElement serialize(Date src, Type typeOfSrc, JsonSerializationContext
                    context) {
                return src == null ? null : new JsonPrimitive(src.getTime());
            }
        };

        JsonDeserializer<Date> deser = new JsonDeserializer<Date>() {
            @Override
            public Date deserialize(JsonElement json, Type typeOfT,
                                    JsonDeserializationContext context) throws JsonParseException {
                return json == null ? null : new Date(json.getAsLong());
            }
        };

        Gson gson = new GsonBuilder()
                .registerTypeAdapter(Date.class, ser)
                .registerTypeAdapter(Date.class, deser).create();
        setConverter(new GsonConverter(gson));
		return super.build();
	}

    public static void logout(){
        hdlr=null;
    }

    public static OAuthHandler getOAuthHandler() {
        return hdlr;
    }

    public static String getAccessToken(){
        return hdlr.getAccessToken();
    }

    public static boolean isLoggedIn() {
        return loggedIn;
    }

    public static void setLoggedIn(boolean loggedIn) {
        PhotoGiftRestBuilder.loggedIn = loggedIn;
    }
}