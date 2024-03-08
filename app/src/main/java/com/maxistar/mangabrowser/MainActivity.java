/**
 * Sources
 * Manga Eden - http://mangaeden.com - in progress
 * Manga Here - http://www.mangahere.com/ - in progress
 * Manga Reader - http://www.mangareader.net/ - in progress
 * Manga Fox - http://mangafox.me/
 * AnimeA - http://manga.animea.net/genre/yaoi
 * Mangable - http://mangable.com/ - in progress
 * Anime Story - http://www.anime-story.com/
 * SubManga - http://submanga.com/
 * Mein Manga - http://www.meinmanga.com/
 * Read Manga - http://readmanga.me/
 * Adult Manga - http://adultmanga.ru
 * 
 * 
 */
package com.maxistar.mangabrowser;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceManager;
import android.provider.DocumentsContract;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class MainActivity extends Activity {

    final int REQUEST_FOLDER_SELECTED = 10;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //
        Button button = (Button) this.findViewById(R.id.search);
        button.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View arg0) {
                Intent intent = new Intent(MainActivity.this,
                        SearchActivity.class);
                startActivity(intent);
            }
        });

        button = (Button) this.findViewById(R.id.favorites);
        button.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View arg0) {
                Intent intent = new Intent(MainActivity.this,
                        FavoritesActivity.class);
                startActivity(intent);
            }
        });

        button = (Button) this.findViewById(R.id.add_folder);
        button.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View arg0) {
                openDirectory();
            }
        });

    }

    public void openDirectory() {
        // Choose a directory using the system's file picker.
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT_TREE);

        // Optionally, specify a URI for the directory that should be opened in
        // the system file picker when it loads.
        // intent.putExtra(DocumentsContract.EXTRA_INITIAL_URI, uriToLoad);

        startActivityForResult(intent, 111);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.activity_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.menu_settings) {
            Intent intent = new Intent(MainActivity.this,
                    SettingsActivity.class);
            startActivity(intent);
            return true;
        } else if (item.getItemId() == R.id.add_manga_folder) {
            Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT_TREE);
            startActivityForResult(intent, REQUEST_FOLDER_SELECTED);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     *
     */
    public synchronized void onActivityResult(
            final int requestCode,
            int resultCode,
            final Intent data
    ) {
        if (requestCode == REQUEST_FOLDER_SELECTED) {
            if (resultCode == Activity.RESULT_OK) {
                if (data != null) {
                    Uri uri = data.getData();
                    if (uri != null) {
                        FavoritesActivity.filesUri = uri;
                        showToast(R.string.About_Software);
                    }
                }
            } else if (resultCode == Activity.RESULT_CANCELED) {
                showToast(R.string.abc_search_hint);
            }
        }
        if (requestCode == 111) {
            if (resultCode == Activity.RESULT_OK) {
                //if (data != null) {
                //    Uri uri = data.getData();
                //    if (uri != null) {
                //        FavoritesActivity.filesUri = uri;
                //        showToast(R.string.About_Software);
                //    }
                //}
                if (data != null && data.getData() != null) {
                    Uri uri = data.getData();
                    Log.d("TAG", "Selected directory: " + uri.toString());

                    // List files in the selected directory
                    listFilesInDirectory(uri);
                }
            } else if (resultCode == Activity.RESULT_CANCELED) {
                showToast(R.string.abc_search_hint);
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void listFilesInDirectory(Uri uri) {
        if (uri == null) return;

        ArrayList<Uri> list = traverseDirectoryEntries(uri);


        for (Uri fileUrld: list) {
            readDocumentContent(fileUrld);
        }

        // Get the document ID of the selected directory
        //String documentId = DocumentsContract.getTreeDocumentId(uri);

        // Build URI for children documents of the selected directory
        //Uri childrenUri = DocumentsContract.buildChildDocumentsUriUsingTree(uri, documentId);

        //try (InputStream inputStream = getContentResolver().openInputStream(childrenUri);
        //     BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {

        //    String line;
        //    while ((line = reader.readLine()) != null) {
                // For each document in the directory, read its content
        //        Uri documentUri = DocumentsContract.buildDocumentUriUsingTree(uri, line);
        //        readDocumentContent(documentUri);
        //    }
        //} catch (IOException e) {
        //    e.printStackTrace();
        //}
    }

    ArrayList<Uri> traverseDirectoryEntries(Uri rootUri) {
        ArrayList<Uri> listUri = new ArrayList<>();

        ContentResolver contentResolver = this.getContentResolver();
        Uri childrenUri = DocumentsContract.buildChildDocumentsUriUsingTree(rootUri,
                DocumentsContract.getTreeDocumentId(rootUri));

        // Keep track of our directory hierarchy
        List<Uri> dirNodes = new LinkedList<>();
        dirNodes.add(childrenUri);

        while(!dirNodes.isEmpty()) {
            childrenUri = dirNodes.remove(0); // get the item from top
            Log.d("dfddf", "node uri: " + childrenUri);
            Cursor c = contentResolver.query(childrenUri, new String[]{
                            DocumentsContract.Document.COLUMN_DOCUMENT_ID,
                            DocumentsContract.Document.COLUMN_DISPLAY_NAME,
                            DocumentsContract.Document.COLUMN_MIME_TYPE},
                    null, null, null);
            try {
                while (c.moveToNext()) {
                    final String docId = c.getString(0);
                    final String name = c.getString(1);
                    final String mime = c.getString(2);
                    Log.d("dfddf", "docId: " + docId + ", name: " + name + ", mime: " + mime);
                    if(isDirectory(mime)) {
                        final Uri newNode = DocumentsContract.buildChildDocumentsUriUsingTree(rootUri, docId);
                        dirNodes.add(newNode);
                    }
                    else {
                        if (name.contains(".txt")) { // maybe you should check mime here?
                            final Uri newNode = DocumentsContract.buildChildDocumentsUriUsingTree(rootUri, docId);
                            listUri.add(newNode);
                        }
                    }
                }
            } finally {
                closeQuietly(c);
            }
        }
        return listUri;
    }

    // Util method to check if the mime type is a directory
    private static boolean isDirectory(String mimeType) {
        return DocumentsContract.Document.MIME_TYPE_DIR.equals(mimeType);
    }

    // Util method to close a closeable
    private static void closeQuietly(Closeable closeable) {
        if (closeable != null) {
            try {
                closeable.close();
            } catch (RuntimeException re) {
                throw re;
            } catch (Exception ignore) {
                // ignore exception
            }
        }
    }

    private void readDocumentContent(Uri documentUri) {
        try (InputStream inputStream = getContentResolver().openInputStream(documentUri);
             BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {

            StringBuilder stringBuilder = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                stringBuilder.append(line).append("\n");
            }

            // Output the content of the document
            Log.d("TAG", "Document content: " + stringBuilder.toString());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    protected void showToast(int toast_str) {
        Context context = getApplicationContext();
        int duration = Toast.LENGTH_SHORT;
        Toast toast = Toast.makeText(context, toast_str, duration);
        toast.show();
    }

      @Override
      public void onResume() {
        super.onResume();

      }

      @Override
      public void onPause() {
        super.onPause();
      }

      /** Called before the activity is destroyed. */
      @Override
      public void onDestroy() {
        super.onDestroy();
      }
}
