package client.potlach.com.potlachandroid.service;

import java.util.Collection;
import java.util.List;

import client.potlach.com.potlachandroid.model.User;
import client.potlach.com.potlachandroid.model.UserCredentialsStatus.UserCredentialsState;
import retrofit.http.Body;
import retrofit.http.Field;
import retrofit.http.FormUrlEncoded;
import retrofit.http.GET;
import retrofit.http.POST;
import retrofit.http.Path;
import retrofit.http.Query;


public interface UserSvcApi {

    public static final String INSECURE_PATH = "/insecure";

    public static final String ID_PARAMETER = "id";

    public static final String USER_ID_PARAMETER = "userid";

    public static final String GIFT_PARAMETER = "giftid";

    public static final String PASSWORD_PARAMETER = "password";

    public static final String USERNAME_PARAMETER = "username";

    public static final String TITLE_PARAMETER = "title";

    public static final String EMAIL_PARAMETER = "email";

    public static final String DURATION_PARAMETER = "duration";

    public static final String PAGENUMBER_PARAMETER = "pagenumber";

    public static final String TOKEN_PATH = "/oauth/token";

    public static final String USER_SVC_PATH = "/user";

    public static final String USER_ADD_NEW_PATH = INSECURE_PATH + USER_SVC_PATH;

    public static final String USER_NAME_SEARCH_PATH = USER_SVC_PATH + "/search/findByName";

    public static final String USER_CHECK_CREDENTIALS_PATH = INSECURE_PATH + USER_SVC_PATH + "/check/{username}/{email}";

    public static final String USER_USERNAME_AND_PASSWORD_SEARCH_PATH = USER_SVC_PATH + "/login";

    public static final String USER_NUMBER_OF_TOUCHES_AND_PAGE_SEARCH_PATH = USER_SVC_PATH + "/topgivers/page/{pagenumber}";

    public static final String USER_COUNTALL_PATH = USER_SVC_PATH + "/countall";

    public static final String USER_LIKETOUCH_GIFT_PATH = USER_SVC_PATH + "/like/gift/{giftid}";

    @POST(USER_ADD_NEW_PATH)
    public User addUser(@Body User u);

    @GET(USER_SVC_PATH)
    public List<User> getUserList();

    @GET(USER_COUNTALL_PATH)
    public Long countAll();

    @GET(USER_NAME_SEARCH_PATH)
    public List<User> findByName(@Query(TITLE_PARAMETER) String name);

    @GET(USER_LIKETOUCH_GIFT_PATH)
    public List<User> findByLikeTouchGiftId(@Path(GIFT_PARAMETER) Long giftId);

    @GET(USER_NUMBER_OF_TOUCHES_AND_PAGE_SEARCH_PATH)
    public List<User>  findByNumberOfTouchesDescAndPage(@Path(PAGENUMBER_PARAMETER)Integer page);

    @GET(USER_CHECK_CREDENTIALS_PATH)
    public UserCredentialsState checkExistingCredentials(@Path(USERNAME_PARAMETER) String username, @Path(EMAIL_PARAMETER) String email);

    @FormUrlEncoded
    @POST(USER_USERNAME_AND_PASSWORD_SEARCH_PATH)
    public User login(@Field(USERNAME_PARAMETER) String username, @Field(PASSWORD_PARAMETER) String email);

}
