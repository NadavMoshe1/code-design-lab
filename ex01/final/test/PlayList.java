package test;

import java.util.Iterator;

public class PlayList extends Item implements Iterable<String>{

	private Item[] items;
	
	public PlayList(String name,Item[] items) {
		super(name);
		this.items=items;
	}
	
	public Item[] getItems() {
		return items;		
	}

	@Override
	public Iterator<String> iterator() {		
		return new PlayListIterator(this);
	}

}
