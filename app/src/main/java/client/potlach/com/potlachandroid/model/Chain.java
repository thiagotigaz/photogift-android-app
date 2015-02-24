package client.potlach.com.potlachandroid.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Chain extends AbstractEntity{

	private Long id;
	private String name;
	private List<Gift> gifts;
	private Gift featuredGift;
	private Integer giftsCount=0;
	private Long followersCount=0l;

    public Chain() {
    }

    public Chain(String name) {
        this.name = name;
    }

    public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public List<Gift> getGifts() {
		return gifts;
	}

	public void setGifts(List<Gift> gifts) {
		this.gifts = gifts;
	}

	public Gift getFeaturedGift() {
		return featuredGift;
	}

	public void setFeaturedGift(Gift featuredGift) {
		this.featuredGift = featuredGift;
	}

	public Integer getGiftsCount() {
		return giftsCount;
	}

	public void setGiftsCount(Integer giftsCount) {
		this.giftsCount = giftsCount;
	}

	public Long getFollowersCount() {
		return followersCount;
	}

	public void setFollowersCount(Long followersCount) {
		this.followersCount = followersCount;
	}
	
}
