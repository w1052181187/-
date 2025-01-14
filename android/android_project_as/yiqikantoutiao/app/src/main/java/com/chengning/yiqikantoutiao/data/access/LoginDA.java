package com.chengning.yiqikantoutiao.data.access;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

import com.chengning.common.base.BaseListDA;
import com.chengning.common.util.SerializeUtil;
import com.chengning.yiqikantoutiao.data.bean.LoginUserBean;
import com.chengning.yiqikantoutiao.data.bean.UserInfoBean;
import com.chengning.yiqikantoutiao.db.provider.dbContent.table_login;

import java.util.List;

public class LoginDA extends BaseListDA<LoginUserBean> {

	private static LoginDA mInst;

	public static LoginDA getInst(Context con) {
	    if (mInst == null) {
            synchronized (LoginDA.class) {
                if (mInst == null) {
                    mInst = new LoginDA(con);
                }
            }
        }
		return mInst;
	}

	private LoginDA(Context con) {
		super(con, table_login.CONTENT_URI);
	}

	@Override
	public String buildDeleteWhere(LoginUserBean t) {
		return null;
	}

	@Override
	public String[] buildDeleteSelectionArgs(LoginUserBean t) {
		return null;
	}
	
	@Override
	public ContentValues buildInsertValues(LoginUserBean bean) {
		ContentValues values = new ContentValues();
		values.put("userinfo", SerializeUtil.serialize(bean.getUserinfo()));
		values.put("cookie", bean.getCookie());
		values.put("lasttime", bean.getLasttime());
		return values;
	}

	@Override
	public LoginUserBean buildQueryValues(Cursor cursor) {
		LoginUserBean bean = new LoginUserBean();
		UserInfoBean uBean = SerializeUtil.deSerialize(
				cursor.getString(cursor.getColumnIndex("userinfo")),
				UserInfoBean.class);
		bean.setUserinfo(uBean);
		bean.setCookie(cursor.getString(cursor
				.getColumnIndex("cookie")));
		bean.setLasttime(cursor.getString(cursor
				.getColumnIndex("lasttime")));
		return bean;
	}

	public LoginUserBean getLoginUserBean() {
		List<LoginUserBean> list = queryAll();
		if (list != null && list.size() > 0) {
			return list.get(0);
		} else {
			return null;
		}
	}

}
