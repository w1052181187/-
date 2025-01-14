package com.shenyuan.militarynews.beans.data;

import java.io.Serializable;
import java.util.ArrayList;

public class ArticlesBean implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -7588984579360315782L;
	private String tid;
	String title;
	String link;
	String image;
	String description;
	ArrayList<String> content;
	ArrayList<String> pics;
	String pubDate;
	String category;
	String author;
	int click;
	CommentListBean comments;

	private int goodpost;
	private int badpost;
	
	private String ad_name;
	private String ad_url;

	String video_play;
	String video_html;
	String video_photo;
	String channel;
	String is_favor;

	private MChannelItemBean add_code;
	private MChannelItemBean add_code_2;
	private MChannelItemBean add_code_big;
	private ArrayList<MChannelItemBean> relations;
	private ArrayList<MChannelItemBean> top_relations;

	private int is_dig;

	public String getTid() {
		return tid;
	}

	public void setTid(String tid) {
		this.tid = tid;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getLink() {
		return link;
	}

	public void setLink(String link) {
		this.link = link;
	}

	public String getImage() {
		return image;
	}

	public void setImage(String image) {
		this.image = image;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public ArrayList<String> getContent() {
		return content;
	}

	public void setContent(ArrayList<String> content) {
		this.content = content;
	}

	public ArrayList<String> getPics() {
		return pics;
	}

	public void setPics(ArrayList<String> pics) {
		this.pics = pics;
	}

	public String getPubDate() {
		return pubDate;
	}

	public void setPubDate(String pubDate) {
		this.pubDate = pubDate;
	}

	public String getCategory() {
		return category;
	}

	public void setCategory(String category) {
		this.category = category;
	}

	public String getAuthor() {
		return author;
	}

	public void setAuthor(String author) {
		this.author = author;
	}

	public int getClick() {
		return click;
	}

	public void setClick(int click) {
		this.click = click;
	}

	public CommentListBean getComments() {
		return comments;
	}

	public void setComments(CommentListBean comments) {
		this.comments = comments;
	}

	public int getGoodpost() {
		return goodpost;
	}

	public void setGoodpost(int goodpost) {
		this.goodpost = goodpost;
	}

	public int getBadpost() {
		return badpost;
	}

	public void setBadpost(int badpost) {
		this.badpost = badpost;
	}

	public String getVideo_play() {
		return video_play;
	}

	public void setVideo_play(String video_play) {
		this.video_play = video_play;
	}

	public String getVideo_html() {
		return video_html;
	}

	public void setVideo_html(String video_html) {
		this.video_html = video_html;
	}

	public String getVideo_photo() {
		return video_photo;
	}

	public void setVideo_photo(String video_photo) {
		this.video_photo = video_photo;
	}

	public String getChannel() {
		return channel;
	}

	public void setChannel(String channel) {
		this.channel = channel;
	}

	public String getIs_favor() {
		return is_favor;
	}

	public void setIs_favor(String is_favor) {
		this.is_favor = is_favor;
	}

	public MChannelItemBean getAdd_code() {
		return add_code;
	}

	public void setAdd_code(MChannelItemBean add_code) {
		this.add_code = add_code;
	}

	public ArrayList<MChannelItemBean> getRelations() {
		return relations;
	}

	public void setRelations(ArrayList<MChannelItemBean> relations) {
		this.relations = relations;
	}

	public MChannelItemBean getAdd_code_big() {
		return add_code_big;
	}

	public void setAdd_code_big(MChannelItemBean add_code_big) {
		this.add_code_big = add_code_big;
	}

	public MChannelItemBean getAdd_code_2() {
		return add_code_2;
	}

	public void setAdd_code_2(MChannelItemBean add_code_2) {
		this.add_code_2 = add_code_2;
	}
	
	public String getAd_name() {
		return ad_name;
	}

	public void setAd_name(String ad_name) {
		this.ad_name = ad_name;
	}

	public String getAd_url() {
		return ad_url;
	}

	public void setAd_url(String ad_url) {
		this.ad_url = ad_url;
	}

	public int getIs_dig() {
		return is_dig;
	}

	public void setIs_dig(int is_dig) {
		this.is_dig = is_dig;
	}

	public ArrayList<MChannelItemBean> getTop_relations() {
		return top_relations;
	}

	public void setTop_relations(ArrayList<MChannelItemBean> top_relations) {
		this.top_relations = top_relations;
	}
}
