/*
 * 官网地站:http://www.mob.com
 * 技术支持QQ: 4006852216
 * 官方微信:ShareSDK   （如果发布新版本的话，我们将会第一时间通过微信将版本更新内容推送给您。如果使用过程中有任何问题，也可以通过微信与我们取得联系，我们将会在24小时内给予回复）
 *
 * Copyright (c) 2013年 mob.com. All rights reserved.
 */

package com.bibinet.biunion.project.onekeyshare.themes.classic;

import android.content.Context;
import android.content.res.Configuration;

import com.bibinet.biunion.project.onekeyshare.OnekeyShareThemeImpl;
import com.bibinet.biunion.project.onekeyshare.themes.classic.land.EditPageLand;
import com.bibinet.biunion.project.onekeyshare.themes.classic.land.PlatformPageLand;
import com.bibinet.biunion.project.onekeyshare.themes.classic.port.EditPagePort;
import com.bibinet.biunion.project.onekeyshare.themes.classic.port.PlatformPagePort;

import cn.sharesdk.framework.Platform;
import cn.sharesdk.framework.Platform.ShareParams;

/** 九宫格经典主题样式的实现类*/
public class ClassicTheme extends OnekeyShareThemeImpl {

	/** 展示平台列表*/
	protected void showPlatformPage(Context context) {
		PlatformPage page;
		int orientation = context.getResources().getConfiguration().orientation;
		if (orientation == Configuration.ORIENTATION_PORTRAIT) {
			page = new PlatformPagePort(this);
		} else {
			page = new PlatformPageLand(this);
		}
		page.show(context, null);
	}

	/** 展示编辑界面*/
	protected void showEditPage(Context context, Platform platform, ShareParams sp) {
		EditPage page;
		int orientation = context.getResources().getConfiguration().orientation;
		if (orientation == Configuration.ORIENTATION_PORTRAIT) {
			page = new EditPagePort(this);
		} else {
			page = new EditPageLand(this);
		}
		page.setPlatform(platform);
		page.setShareParams(sp);
		page.show(context, null);
	}

}
