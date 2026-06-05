package com.insoft.laris.utils;

import static com.insoft.laris.utils.Constants.BASE_URL;

public class UtilsAPI {
    public static final String BASE_ROOT_URL = BASE_URL+"index.php/pelanggan/";

    public static RegisterAPI getApiService() {
        return RetrofitClient.getClient(BASE_ROOT_URL).create(RegisterAPI.class);
    }


}
