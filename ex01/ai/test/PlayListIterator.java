
package test;

import java.util.ArrayDeque;
import java.util.Arrays;
import java.util.Deque;
import java.util.Iterator;
import java.util.NoSuchElementException;

public class PlayListIterator implements Iterator<String> {

    private final Deque<Iterator<Item>> stack = new ArrayDeque<>();
    private Song nextSong;

    public PlayListIterator(PlayList pl) {
        stack.push(Arrays.asList(pl.getItems()).iterator());
    }

    @Override
    public boolean hasNext() {
        if (nextSong != null) {
            return true;
        }

        while (!stack.isEmpty()) {
            Iterator<Item> current = stack.peek();

            if (!current.hasNext()) {
                stack.pop();
                continue;
            }

            Item item = current.next();

            if (item instanceof Song) {
                nextSong = (Song) item;
                return true;
            }

            if (item instanceof PlayList) {
                stack.push(Arrays.asList(
                    ((PlayList) item).getItems()
                ).iterator());
            }
        }

        return false;
    }

    @Override
    public String next() {
        if (!hasNext()) {
            throw new NoSuchElementException();
        }

        String name = nextSong.name;
        nextSong = null;
        return name;
    }
}
