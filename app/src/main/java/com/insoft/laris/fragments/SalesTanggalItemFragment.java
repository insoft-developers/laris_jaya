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
import com.insoft.laris.adapter.SalesTodayItemAdapter;
import com.insoft.laris.json.SalesTanggalRequestJson;
import com.insoft.laris.json.SalesTodayItemResponseJson;
import com.insoft.laris.model.SalesTodayItem;
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

public class SalesTanggalItemFragment extends Fragment {
    View view;
    private TextView summarypenjualan;
    private RecyclerView rvsalestoday;
    private ProgressBar loading;
    private SessionManager sessionManager;
    private SessionTanggal sessionTanggal;
    private RegisterAPI registerAPI;
    private List<SalesTodayItem> salestoday;
    public SalesTanggalItemFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_sales_today_item, container, false);
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
        String awal = tgl.get(sessionTanggal.AWAL);
        String akhir = tgl.get(sessionTanggal.AKHIR);

        fetch_data(userid, awal, akhir);

        return view;
    }


    private void fetch_data(String userid, String awal, String akhir){
        SalesTanggalRequestJson param = new SalesTanggalRequestJson();
        param.setIduser(userid);
        param.setAwal(awal);
        param.setAkhir(akhir);

        loading.setVisibility(View.VISIBLE);
        registerAPI = UtilsAPI.getApiService();
        registerAPI.salestanggalitem(param).enqueue(new Callback<SalesTodayItemResponseJson>() {
            @Override
            public void onResponse(Call<SalesTodayItemResponseJson> call, retrofit2.Response<SalesTodayItemResponseJson> response) {
                if(response.isSuccessful()){
                    loading.setVisibility(View.GONE);
                    salestoday = response.body().getData();
                    SalesTodayItemAdapter adapter = new SalesTodayItemAdapter(getContext(), salestoday);
                    rvsalestoday.setAdapter(adapter);
                    adapter.notifyDataSetChanged();

                    int totalpenjualan = response.body().getTotal();
                    Locale localeID = new Locale("in", "ID");
                    NumberFormat formatRupiah = NumberFormat.getCurrencyInstance(localeID);
                    summarypenjualan.setText("TOTAL PENJUALAN : "+formatRupiah.format(totalpenjualan));
                }else{
                    loading.setVisibility(View.GONE);
                    Log.d("REs", response.toString());
                }
            }

            @Override
            public void onFailure(Call<SalesTodayItemResponseJson> call, Throwable t) {
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