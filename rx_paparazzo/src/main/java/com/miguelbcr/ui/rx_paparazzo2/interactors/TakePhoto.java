/*
 * Copyright 2016 Miguel Garcia
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

package com.miguelbcr.ui.rx_paparazzo2.interactors;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.content.FileProvider;
import com.miguelbcr.ui.rx_paparazzo2.entities.TargetUi;
import io.reactivex.Observable;
import io.reactivex.functions.Function;
import java.io.File;
import java.util.List;

public final class TakePhoto extends UseCase<Uri> {
  private final StartIntent startIntent;
  private final TargetUi targetUi;
  private final ImageUtils imageUtils;

  public TakePhoto(StartIntent startIntent, TargetUi targetUi, ImageUtils imageUtils) {
    this.startIntent = startIntent;
    this.targetUi = targetUi;
    this.imageUtils = imageUtils;
  }

  @Override public Observable<Uri> react() {
    final Uri uri = getUri();
    Function<Intent, Uri> revoke = PermissionUtil.createRevokeFileReadWritePermissionsFunction(targetUi, uri);

    return startIntent.with(getIntentCamera(uri)).react().map(revoke);
  }

  private Uri getUri() {
    Context context = targetUi.getContext();
    File file = imageUtils.getPrivateFile(Constants.SHOOT_APPEND);
    String authority = context.getPackageName() + "." + Constants.FILE_PROVIDER;

    return FileProvider.getUriForFile(context, authority, file);
  }

  private Intent getIntentCamera(Uri uri) {
    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
    intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);

    return PermissionUtil.requestReadWritePermission(targetUi, intent, uri);
  }

}