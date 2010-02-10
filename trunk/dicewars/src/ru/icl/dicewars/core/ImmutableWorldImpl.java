package ru.icl.dicewars.core;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import ru.icl.dicewars.client.Flag;
import ru.icl.dicewars.client.Land;
import ru.icl.dicewars.client.World;

public class ImmutableWorldImpl implements World, Serializable {
	private Flag myFlag;
	private Integer availableLeadCount;
	private Set<Flag> flags = new HashSet<Flag>();
	private Set<Land> lands = new HashSet<Land>();
	private Map<Flag, Integer> diceReserve = new HashMap<Flag, Integer>();

	public ImmutableWorldImpl(final World world) {
		if (world.getAvailableLeadCount() < 0)
			throw new IllegalArgumentException();
		if (world.getLands() == null)
			throw new IllegalArgumentException();
		if (world.getFlags() == null)
			throw new IllegalArgumentException();
		if (world.getMyFlag() == null)
			throw new IllegalArgumentException();
		this.availableLeadCount = world.getAvailableLeadCount();
		this.flags = Collections.unmodifiableSet(new HashSet<Flag>(world.getFlags()));
		this.myFlag = world.getMyFlag();
		
		Map<Flag, Integer> diceReserveMap = new HashMap<Flag, Integer>();
		
		for (Flag flag : this.flags){
			int diceCountInReserve = world.getDiceCountInReserve(flag);
			if (diceCountInReserve >= 0){
				diceReserveMap.put(flag, diceCountInReserve);
			}else{
				throw new IllegalArgumentException();
			}
		}
		
		this.diceReserve = Collections.unmodifiableMap(diceReserveMap);
		
		final Map<Integer, Land> landMap = new HashMap<Integer, Land>();
		final Set<Land> emptySet = Collections.emptySet();
		
		for (Land land : world.getLands()){
			final int landId = land.getLandId();
			final DiceStack diceStack = DiceStack.valueOf(land.getDiceCount());
			final Flag flag = land.getFlag(); 
			final ImmutableLandImpl immutableLandImpl = new ImmutableLandImpl(landId, diceStack, flag, emptySet);
			landMap.put(landId, immutableLandImpl);
		}
		
		for (Land land : world.getLands()){
			final Land l = landMap.get(land.getLandId());
			final Set<Land> neighbouringLands = new HashSet<Land>();
			for (Land w : land.getNeighbouringLands()){
				Land k = landMap.get(w.getLandId());
				neighbouringLands.add(k);
			}
			/* Reflection hack!!! */
			try{              
				Field field = ImmutableLandImpl.class.getDeclaredField("neighbouringLands");
				field.setAccessible(true);
				field.set(l, neighbouringLands);
			}catch (NoSuchFieldException e) {
				throw new RuntimeException(e);
			}catch (IllegalAccessException e) {
				throw new RuntimeException(e);
			}
		}
		
		final Set<Land> lands = new HashSet<Land>();
		for (Land land : landMap.values()){
			lands.add(land);
		}
		
		this.lands = Collections.unmodifiableSet(lands);
	}

	@Override
	public Flag getMyFlag() {
		return myFlag;
	}

	@Override
	public int getAvailableLeadCount() {
		return availableLeadCount;
	}

	@Override
	public Set<Flag> getFlags() {
		return flags;
	}

	@Override
	public int getDiceCountInReserve(Flag flag) {
		Integer diceCount = diceReserve.get(flag);
		if (diceCount == null)
			return 0;
		return diceCount;
	}

	@Override
	public Set<Land> getLands() {
		return lands;
	}
	
	public Map<Flag, Integer> getDiceReserve() {
		return diceReserve;
	}
}
