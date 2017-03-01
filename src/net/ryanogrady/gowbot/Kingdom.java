package net.ryanogrady.gowbot;

public class Kingdom {
	private String name;
	
	public Kingdom(String name) {
		this.name = name;
	}
	
	public Kingdom(Kingdom orig) {
		if(orig == null) {
			throw new IllegalArgumentException("Null parameter passed to copy constructor.");
		}
		
		name = new String(orig.name);
	}
}