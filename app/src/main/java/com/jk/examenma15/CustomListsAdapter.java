package com.jk.examenma15;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by jk on 31/08/16.
 */

public class CustomListsAdapter extends ArrayAdapter<String> {

    private List<String> objects;
    private Context context;

    public CustomListsAdapter(Context context, int resource, int textViewResourceId, List<String> objects) {
        super(context, resource, textViewResourceId, objects);
        this.objects = objects;
        this.context = context;
    }

    static class ViewHolder {
        String todolist;
        TextView todolistTextView;
        ImageButton imageButtonDelete;
    }

    private void setupItem(ViewHolder holder, Integer position) {
        holder.todolistTextView.setText(holder.todolist);
        holder.imageButtonDelete.setTag(position);
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        ViewHolder holder = null;

        if(convertView == null){

            convertView = LayoutInflater.from(getContext()).inflate(R.layout.activity_main_customlistview, parent, false);

        }

        holder = new ViewHolder();
        holder.todolist = objects.get(position);
        holder.todolistTextView = (TextView) convertView.findViewById(R.id.todolistTextView);
        holder.imageButtonDelete = (ImageButton) convertView.findViewById(R.id.imageButtonDelete);

        final String todolist = (String) getItem(position);
        holder.imageButtonDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                remove(todolist);

                if(context instanceof MainActivity){
                    ((MainActivity)context).removeListItem(position);
                }

            }
        });

        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(context instanceof MainActivity){
                    ((MainActivity)context).showList(position, context);
                }

            }
        });

        setupItem(holder, position);
        
        return super.getView(position, convertView, parent);
    }
}
