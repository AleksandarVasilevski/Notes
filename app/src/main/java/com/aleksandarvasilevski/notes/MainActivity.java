package com.aleksandarvasilevski.notes;

import android.content.ContentUris;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.aleksandarvasilevski.notes.data.NoteContract.NoteEntry;
import com.google.firebase.analytics.FirebaseAnalytics;


public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    private FirebaseAnalytics mFirebaseAnalytics;

    private static final int NOTE_LOADER = 0;

    NoteCursorAdapter mCursorAdapter;

    ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Obtain the FirebaseAnalytics instance.
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);

        listView = (ListView)findViewById(R.id.list);

        mCursorAdapter = new NoteCursorAdapter(this, null);
        listView.setAdapter(mCursorAdapter);

        getSupportLoaderManager().initLoader(NOTE_LOADER, null, this);

        listViewOnClick();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // User clicked on a menu option in the app bar overflow menu
        switch (item.getItemId()) {
            // Respond to a click on the "Add note" menu option
            case R.id.action_add:
                Intent newNoteIntent = new Intent(MainActivity.this, NoteActivity.class);
                startActivity(newNoteIntent);
                return true;
            case R.id.action_delete_all_entries:
                deleteAllNotes();
                return true;
            case R.id.action_about:
                Intent aboutIntent = new Intent(MainActivity.this, AboutActivity.class);
                startActivity(aboutIntent);
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void listViewOnClick(){
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                Intent intent = new Intent(MainActivity.this, NoteActivity.class);
                Uri currentNoteUri = ContentUris.withAppendedId(NoteEntry.CONTENT_URI, id);
                intent.setData(currentNoteUri);
                startActivity(intent);
            }
        });
    }

    /**
     * Helper method to delete all notes in the database.
     */
    private void deleteAllNotes() {
        int rowsDeleted = getContentResolver().delete(NoteEntry.CONTENT_URI, null, null);
        Log.v("MainActivity", rowsDeleted + " rows deleted from note database");
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        String[] projection = {
                NoteEntry._ID,
                NoteEntry.COLUMN_TITLE
        };

        return new CursorLoader(this,
                NoteEntry.CONTENT_URI,
                projection,
                null,
                null,
                null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        mCursorAdapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mCursorAdapter.swapCursor(null);

    }
}
