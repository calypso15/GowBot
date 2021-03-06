package net.ryanogrady.gowbot;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

public class Troop {

	private String name;
	private Map<Skill, Integer> maxSkills = new HashMap<Skill, Integer>();
	private Map<Skill, Integer> currentSkills = new HashMap<Skill, Integer>();
	private Kingdom kingdom;
	private Rarity rarity;
	private Set<TroopType> types = new HashSet<TroopType>();
	private Set<GemColor> colors = new HashSet<GemColor>();
	private Set<Spell> spells = new LinkedHashSet<Spell>();
	private Set<Trait> traits = new HashSet<Trait>();

	private Troop() {
	}
	
	public Troop(Troop orig) {
		if(orig == null) {
			throw new IllegalArgumentException("Null parameter passed to copy constructor.");
		}
		
		name = orig.name;
		maxSkills.putAll(orig.maxSkills);
		currentSkills.putAll(orig.currentSkills);
		kingdom = orig.kingdom;
		rarity = orig.rarity;
		types.addAll(orig.types);
		colors.addAll(orig.colors);
		spells.addAll(orig.spells);
		traits.addAll(orig.traits);
	}

	public String getName() {
		return name;
	}
	
	public int get(Skill skill) {
		return currentSkills.get(skill);
	}
	
	public void increase(Skill skill, int amount) {
		currentSkills.put(skill, currentSkills.get(skill)+amount);
		maxSkills.put(skill, maxSkills.get(skill)+amount);
	}
	
	public void decrease(Skill skill, int amount) {
		amount = Math.min(amount, currentSkills.get(skill));
		currentSkills.put(skill, currentSkills.get(skill)-amount);
		maxSkills.put(skill, maxSkills.get(skill)-amount);
	}
	
	public void damage(Skill skill, int amount) {
		amount = Math.min(amount, currentSkills.get(skill));
		currentSkills.put(skill, currentSkills.get(skill)-amount);
	}
	
	public void destroy(Skill skill) {
		currentSkills.put(skill, 0);
		maxSkills.put(skill, 0);
	}

	public Kingdom getKingdom() {
		return kingdom;
	}

	public Rarity getRarity() {
		return rarity;
	}

	public TroopType[] getTypes() {
		return types.toArray(new TroopType[0]);
	}

	public GemColor[] getColors() {
		return colors.toArray(new GemColor[0]);
	}

	public Spell[] getSpells() {
		return spells.toArray(new Spell[0]);
	}

	public Trait[] getTraits() {
		return traits.toArray(new Trait[0]);
	}

	public static ILife name(String name) {
		return new Troop.Builder(name);
	}

	public interface ILife {
		IAttack life(int life);
	}

	public interface IAttack {
		IArmor attack(int attack);
	}

	public interface IArmor {
		IMagic armor(int armor);
	}

	public interface IMagic {
		IBuild magic(int magic);
	}

	public interface IBuild {
		IBuild kingdom(Kingdom kingdom);

		IBuild rarity(Rarity rarity);

		IBuild addType(TroopType type);

		IBuild addColor(GemColor color);

		IBuild addSpell(Spell spell);

		IBuild addTrait(Trait trait);

		Troop build();
	}

	private static class Builder implements ILife, IAttack, IArmor, IMagic, IBuild {
		private Troop instance = new Troop();

		public Builder(String name) {
			instance.name = name;
			instance.maxSkills.put(Skill.MANA, 0);
			instance.currentSkills.put(Skill.MANA, 0);
		}

		@Override
		public IAttack life(int life) {
			instance.maxSkills.put(Skill.LIFE, life);
			instance.currentSkills.put(Skill.LIFE, life);
			return this;
		}

		@Override
		public IArmor attack(int attack) {
			instance.maxSkills.put(Skill.ATTACK, attack);
			instance.currentSkills.put(Skill.ATTACK, attack);
			return this;
		}

		@Override
		public IMagic armor(int armor) {
			instance.maxSkills.put(Skill.ARMOR, armor);
			instance.currentSkills.put(Skill.ARMOR, armor);
			return this;
		}

		@Override
		public IBuild magic(int magic) {
			instance.maxSkills.put(Skill.MAGIC, magic);
			instance.currentSkills.put(Skill.MAGIC, magic);
			return this;
		}

		@Override
		public IBuild kingdom(Kingdom kingdom) {
			instance.kingdom = kingdom;
			return this;
		}

		@Override
		public IBuild rarity(Rarity rarity) {
			instance.rarity = rarity;
			return this;
		}

		@Override
		public Builder addType(TroopType type) {
			instance.types.add(type);
			return this;
		}

		@Override
		public Builder addColor(GemColor color) {
			instance.colors.add(color);
			return this;
		}

		@Override
		public Builder addSpell(Spell spell) {
			instance.spells.add(spell);
			return this;
		}

		@Override
		public Builder addTrait(Trait trait) {
			instance.traits.add(trait);
			return this;
		}

		@Override
		public Troop build() {
			return instance;
		}
	}
}
