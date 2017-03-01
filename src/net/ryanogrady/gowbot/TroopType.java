package net.ryanogrady.gowbot;

public class TroopType {
	private String name;
	
	public TroopType(String name) {
		this.name = name;
	}
	
	public TroopType(TroopType orig) {
		if(orig == null) {
			throw new IllegalArgumentException("Null parameter passed to copy constructor.");
		}
		
		name = new String(orig.name);
	}
}