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
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class MainActivity extends Activity {
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
        }
        return super.onOptionsItemSelected(item);
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
