package com.happypeople.pharmacygo;

import android.content.AsyncTaskLoader;
import android.content.Context;

import java.util.List;

/**
 * Created by Radouane on 11/01/2018.
 */

public class PharmacyLoader extends AsyncTaskLoader<List<Pharmacy>> {

    private String mUrl;

    public PharmacyLoader(Context context, String url) {
        super(context);
        this.mUrl = url;
    }

    @Override
    protected void onStartLoading() {
        forceLoad();
    }

    @Override
    public List<Pharmacy> loadInBackground() {
        if(mUrl == null)
            return null;
        return PharmacyQueryUtils.getPharmacies(mUrl);
    }
}
