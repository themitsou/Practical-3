package gr.academic.city.sdmd.projectissues.db;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ResourceCursorAdapter;
import android.widget.TextView;

import gr.academic.city.sdmd.projectissues.R;

/**
 * Created by Theofanis on 13/8/2017.
 */

public class ClientCursorAdapter extends CursorAdapter {
    private LayoutInflater cursorInflater;

    // Default constructor
    public ClientCursorAdapter(Context context, Cursor cursor, int flags) {
        super(context, cursor, flags);
        cursorInflater = (LayoutInflater) context.getSystemService(
                Context.LAYOUT_INFLATER_SERVICE);

    }

    public void bindView(View view, Context context, Cursor cursor) {
        TextView issue = (TextView) view.findViewById(R.id.tv_issue);
        int pos = cursor.getPosition()+1;
        issue.setText(pos+". "+cursor.getString(cursor.getColumnIndex("title")));

        TextView points = (TextView) view.findViewById(R.id.tv_issue_points);
        String cursorPoints = String.format("%.2f", cursor.getDouble(cursor.getColumnIndex("points")));
        points.setText(cursorPoints);
    }

    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        // R.layout.list_row is your xml layout for each row
        return cursorInflater.inflate(R.layout.item_points, parent, false);
    }
}