/*

 */
package com.maxistar.mangabrowser;



/**
 * @author Jason Polites
 *
 */
public class SimpleAnimation implements Animation {

	private boolean firstFrame = true;

	
	private float touchX;
	private float touchY;
	
	private float zoom;
	
	private float startX;
	private float startY;
	private float startScale;
	
	private float xDiff;
	private float yDiff;
	private float scaleDiff;
	
	private long animationLengthMS = 200;
	private long totalTime = 0;
	
	private SimpleAnimationListener zoomAnimationListener;

	/* (non-Javadoc)
	 * @see com.polites.android.Animation#update(com.polites.android.GestureImageView, long)
	 */
	@Override
	public boolean update() {
			if(zoomAnimationListener != null) {
				zoomAnimationListener.onZoom(0, 0, 0);
				zoomAnimationListener.onComplete();
			}
			return true;
	}
	
	public void reset() {
		firstFrame = true;
		totalTime = 0;
	}

	public float getZoom() {
		return zoom;
	}
	
	public void setZoom(float zoom) {
		this.zoom = zoom;
	}
	
	public float getTouchX() {
		return touchX;
	}
	
	public void setTouchX(float touchX) {
		this.touchX = touchX;
	}
	
	public float getTouchY() {
		return touchY;
	}
	
	public void setTouchY(float touchY) {
		this.touchY = touchY;
	}
	
	public long getAnimationLengthMS() {
		return animationLengthMS;
	}
	
	public void setAnimationLengthMS(long animationLengthMS) {
		this.animationLengthMS = animationLengthMS;
	}
	
	public SimpleAnimationListener getZoomAnimationListener() {
		return zoomAnimationListener;
	}
	
	public void setZoomAnimationListener(SimpleAnimationListener zoomAnimationListener) {
		this.zoomAnimationListener = zoomAnimationListener;
	}
}
