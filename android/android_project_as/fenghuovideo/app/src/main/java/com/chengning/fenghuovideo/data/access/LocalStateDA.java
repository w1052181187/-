package com.chengning.fenghuovideo.data.access;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.text.TextUtils;

import com.chengning.common.base.BaseListDA;
import com.chengning.common.util.SerializeUtil;
import com.chengning.fenghuovideo.Consts;
import com.chengning.fenghuovideo.data.bean.ChannelItemBean;
import com.chengning.fenghuovideo.data.bean.LocalStateBean;
import com.chengning.fenghuovideo.db.provider.dbContent.table_local_state;
import com.chengning.fenghuovideo.util.SPHelper;

import java.util.ArrayList;
import java.util.List;

public class LocalStateDA extends BaseListDA<LocalStateBean> {
	
	public static final String PREFIX_CHANNEL_ITEM = "channel_item_";
	
	public static final String READ_STATE_READ = "1";
	
	public static final int STATE_TRUE = 1;
	public static final int STATE_FALSE = 0;
	
	public static final int COLLECT_ARTICLE = 1;

	private static LocalStateDA mInst;

	public static LocalStateDA getInst(Context con) {
		if (mInst == null) {
            synchronized (LocalStateDA.class) {
                if (mInst == null) {
                    mInst = new LocalStateDA(con);
                }
            }
        }
		return mInst;
	}

	protected LocalStateDA(Context context) {
		super(context, table_local_state.CONTENT_URI);
	}

	@Override
	public String buildDeleteWhere(LocalStateBean t) {
		return "id = ?";
	}

	@Override
	public String[] buildDeleteSelectionArgs(LocalStateBean t) {
		return new String[] { String.valueOf(t.getId())};
	}

	@Override
	public ContentValues buildInsertValues(LocalStateBean t) {
		ContentValues cv = new ContentValues();
		cv.put("id", t.getId());
		cv.put("lasttime", t.getLasttime());
		cv.put("read_state", t.getRead_state());
		cv.put("favorite_state", t.getFavorite_state());
		cv.put("like_state", t.getLike_state());
		cv.put("data_item", t.getData_item());
		cv.put("data_item_article", t.getData_item_article());
		cv.put("collect_time", t.getCollect_time());
		cv.put("collect_type", t.getCollect_type());
		return cv;
	}

	@Override
	public LocalStateBean buildQueryValues(Cursor cursor) {
		LocalStateBean bean = new LocalStateBean();
		bean.setId(cursor.getString(cursor.getColumnIndex("id")));
		bean.setLasttime(cursor.getString(cursor.getColumnIndex("lasttime")));
		bean.setRead_state(cursor.getString(cursor.getColumnIndex("read_state")));
		bean.setFavorite_state(cursor.getInt(cursor.getColumnIndex("favorite_state")));
		bean.setLike_state(cursor.getInt(cursor.getColumnIndex("like_state")));
		bean.setData_item(cursor.getString(cursor.getColumnIndex("data_item")));
		bean.setData_item_article(cursor.getString(cursor.getColumnIndex("data_item_article")));
		bean.setCollect_time(cursor.getString(cursor.getColumnIndex("collect_time")));
		bean.setCollect_type(cursor.getInt(cursor.getColumnIndex("collect_type")));
		return bean;
	}
	
	private String getLasttimeString(){
		return String.valueOf(System.currentTimeMillis());
	}
	
	public static LocalStateBean generateLocalStateBean(String prefix, String id){
		LocalStateBean bean = new LocalStateBean();
		bean.setId(prefix + id);
		return bean;
	}
	
	public LocalStateBean queryLocalState(String prefix, String id){
		List<LocalStateBean> list = queryTarget("id = ?", new String[] { String.valueOf(prefix + id)}, null);
		if(list != null && list.size() > 0){
			return list.get(0);
		}else{
			return null;
		}
	}
	
	public LocalStateBean queryLocalStateNotNull(String prefix, String id){
		LocalStateBean bean = queryLocalState(prefix, id);
		if(bean == null){
			bean = generateLocalStateBean(prefix, id);
		}
		return bean;
	}
	
	// read
	public boolean isRead(String prefix, String id){
		return READ_STATE_READ.equals(queryLocalStateNotNull(prefix, id).getRead_state());
	}
	
	public void setReadStateRead(String prefix, String id){
		LocalStateBean bean = queryLocalStateNotNull(prefix, id);
		bean.setRead_state(READ_STATE_READ);
		bean.setLasttime(getLasttimeString());
		insertOne(bean);
	}
	
	// like
	public boolean isLike(String prefix, String id){
		return STATE_TRUE == queryLocalStateNotNull(prefix, id).getLike_state();
	}
	
	public void setLikeStateTrue(String prefix, String id){
		LocalStateBean bean = queryLocalStateNotNull(prefix, id);
		bean.setLike_state(STATE_TRUE);
		bean.setLasttime(getLasttimeString());
		insertOne(bean);
	}
	
	// favorite
	public boolean isFavorite(String prefix, String id){
		return STATE_TRUE == queryLocalStateNotNull(prefix, id).getFavorite_state();
	}
	
	public void setFavoriteState(boolean state,String prefix, String id){
		LocalStateBean bean = queryLocalStateNotNull(prefix, id);
		bean.setFavorite_state(state ? STATE_TRUE : STATE_FALSE);
		bean.setLasttime(getLasttimeString());
		insertOne(bean);
	}


	
	/**
	 * 保存收藏文章
	 * @param prefix
	 * @param id
	 * @param data
	 */
	public void saveFavArticle(String prefix, String id, String data){
		LocalStateBean bean = queryLocalStateNotNull(prefix, id);
		bean.setData_item(data);
		bean.setFavorite_state(STATE_TRUE);
		bean.setLasttime(getLasttimeString());
		bean.setCollect_time(getLasttimeString());
		bean.setCollect_type(1);
		insertOne(bean);
	}

	/**
	 * 删除收藏文章
	 * @param prefix
	 * @param id
	 */
	public void deleteFavArticle(String prefix, String id) {
		StringBuilder ids = new StringBuilder();
		ids.append(prefix).append(id);
		ContentValues values = new ContentValues();
		values.put("favorite_state", STATE_FALSE);
		updateTarget(values, "id = ?", new String[] {ids.toString()});
	}

	public void deleteFavArticleByBatch(String prefix, ArrayList list) {
//		StringBuilder ids = new StringBuilder();
		ContentValues values = new ContentValues();
		values.put("favorite_state", STATE_FALSE);
		StringBuilder where = new StringBuilder();;
		String[] collect = new String[list.size()];
//		for(int i = 0 ; i < list.size() ; i++){
//			collect[i] = prefix + list.get(i);
//		}
//		ids.append(prefix);
		where.append("id = ?");
		if(list.size() == 1){
			collect[0] = prefix + list.get(0);
			updateTarget(values, where.toString(), collect);
		}else{
			for(int i = 0 ; i < list.size() ; i++){
				if(i>0){
					where.append(" or id= ?");
				}
				collect[i] = prefix + list.get(i);
			}
			updateTarget(values, where.toString(), collect);
		}

	}

	/**
	 * 分页获取收藏的文章
	 * @param offset
	 * @return
	 */
	public ArrayList<ChannelItemBean> getFavArticles(int offset) {
		String limit = table_local_state.Columns.COLLECT_TIME + " desc limit 20 offset " + offset;
		List<LocalStateBean> list = queryTarget("favorite_state = ? and collect_type = ?", new String[] { String.valueOf(STATE_TRUE),String.valueOf(COLLECT_ARTICLE)}, limit);
//		List<LocalStateBean> list = queryTarget("favorite_state = ? and collect_type = ?", new String[] { String.valueOf(STATE_TRUE),String.valueOf(COLLECT_ARTICLE)}, "collect_time desc");
		ArrayList<ChannelItemBean> beans = new ArrayList<ChannelItemBean>();
		String data = null;
		boolean isUpdate = SPHelper.getInst().getInt(SPHelper.KEY_FAV_VERSION) == 0;
		for (LocalStateBean bean:list) {
			data = bean.getData_item();
			if(!TextUtils.isEmpty(data)){
				try {
					ChannelItemBean itemBean = SerializeUtil.deSerialize(data, ChannelItemBean.class);
					if(isUpdate){
						if(itemBean.getShow_type() == Consts.SHOW_TYPE_ONE_SMALL_VIDEO || itemBean.getShow_type() == Consts.SHOW_TYPE_ONE_BIG_VIDEO){
							itemBean.setContent_type(Consts.CONTENT_TYPE_VIDEO);
						}
					}
					beans.add(itemBean);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
		if(isUpdate){
			getAndModifyFavArticle();
		}
		return beans;
	}

	public void getAndModifyFavArticle(){
		String limit = table_local_state.Columns.COLLECT_TIME + " desc";
		List<LocalStateBean> list = queryTarget("favorite_state = ? and collect_type = ?", new String[] { String.valueOf(STATE_TRUE),String.valueOf(COLLECT_ARTICLE)}, limit);
		String data = null;
		boolean isUpdate = SPHelper.getInst().getInt(SPHelper.KEY_FAV_VERSION) == 0;
		for (LocalStateBean bean:list) {
			data = bean.getData_item();
			if(!TextUtils.isEmpty(data)){
				try {
					ChannelItemBean itemBean = SerializeUtil.deSerialize(data, ChannelItemBean.class);
					if(isUpdate){
						if( itemBean.getShow_type() == Consts.SHOW_TYPE_ONE_SMALL_VIDEO || itemBean.getShow_type() == Consts.SHOW_TYPE_ONE_BIG_VIDEO ){
							itemBean.setContent_type(Consts.CONTENT_TYPE_VIDEO);
							saveFavArticle(LocalStateDA.PREFIX_CHANNEL_ITEM, itemBean.getTid(), SerializeUtil.serialize(itemBean));
						}
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
		SPHelper.getInst().saveInt(SPHelper.KEY_FAV_VERSION, 1);
	}

}
