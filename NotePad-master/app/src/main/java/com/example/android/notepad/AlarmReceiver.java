package com.example.android.notepad;

import android.app.KeyguardManager;
import android.app.KeyguardManager.KeyguardLock;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

//闹钟广播接收器
public class AlarmReceiver extends BroadcastReceiver {
	private int _id;
	private String msg;

	@Override
	public void onReceive(Context context, Intent intent) {
		WakeLockOpration.acquire(context);

		KeyguardManager km = (KeyguardManager) context
				.getSystemService(Context.KEYGUARD_SERVICE);
		KeyguardLock kl = km.newKeyguardLock(NoteEditor.TAG);
		kl.disableKeyguard();
        // 获取ID
		_id = intent.getIntExtra("ID", -1);
		Intent i = new Intent("CLOCK");
		i.setClass(context, NoteEditor.class);
		//自定义发送数据
		i.putExtra("ID", _id);
		String msg = intent.getStringExtra("NOTE");
		Toast.makeText(context,msg, Toast.LENGTH_SHORT).show();
		i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		context.startActivity(i);
	}
}