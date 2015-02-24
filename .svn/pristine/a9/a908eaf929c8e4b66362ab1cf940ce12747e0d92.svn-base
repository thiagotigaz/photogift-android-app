package client.potlach.com.potlachandroid.service;

import java.util.Collection;
import java.util.List;

import client.potlach.com.potlachandroid.model.Chain;
import retrofit.http.Body;
import retrofit.http.GET;
import retrofit.http.POST;
import retrofit.http.Path;
import retrofit.http.Query;

public interface ChainSvcApi {

	public static final String NAME_PARAMETER = "name";

    public static final String ID_PARAMETER = "id";

    public static final String PAGENUMBER_PARAMETER = "pagenumber";

	public static final String CHAIN_SVC_PATH = "/chain";

	public static final String CHAIN_ID_SEARCH_PATH = CHAIN_SVC_PATH + "/{id}";

    public static final String CHAIN_PAGE_SEARCH_PATH = CHAIN_SVC_PATH + "/page/{pagenumber}";

	public static final String CHAIN_NAME_SEARCH_PATH = CHAIN_SVC_PATH + "/search/findByNameLikeIgnoreCase";

    public static final String CHAIN_COUNTALL_PATH = CHAIN_SVC_PATH + "/count";

	@GET(CHAIN_SVC_PATH)
	public List<Chain> getChainList();
	
	@POST(CHAIN_SVC_PATH)
	public Chain addGiftchain(@Body Chain g);
	
	@GET(CHAIN_NAME_SEARCH_PATH)
	public Collection<Chain> findAllByName(@Query(NAME_PARAMETER) String name);

    @GET(CHAIN_NAME_SEARCH_PATH)
	public Chain findByName(@Query(NAME_PARAMETER) String name);

    @GET(CHAIN_ID_SEARCH_PATH)
    public Chain findById(@Path(ID_PARAMETER) Long id);

    @GET(CHAIN_PAGE_SEARCH_PATH)
    public List<Chain> findByPage(@Path(PAGENUMBER_PARAMETER)Integer page);

    @GET(CHAIN_COUNTALL_PATH)
    public Long countAll();
}
