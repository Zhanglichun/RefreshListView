package com.example.refreshlistview;

import java.text.SimpleDateFormat;
import java.util.Date;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.RotateAnimation;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

public class RefreshListView extends ListView implements OnScrollListener{

	private View header;
	private View footer;
	private int headerHeight;
	private int firstVisibleItem;
	
	private int lastVisibleItem;
	private int totalCount;
	
	private int scrollState;
	
	private int state;
	private final int STATE_NORMAL = 0;
	private final int STATE_PULL = 1;
	private final int STATE_RELEASE = 2;
	private final int STATE_REFRESHING = 3;
	
	public RefreshListView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		// TODO Auto-generated constructor stub
		initView(context);
	}

	public RefreshListView(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
		initView(context);
	}

	public RefreshListView(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
		initView(context);
	}
	
	/**
	 * 添加header
	 * @param context
	 */
	private void initView(Context context){
		LayoutInflater inflater = LayoutInflater.from(context);
		header = inflater.inflate(R.layout.header, null);
		measureView(header);
		headerHeight = header.getMeasuredHeight();
		topPadding(-headerHeight);
		this.addHeaderView(header);
		
		footer = inflater.inflate(R.layout.footer, null);
		this.addFooterView(footer);
		footer.findViewById(R.id.load).setVisibility(View.GONE);
		
		this.setOnScrollListener(this);
	}
	
	/**
	 * 通知父布局  占用的宽和高
	 * @param view
	 */
	private void measureView(View view){
		android.view.ViewGroup.LayoutParams p = view.getLayoutParams();
		if (p == null) {
			p = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
		}
		int width = ViewGroup.getChildMeasureSpec(0, 0, p.width);
		int height;
		int tempHeight = p.height;
		if (tempHeight > 0) {
			height = MeasureSpec.makeMeasureSpec(tempHeight, MeasureSpec.EXACTLY);
		}else{
			height = MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED);
		}
		view.measure(width, height);
	}
	
	/**
	 * 设置header的上边距
	 * @param padding
	 */
	private void topPadding(int padding){
		header.setPadding(header.getPaddingLeft(), padding, 
				header.getPaddingRight(), header.getPaddingBottom());
		header.invalidate();
	}

	//判断滚动到了哪一个
	@Override
	public void onScroll(AbsListView arg0, int arg1, int arg2, int arg3) {
		// TODO Auto-generated method stub
		firstVisibleItem = arg1;
		
		lastVisibleItem = arg1 + arg2;
		
		totalCount = arg3;
	}

	@Override
	public void onScrollStateChanged(AbsListView arg0, int arg1) {
		// TODO Auto-generated method stub
		scrollState = arg1;
		if (totalCount == lastVisibleItem && scrollState == SCROLL_STATE_IDLE) {
			
			if (!isLoading) {
				isLoading = true;
				footer.findViewById(R.id.load).setVisibility(View.VISIBLE);
				
				iRefreshListener.onLoad();
				//加载更多
			}
		}
	}
	
	boolean isLoading;
	boolean isRemark;
	int startY;
	@Override
	public boolean onTouchEvent(MotionEvent ev) {
		switch (ev.getAction()) {
		case MotionEvent.ACTION_DOWN:
			if (firstVisibleItem == 0) {
				isRemark = true;
				startY = (int) ev.getY();
			}
			break;
		case MotionEvent.ACTION_MOVE:
			onMove(ev);	
			break;
					
		case MotionEvent.ACTION_UP:
			if (state == STATE_RELEASE) {
				state = STATE_REFRESHING;
				refreshView();
				//加载数据。
				iRefreshListener.onRefresh();
				//refreshComplete();
				
			}else if(state == STATE_PULL){
				state = STATE_NORMAL;
				isRemark = false;
				refreshView();
			}
			break;

		default:
			break;
		}
		return super.onTouchEvent(ev);
	}
	
	/**
	 * 
	 */
	private void onMove(MotionEvent ev){
		if (!isRemark) {
			return;
		}
		
		int tempY = (int) ev.getY();
		int space = tempY - startY;
		
		int topPadding = space - headerHeight;
		
		switch(state){
		case STATE_NORMAL:
			if(space > 0){
				state = STATE_PULL;
				refreshView();
			}
			break;
			
		case STATE_PULL:
			topPadding(topPadding);
			if (space > headerHeight + 30 && scrollState == SCROLL_STATE_TOUCH_SCROLL) {
				state = STATE_RELEASE;
				refreshView();
			}
			break;
		
		case STATE_RELEASE:
			topPadding(topPadding);
			if (space < headerHeight + 30) {
				state = STATE_PULL;
			}else if(space <= 0){
				state = STATE_NORMAL;
				isRemark = false;
				refreshView();
			}
			break;
		}
	}
	
	private void refreshView(){
		TextView tip = (TextView) header.findViewById(R.id.tip);
		ImageView img = (ImageView) header.findViewById(R.id.arrow);
		ProgressBar pBar = (ProgressBar) header.findViewById(R.id.progress);
		
		RotateAnimation animation_1 = new RotateAnimation(0, 180, RotateAnimation.RELATIVE_TO_SELF, 0.5f, 
				RotateAnimation.RELATIVE_TO_SELF, 0.5f);
		
		RotateAnimation animation_2 = new RotateAnimation(180, 0, RotateAnimation.RELATIVE_TO_SELF, 0.5f, 
				RotateAnimation.RELATIVE_TO_SELF, 0.5f);
		animation_1.setDuration(500);
		animation_1.setFillAfter(true);
		
		animation_2.setDuration(500);
		animation_2.setFillAfter(true);
		
		
		switch (state) {
			case STATE_NORMAL:
				topPadding(-headerHeight);
				break;
				
			case STATE_PULL:
				img.setVisibility(View.VISIBLE);
				pBar.setVisibility(View.GONE);
				tip.setText("下拉可以刷新");
				
//				img.clearAnimation();
//				img.setAnimation(animation_1);
				break;
			
			case STATE_RELEASE:
				img.setVisibility(View.VISIBLE);
				pBar.setVisibility(View.GONE);
				tip.setText("松开可以刷新");
				
				img.clearAnimation();
				img.setAnimation(animation_1);
				break;
				
			case STATE_REFRESHING:
				topPadding(50);
				img.setVisibility(View.GONE);
				pBar.setVisibility(View.VISIBLE);
				tip.setText("正在刷新");
				
				img.clearAnimation();
				
				break;
		default:
			break;
		}
	}
	
	/**
	 *获取完数据 
	 */
	public void refreshComplete(){
		state = STATE_NORMAL;
		refreshView();
		isRemark = false;
		TextView timeTextView = (TextView) header.findViewById(R.id.time);
		SimpleDateFormat format = new SimpleDateFormat("MM-dd hh:mm:ss");
		timeTextView.setText(format.format(new Date()));
	}
	
	public void loadComplete(){
		isLoading = false;
		footer.findViewById(R.id.load).setVisibility(View.GONE);
	}
	
	private IRefreshListener iRefreshListener;

	public IRefreshListener getiRefreshListener() {
		return iRefreshListener;
	}

	public void setiRefreshListener(IRefreshListener iRefreshListener) {
		this.iRefreshListener = iRefreshListener;
	}

	public interface IRefreshListener{
		void onRefresh();
		void onLoad();
	}

}
