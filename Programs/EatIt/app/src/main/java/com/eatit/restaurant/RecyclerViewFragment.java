package com.eatit.restaurant;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.github.florent37.materialviewpager.header.MaterialViewPagerHeaderDecorator;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import com.eatit.parcel.ParcelableRestaurant;
import com.eatit.vo.Restaurant;

public class RecyclerViewFragment extends Fragment {

    private static final boolean GRID_LAYOUT = false;
    private static final int ITEM_COUNT = 100;
    int category;
    ArrayList<ParcelableRestaurant> resList = new ArrayList<ParcelableRestaurant>();

    @BindView(R.id.recyclerView)
    RecyclerView mRecyclerView;

 /*   public static RecyclerViewFragment newInstance() {
        return new RecyclerViewFragment();
    }*/

    public static RecyclerViewFragment newInstance(int category, ArrayList<ParcelableRestaurant> resList) {
        RecyclerViewFragment recyclerViewFragment = new RecyclerViewFragment();
        Bundle bundle = new Bundle();
        bundle.putInt("CATEGORY", category);
        bundle.putParcelableArrayList("RES_LIST", resList);
        recyclerViewFragment.setArguments(bundle);
        return recyclerViewFragment;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_recyclerview, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.bind(this, view);

        final List<Restaurant> items = new ArrayList<>();

        Bundle data = getArguments();
        category = data.getInt("CATEGORY");
        resList = data.getParcelableArrayList("RES_LIST");

        System.out.println("category = " + category + " majorList size = " + resList.size());

/*        if(resList.size() == 0) {
            for (int i = 0; i < ITEM_COUNT; ++i) {
                Restaurant res = new Restaurant();
                if (i % 6 == category) {
                    res.setCategory(i % 6);
                    res.setRes_name("Restaurant " + i);
                    res.setAddr("대구광역시 북구 산격동 " + i);
                    res.setGrade("4.3");
                    res.setPhone("053-555-5555");
                    items.add(res);
                }
            }
        }*/

        for(int i = 0 ; i < resList.size() ; i ++){
            Restaurant res = new Restaurant();
            res.setStoreID((resList.get(i).getStoreID()));
            res.setCategory(resList.get(i).getCategory());
            res.setRes_name(resList.get(i).getRes_name());
            res.setAddr(resList.get(i).getAddr());
            if(resList.get(i).getGrade().isEmpty()){
                res.setGrade("0.0");
            }
            else{
                res.setGrade(resList.get(i).getGrade());
            }

            res.setPhone(resList.get(i).getPhone());
            res.setBeaconID(resList.get(i).getBeaconID());
            res.setPicture(resList.get(i).getPicture());
            items.add(res);
        }



        //setup materialviewpager

        if (GRID_LAYOUT) {
            mRecyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 2));
        } else {
            mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        }
        mRecyclerView.setHasFixedSize(true);

        //Use this now
        mRecyclerView.addItemDecoration(new MaterialViewPagerHeaderDecorator());
        mRecyclerView.setAdapter(new TestRecyclerViewAdapter(items));
    }
}


