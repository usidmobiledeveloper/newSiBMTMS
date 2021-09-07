package com.usid.mobilebmt.mandirisejahtera.model;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;


public class SysDB {
    private DatabaseHelper mDbHelper;
    private SQLiteDatabase mDb;

    private static final String DATABASE_NAME = "MobileBMTsys.db";
    private static final int DATABASE_VERSION = 2;
    private final Context mCtx;

    private static class DatabaseHelper extends SQLiteOpenHelper {
        DatabaseHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        }
    }

    public SysDB(Context ctx) {
        this.mCtx = ctx;
    }

    public SysDB open() throws SQLException {
        mDbHelper = new DatabaseHelper(mCtx);
        mDb = mDbHelper.getWritableDatabase();
        return this;
    }

    public void close() {
        if (mDbHelper != null) {
            mDbHelper.close();
        }
    }

    // sys
    public void CreateTableSys() {
        mDb.execSQL("CREATE TABLE if not exists log_sys (id INTEGER PRIMARY KEY AUTOINCREMENT,log_tgl VARCHAR(30),pesan VARCHAR);");
    }

    public void CreateTableTransfer() {
        mDb.execSQL("CREATE TABLE if not exists transfer_list (rek VARCHAR(30) PRIMARY KEY,nama VARCHAR);");
    }

    public void CreateTableTransferAB() {
        mDb.execSQL("CREATE TABLE if not exists transfer_listab (kdbank VARCHAR(4), nm_kdbank VARCHAR(30), rek VARCHAR(30) PRIMARY KEY,nama VARCHAR);");
    }

    public void CreateTableDonatur() {
        mDb.execSQL("CREATE TABLE if not exists donatur_list (npwz VARCHAR(30) PRIMARY KEY,nama VARCHAR);");
    }

    public void CreateTablePulsa() {
        mDb.execSQL("CREATE TABLE if not exists pulsa_list (nohp VARCHAR(30) PRIMARY KEY,oprt VARCHAR);");
    }

    public void CreateTablePLNpra() {
        mDb.execSQL("CREATE TABLE if not exists plnpra_list (idpel VARCHAR(30) PRIMARY KEY,nama VARCHAR);");
    }

    public void CreateTablePLNpasca() {
        mDb.execSQL("CREATE TABLE if not exists plnpasca_list (idpel VARCHAR(30) PRIMARY KEY,nama VARCHAR);");
    }

    public void CreateTablePDAM() {
        mDb.execSQL("CREATE TABLE if not exists pdam_list (idpel VARCHAR(30) PRIMARY KEY,nama VARCHAR);");
    }

    public void CreateTableTELKOM() {
        mDb.execSQL("CREATE TABLE if not exists telkom_list (idpel VARCHAR(30) PRIMARY KEY,nama VARCHAR);");
    }

    public void insertSys(String strTgl, String strPesan) {
        ContentValues initialValues = new ContentValues();
        initialValues.put("log_tgl", strTgl);
        initialValues.put("pesan", strPesan);
        mDb.insert("log_sys", null, initialValues);
    }

    public void insertTrf(String strRek, String strNama) {
        ContentValues initialValues = new ContentValues();
        initialValues.put("rek", strRek);
        initialValues.put("nama", strNama);
        mDb.insert("transfer_list", null, initialValues);
    }

    public void insertTrfab(String kdbank, String nm_kdbank, String strRek, String strNama) {
        ContentValues initialValues = new ContentValues();
        initialValues.put("kdbank", kdbank);
        initialValues.put("nm_kdbank", nm_kdbank);
        initialValues.put("rek", strRek);
        initialValues.put("nama", strNama);
        mDb.insert("transfer_listab", null, initialValues);
    }

    public void insertDonatur(String strRek, String strNama) {
        ContentValues initialValues = new ContentValues();
        initialValues.put("npwz", strRek);
        initialValues.put("nama", strNama);
        mDb.insert("donatur_list", null, initialValues);
    }

    public void insertPls(String strRek, String strNama) {
        ContentValues initialValues = new ContentValues();
        initialValues.put("nohp", strRek);
        initialValues.put("oprt", strNama);
        mDb.insert("pulsa_list", null, initialValues);
    }

    public void insertPLNpra(String strRek, String strNama) {
        ContentValues initialValues = new ContentValues();
        initialValues.put("idpel", strRek);
        initialValues.put("nama", strNama);
        mDb.insert("plnpra_list", null, initialValues);
    }

    public void insertPLNpasca(String strRek, String strNama) {
        ContentValues initialValues = new ContentValues();
        initialValues.put("idpel", strRek);
        initialValues.put("nama", strNama);
        mDb.insert("plnpasca_list", null, initialValues);
    }

    public void insertPDAM(String strRek, String strNama) {
        ContentValues initialValues = new ContentValues();
        initialValues.put("idpel", strRek);
        initialValues.put("nama", strNama);
        mDb.insert("pdam_list", null, initialValues);
    }

    public void insertTELKOM(String strRek, String strNama) {
        ContentValues initialValues = new ContentValues();
        initialValues.put("idpel", strRek);
        initialValues.put("nama", strNama);
        mDb.insert("telkom_list", null, initialValues);
    }

    public void Droptable() {
        mDb.execSQL("DROP TABLE IF EXISTS transfer_list");
    }

    public void DeleteData(String rek) {
        mDb.execSQL("DELETE FROM transfer_list WHERE rek='" + rek + "';");
    }

    public void DeleteDataAB(String kdbank, String rek) {
        mDb.execSQL("DELETE FROM transfer_listab WHERE kdbank ='" + kdbank + "' AND rek='" + rek + "';");
    }

    public void DeletePulsa(String rek) {
        mDb.execSQL("DELETE FROM pulsa_list WHERE nohp='" + rek + "';");
    }

    public void DeleteDonatur(String rek) {
        mDb.execSQL("DELETE FROM donatur_list WHERE npwz='" + rek + "';");
    }

    public void DeletePLNpra(String rek) {
        mDb.execSQL("DELETE FROM plnpra_list WHERE idpel='" + rek + "';");
    }

    public void DeletePLNpasca(String rek) {
        mDb.execSQL("DELETE FROM plnpasca_list WHERE idpel='" + rek + "';");
    }

    public void DeletePDAM(String rek) {
        mDb.execSQL("DELETE FROM pdam_list WHERE idpel='" + rek + "';");
    }

    public void DeleteTELKOM(String rek) {
        mDb.execSQL("DELETE FROM telkom_list WHERE idpel='" + rek + "';");
    }

    public void DeleteDataInbox(String tgl) {
        mDb.execSQL("DELETE FROM log_sys WHERE log_tgl='" + tgl + "';");
    }

    public Cursor cekTrfData(String rek) {
        Cursor mCursor = mDb.rawQuery("SELECT count(*) FROM transfer_list WHERE rek='" + rek + "';", null);
        return mCursor;
    }

    public Cursor cekTrfDataAB(String kdbank, String rek) {
        Cursor mCursor = mDb.rawQuery("SELECT count(*) FROM transfer_listab WHERE kdbank ='" + kdbank + "' AND rek='" + rek + "';", null);
        return mCursor;
    }

    public Cursor cekDonaturData(String nohp) {
        Cursor mCursor = mDb.rawQuery("SELECT count(*) FROM donatur_list WHERE npwz='" + nohp + "';", null);
        return mCursor;
    }

    public Cursor cekPulsaData(String nohp) {
        Cursor mCursor = mDb.rawQuery("SELECT count(*) FROM pulsa_list WHERE nohp='" + nohp + "';", null);
        return mCursor;
    }

    public Cursor cekPLNpraData(String idpel) {
        Cursor mCursor = mDb.rawQuery("SELECT count(*) FROM plnpra_list WHERE idpel='" + idpel + "';", null);
        return mCursor;
    }

    public Cursor cekPLNpascaData(String idpel) {
        Cursor mCursor = mDb.rawQuery("SELECT count(*) FROM plnpasca_list WHERE idpel='" + idpel + "';", null);
        return mCursor;
    }

    public Cursor cekPDAMData(String idpel) {
        Cursor mCursor = mDb.rawQuery("SELECT count(*) FROM pdam_list WHERE idpel='" + idpel + "';", null);
        return mCursor;
    }

    public Cursor cekTELKOMData(String idpel) {
        Cursor mCursor = mDb.rawQuery("SELECT count(*) FROM telkom_list WHERE idpel='" + idpel + "';", null);
        return mCursor;
    }

    public Cursor cekTrfData() {
        Cursor mCursor = mDb.rawQuery("SELECT count(*) FROM transfer_list;", null);
        return mCursor;
    }

    public Cursor cekTrfDataAB() {
        Cursor mCursor = mDb.rawQuery("SELECT count(*) FROM transfer_listab;", null);
        return mCursor;
    }

    public Cursor cekDonaturData() {
        Cursor mCursor = mDb.rawQuery("SELECT count(*) FROM donatur_list;", null);
        return mCursor;
    }

    public Cursor cekPulsaData() {
        Cursor mCursor = mDb.rawQuery("SELECT count(*) FROM pulsa_list;", null);
        return mCursor;
    }

    public Cursor cekPLNpraData() {
        Cursor mCursor = mDb.rawQuery("SELECT count(*) FROM plnpra_list;", null);
        return mCursor;
    }

    public Cursor cekPLNpascaData() {
        Cursor mCursor = mDb.rawQuery("SELECT count(*) FROM plnpasca_list;", null);
        return mCursor;
    }

    public Cursor cekPDAMData() {
        Cursor mCursor = mDb.rawQuery("SELECT count(*) FROM pdam_list;", null);
        return mCursor;
    }

    public Cursor cekTELKOMData() {
        Cursor mCursor = mDb.rawQuery("SELECT count(*) FROM telkom_list;", null);
        return mCursor;
    }

    public Cursor cekSysData() {
        Cursor mCursor = mDb.rawQuery("SELECT log_tgl,pesan,id FROM log_sys order by id desc limit 100;", null);
        return mCursor;
    }

    public Cursor daftarRekeningTabungan() {
        Cursor mCursor = mDb.rawQuery("SELECT rek,nama FROM transfer_list ORDER BY nama", null);
        return mCursor;
    }

    public Cursor daftarRekeningTabunganAB() {
        Cursor mCursor = mDb.rawQuery("SELECT kdbank,nm_kdbank,rek,nama FROM transfer_listab ORDER BY nama", null);
        return mCursor;
    }

    public Cursor daftarDonatur() {
        Cursor mCursor = mDb.rawQuery("SELECT npwz,nama FROM donatur_list ORDER BY npwz", null);
        return mCursor;
    }

    public Cursor daftarPulsa() {
        Cursor mCursor = mDb.rawQuery("SELECT nohp,oprt FROM pulsa_list ORDER BY oprt", null);
        return mCursor;
    }

    public Cursor daftarPLNpra() {
        Cursor mCursor = mDb.rawQuery("SELECT idpel,nama FROM plnpra_list ORDER BY nama", null);
        return mCursor;
    }

    public Cursor daftarPLNpasca() {
        Cursor mCursor = mDb.rawQuery("SELECT idpel,nama FROM plnpasca_list ORDER BY nama", null);
        return mCursor;
    }

    public Cursor daftarPDAM() {
        Cursor mCursor = mDb.rawQuery("SELECT idpel,nama FROM pdam_list ORDER BY nama", null);
        return mCursor;
    }

    public Cursor daftarTELKOM() {
        Cursor mCursor = mDb.rawQuery("SELECT idpel,nama FROM telkom_list ORDER BY nama", null);
        return mCursor;
    }

    public void CreateTableEToll() {
        mDb.execSQL("CREATE TABLE if not exists etoll_list (nohp VARCHAR(30) PRIMARY KEY,oprt VARCHAR);");
    }



    public void insertEToll(String strRek, String strNama) {
        ContentValues initialValues = new ContentValues();
        initialValues.put("nohp", strRek);
        initialValues.put("oprt", strNama);
        mDb.insert("etoll_list", null, initialValues);
    }



    public void DeleteEToll(String rek) {
        mDb.execSQL("DELETE FROM etoll_list WHERE nohp='" + rek + "';");
    }



    public Cursor cekETollData(String nohp) {
        Cursor mCursor = mDb.rawQuery("SELECT count(*) FROM etoll_list WHERE nohp='" + nohp + "';", null);
        return mCursor;
    }



    public Cursor cekETollData() {
        Cursor mCursor = mDb.rawQuery("SELECT count(*) FROM etoll_list;", null);
        return mCursor;
    }



    public Cursor daftarEToll() {
        Cursor mCursor = mDb.rawQuery("SELECT nohp,oprt FROM etoll_list ORDER BY oprt", null);
        return mCursor;
    }



    public void CreateTableBPJSpasca() {
        mDb.execSQL("CREATE TABLE if not exists bpjspasca_list (idpel VARCHAR(30) PRIMARY KEY,nama VARCHAR);");
    }

    public void insertBPJSpasca(String strRek, String strNama) {
        ContentValues initialValues = new ContentValues();
        initialValues.put("idpel", strRek);
        initialValues.put("nama", strNama);
        mDb.insert("bpjspasca_list", null, initialValues);
    }

    public void DeleteBPJSpasca(String rek) {
        mDb.execSQL("DELETE FROM bpjspasca_list WHERE idpel='" + rek + "';");
    }

    public Cursor cekBPJSpascaData(String idpel) {
        Cursor mCursor = mDb.rawQuery("SELECT count(*) FROM bpjspasca_list WHERE idpel='" + idpel + "';", null);
        return mCursor;
    }

    public Cursor cekBPJSpascaData() {
        Cursor mCursor = mDb.rawQuery("SELECT count(*) FROM bpjspasca_list;", null);
        return mCursor;
    }

    public Cursor daftarBPJSpasca() {
        Cursor mCursor = mDb.rawQuery("SELECT idpel,nama FROM bpjspasca_list ORDER BY nama", null);
        return mCursor;
    }

    public void CreateTableUang() {
        mDb.execSQL("CREATE TABLE if not exists uang_list (nohp VARCHAR(30) PRIMARY KEY,oprt VARCHAR);");
    }

    public void insertUang(String strRek, String strNama) {
        ContentValues initialValues = new ContentValues();
        initialValues.put("nohp", strRek);
        initialValues.put("oprt", strNama);
        mDb.insert("uang_list", null, initialValues);
    }

    public void DeleteUang(String rek) {
        mDb.execSQL("DELETE FROM uang_list WHERE nohp='" + rek + "';");
    }

    public Cursor cekUangData(String nohp) {
        Cursor mCursor = mDb.rawQuery("SELECT count(*) FROM uang_list WHERE nohp='" + nohp + "';", null);
        return mCursor;
    }

    public Cursor daftarUang() {
        Cursor mCursor = mDb.rawQuery("SELECT nohp,oprt FROM uang_list ORDER BY oprt", null);
        return mCursor;
    }

    public Cursor cekUangData() {
        Cursor mCursor = mDb.rawQuery("SELECT count(*) FROM uang_list;", null);
        return mCursor;
    }

    public void insertMultifinance(String strRek, String strNama) {
        ContentValues initialValues = new ContentValues();
        initialValues.put("idpel", strRek);
        initialValues.put("nama", strNama);
        mDb.insert("multifinance_list", null, initialValues);
    }

    public Cursor cekMultifinanceData(String idpel) {
        Cursor mCursor = mDb.rawQuery("SELECT count(*) FROM multifinance_list WHERE idpel='" + idpel + "';", null);
        return mCursor;
    }

    public Cursor cekMultifinanceData() {
        Cursor mCursor = mDb.rawQuery("SELECT count(*) FROM multifinance_list;", null);
        return mCursor;
    }

    public Cursor daftarMultifinance() {
        Cursor mCursor = mDb.rawQuery("SELECT idpel,nama FROM multifinance_list ORDER BY nama", null);
        return mCursor;
    }

    public void DeleteMultifinance(String rek) {
        mDb.execSQL("DELETE FROM multifinance_list WHERE idpel='" + rek + "';");
    }

    public void CreateTableMultifinance() {
        mDb.execSQL("CREATE TABLE if not exists multifinance_list (idpel VARCHAR(30) PRIMARY KEY,nama VARCHAR);");
    }
}
