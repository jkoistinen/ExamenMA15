package com.jk.examenma15;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import com.firebase.client.Firebase;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by jk on 31/08/16.
 */
public class CustomTodoAdapter extends ArrayAdapter<ToDo> {

    private ArrayList<ToDo> objects;
    private Context context;

    public CustomTodoAdapter(Context context, ArrayList<ToDo> objects) {
        super(context, 0, objects);
        this.objects = objects;
        this.context = context;
    }

    static class ViewHolder {
        ToDo todo;
        TextView text;
        TextView expiretext;
        ImageButton deleteButton;

    }

    private void setupItem(ViewHolder holder) {
        holder.text.setText(holder.todo.getText());
        holder.expiretext.setText(holder.todo.getExpiredate().toString());
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        ViewHolder holder = null;

            convertView = LayoutInflater.from(getContext()).inflate(R.layout.activity_to_do_customlistview, parent, false);

            holder = new ViewHolder();
            holder.todo = objects.get(position);
            holder.text = (TextView) convertView.findViewById(R.id.todoTextView);
            holder.expiretext = (TextView) convertView.findViewById(R.id.todoExpireTextView);

            convertView.setTag(position);
            setupItem(holder);

        return convertView;
    }

}
