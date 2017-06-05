package com.aleksandarvasilevski.notes;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

import com.aleksandarvasilevski.notes.data.NoteContract;

import java.util.Date;


public class NoteCursorAdapter extends CursorAdapter {

    public NoteCursorAdapter(Context context, Cursor c) {
        super(context, c, 0 /* flags */);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.list_item, parent, false);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {

        TextView titleTextView = (TextView)view.findViewById(R.id.title_textview);
        TextView dateTextView = (TextView)view.findViewById(R.id.date_textview);

        int titleColumnIndex = cursor.getColumnIndex(NoteContract.NoteEntry.COLUMN_TITLE);
        int dateColumnIndex = cursor.getColumnIndex(NoteContract.NoteEntry.COLUMN_DATE);

        String title = cursor.getString(titleColumnIndex);
        String date = cursor.getString(dateColumnIndex);

        titleTextView.setText(title);
        dateTextView.setText(date);
    }
}