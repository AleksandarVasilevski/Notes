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

/**
 * Displays list of notes that were entered and stored in the app.
 */
public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    private FirebaseAnalytics mFirebaseAnalytics;

    /** Identifier for the note data loader */
    private static final int NOTE_LOADER = 0;

    /** Adapter for the ListView */
    NoteCursorAdapter mCursorAdapter;

    /** ListView for the View */
    ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Obtain the FirebaseAnalytics instance.
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);

        // Find the ListView which will be populated with the note data
        listView = (ListView)findViewById(R.id.list);

        // Setup an Adapter to create a list item for each row of note data in the Cursor.
        mCursorAdapter = new NoteCursorAdapter(this, null);
        listView.setAdapter(mCursorAdapter);

        // Kick off the loader
        getSupportLoaderManager().initLoader(NOTE_LOADER, null, this);

        // Setup the list item click listener
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
            // Respond to a click on the "Delete all notes" menu option
            case R.id.action_delete_all_entries:
                deleteAllNotes();
                return true;
            // Respond to a click on the "About" menu option
            case R.id.action_about:
                Intent aboutIntent = new Intent(MainActivity.this, AboutActivity.class);
                startActivity(aboutIntent);
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * Setup the list item click listener
     */
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
        // Define a projection that specifies the columns from the table we need.
        String[] projection = {
                NoteEntry._ID,
                NoteEntry.COLUMN_TITLE
        };

        // This loader will execute the ContentProvider's query method on a background thread
        return new CursorLoader(this,       // Parent activity context
                NoteEntry.CONTENT_URI,      // Provider content URI to query
                projection,                 // Columns to include in the resulting Cursor
                null,                       // No selection clause
                null,                       // No selection arguments
                null);                      // Default sort order
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        // Update {@link NoteCursorAdapter} with this new cursor containing updated note data
        mCursorAdapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        // Callback called when the data needs to be deleted
        mCursorAdapter.swapCursor(null);

    }
}
