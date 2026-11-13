package proyek.src.service;

import java.util.HashMap;
import proyek.src.collection.DataEvent;
import proyek.src.model.Event;

public class EventService {
    private DataEvent dataEvent = new DataEvent();

    public HashMap<String, Event> getDaftarEvent() {
        return dataEvent.getDaftarEvent();
    }

    public Event getEvent(String idEvent) {
        return dataEvent.getDaftarEvent().get(idEvent);
    }

    public void tambahEvent(Event event) {
        dataEvent.getDaftarEvent().put(event.getIdEvent(), event);
    }

    public void hapusEvent(String idEvent) {
        dataEvent.getDaftarEvent().remove(idEvent);
    }
}