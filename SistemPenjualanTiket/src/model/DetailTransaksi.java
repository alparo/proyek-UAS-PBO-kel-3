package model;

import java.util.ArrayList;
import java.util.List;

/** Baris item pada sebuah transaksi (tiket apa, berapa, kursi mana). */
public class DetailTransaksi {
    private int id;
    private int transaksiId;
    private int tiketId;
    private int eventId;
    private int qty;
    private double hargaSatuan;
    private List<Integer> seatIds = new ArrayList<>();

    public DetailTransaksi(int id, int transaksiId, int tiketId, int eventId, int qty, double hargaSatuan) {
        this.id = id; this.transaksiId = transaksiId; this.tiketId = tiketId;
        this.eventId = eventId; this.qty = qty; this.hargaSatuan = hargaSatuan;
    }

    public double getSubtotal() { return hargaSatuan * qty; }

    public int getId() { return id; }
    public int getTransaksiId() { return transaksiId; }
    public int getTiketId() { return tiketId; }
    public int getEventId() { return eventId; }
    public int getQty() { return qty; }
    public double getHargaSatuan() { return hargaSatuan; }
    public List<Integer> getSeatIds() { return seatIds; }
}
