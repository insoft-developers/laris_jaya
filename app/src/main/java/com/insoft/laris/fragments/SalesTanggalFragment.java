package com.insoft.laris.fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import com.insoft.laris.R;
import com.insoft.laris.adapter.SalesMonthAdapter;
import com.insoft.laris.json.SalesMonthResponseJson;
import com.insoft.laris.json.SalesTanggalRequestJson;
import com.insoft.laris.model.SalesMonth;
import com.insoft.laris.utils.RegisterAPI;
import com.insoft.laris.utils.SessionManager;
import com.insoft.laris.utils.SessionTanggal;
import com.insoft.laris.utils.UtilsAPI;

import java.text.NumberFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;

public class SalesTanggalFragment extends Fragment {
    View view;
    private TextView summarypenjualan;
    private RecyclerView rvsalestoday;
    private ProgressBar loading;
    private SessionManager sessionManager;
    private SessionTanggal sessionTanggal;
    private RegisterAPI registerAPI;
    private List<SalesMonth> salesmonth;
    public SalesTanggalFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_sales_today, container, false);
        sessionManager = new SessionManager(getContext());
        sessionTanggal = new SessionTanggal(getContext());

        rvsalestoday = view.findViewById(R.id.rvsalestoday);
        loading = view.findViewById(R.id.loading);
        summarypenjualan = view.findViewById(R.id.summarypenjualan);
        LinearLayoutManager llm = new LinearLayoutManager(getContext());
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        rvsalestoday.setLayoutManager(llm);
        HashMap<String, String> user = sessionManager.getSessionData();
        String userid = user.get(sessionManager.ID);

        HashMap<String, String> tgl = sessionTanggal.getSessionTanggal();
        String tglawal = tgl.get(sessionTanggal.AWAL);
        String tglakhir = tgl.get(sessionTanggal.AKHIR);


        fetch_data(userid, tglawal, tglakhir);

        return view;
    }


    private void fetch_data(String userid, String awal, String akhir){
        SalesTanggalRequestJson param = new SalesTanggalRequestJson();
        param.setIduser(userid);
        param.setAwal(awal);
        param.setAkhir(akhir);

        loading.setVisibility(View.VISIBLE);
        registerAPI = UtilsAPI.getApiService();
        registerAPI.salestanggal(param).enqueue(new Callback<SalesMonthResponseJson>() {
            @Override
            public void onResponse(Call<SalesMonthResponseJson> call, retrofit2.Response<SalesMonthResponseJson> response) {
                if(response.isSuccessful()){
                    Log.d("REs", response.toString());
                    loading.setVisibility(View.GONE);
                    salesmonth = response.body().getData();
                    SalesMonthAdapter adapter = new SalesMonthAdapter(getContext(), salesmonth);
                    rvsalestoday.setAdapter(adapter);
                    adapter.notifyDataSetChanged();

                    int totalpenjualan = response.body().getTotal();
                    Locale localeID = new Locale("in", "ID");
                    NumberFormat formatRupiah = NumberFormat.getCurrencyInstance(localeID);
                    summarypenjualan.setText("TOTAL : "+formatRupiah.format(totalpenjualan));
                }else{
                    loading.setVisibility(View.GONE);
                    Log.d("REs", response.toString());
                }
            }

            @Override
            public void onFailure(Call<SalesMonthResponseJson> call, Throwable t) {
                loading.setVisibility(View.GONE);
                Toast.makeText(getActivity(), "System error: " + t.getLocalizedMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }


}