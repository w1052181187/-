package com.chengning.fenghuo.data.bean;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * @description 圈子Bean
 * @author wangyungang
 * @date 2015.7.13 9:10
 */
public class CircleBean extends BaseChannelItemBean implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3475873041548138371L;
	public static String TOPIC = "2";
	public static String UNSELECT = "3";

	/**
	 * 圈子id
	 */
	private String qid;

	/**
	 * 圈子名称
	 */
	private String name;

	/**
	 * 圈子头像
	 */
	private String icon;

	/**
	 * 圈子成员数量
	 */
	private String member_num = "0";

	/**
	 * 圈子讨论数量
	 */
	private String thread_num = "0";

	/**
	 * 圈子简介
	 */
	private String desc;

	/**
	 * 圈子类型
	 */
	private int gview_perm;

	/**
	 * 圈子加入类型
	 */
	private int join_type;

	/**
	 * 圈子创建时间
	 */
	private String dateline;

	/**
	 * 是否圈子成员
	 */
	private int is_qun_member;

	/**
	 * 圈子是否为圈主
	 */
	private int is_founder;
	/**
	 * 圈主头像
	 */
	private String face;
	/**
	 * 圈主昵称
	 */
	private String nickname;

	/**
	 * 圈主ID
	 */
	private String founderuid;

	/**
	 * 是否锁定
	 */
	private int is_lock;

	/**
	 * 讨论列表
	 */
	private ArrayList<DynamicItemBean> topic_list;

	/**
	 * 动态列表地址
	 */
	private String local_url;
	/**
	 * 动态列表地址
	 */
	private String url;

	/**
	 * 短名字
	 */
	private String short_name;

	private int pos;

	/**
	 * 圈子通知id
	 */
	private String id;

	/**
	 * 用户id
	 */
	private String uid;

	/**
	 * 留言
	 */
	private String message;

	/**
	 *  1 申请圈子 2不需审核 加入圈子 3踢出圈子 4 主动退圈 5拒绝入圈 6 审核通过后加入圈子
	 */
	private int action;
	
	/**
	 *  是否忽略 1忽略 0正常
	 */
	private int is_slip;

	/**
	 * 时间
	 */
	private String apply_time;

	/**
	 * 简介 action为3 圈子简介 其他是用户简介
	 */
	private String aboutme;
	
	/**
	 * 圈子数量
	 */
	private String list_size;
	
	private int local_my_index;

	private int local_other_index;
	
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getUid() {
		return uid;
	}

	public void setUid(String uid) {
		this.uid = uid;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public int getAction() {
		return action;
	}

	public void setAction(int action) {
		this.action = action;
	}

	public String getApply_time() {
		return apply_time;
	}

	public void setApply_time(String apply_time) {
		this.apply_time = apply_time;
	}

	public String getAboutme() {
		return aboutme;
	}

	public void setAboutme(String aboutme) {
		this.aboutme = aboutme;
	}

	public String getShort_name() {
		return short_name;
	}

	public void setShort_name(String short_name) {
		this.short_name = short_name;
	}

	public int getPos() {
		return pos;
	}

	public void setPos(int pos) {
		this.pos = pos;
	}

	/**
	 * 在数据保存用来区分是否首页
	 */
	private String local_type;
	private int local_page;

	private String topic_list_json;

	public String getTopic_list_json() {
		return topic_list_json;
	}

	public void setTopic_list_json(String topic_list_json) {
		this.topic_list_json = topic_list_json;
	}

	public String getLocal_type() {
		return local_type;
	}

	public void setLocal_type(String local_type) {
		this.local_type = local_type;
	}

	public String getQid() {
		return qid;
	}

	public void setQid(String qid) {
		this.qid = qid;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getIcon() {
		return icon;
	}

	public void setIcon(String icon) {
		this.icon = icon;
	}

	public String getMember_num() {
		return member_num;
	}

	public void setMember_num(String member_num) {
		this.member_num = member_num;
	}

	public String getThread_num() {
		return thread_num;
	}

	public void setThread_num(String thread_num) {
		this.thread_num = thread_num;
	}

	public String getDesc() {
		return desc;
	}

	public void setDesc(String desc) {
		this.desc = desc;
	}

	public int getGview_perm() {
		return gview_perm;
	}

	public void setGview_perm(int gview_perm) {
		this.gview_perm = gview_perm;
	}

	public int getJoin_type() {
		return join_type;
	}

	public void setJoin_type(int join_type) {
		this.join_type = join_type;
	}

	public String getDateline() {
		return dateline;
	}

	public void setDateline(String dateline) {
		this.dateline = dateline;
	}

	public int getIs_qun_member() {
		return is_qun_member;
	}

	public void setIs_qun_member(int is_qun_member) {
		this.is_qun_member = is_qun_member;
	}

	public int getIs_founder() {
		return is_founder;
	}

	public void setIs_founder(int is_founder) {
		this.is_founder = is_founder;
	}

	public String getFace() {
		return face;
	}

	public void setFace(String face) {
		this.face = face;
	}

	public String getNickname() {
		return nickname;
	}

	public void setNickname(String nickname) {
		this.nickname = nickname;
	}

	public String getFounderuid() {
		return founderuid;
	}

	public void setFounderuid(String founderuid) {
		this.founderuid = founderuid;
	}

	public int getIs_lock() {
		return is_lock;
	}

	public void setIs_lock(int is_lock) {
		this.is_lock = is_lock;
	}

	public String getLocal_url() {
		return local_url;
	}

	public void setLocal_url(String local_url) {
		this.local_url = local_url;
	}

	public ArrayList<DynamicItemBean> getTopic_list() {
		return topic_list;
	}

	public void setTopic_list(ArrayList<DynamicItemBean> topic_list) {
		this.topic_list = topic_list;
	}

	public int getIs_slip() {
		return is_slip;
	}

	public void setIs_slip(int is_slip) {
		this.is_slip = is_slip;
	}

	public String getList_size() {
		return list_size;
	}

	public void setList_size(String list_size) {
		this.list_size = list_size;
	}

	public int getLocal_page() {
		return local_page;
	}

	public void setLocal_page(int local_page) {
		this.local_page = local_page;
	}
	
	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public int getLocal_my_index() {
		return local_my_index;
	}

	public void setLocal_my_index(int local_my_index) {
		this.local_my_index = local_my_index;
	}

	public int getLocal_other_index() {
		return local_other_index;
	}

	public void setLocal_other_index(int local_other_index) {
		this.local_other_index = local_other_index;
	}
}
