# NotePad
=======

主要实现了显示时间，搜索，美化UI,更换背景，导出txt，定时提示的功能。

功能一：时间显示
----

时间戳转换  

```
String re_StrTime = null;  
   SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss MM-dd-yyyy");  
// Gets the current system time in milliseconds  
   Long now = Long.valueOf(System.currentTimeMillis());  
   re_StrTime = sdf.format(new Date(now));
   values.put(NotePad.Notes.COLUMN_NAME_MODIFICATION_DATE, re_StrTime);
```

在notelist里相应的部分增加修改时间的参数
```
private static final String[] PROJECTION = new String[] {
            NotePad.Notes._ID, // 0
            NotePad.Notes.COLUMN_NAME_TITLE, // 1
            NotePad.Notes.COLUMN_NAME_MODIFICATION_DATE, //2修改时间
    };
protected void onCreate(Bundle savedInstanceState) {
Cursor cursor = managedQuery(
            getIntent().getData(),  // Use the default content URI for the provider.
            PROJECTION,                       // Return the note ID and title for each note.
            null,                             // No where clause, return all records.
            null,                             // No where clause, therefore no where column values.
            NotePad.Notes.DEFAULT_SORT_ORDER  // Use the default sort order.
        );
// The names of the cursor columns to display in the view, initialized to the title column
        String[] dataColumns = { NotePad.Notes.COLUMN_NAME_TITLE,NotePad.Notes.COLUMN_NAME_MODIFICATION_DATE } ;//增加修改时间

        // The view IDs that will display the cursor columns, initialized to the TextView in
        // noteslist_item.xml
        int[] viewIDs = { android.R.id.text1, R.id.time };//增加修改时间

        // Creates the backing adapter for the ListView.
        MyAdapter adapter
            = new MyAdapter(
                      this,                             // The Context for the ListView
                      R.layout.noteslist_item,          // Points to the XML for a list item
                      cursor,                           // The cursor to get items from
                      dataColumns,
                      viewIDs
              );
// Sets the ListView's adapter to be the cursor adapter that was just created.
        setListAdapter(adapter);
}
```

![image](https://raw.githubusercontent.com/windarain/picture/master/notepad/home.png)  


功能二：搜索
----

新增加一个activity，NoteSearch.java，用来显示搜索界面，主要使用的函数如下所示，String selection = NotePad.Notes.COLUMN_NAME_TITLE + " LIKE '%" +queryText+ "%' " ;由这句来实现模糊查询

```
public boolean onQueryTextChange(String queryText) {
        String selection = NotePad.Notes.COLUMN_NAME_TITLE + " LIKE '%"
                +queryText+ "%' " ;
        Cursor cursor = getContentResolver().query(
                mUri,    // The URI for the record to update.
                PROJECTION,
                selection,  // The map of column names and new values to apply to them.
                null,    // No selection criteria are used, so no where columns are necessary.
                NotePad.Notes.DEFAULT_SORT_ORDER      // No where columns are used, so no where arguments are necessary.
        );

        String[] dataColumns = { NotePad.Notes.COLUMN_NAME_TITLE,NotePad.Notes.COLUMN_NAME_MODIFICATION_DATE } ;//增加修改时间

        int[] viewIDs = { android.R.id.text1, R.id.time };//增加修改时间

        adapter = new SimpleCursorAdapter(
                this,                             // The Context for the ListView
                R.layout.noteslist_item,          // Points to the XML for a list item
                cursor,                           // The cursor to get items from
                dataColumns,
                viewIDs

        );
        setListAdapter(adapter);
        return true;
    }
```    

新增加一个xml，里面放置了SearchView和ListView，SearchView是系统自带的搜索控件，ListView用来显示查找的信息，ListView的信息随着查找的信息而变化。

![image](https://raw.githubusercontent.com/windarain/picture/master/notepad/search.png)  

功能三：美化UI与更换背景
----

此功能的实现主要是通过自定义一个simplecursoradapter来完成的。
自定义视图和视图容器，初始化构造函数。重写newView函数，获取控件对象，此处的控件都放在ListItemView中；重写bindView函数，主要是获取数据库中存储的背景色数据，在NoteList中更换背景的时候，就把这个笔记的item颜色设置写入数据库中，然后在自定义的MyAdapter中使用，而每一个item显示的字体颜色随着背景色的不同而不同。

``` 
//自定义SimpleCursorAdapter适配器
public class MyAdapter extends SimpleCursorAdapter {
    private Cursor cursor = null;
    private Context m_context;
    private LayoutInflater miInflater;

    // 自定义视图
    private ListItemView listItemView = null;
    // 视图容器
    private LayoutInflater mListContainer;


    public MyAdapter(Context context, int layout, Cursor c,
                                 String[] from, int[] to) {
        super(context, layout, c, from, to);
        mListContainer = LayoutInflater.from(context);
        m_context = context;
        cursor = c;
    }

    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        View convertView = null;
        listItemView = new ListItemView();
            convertView = mListContainer.inflate(
                    R.layout.noteslist_item, parent, false);
            // 获取控件对象
        listItemView.text1=(TextView)convertView
                .findViewById(android.R.id.text1);
        listItemView.time=(TextView)convertView
                .findViewById(R.id.time);
        // 初始化ListView中每一行布局中的LinearLayout
        listItemView.linearlayout = (LinearLayout) convertView
                .findViewById(R.id.mainlayout);
        // 设置控件集到convertView
        convertView.setTag(listItemView);
        return convertView;
    }

    // view newView函数的返回值
    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        listItemView = (ListItemView) view.getTag();
        int bg_color = cursor.getInt(cursor
                .getColumnIndex(NotePad.Notes.COLUMN_NAME_ITEM_BACKGROUND));

        listItemView.linearlayout.setBackgroundResource(bg_color);

        listItemView.text1.setText(cursor.getString(cursor
                .getColumnIndex(NotePad.Notes.COLUMN_NAME_TITLE)));
        listItemView.time.setText(cursor.getString(cursor
                .getColumnIndex(NotePad.Notes.COLUMN_NAME_MODIFICATION_DATE)));

        int color=context.getResources().getColor(R.color.blue);
        switch (bg_color){
            case R.drawable.item_blue: color = context.getResources().getColor(R.color.blue);
                break;
            case R.drawable.item_green: color = context.getResources().getColor(R.color.green);
                break;
            case R.drawable.item_gray: color = context.getResources().getColor(R.color.gray);
                break;
            case R.drawable.item_yellow: color = context.getResources().getColor(R.color.yellow);
                break;
            case R.drawable.item_pink: color = context.getResources().getColor(R.color.pink);
                break;
            case R.drawable.item_violet: color = context.getResources().getColor(R.color.violet);
                break;
        }
        listItemView.text1.setTextColor(color);
        listItemView.time.setTextColor(color);
    }

    public final class ListItemView {
        public LinearLayout linearlayout;
        public TextView text1;
        public TextView time;
    }
}
``` 

在点击菜单的选项时，显示一个AlertDialog，用来选择想要的背景，每一个选项由ImageButton构成，通过监听点击事件，完成背景色的修改。
```
case R.id.menu_setbackground:
                AlertDialog.Builder builder = new AlertDialog.Builder(NoteEditor.this);
                LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
                final View view = inflater.inflate(R.layout.note_background,
                        null);
                final AlertDialog dialog = builder.create();
                dialog.setView(view, 0, 0, 0, 0);
//                //设置透明度
//                Window window = dialog.getWindow();
//                WindowManager.LayoutParams lp = window.getAttributes();
//                lp.alpha = 0.6f;
//                window.setAttributes(lp);

                // 初始化布局文件中的ImageButton对象
                ImageButton blue = (ImageButton) view.findViewById(R.id.blue);
                ImageButton green = (ImageButton) view.findViewById(R.id.green);
                ImageButton grey = (ImageButton) view.findViewById(R.id.gray);
                ImageButton orange = (ImageButton) view.findViewById(R.id.yellow);
                ImageButton pink = (ImageButton) view.findViewById(R.id.pink);
                ImageButton violet = (ImageButton) view.findViewById(R.id.violet);

                // 自定义ImageButton的点击事件监听器
                Button.OnClickListener listener = new Button.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        switch (v.getId()) {
                            case R.id.blue:
                                mBackgroud_Color = R.drawable.bg_blue;
                                mItemBackgroud_Color = R.drawable.item_blue;
                                color = getResources().getColor(R.color.blue);
                                mText.setBackgroundResource(R.drawable.bg_blue);
                                mText.setTextColor(color);
                                break;
                            case R.id.green:
                                mBackgroud_Color = R.drawable.bg_green;
                                mItemBackgroud_Color = R.drawable.item_green;
                                color = getResources().getColor(R.color.green);
                                mText.setBackgroundResource(R.drawable.bg_green);
                                mText.setTextColor(color);
                                break;
                            case R.id.gray:
                                mBackgroud_Color = R.drawable.bg_gray;
                                mItemBackgroud_Color = R.drawable.item_gray;
                                color = getResources().getColor(R.color.gray);
                                mText.setBackgroundResource(R.drawable.bg_gray);
                                mText.setTextColor(color);
                                break;
                            case R.id.yellow:
                                mBackgroud_Color = R.drawable.bg_yellow;
                                mItemBackgroud_Color = R.drawable.item_yellow;
                                color = getResources().getColor(R.color.yellow);
                                mText.setBackgroundResource(R.drawable.bg_yellow);
                                mText.setTextColor(color);
                                break;
                            case R.id.pink:
                                mBackgroud_Color = R.drawable.bg_pink;
                                mItemBackgroud_Color = R.drawable.item_pink;
                                color = getResources().getColor(R.color.pink);
                                mText.setBackgroundResource(R.drawable.bg_pink);
                                mText.setTextColor(color);
                                break;
                            case R.id.violet:
                                mBackgroud_Color = R.drawable.bg_violet;
                                mItemBackgroud_Color = R.drawable.item_violet;
                                color = getResources().getColor(R.color.violet);
                                mText.setBackgroundResource(R.drawable.bg_violet);
                                mText.setTextColor(color);
                                break;
                        }
                        // 结束对话框
                        dialog.dismiss();
                    }
                };

                // 注册点击事件监听器
                blue.setOnClickListener(listener);
                green.setOnClickListener(listener);
                grey.setOnClickListener(listener);
                orange.setOnClickListener(listener);
                pink.setOnClickListener(listener);
                violet.setOnClickListener(listener);
                dialog.show();
```

在NoteEditor的OnCreat方法中从数据库中获取之前保存的数据，将之设为EditText的背景色，那么久能实现背景的保存了。
```
// 根据数据库中的值设定背景颜色
        if (mCursor.moveToFirst()) {
            mBackgroud_Color = mCursor.getInt(mCursor
                    .getColumnIndex(NotePad.Notes.COLUMN_NAME_BACKGROUND));
        }
        mText.setBackgroundResource(mBackgroud_Color);

        int bg_color = mCursor.getInt(mCursor
                .getColumnIndex(NotePad.Notes.COLUMN_NAME_BACKGROUND));
        switch (bg_color) {
            case R.drawable.bg_blue:
                color = getResources().getColor(R.color.blue);
                break;
            case R.drawable.bg_green:
                color = getResources().getColor(R.color.green);
                break;
            case R.drawable.bg_gray:
                color = getResources().getColor(R.color.gray);
                break;
            case R.drawable.bg_yellow:
                color = getResources().getColor(R.color.yellow);
                break;
            case R.drawable.bg_pink:
                color = getResources().getColor(R.color.pink);
                break;
            case R.drawable.bg_violet:
                color = getResources().getColor(R.color.violet);
                break;
        }
        mText.setTextColor(color);
```
![image](https://raw.githubusercontent.com/windarain/picture/master/notepad/bg.gif)  

功能四：导出txt
----

首先要在AndroidManifest.xml中授权

```
<!-- 取得向SDCard写文件的权限 -->
    <uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE" />
    <uses-permission android:name="android.permission.MOUNT_UNMOUNT_FILESYSTEMS" />
```

导出功能是放置在编辑界面中，通过点击菜单项，然后使用Output函数来实现文件的导出。文件导出的位置已经设定好为手机的根目录下。导出成功时，通过Toast来告知用户导出成功。

```
/**
     * 此函数用于导出文件
     *
     * @param c  获取此笔记存储在数据库中的各种信息
     */
    private final void output(Cursor c) {
        //定义变量title存储此笔记的标题
        String title=c.getString(c.getColumnIndex(NotePad.Notes.COLUMN_NAME_TITLE));
        //判断是否存在sd，是否可创建文件
        if (Environment.getExternalStorageState().equals(
                Environment.MEDIA_MOUNTED)) {
            //获取sd卡根目录，一般为内部存储器的根目录
            String filePath = Environment.getExternalStorageDirectory().getAbsolutePath() ;
            //设置文件名为笔记的标题
            String fileName = "/" + title + ".txt";
            //存储导出的文件路径，此函数设定好存储的位置
            String strPathName = filePath + fileName;
            File file = null;
            //创建文件
            file = new File(strPathName);
            file.getParentFile().mkdirs();
            try {
                file.createNewFile();
            }catch (Exception e) {
                e.printStackTrace();
            }
            //创建一个打印流
            PrintWriter pw = null;
            try {
                //用FileOutputStream写入文件，将文件名传入
                FileOutputStream fout=new FileOutputStream(file);
                pw = new PrintWriter(new OutputStreamWriter(fout, "UTF-8"));
                //输入此笔记的内容
                pw.println(c.getString(c.getColumnIndex(NotePad.Notes.COLUMN_NAME_NOTE)));
                pw.println("========================================");
                //输入文件的创建时间
                pw.println("Create date     :"+c.getString(c.getColumnIndex(NotePad.Notes.COLUMN_NAME_CREATE_DATE)));
                //输入文件最后一次修改时间
                pw.println("Last update date:"+c.getString(c.getColumnIndex(NotePad.Notes.COLUMN_NAME_MODIFICATION_DATE)));
            } catch (IOException e){
                e.printStackTrace();
            } finally {
                if (pw != null) {
                    pw.flush();
                }
                //提示用户导出成功
                Toast.makeText(NoteEditor.this, "OUTPUT SUCCESS" , Toast.LENGTH_SHORT).show();
            }
        }
    }
```
![image](https://raw.githubusercontent.com/windarain/picture/master/notepad/output.png)  
![image](https://raw.githubusercontent.com/windarain/picture/master/notepad/output2.png)  
![image](https://raw.githubusercontent.com/windarain/picture/master/notepad/output3.png)  

功能五：定时提醒
----

在编辑界面新增加一个定时提醒的功能，主要通过AlarmManager来实现。用户点击菜单项Alarm，可以选择要定时提醒的时间，到设定好的时间时，将会弹出一个Toast消息，告知用户此笔记的内容。

要先在AndroidManifest.xml中注册闹钟接收器和授权
```
<!-- 唤醒屏幕并解锁 -->
    <uses-permission android:name="android.permission.DISABLE_KEYGUARD" />
    <uses-permission android:name="android.permission.WAKE_LOCK" />
<!-- 注册自定义闹钟接收器 -->
        <receiver android:name="com.example.android.notepad.AlarmReceiver">
            <intent-filter>
                <action android:name="CLOCK" />
            </intent-filter>
        </receiver>
```
用户选择时间主要由DatePickerDialog和TimePickerDialog实现，然后获取用户选择时间，判断时间是否小于当前时间，是的话提示创建不成功；否则告知用户创建成功及设定的时间。

在判断时间是否合理的时候，在合理的情况下，使用am.set(AlarmManager.RTC_WAKEUP, alarmCalendar.getTimeInMillis(), pi);进行广播，然后使用自定义的闹钟接收器AlarmReceiver来相应到达设定时间的任务。

```
   private int mYear;// 提醒时间的年份
    private int mMonth;// 提醒时间的月份
    private int mDay;// 提醒时间的日(dayOfMonth)
    private int mHour;// 提醒时间的小时
    private int mMinute;// 提醒时间的分钟
    private boolean hasSetAlartTime = false;// 用于标识用户是否设置Alarm
    /**
     * 此函数用于实现定时提醒
     */
    private final void alarm() {
        //获得ALarmManager实例
        final AlarmManager am = (AlarmManager) getSystemService(ALARM_SERVICE);
        final Calendar c = Calendar.getInstance();
        mYear = c.get(Calendar.YEAR);
        mMonth = c.get(Calendar.MONTH);
        mDay = c.get(Calendar.DAY_OF_MONTH);
        mHour = c.get(Calendar.HOUR_OF_DAY);
        mMinute = c.get(Calendar.MINUTE);
        AlertDialog.Builder builder = new AlertDialog.Builder(NoteEditor.this);
        builder.setTitle("Set alarm time");
        LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.alarm_set, null);
        builder.setView(view);
        // 点击设置闹钟日期
        final Button btnAlarmDate = (Button) view
                .findViewById(R.id.btnAlarmDate);
        btnAlarmDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                DatePickerDialog tpd = new DatePickerDialog(NoteEditor.this,
                            new DatePickerDialog.OnDateSetListener() {
                                @Override
                                public void onDateSet(DatePicker view,int year, int monthOfYear,
                                                      int dayOfMonth) {
                                    mYear = year;
                                    mMonth = monthOfYear+1;
                                    mDay = dayOfMonth;
                                    String alarmDate = mYear + "-" + mMonth + "-"
                                            + mDay;
                                    btnAlarmDate.setText(alarmDate);
                                }
                            }, mYear, mMonth, mDay);
                    tpd.show();

        // 点击设置闹钟时间
        final Button btnAlarmTime = (Button) view
                .findViewById(R.id.btnAlarmTime);
        btnAlarmTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TimePickerDialog tpd = new TimePickerDialog(NoteEditor.this,
                        new TimePickerDialog.OnTimeSetListener() {
                            @Override
                            public void onTimeSet(TimePicker view,
                                                  int hourOfDay, int minute) {
                                mHour = hourOfDay;
                                mMinute = minute;
                                String alarmTime = hourOfDay + ":" + minute;
                                btnAlarmTime.setText(alarmTime);
                            }
                        }, mHour, mMinute, true);
                tpd.show();
            }
        });
        builder.setPositiveButton("OK",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // 检测时间是否合理,如:不能早于现在
                        ContentValues values = new ContentValues();
                        if (checkAlarmTime(am)) {
                            String alarmtime=mHour+":"+mMinute+":00"+" "+mYear+"-"+mMonth+"-"+mDay;
                            updateNote(text1, null,alarmtime);
                            dialog.dismiss();

                        } else {
                            Toast.makeText(getApplicationContext(), "Set failed",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
        builder.setNegativeButton("Cancel",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        hasSetAlartTime = false;
                        dialog.dismiss();
                    }
                });
        builder.create().show();
    }

    private String openType;
    //检查时间是否设置正确
    private boolean checkAlarmTime(AlarmManager am) {
        Calendar alarmCalendar = Calendar.getInstance();
        alarmCalendar.set(mYear, mMonth-1, mDay, mHour, mMinute,0);
        // 使用传递过来的intent
        Intent i = new Intent("CLOCK");
        i.setClass(this, AlarmReceiver.class);
        i.putExtra(NotePad.Notes._ID, _id);
        i.putExtra("aa", "bb");
        i.putExtra("note",NotePad.Notes.COLUMN_NAME_NOTE);
        //设置一个PendingIntent对象，发送广播
        //  FLAG_UPDATE_CURRENT 如果希望获取的PendingIntent对象与已经存在的PendingIntent对象相比，如果只是Intent附加的数据不 同，
        // 那么当前存在的PendingIntent对象不会被取消，而是重新加载新的Intent附加的数据
        PendingIntent pi = PendingIntent.getBroadcast(this, _id,
                i, PendingIntent.FLAG_UPDATE_CURRENT);

        if (!alarmCalendar.before(Calendar.getInstance())) {
            // 判断时间设置是否合理,合理则发送闹钟请求，设置在alarmCalendar.getTimeInMillis()时间启动由pi指定的组件
            //alarmCalendar.getTimeInMillis()为先前设定的时间
            //RTC_WAKEUP状态：表示闹钟在睡眠状态下会唤醒系统并执行提示功能，该状态下闹钟也使用绝对时间。
        am.set(AlarmManager.RTC_WAKEUP, alarmCalendar.getTimeInMillis(), pi);
            return true;
        }
        return false;
    }
```

```
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
```

WakeLockOpration主要是用来唤醒休眠的手机。

```
// 当到了用户设置的闹钟时间时,如果屏幕关闭,我们可以通过使用WakeLock和KeyguardLock来解锁屏幕并弹出我们自己的Dialog
public final class WakeLockOpration {
	private static PowerManager.WakeLock wakeLock;

	// 获得wakelock
	public static void acquire(Context context) {
		if (wakeLock != null) {
			wakeLock.release();
		}
		PowerManager pm = (PowerManager) context
				.getSystemService(Context.POWER_SERVICE);
		wakeLock = pm.newWakeLock(PowerManager.FULL_WAKE_LOCK
				| PowerManager.ACQUIRE_CAUSES_WAKEUP
				| PowerManager.ON_AFTER_RELEASE, NotesList.TAG);
		wakeLock.acquire();
	}

	// 释放wakelock
	public static void release() {
		if (wakeLock != null)
			wakeLock.release();
		wakeLock = null;
	}
}
```
![image](https://raw.githubusercontent.com/windarain/picture/master/notepad/alarm.gif)  