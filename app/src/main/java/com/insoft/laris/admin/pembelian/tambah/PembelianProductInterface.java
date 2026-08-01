package com.insoft.laris.admin.pembelian.tambah;

import java.util.HashMap;

public interface PembelianProductInterface {
    void EditProduct(HashMap<String,String> item, int position);
    void HapusProduct(HashMap<String,String> item ,int position);
}
