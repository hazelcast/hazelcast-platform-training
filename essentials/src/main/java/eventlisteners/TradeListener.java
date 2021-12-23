package eventlisteners;

import com.hazelcast.core.EntryEvent;
import com.hazelcast.core.EntryListener;
import com.hazelcast.map.MapEvent;
import dto.Trade;

public class TradeListener implements EntryListener<String, Trade> {

    @Override
    public void entryAdded(EntryEvent<String, Trade> event) {
// Print console message when map entry is added, listing stock symbol and price.
        System.out.println("New symbol " + event.getKey() + ", price: " + event.getValue().getPrice());
    }

    @Override
    public void entryUpdated(EntryEvent<String, Trade> event) {
// Print console message when map entry is updated, listing stock symbol and price.
        System.out.println("Symbol updated " + event.getKey() + ", price: " + event.getValue().getPrice());
    }

    @Override
    public void entryEvicted(EntryEvent<String, Trade> event) {

    }

    @Override
    public void entryExpired(EntryEvent<String, Trade> event) {

    }

    @Override
    public void entryRemoved(EntryEvent<String, Trade> event) {

    }


    @Override
    public void mapCleared(MapEvent event) {

    }

    @Override
    public void mapEvicted(MapEvent event) {

    }
}
