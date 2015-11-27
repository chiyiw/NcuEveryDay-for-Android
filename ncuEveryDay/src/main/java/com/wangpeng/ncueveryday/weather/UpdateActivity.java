package com.wangpeng.ncueveryday.weather;

import net.youmi.android.AdManager;
import net.youmi.android.update.AppUpdateInfo;
import net.youmi.android.update.CheckAppUpdateCallBack;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Toast;

/**
 * 检查应用程序更新，使用了有米实用工具SDK
 * @author wangpeng
 *
 */
public class UpdateActivity extends Activity implements CheckAppUpdateCallBack {
	private Context context;
	private boolean isFirstOpen; // 如果是启动时调用，则不用显示“已经是最新版”

	/**
	 * @param context 上下文
	 * @param isFirstOpen 是否是启动时调用，是否显示“已经是最新版”
	 */
	public UpdateActivity(Context context, boolean isFirstOpen) {
		this.context = context;
		this.isFirstOpen = isFirstOpen;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// 调用检查更新接口，这里可以在 UI 线程调用，也可以在非 UI 线程调用。
		AdManager.getInstance(this).asyncCheckAppUpdate(this);
		System.out.println("正在检查版本更新");
	}

	public void onCheckAppUpdateFinish(final AppUpdateInfo updateInfo) {
		System.out.println("检查更新成功！");
		if (updateInfo == null || updateInfo.getUrl() == null) {
			// 当前已经是最新版本
			System.out.println("当前已经是最新版本");
			if (isFirstOpen == false) {
				Toast.makeText(context, "已经是最新版本", Toast.LENGTH_SHORT).show();
			}
		} else {
			System.out.println("有新版本");
			// 有更新信息，开发者应该在这里实现下载新版本
			System.out.println(updateInfo.getUpdateTips());
			
			// 弹出更新信息提示框
			Dialog dialog = new AlertDialog.Builder(context)
					.setTitle("新版本" + updateInfo.getVersionName())
					.setMessage(updateInfo.getUpdateTips().replace("\\n", "\n"))
					.setIcon(android.R.drawable.ic_menu_info_details)
					.setNegativeButton("马上升级", new OnClickListener() {

						public void onClick(DialogInterface dialog, int which) {
							// 跳转到下载链接
							Intent intent = new Intent(Intent.ACTION_VIEW, Uri
									.parse(updateInfo.getUrl()));
							intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
							context.startActivity(intent);
						}
					}).setPositiveButton("下次再说", new OnClickListener() {

						public void onClick(DialogInterface dialog, int which) {
							dialog.cancel();
						}
					}).create();
			dialog.show();
		}
	}
}
