package proyek.src.collection;

import java.util.HashMap;
import proyek.src.model.Event;

public class DataEvent {
    private HashMap<String, Event> daftarEvent = new HashMap<>();

    public DataEvent() {
        daftarEvent.put("EV001", new Event(
            "EV001", "Konser Musik 2026", "20 Desember 2026", "Jakarta"
        ));

        daftarEvent.put("EV002", new Event(
            "EV002", "Festival Musik 2026", "25 Desember 2026", "Bandung"
        ));
    }

    public HashMap<String, Event> getDaftarEvent() {
        return daftarEvent;
    }
}