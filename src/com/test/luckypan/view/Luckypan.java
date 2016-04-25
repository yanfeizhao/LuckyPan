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
	// ���ڻ��Ƶ����߳�
	private Thread thread;
	private boolean mIsRunning;// �߳̿��ƿ��ء��ж��߳̿�ʼ�˻��ǽ����ˡ�

	// �̿�Ľ���
	private String[] mNames = new String[] { "�������", "IPad", "��ϲ����", "IPhone",
			"��װһ��", "��ϲ����" };
	private int[] mImgs = new int[] { R.drawable.danfan, R.drawable.ipad,
			R.drawable.f040, R.drawable.iphone, R.drawable.meizi,
			R.drawable.f015 };
	// ��ͼƬ��Ӧ��bitmap����
	private Bitmap[] mImagBitmap;
	// �̿����ɫ
	private int[] mColors = new int[] { 0xffffc300, 0xfff17e01, 0xffffc300,
			0xfff17e01, 0xffffc300, 0xfff17e01 };

	private int mItemCount = 6;

	// �����̿�ķ�Χ,���η�Χ
	private RectF mRange;
	// Բ��ֱ��
	private int mRadaios;

	private Paint mArcPaint;// ����Բ�̵Ļ���
	private Paint mtextPaint;// �����ı��Ļ���

	private double mSpeed;// Բ�̹����ٶȡ�Ĭ����0
	private volatile float mStartAngle = 0;

	private boolean isShouldEnd;// �ж��Ƿ�����ֹͣ��ť��

	private int mCenter;// Բ�̵�����λ��
	private int mPadding;// ȥ�ĸ�padding�е���Сֵ��������ֱ����paddingleftΪ׼��

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
		// ���ó���
		setKeepScreenOn(true);
	}

	
	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		
		int width = Math.min(getMeasuredWidth(), getMeasuredHeight());//��ΪҪ��һ�������Σ�����ȡС��
		mPadding = getPaddingLeft();
		mRadaios = width - mPadding * 2;// ֱ��
		mCenter = width / 2;// ���ĵ�,
		setMeasuredDimension(width, width);
	}
	
	
	@Override
	public void surfaceCreated(SurfaceHolder holder) {

		// ��ʼ������Բ�̻���
		mArcPaint = new Paint();
		mArcPaint.setAntiAlias(true);//���û��ʵľ��Ч����
		mArcPaint.setDither(true);//ʹ��ͼ�񶶶�����ʹ�û�������ͼƬ�����⻬

		// ��ʼ�������ı�����
		mtextPaint = new Paint();
		mtextPaint.setColor(0xffffffff);
		mtextPaint.setTextSize(mTextSize);

		// ��ʼ���̵Ļ��Ƶķ�Χ��˭�ķ�Χ����
		mRange = new RectF(mPadding, mPadding, mPadding + mRadaios, mPadding
				+ mRadaios);

		// ��ʼ��ͼƬ
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
			// ���ƺ�ÿ50ms����һ�Ρ�
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
				// draw sth ��ʼ���ƣ�Բ�̡�
				drawBg();
				//�����̿�
				float tmpAngle=mStartAngle;
				float sweepAngle=360/mItemCount;
				
				for(int i=0;i<mItemCount;i++){
					mArcPaint.setColor(mColors[i]);
					
					//�����̿顣
					mCanvas.drawArc(mRange, tmpAngle, sweepAngle, true, mArcPaint);
					
					//�����ı�
					drawText(tmpAngle,sweepAngle,mNames[i]);
					
					//����ÿ���̿��ͼƬ��
					drawIcon(tmpAngle,mImagBitmap[i]);
					
					tmpAngle+=sweepAngle;
				}
				
				mStartAngle +=mSpeed;//ֻҪspeed��ֵ������0���ͻ�ת������ÿ����һ�Σ���������ˣ��ͻ���˴������ת������ÿ�λ��Ƽ����ʱ����ǱȽ�С��
				
				//��������ֹͣ��ť��
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
				mSurfaceHolder.unlockCanvasAndPost(mCanvas);// �ͷ�canvas��
			}
		}
	}
	
	/**
	 * �������ת��
	 */
	public void luckyStart(int  index){
		
		//����ÿһ��ĽǶ�
		float angle=360/mItemCount;
		
		//����ÿһ����н���Χ��---���ݳ�ʼ״̬���㡣
		//0:  210--->270  ת�̴ӳ�ʼλ����ת360*n + ��210---270֮������һ�����������õ�0��Ԫ�أ���������ѡ�С�
		//1��  150--->210
		float from =270-(index+1)*angle;
		float end=from + angle;
		
		//����ͣ������Ҫ��ת�ĵľ���
		
		float targetFrom = 4*360 +from;//----Ҫ������ֹ֮ͣ�󣬶�תһ�ᣬ�Ͱ�n���    n*360 +from
		float targetEnd= 4*360+ end;
		
		/*
		 * <pre>
		 * v1---->0;�ٶ�ÿ�μ�1
		 * (v1+0)*(v1+1)/2=targetfrom.
		 * v1*v1+v1-2*tartFrom=0   ���
		 * v1=(-1+Math.sqrt(1-4*1*(-2)*tartFrom))/2;
		 * <pre>
		 */
		float  v1=(float) ((-1+Math.sqrt(1+8*targetFrom))/2);//������ٶ�ת��ǡ�ɸս�����ͷ����
		float  v2=(float) ((-1+Math.sqrt(1+8*targetEnd))/2);//������ٶ�ת��ǡ�ɸ�Ҫ�߹�ͷ�˼�ͷ��
		//���ԣ�Ϊ�����������ţ��ٶȿ����ڶ���֮������õġ��������֮���һ���ٶȾͺá�
		
		
		mSpeed=v1+Math.random()*(v2-v1);
		
//		mSpeed=50;
		isShouldEnd=false;
	}
	
	public void luckyEnd(){
		isShouldEnd=true;
		mStartAngle=0;
		//���ã���Ϊ��֮ǰ�ģ�����ÿһ����н���Χ��---���ݳ�ʼ״̬���㡣Ϊ�˱��������Ҫ�Ȼָ�����ʼ״̬֮����ת   n*360 +from-->n*360 +end֮���һ�����Ⱦ���
	}
	/**
	 * �ж�ת���Ƿ���ת
	 * @return
	 */
	public boolean isStart(){
		return mSpeed!=0;
	}
	
	public boolean isShouldEnd(){
		return isShouldEnd;
	}
	
	
	
	/**
	 * ����ÿ�����������ϵ�ͼ��
	 * @param tmpAngle
	 * @param bitmap
	 */
	private void drawIcon(float tmpAngle, Bitmap bitmap) {
		
		//����ͼƬ�Ŀ����ֱ����1/8
		 int imgWidth=mRadaios/8;
		 
		 //math.pi/180--�ǶȺͻ��ȵ�ת����
		 float angle = (float) ((tmpAngle+360/mItemCount/2)*Math.PI/180);
		 
		 //ͼƬ�����ĵ������
		 int x = (int) (mCenter+mRadaios/2/2*Math.cos(angle)) ; 
		 int y=(int) (mCenter+mRadaios/2/2*Math.sin(angle)) ; 
		
		 //ȷ��ͼƬλ�ã�����һ����Χ��
		 Rect rect=new Rect(x-imgWidth/2, y-imgWidth/2, x+imgWidth/2, y+imgWidth/2);
		 mCanvas.drawBitmap(bitmap, null, rect,null);
	}


	/**
	 * ����ÿ���̿�Ļ����ı�
	 * @param tmpAngle
	 * @param sweepAngle
	 * @param string
	 */
	private void drawText(float tmpAngle, float sweepAngle, String string) {
		Path path=new Path();
		path.addArc(mRange, tmpAngle, sweepAngle);
		
		//����ˮƽƫ�����������־��С�
		float textWidth=mtextPaint.measureText(string);
		int hOffset=(int) (mRadaios*Math.PI/mItemCount/2-textWidth/2);
		int vOffset=mRadaios/2/6;//��ֱƫ�����������Լ�������У����źÿ����С�
		mCanvas.drawTextOnPath(string, path, hOffset, vOffset, mtextPaint);
	}

	
	

	/**
	 * ���Ʊ���
	 */
	private void drawBg() {
		mCanvas.drawColor(0xFFFFFFFF);// ����se
		// ���������ľ�����ߺ��ϱߵľ���Ϊpadding/2..���Ծ�������д�ˡ��������Ļ����Լ�����ͼƬ���۴�С������ʾ����ָ���ķ�Χ���ˣ�Ӧ���ǡ�
		mCanvas.drawBitmap(mGbBirtmap, null, new Rect(mPadding / 2,
				mPadding / 2, getMeasuredWidth() - mPadding / 2,
				getMeasuredHeight() - mPadding / 2), null);
	}

	

}
