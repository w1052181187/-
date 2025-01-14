package com.chengning.fenghuo.data.bean;

import java.io.Serializable;

public class ConversationBean implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8869547790332127528L;
	
	private String face;
	private String uid;
	private String nickname;
	
	public String getFace() {
		return face;
	}
	public void setFace(String face) {
		this.face = face;
	}
	public String getUid() {
		return uid;
	}
	public void setUid(String uid) {
		this.uid = uid;
	}
	public String getNickname() {
		return nickname;
	}
	public void setNickname(String nickname) {
		this.nickname = nickname;
	}
	
	public static ConversationBean convertToConBean(UserInfoBean bean) {
		ConversationBean conversationBean = new ConversationBean();
		conversationBean.setFace(bean.getFace());
		conversationBean.setUid(bean.getUid());
		conversationBean.setNickname(bean.getNickname());
		return conversationBean;
	}
	

}
