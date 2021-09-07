package com.usid.mobilebmt.mandirisejahtera.dashboard.riwayat;

public class ItemRiwayat {

    String no;
    String faktur, tgl, jumlah, keterangan, trxid, jenis, produk, tujuan;

    public ItemRiwayat(String faktur, String no, String tgl, String jumlah, String keterangan, String trxid, String jenis, String produk, String tujuan) {
        this.no = no;
        this.faktur = faktur;
        this.tgl = tgl;
        this.jumlah = jumlah;
        this.keterangan = keterangan;
        this.trxid = trxid;
        this.jenis = jenis;
        this.produk = produk;
        this.tujuan = tujuan;
    }

    public String getNo() {
        return no;
    }

    public void setNo(String no) {
        this.no = no;
    }

    public String getTgl() {
        return tgl;
    }

    public void setTgl(String tgl) {
        this.tgl = tgl;
    }

    public String getJumlah() {
        return jumlah;
    }

    public void setJumlah(String jumlah) {
        this.jumlah = jumlah;
    }

    public String getKeterangan() {
        return keterangan;
    }

    public void setKeterangan(String keterangan) {
        this.keterangan = keterangan;
    }

    public String getTrxid() {
        return trxid;
    }

    public void setTrxid(String trxid) {
        this.trxid = trxid;
    }

    public String getFaktur() {
        return faktur;
    }

    public void setFaktur(String faktur) {
        this.faktur = faktur;
    }

    public String getJenis() {
        return jenis;
    }

    public void setJenis(String jenis) {
        this.jenis = jenis;
    }

    public String getProduk() {
        return produk;
    }

    public void setProduk(String produk) {
        this.produk = produk;
    }

    public String getTujuan() {
        return tujuan;
    }

    public void setTujuan(String tujuan) {
        this.tujuan = tujuan;
    }
}
