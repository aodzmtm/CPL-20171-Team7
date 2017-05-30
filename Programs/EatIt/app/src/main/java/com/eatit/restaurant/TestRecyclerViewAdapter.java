package com.eatit.restaurant;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import com.eatit.util.LoadImagefromUrl;
import com.eatit.util.LoginSession;
import com.eatit.vo.Restaurant;


public class TestRecyclerViewAdapter extends RecyclerView.Adapter<TestRecyclerViewAdapter.ViewHolder> {

    private List<Restaurant> contents;

    static final int TYPE_HEADER = 0;
    static final int TYPE_CELL = 1;

    private static Context context;

    public static class ViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        public TextView res_name;
        public TextView res_address;
        public TextView res_grade;
        public TextView res_phone;
        public ImageView res_picture;
        public CardView cvItem; // for touch listener


        public ViewHolder(View view) {
            super(view);
            context = view.getContext();
            res_name = (TextView) view.findViewById(R.id.res_name);
            res_address = (TextView) view.findViewById(R.id.res_address);
            res_grade = (TextView) view.findViewById(R.id.res_grade);
            res_phone = (TextView) view.findViewById(R.id.res_phone);
            res_picture = (ImageView) view.findViewById(R.id.res_img);
            cvItem = (CardView) view.findViewById(R.id.card_view_small);
        }
    }

    public TestRecyclerViewAdapter(List<Restaurant> contents) {
        this.contents = contents;
    }

    @Override
    public int getItemViewType(int position) {
        switch (position) {
            case 0:
                return TYPE_HEADER;
            default:
                return TYPE_CELL;
        }
    }

    @Override
    public int getItemCount() {
        return contents.size();
    }

    @Override
    public TestRecyclerViewAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = null;

        switch (viewType) {
            /*case TYPE_HEADER: {
                view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.list_item_card_big, parent, false);
                return new RecyclerView.ViewHolder(view) {
                };
            }*/
            case TYPE_HEADER: {
                view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.list_item_card_small, parent, false);
                return new ViewHolder(view) {
                };
            }
            case TYPE_CELL: {
                view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.list_item_card_small, parent, false);
                return new ViewHolder(view) {
                };
            }
        }
        return null;
    }


    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        // variable for listener testing
        //final String rn = contents.get(position).getRes_name();
        final String beacon_id = contents.get(position).getBeaconID();
        final int store_id = contents.get(position).getStoreID();

        switch (getItemViewType(position)) {
            case TYPE_HEADER:
                holder.res_name.setText(contents.get(position).getRes_name());
                holder.res_address.setText(contents.get(position).getAddr());
                holder.res_grade.setText(contents.get(position).getGrade());
                holder.res_phone.setText(contents.get(position).getPhone());
                new LoadImagefromUrl().execute(holder.res_picture, contents.get(position).getPicture());
                break;
            case TYPE_CELL:
                holder.res_name.setText(contents.get(position).getRes_name());
                holder.res_address.setText(contents.get(position).getAddr());
                holder.res_grade.setText(contents.get(position).getGrade());
                holder.res_phone.setText(contents.get(position).getPhone());
                new LoadImagefromUrl().execute(holder.res_picture, contents.get(position).getPicture());
                break;
        }

        /* listener for cardView */
        holder.cvItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LoginSession loginSession = new LoginSession();

                System.out.println("++++++" + loginSession.getInstance().getBeacon_id() + "\n");
                System.out.println("++++++" + loginSession.getInstance().getAdmin_id() + "\n");
                System.out.println("++++++" + loginSession.getInstance().getMember_id() + "\n");
                System.out.println("++++++" + loginSession.getInstance().getLoginType() + "\n");
                try {
                    Intent i = new Intent();

                    // 로그인이 되어 있으면
                    if (loginSession.getMember_id() != null && loginSession.getAdmin_id() != null)
                        // 관리자의 경우에는 해당 beacon ID와 함께 전달.
                        if (loginSession.getInstance().getLoginType() == 1 && loginSession.getInstance().getBeacon_id().equals(contents.get(position).getBeaconID())) {
                            System.out.println("bbb 멤버");
                            i.setClass(context, RestaurantDetailManagerActivity.class);
                            i.putExtra("store_id", Integer.toString(contents.get(position).getStoreID()));
                        }
                        // 멤버의 경우
                        else if (!loginSession.getInstance().getMember_id().isEmpty() || !loginSession.getInstance().getAdmin_id().isEmpty()) {
                            System.out.println("bbb 관리자");
                            i.setClass(context, RestaurantDetailMemberActivity.class);
                            i.putExtra("store_id", Integer.toString(contents.get(position).getStoreID()));
                        }

                    context.startActivity(i);

                } catch (ActivityNotFoundException e) {
                    Log.w("MainActivity", "ActivityNotFoundException");
                }
            }
        });
    }
}