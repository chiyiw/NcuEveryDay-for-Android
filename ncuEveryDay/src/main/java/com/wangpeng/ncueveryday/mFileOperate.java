package com.wangpeng.ncueveryday;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;

import android.content.Context;
import android.widget.Toast;

/**
 * 自定义的文件操作类，用于简化对应用内部（独立）文件的读写操作
 * 
 * @author wangpeng
 */
public class mFileOperate {
	private String filename;
	private Context context;

	/**
	 * 初始化自定义文件操作类
	 * 
	 * @param context
	 *            当前Activity
	 * @param filename
	 *            文件名
	 */
	public mFileOperate(Context context, String filename) {
		this.filename = filename;
		this.context = context;
	}

	/**
	 * 写入数据到应用内部文件中
	 * 
	 * @param data
	 *            要写入的数据
	 */
	public void writeToFile(String data) {
		try {
			// 打开应用内部文件，当文件不存在时会创建，当存在会将之前内容覆盖
			FileOutputStream fos = context.openFileOutput(filename,
					Context.MODE_PRIVATE);
			OutputStreamWriter osw = new OutputStreamWriter(fos, "UTF-8");
			// 写入信息到文件中
			osw.write(data);
			// 清理输入缓冲区
			osw.flush();
			osw.close();
			fos.close();
//			Toast.makeText(context.getApplicationContext(), "写入文件成功",
//					Toast.LENGTH_SHORT).show();

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 读取内部文件中的内容
	 * 
	 * @return String 文件内容
	 */
	public String readFromFile() {
		String result = "";
		try {
			// 打开应用内部文件
			FileInputStream fis = context.openFileInput(filename);
			// 设置读取编码
			InputStreamReader isr = new InputStreamReader(fis, "UTF-8");
			// 创建字节流
			char input[] = new char[fis.available()];
			// 读取数据到字节流中
			isr.read(input);
			isr.close();
			fis.close();
			// 使用字节流创建字符串
			result = new String(input);
//			Toast.makeText(context.getApplicationContext(), result,
//					Toast.LENGTH_SHORT).show();
		} catch (FileNotFoundException e) {
//			Toast.makeText(context.getApplicationContext(), "文件不存在",
//					Toast.LENGTH_SHORT).show();
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return result;
	}
}