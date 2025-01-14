package com.chengning.fenghuo.data.bean;

import java.io.Serializable;

public class MilitaryGradeBean implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -5146767799836791194L;

	/**
	 * 军衔
	 */
	private String name;
	
	/**
	 * 等级
	 */
	private String rank;
	
	/**
	 * 积分
	 */
	private String creditshigher;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getRank() {
		return rank;
	}

	public void setRank(String rank) {
		this.rank = rank;
	}

	public String getCreditshigher() {
		return creditshigher;
	}

	public void setCreditshigher(String creditshigher) {
		this.creditshigher = creditshigher;
	}

	
}
