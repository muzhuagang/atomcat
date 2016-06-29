package com.cat.core;

import com.skull.core.HttpConnect;
import com.skull.core.HttpConnect.HttpCallBack;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Messenger;
import android.app.Activity;
import android.content.Intent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends Activity implements OnClickListener{

    String[] servletList;
    String[] servletPathList;
    Button startbtn,stopbtn;
    HttpCallBack callBackResult;
    TextView lable;
    Handler mHandler=new MyHandle();
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        startbtn=(Button)findViewById(R.id.start_btn);
        stopbtn=(Button)findViewById(R.id.stop_btn);
        lable=(TextView)findViewById(R.id.textView1);
        startbtn.setOnClickListener(this);
        stopbtn.setOnClickListener(this);
        initServlet();
    }
    
    /**
     * 初始化servlet信息
     */
    private void initServlet(){
    	servletList = getResources().getStringArray(R.array.function_servlet_name_list);
    	servletPathList = getResources().getStringArray(R.array.function_servlet_path_list);
    	ServletMappingInfo.create(servletList, servletPathList); //初始化servlet信息
    	ServletMappingInfo.setAContext(this);                    //获取上下文
    }
    
	@Override
	public void onClick(View view) {
		switch (view.getId()) {
		case R.id.start_btn:
			System.out.println("startservice");
			Intent intent = new Intent(MainActivity.this, HttpConnector.class);
			intent.putExtra("handler", new Messenger(mHandler));
			startService(intent);
			startbtn.setEnabled(false);
			stopbtn.setEnabled(true);
			break;
		case R.id.stop_btn:
			sendQuitService();
			break;
		}
	}
	
	/**
	 * 停止服务器的监听线程
	 */
	private void sendQuitService(){
		callBackResult = new HttpCallBack() {
			@Override
			public void result(String str) {
				System.out.println("result="+str);
				Intent intent = new Intent(MainActivity.this, HttpConnector.class);
				stopService(intent);
				startbtn.setEnabled(true);
				stopbtn.setEnabled(false);
			}
		};
		HttpConnect.apacheConGet("http://127.0.0.1:8089/SHUTDOWN", this, callBackResult, "utf-8");
	}
	
    class MyHandle extends Handler {

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case 1:    //启动成功发送的消息
                	lable.setText(msg.obj.toString());
                    break;
                case 2:    //启动失败发送的消息
                	lable.setText(msg.obj.toString());
                    break; 
            }
        }
    }
    
}
