package com.example.android.notepad;

import android.app.ListActivity;
import android.content.ContentUris;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.SimpleCursorAdapter;

public class NoteSearch extends ListActivity implements
        SearchView.OnQueryTextListener{

    private static final String[] PROJECTION =
            new String[] {
                    NotePad.Notes._ID,
                    NotePad.Notes.COLUMN_NAME_TITLE,
                    NotePad.Notes.COLUMN_NAME_MODIFICATION_DATE
            };

    /*UI组件*/
    private SearchView sv_search;

    private  SimpleCursorAdapter adapter;
    private Uri mUri = NotePad.Notes.CONTENT_URI;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.note_search);

        sv_search = (SearchView) findViewById(R.id.searchview);
        sv_search.setOnQueryTextListener(this);

    }
    @Override
    public boolean onQueryTextChange(String queryText) {
        String selection = NotePad.Notes.COLUMN_NAME_TITLE + " LIKE '%"
                +queryText+ "%' " ;
        Cursor cursor = getContentResolver().query(
                mUri,    // The URI for the record to update.
                PROJECTION,
                selection,  // The map of column names and new values to apply to them.
                null,    // No selection criteria are used, so no where columns are necessary.
                NotePad.Notes.DEFAULT_SORT_ORDER      // No where columns are used, so no where arguments are necessary.
        );

        String[] dataColumns = { NotePad.Notes.COLUMN_NAME_TITLE,NotePad.Notes.COLUMN_NAME_MODIFICATION_DATE } ;//增加修改时间

        int[] viewIDs = { android.R.id.text1, R.id.time };//增加修改时间

        adapter = new SimpleCursorAdapter(
                this,                             // The Context for the ListView
                R.layout.noteslist_item,          // Points to the XML for a list item
                cursor,                           // The cursor to get items from
                dataColumns,
                viewIDs

        );
        setListAdapter(adapter);
        return true;
    }

    @Override
    public boolean onQueryTextSubmit(String queryText) {

        return false;
    }

    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {

        // Constructs a new URI from the incoming URI and the row ID
        Uri uri = ContentUris.withAppendedId(mUri, id);

        // Gets the action from the incoming Intent
        String action = getIntent().getAction();

        // Handles requests for note data
        if (Intent.ACTION_PICK.equals(action) || Intent.ACTION_GET_CONTENT.equals(action)) {

            // Sets the result to return to the component that called this Activity. The
            // result contains the new URI
            setResult(RESULT_OK, new Intent().setData(uri));
        } else {

            // Sends out an Intent to start an Activity that can handle ACTION_EDIT. The
            // Intent's data is the note ID URI. The effect is to call NoteEdit.
            startActivity(new Intent(Intent.ACTION_EDIT, uri));
        }
    }
}