package test;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.NoSuchElementException;

public class PlayListIterator implements Iterator<String> {

    private ArrayList<String> songs = new ArrayList<String>();
    private int index = 0;

    public PlayListIterator(PlayList pl) {
        addSongs(pl);
    }

    private void addSongs(PlayList pl) {
        for (Item item : pl.getItems()) {
            if (item instanceof Song) {
                songs.add(item.name);
            } else if (item instanceof PlayList) {
                addSongs((PlayList) item);
            }
        }
    }

    @Override
    public boolean hasNext() {
        return index < songs.size();
    }

    @Override
    public String next() {
        if (!hasNext()) {
            throw new NoSuchElementException();
        }

        String song = songs.get(index);
        index++;
        return song;
    }
}
