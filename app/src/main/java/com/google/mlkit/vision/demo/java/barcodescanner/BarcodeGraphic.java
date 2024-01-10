/*
 * Copyright 2020 Google LLC. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.google.mlkit.vision.demo.java.barcodescanner;

import static java.lang.Math.max;
import static java.lang.Math.min;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;

import com.google.common.primitives.Floats;
import com.google.mlkit.vision.barcode.common.Barcode;
import com.google.mlkit.vision.demo.GraphicOverlay;
import com.google.mlkit.vision.demo.GraphicOverlay.Graphic;
import com.google.mlkit.vision.label.ImageLabel;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Scanner;

/** Graphic instance for rendering Barcode position and content information in an overlay view. */
public class BarcodeGraphic extends Graphic {

  private static final int TEXT_COLOR = Color.BLACK;
  private static final int MARKER_COLOR = Color.WHITE;
  private static final float TEXT_SIZE = 40.0f;
  private static final float STROKE_WIDTH = 4.0f;

  private final Paint textPaint;
  private final Paint labelPaint;
  private final GraphicOverlay overlay;
  private final List<String> items;

  int[] colors = {Color.YELLOW,Color.CYAN,Color.RED,Color.GREEN,Color.MAGENTA};

  BarcodeGraphic(GraphicOverlay overlay, List<String> items) {
    super(overlay);
    this.overlay = overlay;
    this.items = items;
    textPaint = new Paint();
    textPaint.setColor(Color.WHITE);
    textPaint.setTextSize(TEXT_SIZE);

    labelPaint = new Paint();
    labelPaint.setColor(Color.BLACK);
    labelPaint.setStyle(Paint.Style.FILL);
    labelPaint.setAlpha(120);
  }

  //list barcode view

  @Override
  public synchronized void draw(Canvas canvas) {
    float padding = 5;
    float x = padding;
    float y = padding;
    int index = 0;

    for (String item : items) {
      Scanner scanner = new Scanner(item);
      while (scanner.hasNextLine()) {
        String next = scanner.nextLine();
        float maxWidth = textPaint.measureText(next);
        if (y + TEXT_SIZE > overlay.getHeight()) {
          break;
        }
        drawRect(
                canvas,
                x - padding,
                y - padding,
                x + maxWidth + padding,
                y + TEXT_SIZE + padding,
                labelPaint);
        textPaint.setColor(colors[index%colors.length]);
        drawText(canvas, next, x, y + TEXT_SIZE-(padding/2), textPaint);
        y += TEXT_SIZE+(padding*2);
      }
      scanner.close();
      ++index;
    }
  }



  /**
   * Draws the barcode block annotations for position, size, and raw value on the supplied canvas.
   */
  /*
  @Override
  public void draw(Canvas canvas) {
    if (barcode == null) {
      throw new IllegalStateException("Attempting to draw a null barcode.");
    }

    // Draws the bounding box around the BarcodeBlock.
    RectF rect = new RectF(barcode.getBoundingBox());
    // If the image is flipped, the left will be translated to right, and the right to left.
    float x0 = translateX(rect.left);
    float x1 = translateX(rect.right);
    rect.left = min(x0, x1);
    rect.right = max(x0, x1);
    rect.top = translateY(rect.top);
    rect.bottom = translateY(rect.bottom);
    canvas.drawRect(rect, rectPaint);

    // Draws other object info.
    float lineHeight = TEXT_SIZE + (2 * STROKE_WIDTH);
    float textWidth = barcodePaint.measureText(barcode.getDisplayValue());
    canvas.drawRect(
        rect.left - STROKE_WIDTH,
        rect.top - lineHeight,
        rect.left + textWidth + (2 * STROKE_WIDTH),
        rect.top,
        labelPaint);
    // Renders the barcode at the bottom of the box.
    canvas.drawText(barcode.getDisplayValue(), rect.left, rect.top - STROKE_WIDTH, barcodePaint);
  }
  */
}
