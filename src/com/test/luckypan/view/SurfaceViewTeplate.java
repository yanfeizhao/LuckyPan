package com.test.luckypan.view;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.view.SurfaceHolder;
import android.view.SurfaceHolder.Callback;
import android.view.SurfaceView;

public class SurfaceViewTeplate extends  SurfaceView implements Callback, Runnable{
	
	private SurfaceHolder  mSurfaceHolder;
	private Canvas mCanvas;
	//���ڻ��Ƶ����߳�
	private Thread thread;
	private boolean isRunning;//�߳̿��ƿ��ء��ж��߳̿�ʼ�˻��ǽ����ˡ�
	
	public SurfaceViewTeplate(Context context) {
		super(context, null);
	}
	
	public SurfaceViewTeplate(Context context, AttributeSet attrs) {
		super(context, attrs);
		mSurfaceHolder=getHolder();
		mSurfaceHolder.addCallback(this);
		//�ɻ�ý���
		setFocusable(true);
		setFocusableInTouchMode(true);
		//���ó���
		setKeepScreenOn(true);		
	}

	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		isRunning=true;
		thread=new Thread(this);
		thread.start();
	}

	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width,
			int height) {
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		isRunning=false;
	}

	@Override
	public void run() {
		while(isRunning){
			draw();
		}
	}

	private void draw() {
		/*
		 * ��δ������Ȼ�Ѿ�����try����catch��Ϊʲô��Ҫ�����ж��Ƿ�Ϊ���أ�
		 * ԭ�򣺵����ǵ�surfaceView����ҳ��ʱ�򣬵��home����back��surface�ͻᱻ���١������ǵĴ����Ѿ����뵽����draw��
		 * canvas�п���Ϊ�գ����Խ����пա�
		 * ����һ������ǣ���Ȼsurface�����٣������̲߳�����ô���׹رգ����ܻ�������������ʱ���п��ܳ��쳣������catch��������ȥ�������쳣���룬ԭ����£�
		 */
		try {
			mCanvas=mSurfaceHolder.lockCanvas();
			if(mCanvas!=null){
				//draw   sth   ��ʼ���ƣ�Բ�̡�
			}
		} catch (Exception e) {
//			e.printStackTrace();//�������쳣����Ϊ�п��ܳ��쳣�ĵط����ǵ��home����back�����������֮�󣬾��л���home��
		}
		finally{
			if(mCanvas!=null){
				mSurfaceHolder.unlockCanvasAndPost(mCanvas);//�ͷ�canvas��
			}
		}
	}
	
	

}
