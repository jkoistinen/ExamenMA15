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

    private ImageButton deleteButton;

    public CustomListsAdapter(Context context, int resource, int textViewResourceId, List<String> objects) {
        super(context, resource, textViewResourceId, objects);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        final Integer pos = position;

        if(convertView == null){

            convertView = LayoutInflater.from(getContext()).inflate(R.layout.activity_main_customlistview, parent, false);

        }

        deleteButton = (ImageButton) convertView.findViewById(R.id.imageButtonDelete);

        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d("imageButton", "deleteButton clicked on position: "+pos);

                //Initialise MainActivity to access methods
                MainActivity mainactivity= new MainActivity(); //This is not good ? it extends Activity.
                mainactivity.removeListItem(pos); //Send in the position for MainActivity
            }
        });

        convertView.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                Log.d("convertView", "Clicked "+pos);

                //Initialise MainActivity to access methods
                MainActivity mainactivity= new MainActivity(); //This is not good ? it extends Activity.

                Context contextintent = getContext(); // Intent needs context.
                mainactivity.showList(pos, contextintent); //Send in the position and context for the Intent in MainActivity

            }
        });

        return super.getView(position, convertView, parent);
    }
}
