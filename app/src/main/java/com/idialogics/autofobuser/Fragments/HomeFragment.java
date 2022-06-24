package com.idialogics.autofobuser.Fragments;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.airbnb.lottie.LottieAnimationView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.idialogics.autofobuser.Adapter.ProductRecycler;
import com.idialogics.autofobuser.Adapter.SliderAdapter;
import com.idialogics.autofobuser.Model.Banners;
import com.idialogics.autofobuser.Model.Manufacturer;
import com.idialogics.autofobuser.Model.Model;
import com.idialogics.autofobuser.Model.Product;
import com.idialogics.autofobuser.Model.Year;
import com.idialogics.autofobuser.R;
import com.idialogics.autofobuser.Utils.Constants;
import com.idialogics.autofobuser.Utils.Functions;
import com.idialogics.autofobuser.Utils.SharedPref;
import com.smarteist.autoimageslider.IndicatorView.animation.type.IndicatorAnimationType;
import com.smarteist.autoimageslider.SliderAnimations;
import com.smarteist.autoimageslider.SliderView;

import java.util.ArrayList;


public class HomeFragment extends Fragment {

    Context context;
    SharedPref sh;
    DatabaseReference dataRef;
    ArrayList<Manufacturer> manufacturersList;
    ArrayList<Model> modelList;
    ArrayList<Year> yearList;
    ArrayList<Year> duplicateYearList;
    ArrayList<Product> productList;
    ProductRecycler productRecycler;
    Spinner spManufacturer, spModel, spYear;
    RecyclerView rvProducts;
    LottieAnimationView animationView;
    Button btnSearch;
    String searchID;
    SliderAdapter sliderAdapter;
    SliderView imageSlider;
    ConstraintLayout clNoResult;
    boolean isContains;
    String manufacturer, model, year;
    ArrayList<String> stringArrayList;
    int counter = 0;
    ArrayList<Banners> bannerList;
    TextView tvSearchResult;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        initUI(view);


        // getManufactures();

        // spinnerListener();


        //getImages();


        btnSearch.setOnClickListener(v -> {

            manufacturer = spManufacturer.getSelectedItem().toString();
            model = spModel.getSelectedItem().toString();
            year = spYear.getSelectedItem().toString();

            if (manufacturer.equals("") || manufacturer.equals("Make")) {

                Functions.showSnackBar(context, "Please Select Manufacturer first");

            } else if (model.equals("") || model.equals("Model")) {

                Functions.showSnackBar(context, "Please Select Model");

            } else if (year.equals("") || year.equals("Year")) {

                Functions.showSnackBar(context, "Please Select Year");

            } else {


                getProductIds();


            }
        });

        return view;
    }

    private void initUI(View view) {
        context = view.getContext();
        sh = new SharedPref(context);
        dataRef = FirebaseDatabase.getInstance().getReference();
        spManufacturer = view.findViewById(R.id.sp_manufacturer);
        spModel = view.findViewById(R.id.sp_model);
        spYear = view.findViewById(R.id.sp_year);
        rvProducts = view.findViewById(R.id.rv_products);
        animationView = view.findViewById(R.id.animationView);
        btnSearch = view.findViewById(R.id.btn_search);
        imageSlider = view.findViewById(R.id.iv);
        clNoResult = view.findViewById(R.id.cl_no_result);
        tvSearchResult = view.findViewById(R.id.tv_search_result);

        bannerList = new ArrayList<>();
        manufacturersList = new ArrayList<>();
        modelList = new ArrayList<>();
        yearList = new ArrayList<>();
        productList = new ArrayList<>();
        duplicateYearList = new ArrayList<>();
        stringArrayList = new ArrayList<>();

        Functions.pushDownButton(btnSearch);


        spManufacturer.setEnabled(false);
        spModel.setEnabled(false);
        spYear.setEnabled(false);


        Manufacturer manufacturer = new Manufacturer("999", "Make");
        manufacturersList.add(manufacturer);

        ArrayAdapter<Manufacturer> manufacturerArrayAdapter = new ArrayAdapter<>(
                context, R.layout.spinner_layout, manufacturersList);

        manufacturerArrayAdapter.setDropDownViewResource(R.layout.spinner_layout);
        spManufacturer.setAdapter(manufacturerArrayAdapter);


        Model model = new Model("000", "Model", "999");
        modelList.add(model);

        ArrayAdapter<Model> modelArrayAdapter = new ArrayAdapter<>(
                context, R.layout.spinner_layout, modelList);

        modelArrayAdapter.setDropDownViewResource(R.layout.spinner_layout);
        spModel.setAdapter(modelArrayAdapter);


        Year year = new Year("000", "Year", "999", "999");
        yearList.add(year);
        ArrayAdapter<Year> yearArrayAdapter = new ArrayAdapter<>(
                context, R.layout.spinner_layout, yearList);

        yearArrayAdapter.setDropDownViewResource(R.layout.spinner_layout);
        spYear.setAdapter(yearArrayAdapter);

    }


    private void getProductIds() {

        for (int n = 0; n < yearList.size(); n++) {

            Year yearModel = yearList.get(n);

            if (yearModel.getYear().equals(year)) {

                stringArrayList.add(yearModel.getProductId());
            }
        }

        productList.clear();

        Functions.loadingDialog(context, "Loading", true);
        getProducts();


    }

    private void getProducts() {

        String id = stringArrayList.get(counter);

        Query query = dataRef.child(Constants.PRODUCTS).orderByChild(Constants.ID).equalTo(id);

        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {


                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {

                    Product product = dataSnapshot.getValue(Product.class);
                    productList.add(product);

                }
                counter++;
                if (counter < stringArrayList.size()) {

                    getProducts();

                } else {

                    Functions.loadingDialog(context, "Loading", false);


                    if (productList.size() == 0) {

                        animationView.setVisibility(View.GONE);
                        clNoResult.setVisibility(View.VISIBLE);

                        tvSearchResult.setText(getString(R.string.search_results));

                    } else {

                        clNoResult.setVisibility(View.GONE);

                        String text = "Select the Key FOB that Matches Your Current FOB";
                        tvSearchResult.setText(text);


                    }

                    productRecycler = new ProductRecycler(context, productList, manufacturer, model, year);
                    animationView.setVisibility(View.GONE);
                    rvProducts.setVisibility(View.VISIBLE);
                    rvProducts.setAdapter(productRecycler);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    private void spinnerListener() {

        spManufacturer.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                clNoResult.setVisibility(View.GONE);
                animationView.setVisibility(View.VISIBLE);
                rvProducts.setVisibility(View.GONE);

                String idd = manufacturersList.get(position).getId();

                if (!idd.equals("999")) {

                    Query query = dataRef.child(Constants.MODEL).orderByChild(Constants.PARENT_ID).equalTo(idd);

                    query.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            modelList.clear();

                            Model model = new Model("000", "Model", "999");
                            modelList.add(model);

                            for (DataSnapshot dataSnapshot : snapshot.getChildren()) {

                                Model model1 = dataSnapshot.getValue(Model.class);
                                modelList.add(model1);

                            }
                            ArrayAdapter<Model> modelArrayAdapter = new ArrayAdapter<>(
                                    context, R.layout.spinner_layout, modelList);

                            modelArrayAdapter.setDropDownViewResource(R.layout.spinner_layout);
                            spModel.setAdapter(modelArrayAdapter);


                            spModel.setEnabled(true);
                            spYear.setEnabled(false);

                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });


                }


            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

                clNoResult.setVisibility(View.GONE);
                animationView.setVisibility(View.VISIBLE);
                rvProducts.setVisibility(View.GONE);


                spModel.setEnabled(false);
                spYear.setEnabled(false);

            }
        });


        spModel.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                clNoResult.setVisibility(View.GONE);
                animationView.setVisibility(View.VISIBLE);
                rvProducts.setVisibility(View.GONE);

                String idd = modelList.get(position).getId();

                if (!idd.equals("999")) {


                    Query query = dataRef.child(Constants.YEAR).orderByChild(Constants.PARENT_ID).equalTo(idd);

                    query.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            yearList.clear();
                            duplicateYearList.clear();


                            Year year = new Year("000", "Year", "999", "999");
                            yearList.add(year);

                            for (DataSnapshot dataSnapshot : snapshot.getChildren()) {

                                Year year1 = dataSnapshot.getValue(Year.class);
                                yearList.add(year1);

                            }

                            if (yearList.size() > 0) {


                                spYear.setEnabled(true);

                                filterYearList();

                            }

                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });

                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

                clNoResult.setVisibility(View.GONE);
                animationView.setVisibility(View.VISIBLE);
                rvProducts.setVisibility(View.GONE);

                spYear.setEnabled(false);

            }
        });


        spYear.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                clNoResult.setVisibility(View.GONE);
                animationView.setVisibility(View.VISIBLE);
                rvProducts.setVisibility(View.GONE);

                String idd = yearList.get(position).getId();

                if (!idd.equals("999")) {

                    searchID = idd;

                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

                clNoResult.setVisibility(View.GONE);
                animationView.setVisibility(View.VISIBLE);
                rvProducts.setVisibility(View.GONE);

            }
        });


    }

    private void filterYearList() {

        // Functions.loadingDialog(context, "Loading", true);

        for (int i = 0; i < yearList.size(); i++) {

            Year year = yearList.get(i);

            isContains = false;

            if (duplicateYearList.size() > 0) {

                for (int j = 0; j < duplicateYearList.size(); j++) {

                    Year year1 = duplicateYearList.get(j);

                    if (year1.getYear().equals(year.getYear())) {


                        isContains = true;
                        break;

                    }


                }

                if (!isContains) {
                    duplicateYearList.add(year);
                }


            } else {

                duplicateYearList.add(year);

            }

        }

        duplicateYearList.sort((o1, o2) -> o1.getYear().compareTo(o2.getYear()));


        // Functions.loadingDialog(context, "Loading", false);


        ArrayAdapter<Year> yearArrayAdapter = new ArrayAdapter<>(
                context, R.layout.spinner_layout, duplicateYearList);

        yearArrayAdapter.setDropDownViewResource(R.layout.spinner_layout);
        spYear.setAdapter(yearArrayAdapter);


    }

    private void getManufactures() {

        Functions.loadingDialog(context, "Loading", true);

        dataRef.child(Constants.MANUFECTURER)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        manufacturersList.clear();

                        Manufacturer manufacturer = new Manufacturer("999", "Make");
                        manufacturersList.add(manufacturer);

                        for (DataSnapshot dataSnapshot : snapshot.getChildren()) {

                            Manufacturer manufacturer1 = dataSnapshot.getValue(Manufacturer.class);
                            manufacturersList.add(manufacturer1);

                        }

                        Functions.loadingDialog(context, "Loading", false);

                        ArrayAdapter<Manufacturer> manufacturerArrayAdapter = new ArrayAdapter<>(
                                context, R.layout.spinner_layout, manufacturersList);

                        manufacturerArrayAdapter.setDropDownViewResource(R.layout.spinner_layout);

                        spManufacturer.setAdapter(manufacturerArrayAdapter);

                        spManufacturer.setEnabled(true);


                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

    }


    private void getImages() {


        dataRef.child(Constants.BANNERS)
                .child(sh.getString(Constants.ID))
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {

                        for (DataSnapshot dataSnapshot : snapshot.getChildren()) {

                            Banners banners = dataSnapshot.getValue(Banners.class);
                            bannerList.add(banners);

                        }

                        sliderAdapter = new SliderAdapter(context);
                        imageSlider.setSliderAdapter(sliderAdapter);
                        imageSlider.setIndicatorAnimation(IndicatorAnimationType.WORM);
                        imageSlider.setSliderTransformAnimation(SliderAnimations.SIMPLETRANSFORMATION);
                        imageSlider.setAutoCycleDirection(SliderView.AUTO_CYCLE_DIRECTION_BACK_AND_FORTH);
                        imageSlider.setIndicatorSelectedColor(Color.WHITE);
                        imageSlider.setIndicatorUnselectedColor(Color.GRAY);
                        imageSlider.setScrollTimeInSec(3);
                        imageSlider.setIndicatorEnabled(true);
                        imageSlider.setAutoCycle(true);
                        imageSlider.startAutoCycle();

                        //  Functions.loadingDialog(context, "Uploading", false);

                        if (bannerList.size() == 0) {

                            Banners banners = new Banners("0", "https://firebasestorage.googleapis.com/v0/b/autofobadmin.appspot.com/o/Profile%20Pictures%2Ftemp_ads.png?alt=media&token=8b9d7ef7-0b16-4244-bd74-99e69ff9aa82");
                            bannerList.add(banners);

                        }

                        sliderAdapter.setImages(bannerList);

                    }


                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        //  Functions.loadingDialog(context, "Uploading", false);

                    }
                });

    }

    @Override
    public void onResume() {
        super.onResume();

        if (!spModel.isEnabled()) {

            getManufactures();

        }

        spinnerListener();


        getImages();
    }
}