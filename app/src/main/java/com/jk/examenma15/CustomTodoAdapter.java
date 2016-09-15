package com.jk.examenma15;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.TextView;

import java.util.List;

/**
 * Created by jk on 31/08/16.
 */
public class CustomTodoAdapter extends ArrayAdapter<ToDo> {

    private final List<ToDo> objects;

    private ImageButton deleteButton;

    public CustomTodoAdapter(Context context, int resource, int textViewResourceId, List<ToDo> objects) {
        super(context, resource, textViewResourceId, objects);
        this.objects = objects;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        final Integer pos = position;

        if(convertView == null){

            convertView = LayoutInflater.from(getContext()).inflate(R.layout.activity_to_do_customlistview, parent, false);

        }
        TextView text = (TextView) convertView.findViewById(R.id.todoTextView);
        TextView expireDate = (TextView) convertView.findViewById(R.id.todoExpireTextView);

        ToDo todo = objects.get(pos);

        text.setText(todo.getText().toString());
        expireDate.setText(todo.getExpiredate().toString()); // Override todo objects toString() method....

        deleteButton = (ImageButton) convertView.findViewById(R.id.todoImageButtonDelete);

        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d("imageButton", "deleteButton clicked on position: "+pos);

                //Initialise TodoActivity to access methods
                ToDoListActivity todolistactivity = new ToDoListActivity(); //This is not good ? it extends Activity.
                todolistactivity.removeListItem(pos); //Send in the position for MainActivity
            }
        });

        convertView.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                Log.d("convertView", "Clicked "+pos);
            }
        });

        return super.getView(position, convertView, parent);
    }
}
