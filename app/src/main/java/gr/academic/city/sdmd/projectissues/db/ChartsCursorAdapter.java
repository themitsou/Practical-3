package gr.academic.city.sdmd.projectissues.db;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

import gr.academic.city.sdmd.projectissues.R;

/**
 * Created by Theofanis on 13/8/2017.
 */

public class ChartsCursorAdapter extends CursorAdapter {
    private LayoutInflater cursorInflater;

    // Default constructor
    public ChartsCursorAdapter(Context context, Cursor cursor, int flags) {
        super(context, cursor, flags);
        cursorInflater = (LayoutInflater) context.getSystemService(
                Context.LAYOUT_INFLATER_SERVICE);

    }

    public void bindView(View view, Context context, Cursor cursor) {
        TextView assignee = (TextView) view.findViewById(R.id.tv_issue_assignee);

        int pos = cursor.getPosition()+1;

        assignee.setText(cursor.getString(cursor.getColumnIndex("assignee_name")));

        TextView issue = (TextView) view.findViewById(R.id.tv_issue);
        issue.setText(pos + ". " + cursor.getString(cursor.getColumnIndex("title")));

        TextView percentage = (TextView) view.findViewById(R.id.tv_issue_percentage);
        String cursorPercentage = String.format("%.2f", cursor.getDouble(cursor.getColumnIndex("percentage")))+"%";
        percentage.setText(cursorPercentage);

        if (cursor.getColumnIndex("percentage")<=100) {
            percentage.setTextColor(Color.rgb(150,225,0));
        }else{
            percentage.setTextColor(Color.RED);
        }

    }

    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        // R.layout.list_row is your xml layout for each row
        return cursorInflater.inflate(R.layout.item_chart, parent, false);
    }
}