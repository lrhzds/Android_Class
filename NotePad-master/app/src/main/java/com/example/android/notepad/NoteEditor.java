/*
 * Copyright (C) 2007 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.android.notepad;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ImageSpan;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.android.notepad.Utils.ImageUtils;
import com.example.android.notepad.Utils.ScreenUtils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * This Activity handles "editing" a note, where editing is responding to
 * {@link Intent#ACTION_VIEW} (request to view data), edit a note
 * {@link Intent#ACTION_EDIT}, create a note {@link Intent#ACTION_INSERT}, or
 * create a new note from the current contents of the clipboard {@link Intent#ACTION_PASTE}.
 * <p>
 * NOTE: Notice that the provider operations in this Activity are taking place on the UI thread.
 * This is not a good practice. It is only done here to make the code more readable. A real
 * application should use the {@link android.content.AsyncQueryHandler}
 * or {@link android.os.AsyncTask} object to perform operations asynchronously on a separate thread.
 */
public class NoteEditor extends Activity {
    // For logging and debugging purposes
    private static final String TAG = "NoteEditor";
    private static Drawable dr;


    // 准备申请的权限
    private String[] permissions = {Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.CAMERA};

    /*
     * Creates a projection that returns the note ID and the note contents.
     */
    private static final String[] PROJECTION =
            new String[]{
                    NotePad.Notes._ID,
                    NotePad.Notes.COLUMN_NAME_TITLE,
                    NotePad.Notes.COLUMN_NAME_NOTE
            };

    // A label for the saved state of the activity
    private static final String ORIGINAL_CONTENT = "origContent";
    private String path;

    // This Activity can be started by more than one action. Each action is represented
    // as a "state" constant
    private static final int STATE_EDIT = 0;
    private static final int STATE_INSERT = 1;

    // Global mutable variables
    private int mState;
    private Uri mUri;
    private Cursor mCursor;
    private EditText mText;
    private String mOriginalContent;
    private RelativeLayout op_m;


    /**
     * Defines a custom EditText View that draws lines between each line of text that is displayed.
     */
    @SuppressLint("AppCompatCustomView")
    public static class LinedEditText extends EditText {
        private Rect mRect;
        private Paint mPaint;

        // This constructor is used by LayoutInflater
        public LinedEditText(Context context, AttributeSet attrs) {
            super(context, attrs);

            // Creates a Rect and a Paint object, and sets the style and color of the Paint object.
            mRect = new Rect();
            mPaint = new Paint();
            mPaint.setStyle(Paint.Style.STROKE);
            mPaint.setColor(0x00000000);
        }

        /**
         * This is called to draw the LinedEditText object
         *
         * @param canvas The canvas on which the background is drawn.
         */
        @Override
        protected void onDraw(Canvas canvas) {

            // Gets the number of lines of text in the View.
            int count = getLineCount();

            // Gets the global Rect and Paint objects
            Rect r = mRect;
            Paint paint = mPaint;

            /*
             * Draws one line in the rectangle for every line of text in the EditText
             */
            for (int i = 0; i < count; i++) {

                // Gets the baseline coordinates for the current line of text
                int baseline = getLineBounds(i, r);

                /*
                 * Draws a line in the background from the left of the rectangle to the right,
                 * at a vertical position one dip below the baseline, using the "paint" object
                 * for details.
                 */
                canvas.drawLine(r.left, baseline + 1, r.right, baseline + 1, paint);
            }

            // Finishes up by calling the parent method
            super.onDraw(canvas);
        }
    }

    /**
     * This method is called by Android when the Activity is first started. From the incoming
     * Intent, it determines what kind of editing is desired, and then does it.
     */
    private static int j = 1;

    private static int dc;
    SharedPreferences.Editor editor;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Sets the layout for this Activity. See res/layout/note_editor.xml
        setContentView(R.layout.note_editor);

        // Gets a handle to the EditText in the the layout.
        mText = findViewById(R.id.note);


//        RelativeLayout textBgColor = findViewById(R.id.open_menu);
        SharedPreferences pref = getSharedPreferences("bgColor", MODE_PRIVATE);
        int win_rgb = pref.getInt("win_rgb", 0);
        int dc = pref.getInt("dc", 0);
        int editTextBgColor = pref.getInt("editTextBgColor", 0);
        int editTextColor = pref.getInt("editTextColor", 0);
        Intent intent = getIntent();
        Window window = this.getWindow();
        window.setStatusBarColor(win_rgb);
        ActionBar actionBar = getActionBar();
        dr = this.getResources().getDrawable(dc);
        actionBar.setBackgroundDrawable(dr);
        mText.setBackgroundColor(editTextBgColor);
        mText.setTextColor(editTextColor);

        // If there is no data associated with the Intent, sets the data to the default URI, which
        // accesses a list of notes.

        /*
         * Creates an Intent to use when the Activity object's result is sent back to the
         * caller.
         */
        /*
         *  Sets up for the edit, based on the action specified for the incoming Intent.
         */

        // Gets the action that triggered the intent filter for this Activity
        final String action = intent.getAction();

        // For an edit action:
        if (Intent.ACTION_EDIT.equals(action)) {

            // Sets the Activity state to EDIT, and gets the URI for the data to be edited.
            mState = STATE_EDIT;
            mUri = intent.getData();

            // For an insert or paste action:
        } else if (Intent.ACTION_INSERT.equals(action)
                || Intent.ACTION_PASTE.equals(action)) {

            // Sets the Activity state to INSERT, gets the general note URI, and inserts an
            // empty record in the provider
            mState = STATE_INSERT;
            mUri = getContentResolver().insert(intent.getData(), null);

            /*
             * If the attempt to insert the new note fails, shuts down this Activity. The
             * originating Activity receives back RESULT_CANCELED if it requested a result.
             * Logs that the insert failed.
             */
            if (mUri == null) {

                // Writes the log identifier, a message, and the URI that failed.
                Log.e(TAG, "Failed to insert new note into " + getIntent().getData());

                // Closes the activity.
                finish();
                return;
            }

            // Since the new entry was created, this sets the result to be returned
            // set the result to be returned.
            setResult(RESULT_OK, (new Intent()).setAction(mUri.toString()));

            // If the action was other than EDIT or INSERT:
        } else {

            // Logs an error that the action was not understood, finishes the Activity, and
            // returns RESULT_CANCELED to an originating Activity.
            Log.e(TAG, "Unknown action, exiting");
            finish();
            return;
        }

        /*
         * Using the URI passed in with the triggering Intent, gets the note or notes in
         * the provider.
         * Note: This is being done on the UI thread. It will block the thread until the query
         * completes. In a sample app, going against a simple provider based on a local database,
         * the block will be momentary, but in a real app you should use
         * android.content.AsyncQueryHandler or android.os.AsyncTask.
         */
        mCursor = managedQuery(
                mUri,         // The URI that gets multiple notes from the provider.
                PROJECTION,   // A projection that returns the note ID and note content for each note.
                null,         // No "where" clause selection criteria.
                null,         // No "where" clause selection values.
                null          // Use the default sort order (modification date, descending)
        );

        // For a paste, initializes the data from clipboard.
        // (Must be done after mCursor is initialized.)
        if (Intent.ACTION_PASTE.equals(action)) {
            // Does the paste
            performPaste();
            // Switches the state to EDIT so the title can be modified.
            mState = STATE_EDIT;
        }


        /*
         * If this Activity had stopped previously, its state was written the ORIGINAL_CONTENT
         * location in the saved Instance state. This gets the state.
         */
        if (savedInstanceState != null) {
            mOriginalContent = savedInstanceState.getString(ORIGINAL_CONTENT);
        }

        View x = findViewById(R.id.edit_title);

        x.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent1 = new Intent(null, mUri);
                intent1.addCategory(Intent.CATEGORY_ALTERNATIVE);
                startActivity(intent1);
            }
        });
        Intent intent1 = new Intent(null, mUri);
        intent1.addCategory(Intent.CATEGORY_ALTERNATIVE);


    }

    /**
     * This method is called when the Activity is about to come to the foreground. This happens
     * when the Activity comes to the top of the task stack, OR when it is first starting.
     * <p>
     * Moves to the first note in the list, sets an appropriate title for the action chosen by
     * the user, puts the note contents into the TextView, and saves the original text as a
     * backup.
     */

    private static String note;

    @Override
    protected void onResume() {
        super.onResume();

        /*
         * mCursor is initialized, since onCreate() always precedes onResume for any running
         * process. This tests that it's not null, since it should always contain data.
         */
        if (mCursor != null) {
            // Requery in case something changed while paused (such as the title)
            mCursor.requery();

            /* Moves to the first record. Always call moveToFirst() before accessing data in
             * a Cursor for the first time. The semantics of using a Cursor are that when it is
             * created, its internal index is pointing to a "place" immediately before the first
             * record.
             */
            mCursor.moveToFirst();

            // Modifies the window title for the Activity according to the current Activity state.
            if (mState == STATE_EDIT) {
                // Set the title of the Activity to include the note title
                int colTitleIndex = mCursor.getColumnIndex(NotePad.Notes.COLUMN_NAME_TITLE);
                String title = mCursor.getString(colTitleIndex);
                Resources res = getResources();
                String text = String.format(res.getString(R.string.title_edit), title);
                setTitle(text);
                // Sets the title to "create" for inserts
            } else if (mState == STATE_INSERT) {
                setTitle(getText(R.string.title_create));
            }

            /*
             * onResume() may have been called after the Activity lost focus (was paused).
             * The user was either editing or creating a note when the Activity paused.
             * The Activity should re-display the text that had been retrieved previously, but
             * it should not move the cursor. This helps the user to continue editing or entering.
             */

            // Gets the note text from the Cursor and puts it in the TextView, but doesn't change
            // the text cursor's position.
            int colNoteIndex = mCursor.getColumnIndex(NotePad.Notes.COLUMN_NAME_NOTE);
            note = mCursor.getString(colNoteIndex);
//            mText.setTextKeepState(note);

            initContent();
//            mText.setText(Html.fromHtml(note,imageGetter,null));

            // Stores the original note text, to allow the user to revert changes.
            if (mOriginalContent == null) {
                mOriginalContent = note;
            }

            /*
             * Something is wrong. The Cursor should always contain data. Report an error in the
             * note.
             */
        } else {
            setTitle(getText(R.string.error_title));
            mText.setText(getText(R.string.error_message));
        }
    }

    /**
     * This method is called when an Activity loses focus during its normal operation, and is then
     * later on killed. The Activity has a chance to save its state so that the system can restore
     * it.
     * <p>
     * Notice that this method isn't a normal part of the Activity lifecycle. It won't be called
     * if the user simply navigates away from the Activity.
     */
//    @Override
//    protected void onSaveInstanceState(Bundle outState) {
//        // Save away the original text, so we still have it if the activity
//        // needs to be killed while paused.
//        outState.putString(ORIGINAL_CONTENT, mOriginalContent);
//    }

    /**
     * This method is called when the Activity loses focus.
     * <p>
     * For Activity objects that edit information, onPause() may be the one place where changes are
     * saved. The Android application model is predicated on the idea that "save" and "exit" aren't
     * required actions. When users navigate away from an Activity, they shouldn't have to go back
     * to it to complete their work. The act of going away should save everything and leave the
     * Activity in a state where Android can destroy it if necessary.
     * <p>
     * If the user hasn't done anything, then this deletes or clears out the note, otherwise it
     * writes the user's work to the provider.
     */
    @Override
    protected void onPause() {
        super.onPause();

        /*
         * Tests to see that the query operation didn't fail (see onCreate()). The Cursor object
         * will exist, even if no records were returned, unless the query failed because of some
         * exception or error.
         *
         */
        if (mCursor != null) {

            // Get the current note text.
            String text = mText.getText().toString();
            int length = text.length();
            /*
             * If the Activity is in the midst of finishing and there is no text in the current
             * note, returns a result of CANCELED to the caller, and deletes the note. This is done
             * even if the note was being edited, the assumption being that the user wanted to
             * "clear out" (delete) the note.
             */
            if (isFinishing() && (length == 0)) {
                setResult(RESULT_CANCELED);
                deleteNote();

                /*
                 * Writes the edits to the provider. The note has been edited if an existing note was
                 * retrieved into the editor *or* if a new note was inserted. In the latter case,
                 * onCreate() inserted a new empty note into the provider, and it is this new note
                 * that is being edited.
                 */
            } else if (mState == STATE_EDIT) {
                // Creates a map to contain the new values for the columns
                updateNote(text, null);
            } else if (mState == STATE_INSERT) {
                updateNote(text, text);
                mState = STATE_EDIT;
            }
        }
    }

    /**
     * This method is called when the user clicks the device's Menu button the first time for
     * this Activity. Android passes in a Menu object that is populated with items.
     * <p>
     * Builds the menus for editing and inserting, and adds in alternative actions that
     * registered themselves to handle the MIME types for this application.
     *
     * @param menu A Menu object to which items should be added.
     * @return True to display the menu.
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate menu from XML resource
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.editor_options_menu, menu);


        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        // Check if note has changed and enable/disable the revert option
        int colNoteIndex = mCursor.getColumnIndex(NotePad.Notes.COLUMN_NAME_NOTE);
        String savedNote = mCursor.getString(colNoteIndex);
        String currentNote = mText.getText().toString();
//        if (savedNote.equals(currentNote)) {
//            menu.findItem(R.id.menu_revert).setVisible(false);
//        } else {
//            menu.findItem(R.id.menu_revert).setVisible(true);
//        }
        return super.onPrepareOptionsMenu(menu);
    }

    /**
     * This method is called when a menu item is selected. Android passes in the selected item.
     * The switch statement in this method calls the appropriate method to perform the action the
     * user chose.
     *
     * @param item The selected MenuItem
     * @return True to indicate that the item was processed, and no further work is necessary. False
     * to proceed to further processing as indicated in the MenuItem object.
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle all of the possible menu actions.
        switch (item.getItemId()) {
            case R.id.menu_save:
                String text = mText.getText().toString();

                updateNote(text, null);
                finish();
                break;
            case R.id.menu_picture:
                // 版本判断。当手机系统大于 23 时，才有必要去判断权限是否获取
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    // 检查该权限是否已经获取

                    for (String permission : permissions) {
                        if (ContextCompat.checkSelfPermission(getApplicationContext(), permission) == PackageManager.PERMISSION_DENIED) {
                            ActivityCompat.requestPermissions(this, permissions, 10001);
                        }
                    }
                }
                callGallery();
                break;
            case R.id.menu_delete:
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("提示");
                builder.setMessage("是否删除");
                AlertDialog.Builder builder1 = builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        deleteNote();
                        finish();
                        //Toast.makeText(NoteEditor.this,"选则了取消",Toast.LENGTH_SHORT).show();
                    }
                });
                builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        //Toast.makeText(NoteEditor.this,"选则了取消",Toast.LENGTH_SHORT).show();
                    }
                });
                AlertDialog alertDialog = builder.create();
                alertDialog.show();
                break;
//            case R.id.menu_revert:
//                cancelNote();
//                break;
        }
        return super.onOptionsItemSelected(item);
    }



    /**
     * =================================插入图片==================================
     */

    private void initContent() {
        //input是获取将被解析的字符串
        String input = note;

        //将图片那一串字符串解析出来,即<img src=="xxx" />
        Pattern p = Pattern.compile("\\<img src=\".*?\"\\/>");
        Matcher m = p.matcher(input);

        SpannableString spannable = new SpannableString(input);
        while (m.find()) {
            //Log.d("YYPT_RGX", m.group());
            //这里s保存的是整个式子，即<img src="xxx"/>，start和end保存的是下标
            String s = m.group();
            int start = m.start();
            int end = m.end();
            //path是去掉<img src=""/>的中间的图片路径
            String path = s.replaceAll("\\<img src=\"|\"\\/>", "").trim();

            Bitmap bitmap = ImageUtils.getSmallBitmap(path, 50, 300);
            ImageSpan imageSpan = new ImageSpan(this, bitmap);
            spannable.setSpan(imageSpan, start, end, Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
        }
        mText.setText(spannable);
        mText.append("\n");
        updateNote(mText.getText().toString(), getTitle().toString());
    }

    private int IMAGE_CODE = 0;

    //region 调用图库
    private void callGallery() {
        Intent getAlbum = new Intent(Intent.ACTION_GET_CONTENT);
        getAlbum.setType("image/*");
        startActivityForResult(getAlbum, IMAGE_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        //参考网址：http://blog.csdn.net/abc__d/article/details/51790806
        Bitmap bm = null;
        // 外界的程序访问ContentProvider所提供数据 可以通过ContentResolver接口
        ContentResolver resolver = getContentResolver();
        if (requestCode == IMAGE_CODE) {
            try {
                // 获得图片的uri
                Uri originalUri = data.getData();


                path = getPathByUri4kitkat(NoteEditor.this, originalUri);

                insertImg(path);
                Toast.makeText(NoteEditor.this, path, Toast.LENGTH_SHORT).show();
            } catch (Exception e) {
                e.printStackTrace();
                Toast.makeText(this, "图片插入失败", Toast.LENGTH_SHORT).show();
            }
        }
    }


    // 专为Android4.4设计的从Uri获取文件绝对路径，以前的方法已不好使
    @SuppressLint("NewApi")
    public static String getPathByUri4kitkat(final Context context, final Uri uri) {
        final boolean isKitKat = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;
        // DocumentProvider
        if (isKitKat && DocumentsContract.isDocumentUri(context, uri)) {
            if (isExternalStorageDocument(uri)) {// ExternalStorageProvider
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];
                if ("primary".equalsIgnoreCase(type)) {
                    return Environment.getExternalStorageDirectory() + "/" + split[1];
                }
            } else if (isDownloadsDocument(uri)) {// DownloadsProvider
                final String id = DocumentsContract.getDocumentId(uri);
                final Uri contentUri = ContentUris.withAppendedId(Uri.parse("content://downloads/public_downloads"),
                        Long.valueOf(id));
                return getDataColumn(context, contentUri, null, null);
            } else if (isMediaDocument(uri)) {// MediaProvider
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];
                Uri contentUri = null;
                if ("image".equals(type)) {
                    contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                } else if ("video".equals(type)) {
                    contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                } else if ("audio".equals(type)) {
                    contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                }
                final String selection = "_id=?";
                final String[] selectionArgs = new String[]{split[1]};
                return getDataColumn(context, contentUri, selection, selectionArgs);
            }
        } else if ("content".equalsIgnoreCase(uri.getScheme())) {// MediaStore
            // (and
            // general)
            return getDataColumn(context, uri, null, null);
        } else if ("file".equalsIgnoreCase(uri.getScheme())) {// File
            return uri.getPath();
        }
        return null;
    }

    /**
     * Get the value of the data column for this Uri. This is useful for
     * MediaStore Uris, and other file-based ContentProviders.
     *
     * @param context       The context.
     * @param uri           The Uri to query.
     * @param selection     (Optional) Filter used in the query.
     * @param selectionArgs (Optional) Selection arguments used in the query.
     * @return The value of the _data column, which is typically a file path.
     */
    public static String getDataColumn(Context context, Uri uri, String selection, String[] selectionArgs) {
        Cursor cursor = null;
        final String column = "_data";
        final String[] projection = {column};
        try {
            cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs, null);
            if (cursor != null && cursor.moveToFirst()) {
                final int column_index = cursor.getColumnIndexOrThrow(column);
                return cursor.getString(column_index);
            }
        } finally {
            if (cursor != null)
                cursor.close();
        }
        return null;
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is ExternalStorageProvider.
     */
    public static boolean isExternalStorageDocument(Uri uri) {
        return "com.android.externalstorage.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is DownloadsProvider.
     */
    public static boolean isDownloadsDocument(Uri uri) {
        return "com.android.providers.downloads.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is MediaProvider.
     */
    public static boolean isMediaDocument(Uri uri) {
        return "com.android.providers.media.documents".equals(uri.getAuthority());
    }


    //region 插入图片
    private void insertImg(String path) {
        String tagPath = "<img src=\"" + path + "\"/>";//为图片路径加上<img>标签
        Bitmap bitmap = BitmapFactory.decodeFile(path);

        if (bitmap != null) {
            SpannableString ss = getBitmapMime(path, tagPath);

            insertPhotoToEditText(ss);
            mText.append("\n");
            Log.d("YYPT", mText.getText().toString());
        }
    }
    //endregion

    //region 将图片插入到EditText中
    private void insertPhotoToEditText(SpannableString ss) {
        Editable et = mText.getText();
        int start = mText.getSelectionStart();
        et.insert(start, ss);
        mText.setText(et);


        mText.setSelection(start + ss.length());
        mText.setFocusableInTouchMode(true);
        mText.setFocusable(true);


        updateNote(mText.getText().toString(), getTitle().toString());

    }
    //endregion

    private SpannableString getBitmapMime(String path, String tagPath) {
        SpannableString ss = new SpannableString(tagPath);//这里使用加了<img>标签的图片路径

        int width = ScreenUtils.getScreenWidth(this);
        int height = ScreenUtils.getScreenHeight(this);

        Bitmap bitmap = ImageUtils.getSmallBitmap(path, width, 480);
        ImageSpan imageSpan = new ImageSpan(this, bitmap);
        ss.setSpan(imageSpan, 0, tagPath.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        return ss;
    }

/**
 * ===============================插入图片==================================================
 */
    //endregion

//BEGIN_INCLUDE(paste)

    /**
     * A helper method that replaces the note's data with the contents of the clipboard.
     */
    private final void performPaste() {

        // Gets a handle to the Clipboard Manager
        ClipboardManager clipboard = (ClipboardManager)
                getSystemService(Context.CLIPBOARD_SERVICE);

        // Gets a content resolver instance
        ContentResolver cr = getContentResolver();

        // Gets the clipboard data from the clipboard
        ClipData clip = clipboard.getPrimaryClip();
        if (clip != null) {

            String text = null;
            String title = null;

            // Gets the first item from the clipboard data
            ClipData.Item item = clip.getItemAt(0);

            // Tries to get the item's contents as a URI pointing to a note
            Uri uri = item.getUri();

            // Tests to see that the item actually is an URI, and that the URI
            // is a content URI pointing to a provider whose MIME type is the same
            // as the MIME type supported by the Note pad provider.
            if (uri != null && NotePad.Notes.CONTENT_ITEM_TYPE.equals(cr.getType(uri))) {

                // The clipboard holds a reference to data with a note MIME type. This copies it.
                Cursor orig = cr.query(
                        uri,            // URI for the content provider
                        PROJECTION,     // Get the columns referred to in the projection
                        null,           // No selection variables
                        null,           // No selection variables, so no criteria are needed
                        null            // Use the default sort order
                );

                // If the Cursor is not null, and it contains at least one record
                // (moveToFirst() returns true), then this gets the note data from it.
                if (orig != null) {
                    if (orig.moveToFirst()) {
                        int colNoteIndex = mCursor.getColumnIndex(NotePad.Notes.COLUMN_NAME_NOTE);
                        int colTitleIndex = mCursor.getColumnIndex(NotePad.Notes.COLUMN_NAME_TITLE);
                        text = orig.getString(colNoteIndex);
                        title = orig.getString(colTitleIndex);
                    }

                    // Closes the cursor.
                    orig.close();
                }
            }

            // If the contents of the clipboard wasn't a reference to a note, then
            // this converts whatever it is to text.
            if (text == null) {
                text = item.coerceToText(this).toString();
            }

            // Updates the current note with the retrieved title and text.
            updateNote(text, title);
        }
    }
//END_INCLUDE(paste)

    /**
     * Replaces the current note contents with the text and title provided as arguments.
     *
     * @param text  The new note contents to use.
     * @param title The new note title to use
     */
    private final void updateNote(String text, String title) {

        // Sets up a map to contain values to be updated in the provider.
        ContentValues values = new ContentValues();
        values.put(NotePad.Notes.COLUMN_NAME_MODIFICATION_DATE, System.currentTimeMillis());

        // If the action is to insert a new note, this creates an initial title for it.
        if (mState == STATE_INSERT) {

            // If no title was provided as an argument, create one from the note text.
            if (title == null) {

                // Get the note's length
                int length = text.length();

                // Sets the title by getting a substring of the text that is 31 characters long
                // or the number of characters in the note plus one, whichever is smaller.
                title = text.substring(0, Math.min(30, length));

                // If the resulting length is more than 30 characters, chops off any
                // trailing spaces
                if (length > 30) {
                    int lastSpace = title.lastIndexOf(' ');
                    if (lastSpace > 0) {
                        title = title.substring(0, lastSpace);
                    }
                }
            }
            // In the values map, sets the value of the title
            values.put(NotePad.Notes.COLUMN_NAME_TITLE, title);
        } else if (title != null) {
            // In the values map, sets the value of the title
            values.put(NotePad.Notes.COLUMN_NAME_TITLE, title);
        }

        // This puts the desired notes text into the map.
        values.put(NotePad.Notes.COLUMN_NAME_NOTE, text);

        /*
         * Updates the provider with the new values in the map. The ListView is updated
         * automatically. The provider sets this up by setting the notification URI for
         * query Cursor objects to the incoming URI. The content resolver is thus
         * automatically notified when the Cursor for the URI changes, and the UI is
         * updated.
         * Note: This is being done on the UI thread. It will block the thread until the
         * update completes. In a sample app, going against a simple provider based on a
         * local database, the block will be momentary, but in a real app you should use
         * android.content.AsyncQueryHandler or android.os.AsyncTask.
         */
        getContentResolver().update(
                mUri,    // The URI for the record to update.
                values,  // The map of column names and new values to apply to them.
                null,    // No selection criteria are used, so no where columns are necessary.
                null     // No where columns are used, so no where arguments are necessary.
        );


    }

    /**
     * This helper method cancels the work done on a note.  It deletes the note if it was
     * newly created, or reverts to the original text of the note i
     */
    private final void cancelNote() {
        if (mCursor != null) {
            if (mState == STATE_EDIT) {
                // Put the original note text back into the database
                mCursor.close();
                mCursor = null;
                ContentValues values = new ContentValues();
                values.put(NotePad.Notes.COLUMN_NAME_NOTE, mOriginalContent);
                getContentResolver().update(mUri, values, null, null);
            } else if (mState == STATE_INSERT) {
                // We inserted an empty note, make sure to delete it
                deleteNote();
            }
        }
        setResult(RESULT_CANCELED);
        finish();
    }

    /**
     * Take care of deleting a note.  Simply deletes the entry.
     */
    private final void deleteNote() {
        if (mCursor != null) {
            mCursor.close();
            mCursor = null;
            getContentResolver().delete(mUri, null, null);
            mText.setText("");
        }
    }
}





