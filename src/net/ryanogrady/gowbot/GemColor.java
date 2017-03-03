package net.ryanogrady.gowbot;

import java.awt.Color;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;

public enum GemColor {
	UNKNOWN(0), RED(1), GREEN(2), BLUE(3), YELLOW(4), PURPLE(5), BROWN(6), SKULL(7);

	private final int i;
	private static Map<Integer, GemColor> map = new HashMap<Integer, GemColor>();
	
    static {
        for (GemColor color : GemColor.values()) {
            map.put(color.getValue(), color);
        }
    }

	GemColor(int i) {
		this.i = i;
	}

	public int getValue() {
		return i;
	}

	Color toColor() {
		return GemColor.toColor(this);
	}
	
	static GemColor fromInt(int i) {
		return map.get(i);
	}
	
	static GemColor random() {
		return fromInt(ThreadLocalRandom.current().nextInt(1, 8));
	}

	static Color toColor(GemColor c) {
		switch (c) {
		case UNKNOWN:
			return Color.gray;
		case RED:
			return Color.red;
		case GREEN:
			return Color.green;
		case BLUE:
			return Color.blue;
		case PURPLE:
			return Color.decode("0x800080");
		case YELLOW:
			return Color.yellow;
		case BROWN:
			return Color.decode("0xA52A2A");
		case SKULL:
			return Color.white;
		default:
			return Color.black;
		}
	}
}