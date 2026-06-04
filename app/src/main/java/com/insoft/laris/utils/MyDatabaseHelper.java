package com.insoft.laris.utils;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.insoft.laris.model.Produk;

public class MyDatabaseHelper extends SQLiteOpenHelper {
    private Context context;
    private static final String DATABASE_NAME = "ikasir.db";
    private static final int DATABASE_VERSION = 1;

    private static final String TABLE_COUNTER = "counter_nota";
    private static final String TABLE_NAME = "tmp_penjualan";
    private static final String TABLE_PENJUALAN = "penjualan";
    private static final String TABLE_PENJUALAN_ITEM = "penjualan_item";
    private static final String TABLE_TRANSAKSI = "transaksi";
    private static final String MASTER_BARANG = "master_barang";
    private static final String MASTER_PELANGGAN = "master_pelanggan";

    private static final String COLUMN_ID = "id"; //0
    private static final String COLUMN_KD_BARANG = "kd_barang"; //1
    private static final String COLUMN_BARCODE = "barcode"; //2
    private static final String COLUMN_NM_BARANG = "nm_barang"; //3
    private static final String COLUMN_SATUAN = "satuan"; //4
    private static final String COLUMN_JUMLAH = "jumlah"; //5
    private static final String COLUMN_HARGA = "harga"; //6
    private static final String COLUMN_MODAL = "modal"; //7
    private static final String COLUMN_TOTAL = "total"; //8
    private static final String COLUMN_KD_USER = "kd_user";//9
    private static final String COLUMN_STATUS = "status";//10
    private static final String COLUMN_DISK = "disk";//10
    private static final String COLUMN_KONVERSI = "konversi";

    private static final String CUSTOMER_ID = "customer_id";
    private static final String CUSTOMER_NAME = "customer_name";
    private static final String CUSTOMER_ADDRESS = "customer_address";
    private static final String CUSTOMER_GROUP = "customer_group";
    private static final String ID_HOLD = "id_hold";//10
    private static final String TANGGAL = "tanggal";

    private static final String COLUMN_KATEGORI = "kd_kategori";
    private static final String COLUMN_HARGA_BELI = "harga_beli";
    private static final String COLUMN_HARGA_JUAL = "harga_jual";
    private static final String COLUMN_STOK = "stok";
    private static final String COLUMN_HJ = "hj";
    private static final String COLUMN_HARGA_MEMBER = "harga_member";
    private static final String COLUMN_DISKON_MEMBER = "diskon_member";
    private static final String COLUMN_SUPPLIER= "kd_supplier";
    private static final String COLUMN_DISKON = "diskon";

    private static final String COLUMN_CUSTMER_ID = "id";
    private static final String COLUMN_CUSTMER_CODE = "kd_pelanggan";
    private static final String COLUMN_CUSTMER_NAME = "nm_pelanggan";
    private static final String COLUMN_CUSTMER_ADDRESS = "alamat";
    private static final String COLUMN_CUSTMER_CONTACT = "contact";
    private static final String COLUMN_CUSTMER_GRUP = "grup";
    private static final String COLUMN_CUSTMER_PHONE = "telepon";


    private static final String COLUMN_NOTA = "nota";
    private static final String COLUMN_KETERANGAN = "keterangan";
    private static final String COLUMN_TANGGAL = "tanggal";
    private static final String COLUMN_BELANJA = "belanja";
    private static final String COLUMN_BAYAR = "bayar";
    private static final String COLUMN_DONASI = "donasi";
    private static final String COLUMN_KEMBALI = "kembali";
    private static final String COLUMN_DEPO = "depo";
    private static final String COLUMN_BANK_DEPOSIT = "bank_deposit";

    public MyDatabaseHelper(@Nullable Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        String query_counter = "CREATE TABLE " + TABLE_COUNTER + "(" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "last_nota INTEGER)";
        db.execSQL(query_counter);


        String query_penjualan_item = "CREATE TABLE " + TABLE_PENJUALAN_ITEM +
                " (" + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "+
                COLUMN_NOTA + " TEXT, " +
                COLUMN_KD_BARANG + " TEXT, " +
                COLUMN_BARCODE + " TEXT, " +
                COLUMN_NM_BARANG + " TEXT, " +
                COLUMN_SATUAN + " INTEGER, " +
                COLUMN_JUMLAH + " INTEGER, " +
                COLUMN_HARGA + " INTEGER, " +
                COLUMN_MODAL + " INTEGER, " +
                COLUMN_TOTAL + " TEXT, " +
                COLUMN_STATUS + " INTEGER, " +
                COLUMN_DISK + " INTEGER);";
        db.execSQL(query_penjualan_item);


        String query_penjualan = "CREATE TABLE " + TABLE_PENJUALAN +
                " (" + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "+
                COLUMN_NOTA + " TEXT, " +
                COLUMN_CUSTMER_CODE + " TEXT, " +
                COLUMN_KETERANGAN + " TEXT, " +
                COLUMN_TANGGAL + " TEXT, " +
                COLUMN_BELANJA + " INTEGER, " +
                COLUMN_BAYAR + " INTEGER, " +
                COLUMN_DONASI + " INTEGER, " +
                COLUMN_KEMBALI + " INTEGER, " +
                COLUMN_KD_USER + " TEXT, " +
                COLUMN_DEPO + " INTEGER, " +
                COLUMN_BANK_DEPOSIT + " INTEGER);";
        db.execSQL(query_penjualan);



        String query = "CREATE TABLE " + TABLE_NAME +
                " (" + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "+
                COLUMN_KD_BARANG + " TEXT, " +
                COLUMN_BARCODE + " TEXT, " +
                COLUMN_NM_BARANG + " TEXT, " +
                COLUMN_SATUAN + " TEXT, " +
                COLUMN_JUMLAH + " INTEGER, " +
                COLUMN_HARGA + " INTEGER, " +
                COLUMN_MODAL + " INTEGER, " +
                COLUMN_TOTAL + " INTEGER, " +
                COLUMN_KD_USER + " TEXT, " +
                COLUMN_STATUS + " INTEGER, " +
                COLUMN_DISK + " INTEGER, " +
                COLUMN_KONVERSI + " INTEGER);";


        db.execSQL(query);


        String transaksi_table = "CREATE TABLE " + TABLE_TRANSAKSI +
                " (" + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "+
                COLUMN_KD_BARANG + " TEXT, " +
                COLUMN_BARCODE + " TEXT, " +
                COLUMN_NM_BARANG + " TEXT, " +
                COLUMN_SATUAN + " TEXT, " +
                COLUMN_JUMLAH + " INTEGER, " +
                COLUMN_HARGA + " INTEGER, " +
                COLUMN_MODAL + " INTEGER, " +
                COLUMN_TOTAL + " INTEGER, " +
                COLUMN_KD_USER + " TEXT, " +
                COLUMN_STATUS + " INTEGER, " +
                COLUMN_DISK + " INTEGER, " +
                CUSTOMER_ID + " TEXT, " +
                CUSTOMER_NAME + " TEXT, " +
                CUSTOMER_ADDRESS + " TEXT, " +
                CUSTOMER_GROUP + " TEXT, " +
                TANGGAL + " TEXT, " +
                ID_HOLD + " INTEGER);";


        db.execSQL(transaksi_table);


        String master_barang_table = "CREATE TABLE " + MASTER_BARANG +
                " (" + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "+
                COLUMN_KD_BARANG + " TEXT, " +
                COLUMN_BARCODE + " TEXT, " +
                COLUMN_NM_BARANG + " TEXT, " +
                COLUMN_KATEGORI + " TEXT, " +
                COLUMN_HARGA_BELI + " INTEGER, " +
                COLUMN_HARGA_JUAL + " INTEGER, " +
                COLUMN_SATUAN + " TEXT, " +
                COLUMN_STOK+ " INTEGER, " +
                COLUMN_KONVERSI + " INTEGER, " +
                COLUMN_HJ + " INTEGER, " +
                COLUMN_HARGA_MEMBER + " INTEGER, " +
                COLUMN_DISKON_MEMBER + " INTEGER, " +
                COLUMN_SUPPLIER + " TEXT, " +
                COLUMN_DISKON + " INTEGER);";

        db.execSQL(master_barang_table);


        String master_pelanggan_table = "CREATE TABLE " + MASTER_PELANGGAN +
                " (" + COLUMN_CUSTMER_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "+
                COLUMN_CUSTMER_CODE + " TEXT, " +
                COLUMN_CUSTMER_NAME + " TEXT, " +
                COLUMN_CUSTMER_ADDRESS + " TEXT, " +
                COLUMN_CUSTMER_CONTACT + " TEXT, " +
                COLUMN_CUSTMER_PHONE + " TEXT, " +
                COLUMN_CUSTMER_GRUP + " INTEGER);";

        db.execSQL(master_pelanggan_table);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_TRANSAKSI);
        db.execSQL("DROP TABLE IF EXISTS " + MASTER_BARANG);
        db.execSQL("DROP TABLE IF EXISTS " + MASTER_PELANGGAN);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_PENJUALAN);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_PENJUALAN_ITEM);
        db.execSQL("DROP TABLE IF EXISTS " + MASTER_PELANGGAN);
    }


    public void insert_master_pelanggan(
            String code,
            String name,
            String address,
            String contact,
            String grup,
            String phone

    ) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();


        cv.put(COLUMN_CUSTMER_CODE, code);
        cv.put(COLUMN_CUSTMER_NAME, name);
        cv.put(COLUMN_CUSTMER_ADDRESS, address);
        cv.put(COLUMN_CUSTMER_CONTACT, contact);
        cv.put(COLUMN_CUSTMER_GRUP, grup);
        cv.put(COLUMN_CUSTMER_PHONE, phone);

        long result =  db.insert(MASTER_PELANGGAN, null, cv);
        if(result == -1) {
            Toast.makeText(context, "Gagal Tambah Item Master Pelanggan", Toast.LENGTH_SHORT).show();
        }else {

        }
    }


    public void insert_master_barang(
            String kdbarang,
            String barcode,
            String nmbarang,
            String kategori,
            int harga_beli,
            int harga_jual,
            String satuan,
            int stok,
            int konversi,
            int hj,
            int harga_member,
            int diskon_member,
            String supplier,
            int diskon
    ) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();


        cv.put(COLUMN_KD_BARANG, kdbarang);
        cv.put(COLUMN_BARCODE, barcode);
        cv.put(COLUMN_NM_BARANG, nmbarang);
        cv.put(COLUMN_KATEGORI, kategori);
        cv.put(COLUMN_HARGA_BELI, harga_beli);
        cv.put(COLUMN_HARGA_JUAL, harga_jual);
        cv.put(COLUMN_SATUAN, satuan);
        cv.put(COLUMN_STOK, stok);
        cv.put(COLUMN_KONVERSI, konversi);
        cv.put(COLUMN_HJ, hj);
        cv.put(COLUMN_HARGA_MEMBER, harga_member);
        cv.put(COLUMN_DISKON_MEMBER, diskon_member);
        cv.put(COLUMN_SUPPLIER, supplier);
        cv.put(COLUMN_DISKON, diskon);


        long result =  db.insert(MASTER_BARANG, null, cv);
        if(result == -1) {
            Toast.makeText(context, "Gagal Tambah Item Master Barang", Toast.LENGTH_SHORT).show();
        }else {
//            Toast.makeText(context, "Sukses Tambah Item", Toast.LENGTH_SHORT).show();
        }
    }

    public void tambahtransaksi(
            String kdbarang,
            String barcode,
            String nmbarang,
            String satuan,
            int jumlah,
            int harga,
            int modal,
            int total,
            String kduser,
            int status,
            int disk,
            String customerid,
            String customername,
            String customeraddress,
            String customergroup,
            String tanggal,
            int idhold
    ) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();


        cv.put(COLUMN_KD_BARANG, kdbarang);
        cv.put(COLUMN_BARCODE, barcode);
        cv.put(COLUMN_NM_BARANG, nmbarang);
        cv.put(COLUMN_SATUAN, satuan);
        cv.put(COLUMN_JUMLAH, jumlah);
        cv.put(COLUMN_HARGA, harga);
        cv.put(COLUMN_MODAL, modal);
        cv.put(COLUMN_TOTAL, total);
        cv.put(COLUMN_KD_USER, kduser);
        cv.put(COLUMN_STATUS, status);
        cv.put(COLUMN_DISK, disk);
        cv.put(CUSTOMER_ID, customerid);
        cv.put(CUSTOMER_NAME, customername);
        cv.put(CUSTOMER_ADDRESS, customeraddress);
        cv.put(CUSTOMER_GROUP, customergroup);
        cv.put(TANGGAL, tanggal);
        cv.put(ID_HOLD, idhold);


        long result =  db.insert(TABLE_TRANSAKSI, null, cv);
        if(result == -1) {
            Toast.makeText(context, "Gagal Tambah Transaksi", Toast.LENGTH_SHORT).show();
        }else {
            Toast.makeText(context, "Sukses Tambah Transaksi", Toast.LENGTH_SHORT).show();
        }
    }

    public void tambahitem(
            String kdbarang,
            String barcode,
            String nmbarang,
            String satuan,
            int jumlah,
            int harga,
            int modal,
            int total,
            String kduser,
            int status,
            int disk,
            int konversi
            ) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();


        cv.put(COLUMN_KD_BARANG, kdbarang);
        cv.put(COLUMN_BARCODE, barcode);
        cv.put(COLUMN_NM_BARANG, nmbarang);
        cv.put(COLUMN_SATUAN, satuan);
        cv.put(COLUMN_JUMLAH, jumlah);
        cv.put(COLUMN_HARGA, harga);
        cv.put(COLUMN_MODAL, modal);
        cv.put(COLUMN_TOTAL, total);
        cv.put(COLUMN_KD_USER, kduser);
        cv.put(COLUMN_STATUS, status);
        cv.put(COLUMN_DISK, disk);
        cv.put(COLUMN_KONVERSI, konversi);


        long result =  db.insert(TABLE_NAME, null, cv);
        if(result == -1) {
            Toast.makeText(context, "Gagal Tambah Item", Toast.LENGTH_SHORT).show();
        }else {
            Toast.makeText(context, "Sukses Tambah Penjualan", Toast.LENGTH_SHORT).show();
        }
    }

    public void updateitem(
            String idproduk,
            int qty,
            int harga,
            int total
    ) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(COLUMN_JUMLAH, qty);
        cv.put(COLUMN_HARGA, harga);
        cv.put(COLUMN_TOTAL, total);

        long result =  db.update(TABLE_NAME, cv, "kd_barang=?", new String[]{idproduk});
        if(result == 0) {
            Toast.makeText(context, "Gagal Update Item", Toast.LENGTH_SHORT).show();
        }else {

        }
    }


    public Cursor tampilkandata() {
        String query = "SELECT * FROM " + TABLE_NAME + " ORDER BY "+ COLUMN_ID+" DESC";
        SQLiteDatabase db  = this.getReadableDatabase();

        Cursor cursor = null;
        if(db != null) {
            cursor = db.rawQuery(query, null);
        }
        return cursor;
    }

    public Cursor tampilkan_master_barang(String keyword) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor;
        if (keyword == null || keyword.isEmpty()) {
            cursor = db.rawQuery("SELECT * FROM " + MASTER_BARANG + " ORDER BY "+ COLUMN_ID+" DESC", null);
        } else {
            cursor = db.rawQuery("SELECT * FROM "+ MASTER_BARANG +" WHERE nm_barang LIKE ?",
                    new String[]{"%" + keyword + "%"});
        }
        return cursor;
    }


    public Cursor tampilkan_master_pelanggan(String keyword) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor;
        if (keyword == null || keyword.isEmpty()) {
            cursor = db.rawQuery("SELECT * FROM " + MASTER_PELANGGAN + " ORDER BY "+ COLUMN_ID+" DESC", null);
        } else {
            cursor = db.rawQuery("SELECT * FROM "+ MASTER_PELANGGAN +" WHERE nm_pelanggan LIKE ?",
                    new String[]{"%" + keyword + "%"});
        }
        return cursor;
    }


    public Cursor displayItem(String idhold) {
        String query = "SELECT * FROM " + TABLE_TRANSAKSI + " WHERE "+ID_HOLD+"="+idhold+" ORDER BY "+ COLUMN_ID+" DESC";
        SQLiteDatabase db  = this.getReadableDatabase();

        Cursor cursor = null;
        if(db != null) {
            cursor = db.rawQuery(query, null);
        }
        return cursor;
    }

    public Cursor tampilkantransaksi() {
        String query = "SELECT "+COLUMN_ID+","+CUSTOMER_NAME+","+CUSTOMER_ADDRESS+", SUM("+COLUMN_TOTAL+"),"+ID_HOLD+","+TANGGAL+" FROM " + TABLE_TRANSAKSI + " GROUP BY "+ ID_HOLD +" ORDER BY "+ COLUMN_ID+" DESC";
        SQLiteDatabase db  = this.getReadableDatabase();

        Cursor cursor = null;
        if(db != null) {
            cursor = db.rawQuery(query, null);
        }
        return cursor;
    }

    public Cursor tampilkan_penjualan() {

        String query = "SELECT p.id, c.nm_pelanggan, c.alamat, p.belanja, p.tanggal, p.nota " +
                "FROM "+TABLE_PENJUALAN+" p " +
                "LEFT JOIN "+MASTER_PELANGGAN+" c ON p.kd_pelanggan = c.kd_pelanggan ORDER BY p.id DESC";



        SQLiteDatabase db  = this.getReadableDatabase();

            Cursor cursor = null;
            if(db != null) {
                cursor = db.rawQuery(query, null);
            }
            return cursor;

    }

    public Cursor tampilkan_penjualan_item(String nota) {
        String query = "SELECT "+COLUMN_ID+","+COLUMN_NM_BARANG+","+COLUMN_TOTAL+", "+COLUMN_JUMLAH+","+COLUMN_HARGA+" FROM " + TABLE_PENJUALAN_ITEM + " WHERE "+ COLUMN_NOTA+ "='"+ nota +"'  ORDER BY "+ COLUMN_ID+" DESC";
        SQLiteDatabase db  = this.getReadableDatabase();

        Cursor cursor = null;
        if(db != null) {
            cursor = db.rawQuery(query, null);
        }
        return cursor;
    }


    public Cursor periksadata(String kdbarang) {
        String query = "SELECT * FROM " + TABLE_NAME + " WHERE "+ COLUMN_KD_BARANG+ "='"+ kdbarang +"'";
        SQLiteDatabase db  = this.getReadableDatabase();

        Cursor cursor = null;
        if(db != null) {
            cursor = db.rawQuery(query, null);

        }
        return cursor;
    }



    public void hapusbaris(String row_id) {
        SQLiteDatabase db = this.getWritableDatabase();
        long result = db.delete(TABLE_NAME, "kd_barang=?", new String[]{row_id});
        if(result == -1) {
            Toast.makeText(context, "Gagal Hapus Item", Toast.LENGTH_SHORT).show();
        }else {
            Toast.makeText(context, "Sukses Hapus Item", Toast.LENGTH_SHORT).show();
        }
    }


    public void hapustransaksibyhold(String idhold) {
        SQLiteDatabase db = this.getWritableDatabase();
        long result = db.delete(TABLE_TRANSAKSI, "id_hold=?", new String[]{idhold});
        if(result == -1) {
            Toast.makeText(context, "Gagal Hapus Transaksi", Toast.LENGTH_SHORT).show();
        }else {
            Toast.makeText(context, "Sukses Hapus Transaksi", Toast.LENGTH_SHORT).show();
        }
    }

    public boolean is_master_barang_empty() {
        SQLiteDatabase db  = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT COUNT(*) FROM "+MASTER_BARANG, null);
        boolean isEmpty = true;
        if (cursor.moveToFirst()) {
            int count = cursor.getInt(0);
            isEmpty = (count == 0);
        }
        cursor.close();
        return isEmpty;
    }


    public boolean is_master_pelanggan_empty() {
        SQLiteDatabase db  = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT COUNT(*) FROM "+MASTER_PELANGGAN, null);
        boolean isEmpty = true;
        if (cursor.moveToFirst()) {
            int count = cursor.getInt(0);
            isEmpty = (count == 0);
        }
        cursor.close();
        return isEmpty;
    }





    public void hapussemua() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_NAME, null, null);

    }


    public void hapustransaksi() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_TRANSAKSI, null, null);

    }


    public void clear_master_barang() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(MASTER_BARANG, null, null);

    }


    public void clear_master_pelanggan() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(MASTER_PELANGGAN, null, null);

    }

    public int totalpenjualan(){
        int total =0;
        SQLiteDatabase db = getReadableDatabase();

        Cursor cursor = db.rawQuery("SELECT SUM("+ COLUMN_TOTAL +") FROM "+TABLE_NAME, null);
        if(cursor.moveToFirst()) {
            total = cursor.getInt(0);
        }
        return total;
    }



    public int totalitem(){
        int total =0;
        SQLiteDatabase db = getReadableDatabase();

        Cursor cursor = db.rawQuery("SELECT COUNT("+ COLUMN_JUMLAH +") FROM "+TABLE_NAME, null);
        if(cursor.moveToFirst()) {
            total = cursor.getInt(0);
        }

        return total;
    }


    public Produk findProdukByBarcode(String barcode) {
        SQLiteDatabase db = this.getReadableDatabase();
        Produk p = null;
        Cursor cursor = db.rawQuery("SELECT * FROM "+MASTER_BARANG+" WHERE barcode = ?", new String[]{barcode});
        if (cursor.moveToFirst()) {
            p = new Produk();
            p.setKd_barang(cursor.getString(cursor.getColumnIndexOrThrow("kd_barang")));
            p.setBarcode(cursor.getString(cursor.getColumnIndexOrThrow("barcode")));
            p.setNm_barang(cursor.getString(cursor.getColumnIndexOrThrow("nm_barang")));
            p.setSatuan(cursor.getString(cursor.getColumnIndexOrThrow("satuan")));
            p.setHarga_beli(cursor.getInt(cursor.getColumnIndexOrThrow("harga_beli")));
            p.setHarga_jual(cursor.getInt(cursor.getColumnIndexOrThrow("harga_jual")));
            p.setHarga_member(cursor.getInt(cursor.getColumnIndexOrThrow("harga_member")));
            p.setDiskon_member(cursor.getInt(cursor.getColumnIndexOrThrow("diskon_member")));
            p.setHj(cursor.getInt(cursor.getColumnIndexOrThrow("hj")));
            p.setStok(cursor.getInt(cursor.getColumnIndexOrThrow("stok")));
            p.setKonversi(cursor.getInt(cursor.getColumnIndexOrThrow("konversi")));
            p.setKd_supplier(cursor.getString(cursor.getColumnIndexOrThrow("kd_supplier")));
            p.setDiskon(cursor.getInt(cursor.getColumnIndexOrThrow("diskon")));
        }
        cursor.close();
        db.close();
        return p;
    }


    public Cursor get_barang_by_kode(String kd_barang) {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery("SELECT * FROM " + MASTER_BARANG + " WHERE " + COLUMN_KD_BARANG + " = ?", new String[]{kd_barang});
    }

    public String generateNota() {
        SQLiteDatabase db = this.getWritableDatabase();
        int nextNumber = 1;

        // cek apakah sudah ada data
        Cursor cursor = db.rawQuery("SELECT last_nota FROM "+ TABLE_COUNTER +" ORDER BY id DESC LIMIT 1", null);
        if (cursor != null && cursor.moveToFirst()) {
            int lastNota = cursor.getInt(0);
            nextNumber = lastNota + 1;
        }
        if (cursor != null) cursor.close();

        // simpan/update counter terbaru
        ContentValues cv = new ContentValues();
        cv.put("last_nota", nextNumber);
        db.insert(TABLE_COUNTER, null, cv);

        // format nomor nota, contoh: INV00016
        return String.format("INP%05d", nextNumber);
    }


    public void tambah_penjualan(
            String nota,
            String cust_cd,
            String keterangan,
            String tanggal,
            int belanja,
            int bayar,
            int donasi,
            int kembali,
            String kd_user,
            int depo,
            int bank_deposit
    ) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(COLUMN_NOTA, nota);
        cv.put(COLUMN_CUSTMER_CODE, cust_cd);
        cv.put(COLUMN_KETERANGAN, keterangan);
        cv.put(COLUMN_TANGGAL, tanggal);
        cv.put(COLUMN_BELANJA, belanja);
        cv.put(COLUMN_BAYAR, bayar);
        cv.put(COLUMN_DONASI, donasi);
        cv.put(COLUMN_KEMBALI, kembali);
        cv.put(COLUMN_KD_USER, kd_user);
        cv.put(COLUMN_DEPO, depo);
        cv.put(COLUMN_BANK_DEPOSIT, bank_deposit);

        long result =  db.insert(TABLE_PENJUALAN, null, cv);
        if(result == -1) {
            Toast.makeText(context, "Gagal Tambah Penjualan", Toast.LENGTH_SHORT).show();
        }else {
            Toast.makeText(context, "Sukses Tambah Penjualan", Toast.LENGTH_SHORT).show();
        }
    }



    public void tambah_penjualan_item(
            String nota,
            String kd_barang,
            String barcode,
            String nm_barang,
            String satuan,
            int jumlah,
            int harga,
            int modal,
            int total,
            int status,
            int disk
    ) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(COLUMN_NOTA, nota);
        cv.put(COLUMN_KD_BARANG, kd_barang);
        cv.put(COLUMN_BARCODE, barcode);
        cv.put(COLUMN_NM_BARANG, nm_barang);
        cv.put(COLUMN_SATUAN, satuan);
        cv.put(COLUMN_JUMLAH, jumlah);
        cv.put(COLUMN_HARGA, harga);
        cv.put(COLUMN_MODAL, modal);
        cv.put(COLUMN_TOTAL, total);
        cv.put(COLUMN_STATUS, status);
        cv.put(COLUMN_DISK, disk);

        long result =  db.insert(TABLE_PENJUALAN_ITEM, null, cv);
        if(result == -1) {
            Toast.makeText(context, "Gagal Tambah Penjualan Item", Toast.LENGTH_SHORT).show();
        }else {
            Toast.makeText(context, "Sukses Tambah Penjualan Item", Toast.LENGTH_SHORT).show();
        }
    }



}

