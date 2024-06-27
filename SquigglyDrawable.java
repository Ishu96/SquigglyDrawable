package com.brar.squigglydrawable;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;

import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.view.animation.LinearInterpolator;
import android.view.animation.PathInterpolator;
import androidx.annotation.FloatRange;

public class SquigglyDrawable extends Drawable {
	
	public static float TWO_PI = (float) (Math.PI * 2f);
	public static float SEGMENTS_PER_WAVELENGTH = 10;
	
	public float waveLength = 72f;
	public float lineAmplitude = 8f;
	public float lineAmplitudeSpeed = .25f;
	public float phaseSpeed = .5f;
	public float phaseOffset = 0f;
	public float strokeWidth = 8f;
	
	private Paint paint = new Paint();
	private Path squigglyPath = new Path();
	
	private boolean animate = true;
	
	private ValueAnimator phaseAnimator;
	
	public SquigglyDrawable() {
		paint.setStyle(Paint.Style.STROKE);
		paint.setColor(Color.BLUE);
		paint.setStrokeWidth(strokeWidth);
		paint.setStrokeCap(Paint.Cap.ROUND);
		paint.setStrokeJoin(Paint.Join.ROUND);
		
		initPhaseAnimator();
	}
	//Copied and Translated to java From [saket/extended-spans](https://github.com/saket/extended-spans)
	@Override
	public void draw(Canvas canvas) {
		Rect bounds = getBounds();
		
		float lineStart = bounds.left + strokeWidth;
		float lineEnd = (bounds.right - strokeWidth);
		float centerY = bounds.bottom / 2f;
		
		float segmentWidth = waveLength / SEGMENTS_PER_WAVELENGTH;
		int numOfPoints = (int) Math.ceil((lineEnd - lineStart) / segmentWidth) + 1;
		float pointX = lineStart;
		
		for (int point = 0; point < numOfPoints; point++) {
			float proportionOfWavelength = (pointX - lineStart) / waveLength;
			float radiansX = proportionOfWavelength * TWO_PI + (TWO_PI * phaseOffset);
			float offsetY = centerY + (float) (Math.sin(radiansX) * lineAmplitude);
			if (point == 0) {
				squigglyPath.moveTo(pointX, offsetY);
				} else {
				squigglyPath.lineTo(pointX, offsetY);
			}
			pointX = Math.min(pointX + segmentWidth, lineEnd);
		}
		canvas.drawPath(squigglyPath, paint);
	}
	
	public static float lerp(float start, float stop, float amount) {
		return start + (stop - start) * amount;
	}
	
	@Override
	public void setAlpha(int alpha) {
		paint.setAlpha(alpha);
	}
	
	@Override
	public void setColorFilter(ColorFilter colorFilter) {
		paint.setColorFilter(colorFilter);
	}
	@Deprecated
	@Override
	public int getOpacity() {
		return paint.getAlpha();
	}
	
	void initPhaseAnimator() {
		phaseAnimator = ValueAnimator.ofFloat(0f, 1f);
		phaseAnimator.setDuration(1000);
		phaseAnimator.setInterpolator(new LinearInterpolator());
		phaseAnimator.setRepeatCount(ValueAnimator.INFINITE);
		phaseAnimator.setRepeatMode(ValueAnimator.RESTART);
		phaseAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
			@Override
			public void onAnimationUpdate(ValueAnimator arg) {
				phaseOffset = (float) phaseAnimator.getAnimatedValue();
				squigglyPath.reset();
				invalidateSelf();
			}
		});
		phaseAnimator.start();
	}
	
	public void setWaveLength(float arg){
		this.waveLength = arg;
	}
	
	public void setLineAmplitude(float arg){
		this.lineAmplitude = arg;
	}
	
	public void setPhaseSpeed(float arg){
		this.phaseSpeed = arg;
	}
	
	public void setStrokeWidth(float arg){
		this.strokeWidth = arg;
	}
	
}