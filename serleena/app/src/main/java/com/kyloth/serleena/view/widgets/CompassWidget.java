///////////////////////////////////////////////////////////////////////////////
// 
// This file is part of Serleena.
// 
// The MIT License (MIT)
//
// Copyright (C) 2015 Antonio Cavestro, Gabriele Pozzan, Matteo Lisotto, 
//   Nicola Mometto, Filippo Sestini, Tobia Tesan, Sebastiano Valle.    
//
// Permission is hereby granted, free of charge, to any person obtaining a copy
// of this software and associated documentation files (the "Software"), to 
// deal in the Software without restriction, including without limitation the
// rights to use, copy, modify, merge, publish, distribute, sublicense, and/or
// sell copies of the Software, and to permit persons to whom the Software is
// furnished to do so, subject to the following conditions:
//
// The above copyright notice and this permission notice shall be included in
// all copies or substantial portions of the Software.
//
// THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
// IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
// FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
// AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
// LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING
// FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS
// IN THE SOFTWARE.
//
///////////////////////////////////////////////////////////////////////////////


/**
 * Name: CompassWidget
 * Package: com.kyloth.serleena.view.widgets
 * Author: Sebastiano Valle
 *
 * History:
 * Version   Programmer         Changes
 * 1.0.0     Sebastiano Valle   Creazione del file, scrittura del codice e di Javadoc
 */
package com.kyloth.serleena.view.widgets;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.graphics.PointF;
import android.graphics.RadialGradient;
import android.graphics.RectF;
import android.graphics.Shader;
import android.util.AttributeSet;
import android.view.View;
import android.view.accessibility.AccessibilityEvent;
import android.widget.Toast;

import com.kyloth.serleena.R;

/**
 * Classe che implementa il widget presente nella visuale Bussola della schermata Esperienza
 *
 * @field bearing : float inclinazione della bussola
 * @field markerPaint : Paint modo con cui vengono colorate le tacche sulla bussola
 * @field textPaint : Paint modo con cui viene colorato il testo sulla bussola
 * @field circlePaint : Paint modo con cui viene colorato il contorno del cerchio sulla bussola
 * @field northString : String stringa che viene visualizzata in corrispondenza del Nord
 * @field southString : String stringa che viene visualizzata in corrispondenza del Sud
 * @field eastString : String stringa che viene visualizzata in corrispondenza del Est
 * @field westString : String stringa che viene visualizzata in corrispondenza dell'Ovest
 * @field textHeight : int grandezza del testo
 * @author Sebastiano Valle <valle.sebastiano93@gmail.com>
 * @version 1.0.0
 * @see android.widget.ImageView
 */
public class CompassWidget extends View {

    private float bearing;

    private Paint markerPaint;
    private Paint textPaint;
    private Paint circlePaint;
    private int textHeight;

    private float pitch;
    private float roll;

    private int[] borderGradientColors;
    private float[] borderGradientPositions;

    private int[] glassGradientColors;
    private float[] glassGradientPositions;

    int skyHorizonColorFrom;
    int skyHorizonColorTo;
    int groundHorizonColorFrom;
    int groundHorizonColorTo;

    private enum CompassDirection { N, NNE, NE, ENE,
                                    E, ESE, SE, SSE,
                                    S, SSW, SW, WSW,
                                    W, WNW, NW, NNW }

    public CompassWidget(Context context) {
        super(context);
        initCompassView();
    }

    public CompassWidget(Context context, AttributeSet attrs) {
        super(context,attrs);
        initCompassView();
    }

    public CompassWidget(Context context, AttributeSet attrs,int defaultStyle) {
        super(context,attrs,defaultStyle);
        initCompassView();
    }

    protected void initCompassView() {
        setFocusable(true);

        Resources r = getContext().getResources();

        circlePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        circlePaint.setColor(r.getColor(R.color.background_compass_color));
        circlePaint.setStrokeWidth(1);
        circlePaint.setStyle(Paint.Style.STROKE);

        textPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        textPaint.setColor(r.getColor(R.color.text_compass_color));
        textPaint.setFakeBoldText(true);
        textPaint.setSubpixelText(true);
        textPaint.setTextAlign(Paint.Align.LEFT);

        textHeight = (int) textPaint.measureText("yY");

        markerPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        markerPaint.setColor(r.getColor(R.color.marker_compass_color));
        markerPaint.setAlpha(200);
        markerPaint.setStrokeWidth(1);
        markerPaint.setStyle(Paint.Style.STROKE);
        markerPaint.setShadowLayer(2, 1, 1, r.getColor(R.color.shadow_color));

        borderGradientColors = new int[4];
        borderGradientPositions = new float[4];

        borderGradientColors[3] = r.getColor(R.color.outer_border);
        borderGradientColors[2] = r.getColor(R.color.inner_border_one);
        borderGradientColors[1] = r.getColor(R.color.inner_border_two);
        borderGradientColors[0] = r.getColor(R.color.inner_border);
        borderGradientPositions[3] = 0.0f;
        borderGradientPositions[2] = 1 - .03f;
        borderGradientPositions[1] = 1 - .06f;
        borderGradientPositions[0] = 1.0f;

        glassGradientColors = new int[5];
        glassGradientPositions = new float[5];

        int glassColor = 245;
        glassGradientColors[4] = Color.argb(65,glassColor,glassColor,glassColor);
        glassGradientColors[3] = Color.argb(100,glassColor,glassColor,glassColor);
        glassGradientColors[2] = Color.argb(50,glassColor,glassColor,glassColor);
        glassGradientColors[1] = Color.argb(0,glassColor,glassColor,glassColor);
        glassGradientColors[0] = Color.argb(0,glassColor,glassColor,glassColor);
        glassGradientPositions[4] = 1 - 0.0f;
        glassGradientPositions[3] = 1 - .06f;
        glassGradientPositions[2] = 1 - .1f;
        glassGradientPositions[1] = 1 - .2f;
        glassGradientPositions[0] = 1 - 1.0f;

        skyHorizonColorFrom = r.getColor(R.color.horizon_sky_from);
        skyHorizonColorTo = r.getColor(R.color.horizon_sky_to);
        groundHorizonColorFrom = r.getColor(R.color.horizon_ground_from);
        groundHorizonColorTo = r.getColor(R.color.horizon_ground_to);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int measuredWidth = measure(widthMeasureSpec);
        int d = Math.min(widthMeasureSpec,heightMeasureSpec);
        setMeasuredDimension(d, d);
    }

    private int measure(int measureSpec) {
        int result = 0;
        int specMode = MeasureSpec.getMode(measureSpec);
        int specSize = MeasureSpec.getSize(measureSpec);

        if(specMode == MeasureSpec.UNSPECIFIED)
            result = 100;
        else
            result = specSize;

        return result;
    }

    public void setBearing(float _bearing) {
        bearing = _bearing;
        sendAccessibilityEvent(AccessibilityEvent.TYPE_VIEW_TEXT_CHANGED);
    }

    public float getBearing() {
        return bearing;
    }

    @Override
    public void onDraw(Canvas canvas){
        float ringWidth = textHeight + 4;

        int width = getMeasuredWidth();
        int height = getMeasuredHeight();

        int px = width / 2;
        int py = height / 2;
        Point center = new Point(px,py);

        int radius = Math.min(px,py);

        RectF boundingBox = new RectF(center.x - radius,
                center.y - radius,
                center.x + radius,
                center.y + radius);

        RectF innerBoundingBox = new RectF(center.x - radius + ringWidth,
                                           center.y - radius + ringWidth,
                                           center.x + radius - ringWidth,
                                           center.y + radius - ringWidth);
        float innerRadius = innerBoundingBox.height()/2;

        RadialGradient borderGradient = new RadialGradient(px, py, radius,
                borderGradientColors,borderGradientPositions, Shader.TileMode.CLAMP);

        Paint pgb = new Paint();
        pgb.setShader(borderGradient);

        Path outerRingPath = new Path();
        outerRingPath.addOval(boundingBox, Path.Direction.CW);

        canvas.drawPath(outerRingPath, pgb);

        LinearGradient skyShader = new LinearGradient(center.x,
                innerBoundingBox.top, center.x, innerBoundingBox.bottom,
                skyHorizonColorFrom, skyHorizonColorTo, Shader.TileMode.CLAMP);

        Paint skyPaint = new Paint();
        skyPaint.setShader(skyShader);

        LinearGradient groundShader = new LinearGradient(center.x,
                innerBoundingBox.top, center.x, innerBoundingBox.bottom,
                groundHorizonColorFrom, groundHorizonColorTo, Shader.TileMode.CLAMP);

        Paint groundPaint = new Paint();
        groundPaint.setShader(groundShader);

        float tiltDegree = pitch;
        while(tiltDegree > 90 || tiltDegree < -90) {
            if(tiltDegree > 90) tiltDegree = -90 + (tiltDegree - 90);
            if(tiltDegree < -90) tiltDegree = 90 - (tiltDegree + 90);
        }

        float rollDegree = roll;
        while (rollDegree > 180 || rollDegree < -180) {
            if(rollDegree > 180) rollDegree = -180 + (rollDegree - 180);
            if(rollDegree < -180) rollDegree = 180 - (rollDegree + 180);
        }

        Path skyPath = new Path();
        skyPath.addArc(innerBoundingBox, -tiltDegree, (180 + (2 * tiltDegree)));

        canvas.save();
        canvas.rotate(-bearing, px, py);
        canvas.rotate(-rollDegree, px, py);
        canvas.drawOval(innerBoundingBox, groundPaint);
        canvas.drawPath(skyPath, skyPaint);
        canvas.drawPath(skyPath,markerPaint);

        int markWidth = radius / 3;
        int startX = center.x - markWidth;
        int endX = center.x + markWidth;

        double h = innerRadius * Math.cos(Math.toRadians(90 - tiltDegree));
        double justTiltY = center.y - h;

        float pxPerDegree = (innerBoundingBox.height()/2)/45f;

        for(int i = 90; i >= -90; i -= 10) {
            double ypos = justTiltY + i * pxPerDegree;

            if((ypos < (innerBoundingBox.top + textHeight)) ||
               (ypos > innerBoundingBox.bottom - textHeight))
                continue;

            canvas.drawLine(startX, (float) ypos, endX, (float) ypos, markerPaint);
            int displayPos = (int) (tiltDegree - i);
            String displayString = String.valueOf(displayPos);
            float stringSizeWidth = textPaint.measureText(displayString);
            canvas.drawText(displayString,
                            (int) (center.x - stringSizeWidth/2),
                            (int) (ypos)+1,
                            textPaint);
        }

        markerPaint.setStrokeWidth(2);
        canvas.drawLine(center.x - radius / 2,
                (float) justTiltY,
                center.x + radius / 2,
                (float) justTiltY,
                markerPaint);
        markerPaint.setStrokeWidth(1);

        Path rollArrow = new Path();
        rollArrow.moveTo(center.x - 3, (int) innerBoundingBox.top + 14);
        rollArrow.lineTo(center.x, (int) innerBoundingBox.top + 10);
        rollArrow.moveTo(center.x + 3, innerBoundingBox.top + 14);
        rollArrow.lineTo(center.x, innerBoundingBox.top + 10);
        canvas.drawPath(rollArrow, markerPaint);

        String rollText = String.valueOf(rollDegree);
        double rollTextWidth = textPaint.measureText(rollText);
        canvas.drawText(rollText,
                (float) (center.x - rollTextWidth / 2),
                innerBoundingBox.top + textHeight + 2,
                textPaint);

        canvas.restore();
        canvas.save();
        canvas.rotate(-bearing, px, py);
        canvas.rotate(180,center.x,center.y);
        for(int i = -180; i < 180; i += 10) {
            if(i % 30 == 0) {
                String rollString = String.valueOf(i * (-1));
                float rollStringWidth = textPaint.measureText(rollString);
                PointF rollStringCenter = new PointF(center.x - rollStringWidth/2,
                        innerBoundingBox.top + 1 +textHeight);
                canvas.drawText(rollString, rollStringCenter.x, rollStringCenter.y, textPaint);
            } else {
                canvas.drawLine(center.x, (int) innerBoundingBox.top,
                                center.x, (int) innerBoundingBox.top + 5,
                                markerPaint);
            }
            canvas.rotate(10, center.x, center.y);
        }
        canvas.restore();

        canvas.save();
        canvas.rotate(-bearing, px, py);
        float increment = 22.5f;
        for(float i = 0; i < 360; i += increment) {
            CompassDirection cd = CompassDirection.values()[(int) (i / 22.5)];
            String headString = cd.toString();
            float headStringWidth = textPaint.measureText(headString);
            PointF headStringCenter = new PointF(center.x - headStringWidth / 2,
                                                 boundingBox.top + 1 + textHeight);
            if(i % increment == 0)
                canvas.drawText(headString, headStringCenter.x, headStringCenter.y, textPaint);
            else
                canvas.drawLine(center.x, (int) boundingBox.top,
                                center.x, (int) boundingBox.top + 3,
                                markerPaint);
            canvas.rotate((int) increment, center.x, center.y);
        }
        canvas.restore();
        canvas.save();

        RadialGradient glassShader = new RadialGradient(px, py, (int) innerRadius,
                glassGradientColors, glassGradientPositions, Shader.TileMode.CLAMP);
        Paint glassPaint = new Paint();
        glassPaint.setShader(glassShader);
        canvas.drawOval(innerBoundingBox, glassPaint);

        canvas.drawOval(boundingBox, circlePaint);
        circlePaint.setStrokeWidth(2);
        canvas.drawOval(innerBoundingBox, circlePaint);
        canvas.restore();
    }

    @Override
    public boolean dispatchPopulateAccessibilityEvent(final AccessibilityEvent event) {
        super.dispatchPopulateAccessibilityEvent(event);

        if(isShown()) {
            String bearingStr = String.valueOf(bearing);
            if(bearingStr.length() > AccessibilityEvent.MAX_TEXT_LENGTH)
                bearingStr = bearingStr.substring(0, AccessibilityEvent.MAX_TEXT_LENGTH);

            event.getText().add(bearingStr);
            return true;
        }
        else
            return false;
    }

    public void setPitch(float _pitch) {
        pitch = _pitch;
        sendAccessibilityEvent(AccessibilityEvent.TYPE_VIEW_TEXT_CHANGED);
    }

    public float getPitch() {
        return pitch;
    }

    public void setRoll(float _roll) {
        pitch = _roll;
        sendAccessibilityEvent(AccessibilityEvent.TYPE_VIEW_TEXT_CHANGED);
    }

    public float getRoll() {
        return roll;
    }
}
