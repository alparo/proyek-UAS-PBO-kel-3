package proyek.src.model;

public class Event {
    private String idEvent;
    private String namaEvent;
    private String tanggal;
    private String lokasi;

    public Event(String idEvent, String namaEvent, String tanggal, String lokasi) {
        this.idEvent = idEvent;
        this.namaEvent = namaEvent;
        this.tanggal = tanggal;
        this.lokasi = lokasi;
    }

    public String getIdEvent() {
        return idEvent;
    }

    public String getNamaEvent() {
        return namaEvent;
    }

    public String getTanggal() {
        return tanggal;
    }

    public String getLokasi() {
        return lokasi;
    }
}