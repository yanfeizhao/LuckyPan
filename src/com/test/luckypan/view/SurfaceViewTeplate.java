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
	//用于绘制的子线程
	private Thread thread;
	private boolean isRunning;//线程控制开关。判断线程开始了还是结束了。
	
	public SurfaceViewTeplate(Context context) {
		super(context, null);
	}
	
	public SurfaceViewTeplate(Context context, AttributeSet attrs) {
		super(context, attrs);
		mSurfaceHolder=getHolder();
		mSurfaceHolder.addCallback(this);
		//可获得焦点
		setFocusable(true);
		setFocusableInTouchMode(true);
		//设置常量
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
		 * 这段代码里，既然已经有了try——catch，为什么还要进行判断是否为空呢？
		 * 原因：当我们的surfaceView在主页面时候，点击home或者back，surface就会被销毁。而我们的代码已经进入到这里draw，
		 * canvas有可能为空，所以进行判空。
		 * 还有一种情况是：虽然surface被销毁，但是线程不是那么容易关闭，可能还在做操作，这时就有可能出异常。所以catch，但是又去掉了抛异常代码，原因见下：
		 */
		try {
			mCanvas=mSurfaceHolder.lockCanvas();
			if(mCanvas!=null){
				//draw   sth   开始绘制，圆盘。
			}
		} catch (Exception e) {
//			e.printStackTrace();//不用抛异常，因为有可能出异常的地方就是点击home或者back，而点击完了之后，据切换到home了
		}
		finally{
			if(mCanvas!=null){
				mSurfaceHolder.unlockCanvasAndPost(mCanvas);//释放canvas。
			}
		}
	}
	
	

}
