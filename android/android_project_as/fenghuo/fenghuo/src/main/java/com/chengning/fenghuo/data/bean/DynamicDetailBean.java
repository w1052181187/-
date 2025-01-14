package com.chengning.fenghuo.data.bean;

import java.io.Serializable;
import java.util.ArrayList;

public class DynamicDetailBean extends BaseArticlesBean<ArrayList<Image>> {

	/**
	 * 
	 */
	private static final long serialVersionUID = -4093826687957787108L;
	private ArrayList<SubscribeContentItemBean> content;
	private DynamicItemBean parent_list;
	private DynamicItemBean root_list;
	private ArrayList<QunListBean> qun_list;
	private int is_follow;
	private int is_subscribe;

	private int replys2;
	
	private ArrayList<DigBean> dig_list;
	private String redirecttitle;
	private String redirecturl;

	// 一些解析用的属性，暂时不做存储
	// private String type;
	private String toface;
	private String tonickname;

	public ArrayList<DigBean> getDig_list() {
		return dig_list;
	}

	public void setDig_list(ArrayList<DigBean> dig_list) {
		this.dig_list = dig_list;
	}

	public String getRedirecttitle() {
		return redirecttitle;
	}

	public void setRedirecttitle(String redirecttitle) {
		this.redirecttitle = redirecttitle;
	}

	public String getRedirecturl() {
		return redirecturl;
	}

	public void setRedirecturl(String redirecturl) {
		this.redirecturl = redirecturl;
	}

	public DynamicItemBean getParent_list() {
		return parent_list;
	}

	public void setParent_list(DynamicItemBean parent_list) {
		this.parent_list = parent_list;
	}

	public DynamicItemBean getRoot_list() {
		return root_list;
	}

	public void setRoot_list(DynamicItemBean root_list) {
		this.root_list = root_list;
	}

	public ArrayList<QunListBean> getQun_list() {
		return qun_list;
	}

	public void setQun_list(ArrayList<QunListBean> qun_list) {
		this.qun_list = qun_list;
	}

	public int getIs_follow() {
		return is_follow;
	}

	// public String getType() {
	// return type;
	// }

	// public void setType(String type) {
	// this.type = type;
	// }

	public void setIs_follow(int is_follow) {
		this.is_follow = is_follow;
	}

	public String getToface() {
		return toface;
	}

	public void setToface(String toface) {
		this.toface = toface;
	}

	public String getTonickname() {
		return tonickname;
	}

	public void setTonickname(String tonickname) {
		this.tonickname = tonickname;
	}

	public int getReplys2() {
		return replys2;
	}

	public void setReplys2(int replys2) {
		this.replys2 = replys2;
	}

	public int getIs_subscribe() {
		return is_subscribe;
	}

	public void setIs_subscribe(int is_subscribe) {
		this.is_subscribe = is_subscribe;
	}

	public ArrayList<SubscribeContentItemBean> getContent() {
		return content;
	}

	public void setContent(ArrayList<SubscribeContentItemBean> content) {
		this.content = content;
	}

	public class QunListBean implements Serializable {
		/**
			 * 
			 */
		private static final long serialVersionUID = -1552597376899308091L;
		String name;
		String qid;

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		public String getQid() {
			return qid;
		}

		public void setQid(String qid) {
			this.qid = qid;
		}

	}
}
