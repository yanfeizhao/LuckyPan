package com.test.luckypan.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.SurfaceHolder;
import android.view.SurfaceHolder.Callback;
import android.view.SurfaceView;
import com.test.luckypan.R;

@SuppressLint("NewApi")
public class Luckypan extends SurfaceView implements Callback, Runnable {
	
	private SurfaceHolder mSurfaceHolder;
	private Canvas mCanvas;
	// 用于绘制的子线程
	private Thread thread;
	private boolean mIsRunning;// 线程控制开关。判断线程开始了还是结束了。

	// 盘快的奖项
	private String[] mNames = new String[] { "单反相机", "IPad", "恭喜发财", "IPhone",
			"服装一套", "恭喜发财" };
	private int[] mImgs = new int[] { R.drawable.danfan, R.drawable.ipad,
			R.drawable.f040, R.drawable.iphone, R.drawable.meizi,
			R.drawable.f015 };
	// 与图片对应的bitmap数组
	private Bitmap[] mImagBitmap;
	// 盘块的颜色
	private int[] mColors = new int[] { 0xffffc300, 0xfff17e01, 0xffffc300,
			0xfff17e01, 0xffffc300, 0xfff17e01 };

	private int mItemCount = 6;

	// 整个盘块的范围,矩形范围
	private RectF mRange;
	// 圆盘直径
	private int mRadaios;

	private Paint mArcPaint;// 绘制圆盘的画笔
	private Paint mtextPaint;// 绘制文本的画笔

	private double mSpeed;// 圆盘滚动速度。默认是0
	private volatile float mStartAngle = 0;

	private boolean isShouldEnd;// 判断是否点击了停止按钮。

	private int mCenter;// 圆盘的中心位置
	private int mPadding;// 去四个padding中的最小值。或者是直接以paddingleft为准。

	private Bitmap mGbBirtmap = BitmapFactory.decodeResource(getResources(),
			R.drawable.bg2);

	private float mTextSize = TypedValue.applyDimension(
			TypedValue.COMPLEX_UNIT_SP, 20, getResources().getDisplayMetrics());
	
	
	
	

	public Luckypan(Context context) {
		super(context, null);
	}

	public Luckypan(Context context, AttributeSet attrs) {
		super(context, attrs);
		
		mSurfaceHolder = getHolder();
		mSurfaceHolder.addCallback(this);
		
		setFocusable(true);
		setFocusableInTouchMode(true);
		// 设置常量
		setKeepScreenOn(true);
	}

	
	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		
		int width = Math.min(getMeasuredWidth(), getMeasuredHeight());//因为要做一个正方形，所以取小的
		mPadding = getPaddingLeft();
		mRadaios = width - mPadding * 2;// 直径
		mCenter = width / 2;// 中心点,
		setMeasuredDimension(width, width);
	}
	
	
	@Override
	public void surfaceCreated(SurfaceHolder holder) {

		// 初始化绘制圆盘画笔
		mArcPaint = new Paint();
		mArcPaint.setAntiAlias(true);//设置画笔的锯齿效果。
		mArcPaint.setDither(true);//使用图像抖动处理，使得画出来的图片饱满光滑

		// 初始化绘制文本画笔
		mtextPaint = new Paint();
		mtextPaint.setColor(0xffffffff);
		mtextPaint.setTextSize(mTextSize);

		// 初始化盘的绘制的范围。谁的范围？、
		mRange = new RectF(mPadding, mPadding, mPadding + mRadaios, mPadding
				+ mRadaios);

		// 初始化图片
		mImagBitmap = new Bitmap[mItemCount];
		for (int i = 0; i < mImagBitmap.length; i++) {
			mImagBitmap[i] = BitmapFactory.decodeResource(getResources(),
					mImgs[i]);
		}

		mIsRunning = true;
		thread = new Thread(this);
		thread.start();

	}

	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width,
			int height) {

	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		mIsRunning = false;
	}

	@Override
	public void run() {
		while (mIsRunning) {
			// 控制好每50ms绘制一次。
			long start = System.currentTimeMillis();
			draw();
			long end = System.currentTimeMillis();
			if (end - start < 50) {
				try {
					Thread.sleep(50 - (end - start));
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
	}

	private void draw() {
		try {
			mCanvas = mSurfaceHolder.lockCanvas();
			if (mCanvas != null) {
				// draw sth 开始绘制，圆盘。
				drawBg();
				//绘制盘块
				float tmpAngle=mStartAngle;
				float sweepAngle=360/mItemCount;
				
				for(int i=0;i<mItemCount;i++){
					mArcPaint.setColor(mColors[i]);
					
					//绘制盘块。
					mCanvas.drawArc(mRange, tmpAngle, sweepAngle, true, mArcPaint);
					
					//绘制文本
					drawText(tmpAngle,sweepAngle,mNames[i]);
					
					//绘制每个盘块的图片。
					drawIcon(tmpAngle,mImagBitmap[i]);
					
					tmpAngle+=sweepAngle;
				}
				
				mStartAngle +=mSpeed;//只要speed有值，不是0，就会转。。，每绘制一次，如果起点变了，就会给人错觉是在转，而且每次绘制间隔的时间就是比较小。
				
				//如果点击了停止按钮。
				if(isShouldEnd){
					mSpeed-=1;
				}
				if(mSpeed<=0){
					mSpeed=0;
					isShouldEnd=false;
				}
				
			}
		} catch (Exception e) {
		} finally {
			if (mCanvas != null) {
				mSurfaceHolder.unlockCanvasAndPost(mCanvas);// 释放canvas。
			}
		}
	}
	
	/**
	 * 点击启动转盘
	 */
	public void luckyStart(int  index){
		
		//计算每一项的角度
		float angle=360/mItemCount;
		
		//计算每一项的中奖范围。---根据初始状态来算。
		//0:  210--->270  转盘从初始位置旋转360*n + （210---270之间任意一个数），会让地0个元素（单反）被选中。
		//1：  150--->210
		float from =270-(index+1)*angle;
		float end=from + angle;
		
		//设置停下来需要旋转的的距离
		
		float targetFrom = 4*360 +from;//----要是想点击停止之后，多转一会，就把n变大    n*360 +from
		float targetEnd= 4*360+ end;
		
		/*
		 * <pre>
		 * v1---->0;速度每次减1
		 * (v1+0)*(v1+1)/2=targetfrom.
		 * v1*v1+v1-2*tartFrom=0   求解
		 * v1=(-1+Math.sqrt(1-4*1*(-2)*tartFrom))/2;
		 * <pre>
		 */
		float  v1=(float) ((-1+Math.sqrt(1+8*targetFrom))/2);//以这个速度转，恰巧刚进到箭头处。
		float  v2=(float) ((-1+Math.sqrt(1+8*targetEnd))/2);//以这个速度转，恰巧刚要走过头了箭头处
		//所以，为了让它更可信，速度控制在二者之间是最好的。随机他们之间的一个速度就好。
		
		
		mSpeed=v1+Math.random()*(v2-v1);
		
//		mSpeed=50;
		isShouldEnd=false;
	}
	
	public void luckyEnd(){
		isShouldEnd=true;
		mStartAngle=0;
		//作用：因为你之前的，计算每一项的中奖范围。---根据初始状态来算。为了避免出错，就要先恢复到初始状态之后，再转   n*360 +from-->n*360 +end之间的一个弧度距离
	}
	/**
	 * 判断转盘是否在转
	 * @return
	 */
	public boolean isStart(){
		return mSpeed!=0;
	}
	
	public boolean isShouldEnd(){
		return isShouldEnd;
	}
	
	
	
	/**
	 * 绘制每个扇形区域上的图标
	 * @param tmpAngle
	 * @param bitmap
	 */
	private void drawIcon(float tmpAngle, Bitmap bitmap) {
		
		//设置图片的宽度是直径的1/8
		 int imgWidth=mRadaios/8;
		 
		 //math.pi/180--角度和弧度的转换。
		 float angle = (float) ((tmpAngle+360/mItemCount/2)*Math.PI/180);
		 
		 //图片的中心点的坐标
		 int x = (int) (mCenter+mRadaios/2/2*Math.cos(angle)) ; 
		 int y=(int) (mCenter+mRadaios/2/2*Math.sin(angle)) ; 
		
		 //确定图片位置，给它一个范围。
		 Rect rect=new Rect(x-imgWidth/2, y-imgWidth/2, x+imgWidth/2, y+imgWidth/2);
		 mCanvas.drawBitmap(bitmap, null, rect,null);
	}


	/**
	 * 绘制每个盘块的弧形文本
	 * @param tmpAngle
	 * @param sweepAngle
	 * @param string
	 */
	private void drawText(float tmpAngle, float sweepAngle, String string) {
		Path path=new Path();
		path.addArc(mRange, tmpAngle, sweepAngle);
		
		//利用水平偏移量，让文字居中。
		float textWidth=mtextPaint.measureText(string);
		int hOffset=(int) (mRadaios*Math.PI/mItemCount/2-textWidth/2);
		int vOffset=mRadaios/2/6;//竖直偏移量，可以自己定义就行，看着好看就行。
		mCanvas.drawTextOnPath(string, path, hOffset, vOffset, mtextPaint);
	}

	
	

	/**
	 * 绘制背景
	 */
	private void drawBg() {
		mCanvas.drawColor(0xFFFFFFFF);// 背景se
		// 我想让它的距离左边和上边的距离为padding/2..所以就在下面写了。。这样的话，自己背景图片无论大小都会显示在它指定的范围内了？应该是。
		mCanvas.drawBitmap(mGbBirtmap, null, new Rect(mPadding / 2,
				mPadding / 2, getMeasuredWidth() - mPadding / 2,
				getMeasuredHeight() - mPadding / 2), null);
	}

	

}
