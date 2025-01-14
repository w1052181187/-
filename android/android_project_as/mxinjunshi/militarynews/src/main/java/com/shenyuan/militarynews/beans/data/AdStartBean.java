package com.shenyuan.militarynews.beans.data;

import java.io.Serializable;

public class AdStartBean implements Serializable {

	private static final long serialVersionUID = 6545417384783861738L;

	private String image;
	private String url;
	private String video_url;
	private String mall_url;
	private String order_url;
	private String mall_logout_url;
	// 引导
	private String boot_image;
	private String download_url;
	private int is_boot;
	private String appid;
	private String app_marking;
	private String dateformat;

	// 是否轮询查找新文章，来显示桌面红点
	private int is_polling;

	// 红点轮询时间
	private int check_news_interval;
	
	// 是否登录
	private int is_login;
	
	//推送主通道
	private int tunnel;

	public String getImage() {
		return image;
	}

	public void setImage(String image) {
		this.image = image;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getMall_url() {
		return mall_url;
	}

	public void setMall_url(String mall_url) {
		this.mall_url = mall_url;
	}

	public String getOrder_url() {
		return order_url;
	}

	public void setOrder_url(String order_url) {
		this.order_url = order_url;
	}

	public String getMall_logout_url() {
		return mall_logout_url;
	}

	public void setMall_logout_url(String mall_logout_url) {
		this.mall_logout_url = mall_logout_url;
	}

	public String getBoot_image() {
		return boot_image;
	}

	public void setBoot_image(String boot_image) {
		this.boot_image = boot_image;
	}

	public String getDownload_url() {
		return download_url;
	}

	public void setDownload_url(String download_url) {
		this.download_url = download_url;
	}

	public int getIs_boot() {
		return is_boot;
	}

	public void setIs_boot(int is_boot) {
		this.is_boot = is_boot;
	}

	public String getAppid() {
		return appid;
	}

	public void setAppid(String appid) {
		this.appid = appid;
	}

	public String getApp_marking() {
		return app_marking;
	}

	public void setApp_marking(String app_marking) {
		this.app_marking = app_marking;
	}

	public String getDateformat() {
		return dateformat;
	}

	public void setDateformat(String dateformat) {
		this.dateformat = dateformat;
	}

	public int getIs_polling() {
		return is_polling;
	}

	public void setIs_polling(int is_polling) {
		this.is_polling = is_polling;
	}

	public int getCheck_news_interval() {
		return check_news_interval;
	}

	public void setCheck_news_interval(int check_news_interval) {
		this.check_news_interval = check_news_interval;
	}

	public int getIs_login() {
		return is_login;
	}

	public void setIs_login(int is_login) {
		this.is_login = is_login;
	}

	public int getTunnel() {
		return tunnel;
	}

	public void setTunnel(int tunnel) {
		this.tunnel = tunnel;
	}

	public String getVideo_url() {
		return video_url;
	}

	public void setVideo_url(String video_url) {
		this.video_url = video_url;
	}
}
