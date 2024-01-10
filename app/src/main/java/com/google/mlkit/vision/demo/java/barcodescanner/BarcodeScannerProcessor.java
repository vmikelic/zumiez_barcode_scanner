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

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Point;
import android.util.Log;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.google.android.gms.tasks.Task;
import com.google.android.odml.image.MlImage;
import com.google.mlkit.vision.barcode.BarcodeScanner;
import com.google.mlkit.vision.barcode.BarcodeScannerOptions;
import com.google.mlkit.vision.barcode.BarcodeScanning;
import com.google.mlkit.vision.barcode.ZoomSuggestionOptions;
import com.google.mlkit.vision.barcode.ZoomSuggestionOptions.ZoomCallback;
import com.google.mlkit.vision.barcode.common.Barcode;
import com.google.mlkit.vision.common.InputImage;
import com.google.mlkit.vision.demo.GraphicOverlay;
import com.google.mlkit.vision.demo.java.VisionProcessorBase;
import com.google.mlkit.vision.demo.java.labeldetector.LabelGraphic;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import java.util.LinkedList;
import java.util.Objects;
import java.util.Scanner;
import java.util.regex.Pattern;

/** Barcode Detector Demo. */
public class BarcodeScannerProcessor extends VisionProcessorBase<List<Barcode>> {

  private static final String TAG = "BarcodeProcessor";

  private final BarcodeScanner barcodeScanner;

  HashMap<String, String> info = new HashMap<>();
  List<String> items = new ArrayList<>();

  public void readItems(String fileDir) {
    Pattern infoPattern = Pattern.compile("\r\n\r\n");
    Pattern skuPattern = Pattern.compile("\r\n");
    File infof = new File(fileDir+"/CombinedInfo.txt");
    File skuf = new File(fileDir+"/CombinedSKU.txt");
    Scanner r;
    Scanner rr;
    try {
      r = new Scanner(new FileReader(infof));
      r.useDelimiter(infoPattern);
      rr = new Scanner(new FileReader(skuf));
      rr.useDelimiter(skuPattern);
      String infoline;
      String skuline;
      while (true) {
        if ((infoline = r.next()) == null)
          break;
        skuline = rr.next();
        info.put(skuline, infoline);
      }
    } catch (Exception e) {
      e.printStackTrace(); // file not found
    }
  }
  public BarcodeScannerProcessor(Context context, @Nullable ZoomCallback zoomCallback) {
    super(context);
    BarcodeScannerOptions options =
    new BarcodeScannerOptions.Builder()
            .setBarcodeFormats(Barcode.FORMAT_ITF)
            .build();
    barcodeScanner = BarcodeScanning.getClient(options);

    readItems(String.valueOf(context.getFilesDir()));
  }

  @Override
  public void stop() {
    super.stop();
    barcodeScanner.close();
  }

  @Override
  protected Task<List<Barcode>> detectInImage(InputImage image) {
    return barcodeScanner.process(image);
  }

  @Override
  protected void onSuccess(
      @NonNull List<Barcode> barcodes, @NonNull GraphicOverlay graphicOverlay) {
    if(!barcodes.isEmpty()){
      for (Barcode barcode : barcodes) {
        if(Objects.requireNonNull(barcode.getDisplayValue()).length() != 14 &&
                Objects.requireNonNull(barcode.getDisplayValue()).length() != 6 )
          continue;

        String six = barcode.getDisplayValue().substring(0,6);

        if(info.containsKey(barcode.getDisplayValue()))
          if(!items.contains(info.get(barcode.getDisplayValue()))){
            items.add(info.get(barcode.getDisplayValue()));
            continue;
          }
        if(info.containsKey(six))
          if(!items.contains(info.get(six)))
            items.add(info.get(six));
      }

      graphicOverlay.add(new BarcodeGraphic(graphicOverlay, items));
    }
  }

  private static void logExtrasForTesting(Barcode barcode) {
    if (barcode != null) {
      if (barcode.getBoundingBox() != null) {
        Log.v(
            MANUAL_TESTING_LOG,
            String.format(
                "Detected barcode's bounding box: %s", barcode.getBoundingBox().flattenToString()));
      }
      if (barcode.getCornerPoints() != null) {
        Log.v(
            MANUAL_TESTING_LOG,
            String.format(
                "Expected corner point size is 4, get %d", barcode.getCornerPoints().length));
      }
      for (Point point : barcode.getCornerPoints()) {
        Log.v(
            MANUAL_TESTING_LOG,
            String.format("Corner point is located at: x = %d, y = %d", point.x, point.y));
      }
      Log.v(MANUAL_TESTING_LOG, "barcode display value: " + barcode.getDisplayValue());
      Log.v(MANUAL_TESTING_LOG, "barcode raw value: " + barcode.getRawValue());
      Barcode.DriverLicense dl = barcode.getDriverLicense();
      if (dl != null) {
        Log.v(MANUAL_TESTING_LOG, "driver license city: " + dl.getAddressCity());
        Log.v(MANUAL_TESTING_LOG, "driver license state: " + dl.getAddressState());
        Log.v(MANUAL_TESTING_LOG, "driver license street: " + dl.getAddressStreet());
        Log.v(MANUAL_TESTING_LOG, "driver license zip code: " + dl.getAddressZip());
        Log.v(MANUAL_TESTING_LOG, "driver license birthday: " + dl.getBirthDate());
        Log.v(MANUAL_TESTING_LOG, "driver license document type: " + dl.getDocumentType());
        Log.v(MANUAL_TESTING_LOG, "driver license expiry date: " + dl.getExpiryDate());
        Log.v(MANUAL_TESTING_LOG, "driver license first name: " + dl.getFirstName());
        Log.v(MANUAL_TESTING_LOG, "driver license middle name: " + dl.getMiddleName());
        Log.v(MANUAL_TESTING_LOG, "driver license last name: " + dl.getLastName());
        Log.v(MANUAL_TESTING_LOG, "driver license gender: " + dl.getGender());
        Log.v(MANUAL_TESTING_LOG, "driver license issue date: " + dl.getIssueDate());
        Log.v(MANUAL_TESTING_LOG, "driver license issue country: " + dl.getIssuingCountry());
        Log.v(MANUAL_TESTING_LOG, "driver license number: " + dl.getLicenseNumber());
      }
    }
  }

  @Override
  protected void onFailure(@NonNull Exception e) {
    Log.e(TAG, "Barcode detection failed " + e);
  }
}
