package com.chengning.fenghuo.data.access;

import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

import com.chengning.common.base.BaseListDA;
import com.chengning.fenghuo.data.bean.SettingBean;
import com.chengning.fenghuo.db.provider.dbContent.table_setting;

public class SettingDA extends BaseListDA<SettingBean> {

	private static SettingDA mInst;

	public static SettingDA getInst(Context con) {
	    if (mInst == null) {
            synchronized (SettingDA.class) {
                if (mInst == null) {
                    mInst = new SettingDA(con);
                }
            }
        }
		return mInst;
	}

	private SettingDA(Context con) {
		super(con, table_setting.CONTENT_URI);
	}

	@Override
	public String buildDeleteWhere(SettingBean t) {
		return null;
	}

	@Override
	public String[] buildDeleteSelectionArgs(SettingBean t) {
		return null;
	}

	@Override
	public ContentValues buildInsertValues(SettingBean bean) {
		ContentValues values = new ContentValues();
		values.put("is_push",bean.getIs_push());
		values.put("fontsize",bean.getFontsize());
		values.put("lasttime",bean.getLasttime());
		values.put("is_no_pic_model", bean.getIs_no_pic_model());
		values.put("is_night_model", bean.getIs_night_model());
		
		values.put("config_push_is_at",bean.getConfig_push_is_at());
		values.put("config_push_is_reply",bean.getConfig_push_is_reply());
		values.put("config_push_is_dig",bean.getConfig_push_is_dig());
		values.put("config_push_is_pm",bean.getConfig_push_is_pm());
		values.put("config_push_is_fan",bean.getConfig_push_is_fan());
		values.put("config_push_start_time",bean.getConfig_push_start_time());
		values.put("config_push_end_time",bean.getConfig_push_end_time());
		values.put("config_reply_access",bean.getConfig_reply_access());
		values.put("config_push_enable",bean.getConfig_push_enable());
		
		return values;
	}

	@Override
	public SettingBean buildQueryValues(Cursor cursor) {
		SettingBean bean = new SettingBean();
		bean.setIs_push(cursor.getInt(cursor.getColumnIndex("is_push"))); 
		bean.setFontsize(cursor.getInt(cursor.getColumnIndex("fontsize"))); 
		bean.setLasttime(cursor.getString(cursor.getColumnIndex("lasttime")));
		bean.setIs_no_pic_model(cursor.getInt(cursor.getColumnIndex("is_no_pic_model"))); 
		bean.setIs_night_model(cursor.getInt(cursor.getColumnIndex("is_night_model")));
		
		bean.setConfig_push_is_at(cursor.getInt(cursor.getColumnIndex("config_push_is_at"))); 
		bean.setConfig_push_is_reply(cursor.getInt(cursor.getColumnIndex("config_push_is_reply"))); 
		bean.setConfig_push_is_dig(cursor.getInt(cursor.getColumnIndex("config_push_is_dig"))); 
		bean.setConfig_push_is_pm(cursor.getInt(cursor.getColumnIndex("config_push_is_pm"))); 
		bean.setConfig_push_is_fan(cursor.getInt(cursor.getColumnIndex("config_push_is_fan"))); 
		bean.setConfig_push_start_time(cursor.getString(cursor.getColumnIndex("config_push_start_time")));
		bean.setConfig_push_end_time(cursor.getString(cursor.getColumnIndex("config_push_end_time")));
		bean.setConfig_reply_access(cursor.getInt(cursor.getColumnIndex("config_reply_access"))); 
		bean.setConfig_push_enable(cursor.getInt(cursor.getColumnIndex("config_push_enable"))); 
		return bean;
	}
	
	public SettingBean getSettingBean(){
		List<SettingBean> list = queryAll();
		if(list != null && list.size() > 0){
			return list.get(0);
		}else{
			return null;
		}
	}

}
