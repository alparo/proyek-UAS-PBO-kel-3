package collection;

import java.time.LocalDateTime;
import model.*;
import service.AuthService;

/**
 * Data contoh (seed) yang dipakai saat database MySQL tidak tersedia
 * (mode memori). Isinya sama dengan database/db_penjualan_tiket.sql.
 */
public class DataAwal {
    public static void isi() {
        DataUser users = DataUser.getInstance();
        DataEvent events = DataEvent.getInstance();
        DataZona zonas = DataZona.getInstance();
        DataTiket tikets = DataTiket.getInstance();

        users.tambah(new User(1, "Admin KonserKu", "admin@konserku.id", AuthService.hash("admin123"), User.Role.ADMIN));
        users.tambah(new User(2, "Demo Pembeli", "demo@konserku.id", AuthService.hash("demo123"), User.Role.USER));

        events.tambah(new Event(1, "Jakarta Music Festival 2026", LocalDateTime.of(2026, 12, 20, 19, 0),
                "Jakarta International Stadium", "Malam musik bersama musisi pilihan Indonesia.", Event.Status.AKTIF));
        events.tambah(new Event(2, "Soundrenaline: Sana Sini", LocalDateTime.of(2026, 11, 28, 15, 30),
                "Bandung", "Festival musik lintas genre dan komunitas.", Event.Status.AKTIF));
        events.tambah(new Event(3, "Kadin Fest Bekasi 2027", LocalDateTime.of(2027, 1, 30, 10, 0),
                "Transera Waterpark, Bekasi", "Pameran, hiburan, dan pertunjukan.", Event.Status.AKTIF));
        events.tambah(new Event(4, "Coldplay Music of the Spheres Tour", LocalDateTime.of(2027, 5, 25, 19, 0),
                "GBK Jakarta", "Malam penuh cahaya dan musik bersama Coldplay.", Event.Status.AKTIF));
        events.tambah(new Event(5, "Dewa 19 Live in Concert", LocalDateTime.of(2027, 5, 31, 19, 30),
                "JIS Jakarta", "Konser spesial bersama Dewa 19.", Event.Status.AKTIF));

        zonas.tambah(new Zona(1, 1, "VIP", Zona.Tipe.NUMBERED));
        zonas.tambah(new Zona(2, 1, "Festival", Zona.Tipe.FREE_SEATING));
        zonas.tambah(new Zona(3, 2, "Regular", Zona.Tipe.FREE_SEATING));
        zonas.tambah(new Zona(4, 3, "Tribune", Zona.Tipe.NUMBERED));
        zonas.tambah(new Zona(5, 4, "VIP", Zona.Tipe.NUMBERED));
        zonas.tambah(new Zona(6, 4, "Festival", Zona.Tipe.FREE_SEATING));
        zonas.tambah(new Zona(7, 5, "VIP", Zona.Tipe.NUMBERED));
        zonas.tambah(new Zona(8, 5, "Festival", Zona.Tipe.FREE_SEATING));

        tikets.tambah(Tiket.buat("VIP", 1, 1, "VIP", 750000, 24));
        tikets.tambah(Tiket.buat("FESTIVAL", 2, 2, "Festival", 250000, 180));
        tikets.tambah(Tiket.buat("FESTIVAL", 3, 3, "Regular", 150000, 220));
        tikets.tambah(Tiket.buat("VIP", 4, 4, "Tribune", 200000, 24));
        tikets.tambah(Tiket.buat("EARLY_BIRD", 5, 2, "Early Bird", 175000, 80));
        tikets.tambah(Tiket.buat("VIP", 6, 5, "VIP", 3000000, 24));
        tikets.tambah(Tiket.buat("FESTIVAL", 7, 6, "Festival", 1500000, 100));
        tikets.tambah(Tiket.buat("VIP", 8, 7, "VIP", 1500000, 24));
        tikets.tambah(Tiket.buat("FESTIVAL", 9, 8, "Festival", 500000, 150));

        DataSeat seats = DataSeat.getInstance();
        int[] zonaBernomor = {1, 4, 5, 7};
        for (int zid : zonaBernomor)
            for (int i = 1; i <= 24; i++)
                seats.tambah(new Seat(seats.idBerikutnya(), zid, String.format("A%02d", i), Seat.Status.AVAILABLE));
    }
}
