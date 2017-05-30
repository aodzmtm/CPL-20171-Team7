package com.eatit.util;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.eatit.restaurant.R;

public class CustomSpinnerAdapt extends BaseAdapter {
    Context context;
    //   String[] MenuIMG;
    String[] MenuNames;
    LayoutInflater inflter;

    public CustomSpinnerAdapt(Context applicationContext, String[] MenuNames) {
        this.context = applicationContext;
//        this.MenuIMG = MenuIMG;
        this.MenuNames = MenuNames;
        inflter = (LayoutInflater.from(applicationContext));
    }

    @Override
    public int getCount() {
        return MenuNames.length;
    }

    @Override
    public Object getItem(int i) {
        return null;
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
       /* LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                ImageView miv = (ImageView) listViewItem.findViewById(R.id.menuView);
                new LoadImagefromUrl( ).execute(  miv,item.getMenu_picture());
                TextView tv = (TextView) listViewItem.findViewById(R.id.menuName);
                tv.setText(item.getMenu_name().toString());
                TextView tv1 = (TextView) listViewItem.findViewById(R.id.menuPrice);
                tv1.setText(item.getMenu_price().toString());
                inLayout.addView(listViewItem);*/

        view = inflter.inflate(R.layout.spin_item, null);

        //ImageView icon = (ImageView) view.findViewById(R.id.SpinImage);
        //new LoadImagefromUrl().execute(icon, MenuIMG[i]);
        //icon.setImageResource(MenuIMG[i]);

        TextView names = (TextView) view.findViewById(R.id.SpinText);
        names.setText(MenuNames[i]);

        return view;
    }
}

