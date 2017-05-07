package com.aleksandarvasilevski.notes;

import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.aleksandarvasilevski.notes.data.NoteContract.NoteEntry;

public class NoteActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    /** Identifier for the note data loader */
    private static final int EXISTING_NOTE_LOADER = 0;

    // Content URI for the existing note (null if it's a new note)
    private Uri mCurrentNoteUri;

    private EditText mTitleEditText;
    private EditText mDescriptionEditText;

    /** Boolean flag that keeps track of whether the pet has been edited (true) or not (false) */
    private boolean mNoteHasChanged = false;

    /**
     * OnTouchListener that listens for any user touches on a View, implying that they are modifying
     * the view, and we change the mPetHasChanged boolean to true.
     */
    private View.OnTouchListener mTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            mNoteHasChanged = true;
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note);

        Intent intent = getIntent();
        mCurrentNoteUri = intent.getData();
        if (mCurrentNoteUri == null){
            setTitle("Add Note");
            invalidateOptionsMenu();
        }else {
            setTitle("Edit Note");
            // Initialize a loader to read the note data from the database
            // and display the current values in the editor
            getSupportLoaderManager().initLoader(EXISTING_NOTE_LOADER, null, this);
        }

        //Adding reference to each of the UI widgets
        mTitleEditText = (EditText)findViewById(R.id.editTitle);
        mDescriptionEditText = (EditText)findViewById(R.id.editDescription);
    }

    private void saveNote(){
        String titleString = mTitleEditText.getText().toString();
        String descriptionString = mDescriptionEditText.getText().toString();

        // Check if this is supposed to be a new pet
        // and check if all the fields in the editor are blank
        if (mCurrentNoteUri == null &&
                TextUtils.isEmpty(titleString) && TextUtils.isEmpty(descriptionString)) {
            // Since no fields were modified, we can return early without creating a new pet.
            // No need to create ContentValues and no need to do any ContentProvider operations.
            return;
        }

        ContentValues values = new ContentValues();
        values.put(NoteEntry.COLUMN_TITLE, titleString);
        values.put(NoteEntry.COLUMN_DESCRIPTION, descriptionString);

        // Determine if this is a new or existing note by checking if mCurrentNoteUri is null or not
        if (mCurrentNoteUri == null) {
            // This is a NEW note, so insert a new note into the provider,
            // returning the content URI for the new note.
            Uri newUri = getContentResolver().insert(NoteEntry.CONTENT_URI, values);
            if (newUri == null){
            Toast.makeText(this, "Error with saving the note", Toast.LENGTH_SHORT).show();
            }else {
            Toast.makeText(this, "Note saved", Toast.LENGTH_SHORT).show();
            }
        } else {
            // Otherwise this is an EXISTING note, so update the note with content URI: mCurrentNoteUri
            // and pass in the new ContentValues. Pass in null for the selection and selection args
            // because mCurrentNoteUri will already identify the correct row in the database that
            // we want to modify.
            int rowsAffected = getContentResolver().update(mCurrentNoteUri, values, null, null);

            // Show a toast message depending on whether or not the update was successful.
            if (rowsAffected == 0) {
                // If no rows were affected, then there was an error with the update.
                Toast.makeText(this, "Error with updating note",
                        Toast.LENGTH_SHORT).show();
            } else {
                // Otherwise, the update was successful and we can display a toast.
                Toast.makeText(this, "Note updated",
                        Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void showDeleteConfirmationDialog() {
        // Create an AlertDialog.Builder and set the message, and click listeners
        // for the postivie and negative buttons on the dialog.
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Delete this note?");

        builder.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked the "Delete" button, so delete the note.
                deleteNote();
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked the "Cancel" button, so dismiss the dialog
                // and continue editing the note.
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });

        // Create and show the AlertDialog
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    /**
     * Perform the deletion of the note in the database.
     */
    private void deleteNote() {
        // Only perform the delete if this is an existing note.
        if (mCurrentNoteUri != null) {
            // Call the ContentResolver to delete the note at the given content URI.
            // Pass in null for the selection and selection args because the mCurrentNoteUri
            // content URI already identifies the note that we want.
            int rowsDeleted = getContentResolver().delete(mCurrentNoteUri, null, null);

            // Show a toast message depending on whether or not the delete was successful.
            if (rowsDeleted == 0) {
                // If no rows were deleted, then there was an error with the delete.
                Toast.makeText(this, "Error with deleting note",
                        Toast.LENGTH_SHORT).show();
            } else {
                // Otherwise, the delete was successful and we can display a toast.
                Toast.makeText(this, "Note deleted",
                        Toast.LENGTH_SHORT).show();
            }
        }

        // Close the activity
        finish();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_note, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Respond to a click on the "Save" menu option
        switch (item.getItemId()) {
            // Respond to a click on the "ok" menu option
            case R.id.action_ok:
                // Save note to database
                saveNote();
                // Exit activity
                finish();
                return true;
            // Respond to a click on the "Up" arrow button in the app bar
            case R.id.action_cancel:
                finish();
                return true;
            // Respond to a click on the "Delete" menu option
            case R.id.action_delete:
                showDeleteConfirmationDialog();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        // Since the editor shows all note attributes, define a projection that contains
        // all columns from the note table
        String[] projection = {
                NoteEntry._ID,
                NoteEntry.COLUMN_TITLE,
                NoteEntry.COLUMN_DESCRIPTION,};

        // This loader will execute the ContentProvider's query method on a background thread
        return new CursorLoader(this,   // Parent activity context
                mCurrentNoteUri,         // Query the content URI for the current note
                projection,             // Columns to include in the resulting Cursor
                null,                   // No selection clause
                null,                   // No selection arguments
                null);                  // Default sort order
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        // Bail early if the cursor is null or there is less than 1 row in the cursor
        if (cursor == null || cursor.getCount() < 1) {
            return;
        }
        // Proceed with moving to the first row of the cursor and reading data from it
        // (This should be the only row in the cursor)
        if (cursor.moveToFirst()) {
            if (cursor.moveToFirst()) {
                // Find the columns of pet attributes that we're interested in
                int titleColumnIndex = cursor.getColumnIndex(NoteEntry.COLUMN_TITLE);
                int descriptionColumnIndex = cursor.getColumnIndex(NoteEntry.COLUMN_DESCRIPTION);

                // Extract out the value from the Cursor for the given column index
                String title = cursor.getString(titleColumnIndex);
                String description = cursor.getString(descriptionColumnIndex);

                // Update the views on the screen with the values from the database
                mTitleEditText.setText(title);
                mDescriptionEditText.setText(description);
            }
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        // If the loader is invalidated, clear out all the data from the input fields.
        mTitleEditText.setText("");
        mDescriptionEditText.setText("");
    }
}
