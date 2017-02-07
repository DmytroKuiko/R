package de.h_da.sqlitetest;

import android.graphics.Color;
import android.media.Image;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;

/**
 * ArrayAdapter for showing info from the DB in my specified xml file
 */
public class MyArrayAdapter extends BaseAdapter {


    ArrayList<MyData> arrayList;

    public MyArrayAdapter (ArrayList<MyData> list) {

        arrayList = list;
    }
    @Override
    public int getCount() {
        return arrayList.size();
    }

    @Override
    public Object getItem(int position) {
        return arrayList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return arrayList.get(position).getId();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        MyViewHolder holder = null;
        View row = convertView;

        if(row == null) {
            LayoutInflater inflater = LayoutInflater.from(parent.getContext());
            row = inflater.inflate(R.layout.list_view_row, parent, false);

            holder = new MyViewHolder(row);
            row.setTag(holder);
        }
        else {
            holder = (MyViewHolder)row.getTag();
        }
        MyData temp = arrayList.get(position);


//        holder.textViewId.setText(String.valueOf(temp.getId()));
        Calendar calendar = new GregorianCalendar(Integer.parseInt(temp.getYear()), Integer.parseInt(temp.getMonth()), Integer.parseInt(temp.getDay()));
        //String day = String.format("%1$te %1$tB %1$tY", calendar);
        String day = String.format("%1$td", calendar); // e - 1 digit with day, d - with a zero
        String month = String.format("%1$tB", calendar); // Months is shown in letters
        String year = String.format(" %1$tY", calendar);
        holder.textViewDay.setText(day);
        holder.textViewMonth.setText(month);
        holder.textViewYear.setText(year);


        if (!(temp.getDescription().equals(""))) {
            holder.imageView.setImageResource(R.drawable.ic_star_outline_24dp);
//            holder.textViewSum.setBackgroundColor(Color.GREEN);

        }
        else {
            holder.imageView.setImageResource(0);
        }
        // Plus Euro sign
        //String sum = String.valueOf(temp.getSum()).concat(" \u20ac");
        String sum = String.valueOf(temp.getSum());
        holder.textViewSum.setText(sum);
        holder.textViewAllInfo.setText(day.concat(" ").concat(month).concat(" ").concat(year).concat("    ").concat(sum));



        return row;
    }

    class MyViewHolder {
        TextView textViewDay, textViewMonth, textViewYear, textViewSum, textViewAllInfo;
        ImageView imageView;

        MyViewHolder (View v) {
//            textViewId = (TextView) v.findViewById(R.id.textViewId);
            textViewDay = (TextView) v.findViewById(R.id.textViewDay);
            textViewMonth = (TextView) v.findViewById(R.id.textViewMonth);
            textViewYear = (TextView) v.findViewById(R.id.textViewYear);
            textViewSum = (TextView) v.findViewById(R.id.textViewSum);
            textViewAllInfo = (TextView) v.findViewById(R.id.textViewAllInfo);
//            textViewDescription = (TextView) v.findViewById(R.id.textViewDescription);
            imageView = (ImageView)v.findViewById(R.id.imageView);
        }

    }
}
