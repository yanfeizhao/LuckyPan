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
				 * 如果转盘没有转，那么就开始转。
				 * 如果还转着，且没有按下停止，点击，就放慢速度，直到停止。
				 * 已经点击了停止了，但是还转着没有停下来，此时，没有对应的else，所以就没有操作，跟不支持点击差不多。
				 */
				if(!mLuckypan.isStart()){
					mLuckNameNum=Integer.valueOf(mLuckNum.getText().toString());
					if(mLuckNameNum<0||mLuckNameNum>5)
						mLuckNameNum=1;
					mLuckypan.luckyStart(mLuckNameNum);//设置固定的奖项ipad。
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
