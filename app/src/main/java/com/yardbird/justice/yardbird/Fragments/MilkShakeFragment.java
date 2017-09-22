package com.yardbird.justice.yardbird.Fragments;


import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import com.amigold.fundapter.BindDictionary;
import com.amigold.fundapter.FunDapter;
import com.amigold.fundapter.extractors.StringExtractor;
import com.amigold.fundapter.interfaces.DynamicImageLoader;
import com.amigold.fundapter.interfaces.ItemClickListener;
import com.kosalgeek.android.json.JsonConverter;
import com.kosalgeek.genasync12.AsyncResponse;
import com.kosalgeek.genasync12.PostResponseAsyncTask;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.yardbird.justice.yardbird.Interfaces.Products;
import com.yardbird.justice.yardbird.Interfaces.UILConfig;
import com.yardbird.justice.yardbird.R;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * A simple {@link Fragment} subclass.
 */
public class MilkShakeFragment extends Fragment implements AsyncResponse{
    public static final String PREFS = "prefFile";

    final static String url = "http://group8.hol.es/milkshake.php";

    private ArrayList<Products> productList;
    private ListView lv;
    FunDapter<Products> adapter;

    View view;


    public MilkShakeFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_milk_shake, container, false);

        ImageLoader.getInstance().init(UILConfig.config(MilkShakeFragment.this.getActivity()));

        PostResponseAsyncTask taskRead = new PostResponseAsyncTask(MilkShakeFragment.this.getActivity(), this);
        taskRead.execute(url);

        return view;
    }

    @Override
    public void processFinish(String s) {

        productList = new JsonConverter<Products>().toArrayList(s, Products.class);

        BindDictionary dic = new BindDictionary();

        dic.addStringField(R.id.tvName, new StringExtractor<Products>() {
            @Override
            public String getStringValue(Products item, int position) {
                return item.name;
            }
        });

        dic.addStringField(R.id.tvDesc, new StringExtractor<Products>() {
            @Override
            public String getStringValue(Products item, int position) {
                return item.description;
            }
        }).visibilityIfNull(View.GONE);

        dic.addStringField(R.id.tvPrice, new StringExtractor<Products>() {
            @Override
            public String getStringValue(Products item, int position) {
                return ""+item.price;
            }
        });

        dic.addDynamicImageField(R.id.ivImage, new StringExtractor<Products>() {
            @Override
            public String getStringValue(Products item, int position) {
                return item.img_url;
            }
        }, new DynamicImageLoader() {
            @Override
            public void loadImage(String url, ImageView img) {
                //Set image
                ImageLoader.getInstance().displayImage(url, img);
            }
        });

        dic.addBaseField(R.id.btnCart).onClick(new ItemClickListener() {
            @Override
            public void onClick(Object item, int position, View view) {

                SharedPreferences preferences = MilkShakeFragment.this.getActivity().getSharedPreferences(PREFS, 0);
                final String customer = preferences.getString("username", null);

                final String qty = ""+1;
                final Products selectedItem = productList.get(position);
                final HashMap postData = new HashMap();

                postData.put("txtName", selectedItem.name);
                postData.put("txtDesc", selectedItem.description);
                postData.put("txtQty", qty);
                postData.put("txtPrice", ""+selectedItem.price);
                postData.put("txtImageUrl", selectedItem.img_url);
                postData.put("txtCustomer", customer);

                PostResponseAsyncTask insertTask = new PostResponseAsyncTask(
                        MilkShakeFragment.this.getActivity(), postData, new AsyncResponse() {
                    @Override
                    public void processFinish(String s) {
                        // Log.d(LOG, s);
                        String aName = selectedItem.name;
                        String aQty = qty.toString();

                        if(s.contains("success"))
                        {
                            if(postData.equals(aName))
                            {
                                aQty += 1;
                            }
                            Toast.makeText(MilkShakeFragment.this.getActivity(), "Added to Cart", Toast.LENGTH_SHORT).show();
                        }
                    }
                });

                insertTask.execute("http://group8.hol.es/cart_insert.php");

            }
        });

        adapter = new FunDapter<>(MilkShakeFragment.this.getActivity(), productList, R.layout.product_row, dic);
        lv = (ListView)view.findViewById(R.id.lvProduct);
        lv.setAdapter(adapter);
    }

}
