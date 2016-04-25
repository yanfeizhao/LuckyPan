package com.test.luckypan;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.ImageView;

import com.test.luckypan.view.Luckypan;


public class MainActivity extends Activity {

	private Luckypan mLuckypan;
	private ImageView mStartImageView;
	private EditText mLuckNum;
	private int  mLuckNameNum;
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
      
        initView();
    }

	private void initView() {
		
		mLuckNum=(EditText) findViewById(R.id.et_luckNum);
		mLuckypan=(Luckypan) findViewById(R.id.luckypan);
		mStartImageView=(ImageView) findViewById(R.id.iv_start);
		
		mStartImageView.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				/*
				 * ���ת��û��ת����ô�Ϳ�ʼת��
				 * �����ת�ţ���û�а���ֹͣ��������ͷ����ٶȣ�ֱ��ֹͣ��
				 * �Ѿ������ֹͣ�ˣ����ǻ�ת��û��ͣ��������ʱ��û�ж�Ӧ��else�����Ծ�û�в���������֧�ֵ����ࡣ
				 */
				if(!mLuckypan.isStart()){
					mLuckNameNum=Integer.valueOf(mLuckNum.getText().toString());
					if(mLuckNameNum<0||mLuckNameNum>5)
						mLuckNameNum=1;
					mLuckypan.luckyStart(mLuckNameNum);//���ù̶��Ľ���ipad��
					mStartImageView.setImageResource(R.drawable.stop);
					
				}
				else{
					if(!mLuckypan.isShouldEnd()){
						mLuckypan.luckyEnd();
						mStartImageView.setImageResource(R.drawable.start);
					}
				}
				
			}
		});
	}


   
}
