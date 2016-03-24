package com.badoo.chateau.example.data.repos.messages;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.badoo.chateau.example.data.util.ParseUtils;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;

import java.io.Closeable;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class ImageUploadService extends IntentService {

    private static final String TAG = "ImageUploadService";

    private static final int BUFFER_SIZE = 16 * 1024; // 16k

    private static final String KEY_LOCAL_MESSAGE_ID = ImageUploadService.class.getName() + ":localId";
    private static final String KEY_IMAGE_URI = ImageUploadService.class.getName() + ":imageUri";

    public static Intent createIntent(@NonNull Context context, @NonNull String localMessageId, @NonNull Uri imageUri) {
        final Intent intent = new Intent(context, ImageUploadService.class);
        intent.putExtra(KEY_LOCAL_MESSAGE_ID, localMessageId);
        intent.putExtra(KEY_IMAGE_URI, imageUri);
        return intent;
    }

    public ImageUploadService() {
        super(TAG);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        final String localMessageId = intent.getStringExtra(KEY_LOCAL_MESSAGE_ID);
        final Uri uri = intent.getParcelableExtra(KEY_IMAGE_URI);

        // TODO: Put shortcut in if already file
        Log.d(TAG, "Starting upload of " + uri + " for message " + localMessageId);
        InputStream is = null;
        OutputStream os = null;
        File cacheFile = null;
        try {
            is = getApplicationContext().getContentResolver().openInputStream(uri);
            if (is == null) {
                Log.e(TAG, "Unable to open input stream from uri " + uri.toString());
                return;
            }

            cacheFile = getCacheFile();
            os = new FileOutputStream(cacheFile);
            pipeStream(is, os);
            saveParseFile(localMessageId, cacheFile);
        }
        catch (IOException e) {
            Log.e(TAG, "Unable to pipe uri to temp file " + uri.toString(), e);
        }
        finally {
            silentClose(is);
            silentClose(os);
            if (cacheFile != null) {
                cacheFile.delete();
            }
        }
    }

    @NonNull
    private File getCacheFile() throws IOException {
        final File cacheDir = getApplicationContext().getCacheDir();
        return File.createTempFile("ius", null, cacheDir);
    }

    private void pipeStream(@NonNull InputStream is, @NonNull OutputStream os) throws IOException {
        final byte[] buffer = new byte[BUFFER_SIZE];
        int len;
        while ((len = is.read(buffer)) != -1) {
            os.write(buffer, 0, len);
        }
    }

    private void saveParseFile(@NonNull String localMessageId, @NonNull File cacheFile) {
        final ParseObject image = ParseObject.create(ParseUtils.ImagesTable.NAME);
        try {
            image.put(ParseUtils.ImagesTable.Fields.IMAGE, new ParseFile(cacheFile));
            image.put(ParseUtils.ImagesTable.Fields.LOCAL_MESSAGE_ID, localMessageId);
            image.save();
            Log.d(TAG, "Saved file to Parse cloud");
        }
        catch (ParseException e) {
            Log.e(TAG, "Unable to save parse object " + image, e);
        }
    }

    private void silentClose(@Nullable Closeable closeable) {
        if (closeable == null) return;
        try {
            closeable.close();
        }
        catch (IOException e) {
            // Ignore
        }
    }
}
