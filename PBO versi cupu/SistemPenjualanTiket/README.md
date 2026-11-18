# Sistem Penjualan Tiket Konser — KonserKu (Java Swing + JDBC)

Konversi dari prototype web (HTML) ke Java desktop: **Swing** (GUI), **Java Collection Framework** (data di memori), **JDBC + MySQL** (database).
Akun demo: `admin@konserku.id / admin123` (Admin) dan `demo@konserku.id / demo123` (Pembeli).

## Cara menjalankan (VS Code)
1. Install JDK 11+ dan ekstensi **Extension Pack for Java**.
2. *(Opsional, untuk database)* Import `database/db_penjualan_tiket.sql` lewat phpMyAdmin / `mysql -u root -p < database/db_penjualan_tiket.sql`.
   Taruh `mysql-connector-j-*.jar` di folder `lib/`. Sesuaikan URL/user/password di `src/database/Koneksi.java`.
3. Buka folder project di VS Code -> buka `src/Main.java` -> **Run**. (Atau `jalankan.bat` / `./jalankan.sh`.)
4. Layar login menunjukkan mode: **Mode database MySQL** atau **Mode memori** (otomatis jika DB/driver tidak ada).

## Arsitektur
| Layer | Paket | Peran |
|---|---|---|
| View | `gui` | Swing: Login, Dashboard (sidebar+CardLayout), Event, Tiket, ZonaSeat, Pembelian, Riwayat, Laporan |
| Controller/Service | `service` | Validasi & aturan bisnis, mengkoordinasi koleksi + DAO |
| Model | `model` | Entitas; `Tiket` abstract -> `TiketVIP/Festival/EarlyBird` |
| Memory | `collection` | `ArrayList` + `HashMap` per entitas (singleton `DataXxx`) |
| Database | `database` | `Koneksi` (JDBC) + DAO (PreparedStatement, transaksi commit/rollback) |

Alur data: saat start `Main` memuat tabel ke koleksi. Setiap perubahan: **service -> DAO (simpan ke DB) -> baru koleksi diubah**, sehingga jika DB gagal, memori tidak ikut berubah.

## Penyesuaian dari daftar folder awal
- Ditambah: `model/User`, `collection/DataUser` + `DataAwal` (data contoh), `service/AuthService`, `database/UserDAO`, `ZonaDAO`, `DatabaseException`, `gui/Tema`, `gui/Refreshable` (login, registrasi, dan tema bersama).
- File `...Frame` selain `LoginFrame` & `DashboardFrame` adalah **JPanel** yang dipasang di `DashboardFrame` (meniru sidebar web).
- Aturan bisnis tambahan: batas maksimal tiket per transaksi per jenis (VIP 6, Festival 10, Early Bird 4).

## Peta tugas
- **P10 Collection**: `src/collection/DataKoleksi.java`, `DataSeat.java`, `DataEvent.java`
- **P11 GUI Layout**: `src/gui/PembelianFrame.java` (atau `EventFrame.java`), `DashboardFrame.java`; screenshot ada di `dokumentasi/screenshot/`
- **P12 Event Handling**: `EventFrame.java` (`pasangEvent()`), `PembelianFrame.java`, `LoginFrame.prosesLogin()`
- **P13 JDBC**: `database/Koneksi.java`, `EventDAO.java`, `TransaksiDAO.simpanPembelian()`; skema `database/db_penjualan_tiket.sql`

### Catatan analisis (bahan penjelasan laporan)
- **ArrayList** dipilih untuk daftar (event, tiket, transaksi) karena urutan terjaga & mudah di-iterasi ke JTable/kartu; **HashMap<Integer,T>** untuk pencarian by-ID O(1) pada relasi tiket->zona->event dan validasi checkout; **HashMap<zonaId,List<Seat>>** untuk denah kursi per zona; **HashSet/LinkedHashSet** untuk kursi terpilih agar tidak ada duplikat; **LinkedHashMap** untuk laporan per event agar urutan tetap.
- **Layout**: BorderLayout (kerangka halaman & dashboard), CardLayout (pindah menu), GridLayout (kartu event, denah kursi, kartu statistik), GridBagLayout (form), FlowLayout (baris tombol), BoxLayout (sidebar & login).
- **JDBC**: URL/user/password di `Koneksi`; `PreparedStatement` mencegah SQL injection; `try-with-resources` menutup Statement/ResultSet; `SQLException` dibungkus `DatabaseException` lalu ditampilkan GUI lewat `JOptionPane`; pembelian memakai `setAutoCommit(false)` + `commit()`/`rollback()`.
