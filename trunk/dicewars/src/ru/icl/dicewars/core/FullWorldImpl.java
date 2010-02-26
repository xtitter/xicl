package ru.icl.dicewars.core;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;

import ru.icl.dicewars.client.Flag;
import ru.icl.dicewars.client.Land;

public class FullWorldImpl implements FullWorld, Serializable {
	private static final long serialVersionUID = -7073748047091860751L;

	private Flag myFlag;
	private int availableAttackCount;
	private List<Flag> flags = new ArrayList<Flag>();
	private Set<FullLand> fullLands = new HashSet<FullLand>();
	private Map<Flag, Integer> diceReserve = new HashMap<Flag, Integer>();

	public FullWorldImpl(final FullWorld fullWorld){
		if (fullWorld.getAvailableAttackCount() < 0) throw new IllegalArgumentException();
		if (fullWorld.getFlags() == null) throw new IllegalArgumentException();
		if (fullWorld.getLands() == null) throw new IllegalArgumentException();
		
		this.availableAttackCount = fullWorld.getAvailableAttackCount();
		this.myFlag = fullWorld.getMyFlag();
		this.flags = new ArrayList<Flag>(fullWorld.getFlags());
		
		Map<Flag, Integer> diceReserveMap = new HashMap<Flag, Integer>();
		
		for (Flag flag : this.flags){
			int diceCountInReserve = fullWorld.getDiceCountInReserve(flag);
			if (diceCountInReserve >= 0){
				diceReserveMap.put(flag, diceCountInReserve);
			}else{
				throw new IllegalArgumentException();
			}
		}
		
		this.diceReserve = diceReserveMap;
		
		final Map<Integer, FullLand> landMap = new HashMap<Integer, FullLand>();
		final Set<FullLand> emptySet = Collections.emptySet();
		for (FullLand fullLand : fullWorld.getFullLands()){
			final int landId = fullLand.getLandId();
			final DiceStack diceStack = DiceStack.valueOf(fullLand.getDiceCount());
			final Flag flag = fullLand.getFlag(); 
			final FullLand fullLandImpl = new FullLandImpl(landId);
			final Set<Point> points = new HashSet<Point>(fullLand.getPoints());
			fullLandImpl.setFlag(flag);
			fullLandImpl.setNeighbouringLands(emptySet);
			fullLandImpl.setDiceCount(diceStack);
			fullLandImpl.setPoints(points);
			landMap.put(landId, fullLandImpl);
		}

		for (FullLand fullland : fullWorld.getFullLands()){
			final FullLand l = landMap.get(fullland.getLandId());
			final Set<FullLand> neighbouringLands = new HashSet<FullLand>();
			for (Land w : fullland.getNeighbouringLands()){
				FullLand k = landMap.get(w.getLandId());
				neighbouringLands.add(k);
			}
			l.setNeighbouringLands(neighbouringLands);
		}
		
		final Set<FullLand> fullLands = new HashSet<FullLand>();
		for (FullLand fullLand : landMap.values()){
			fullLands.add(fullLand);
		}
		
		this.fullLands = fullLands;
	}
	
	public FullWorldImpl(int availableAttackCount, Set<FullLand> fullLands,
			List<Flag> flags, Map<Flag, Integer> diceReserve) {
		if (availableAttackCount < 0)
			throw new IllegalArgumentException();
		if (fullLands == null)
			throw new IllegalArgumentException();
		if (flags == null)
			throw new IllegalArgumentException();
		this.availableAttackCount = availableAttackCount;
		this.fullLands = fullLands;
		this.diceReserve = diceReserve;
		this.flags = flags;
	}

	@Override
	public Flag getMyFlag() {
		return myFlag;
	}

	@Override
	public int getAvailableAttackCount() {
		return availableAttackCount;
	}

	@Override
	public List<Flag> getFlags() {
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
		Set<Land> result = new HashSet<Land>(fullLands);
		return result;
	}
	
	@Override
	public void setMyFlag(Flag myFlag) {
		if (myFlag == null)
			throw new IllegalArgumentException();
		this.myFlag = myFlag;
	}

	@Override
	public void setAvailableAttackCount(int availableAttackCount) {
		if (availableAttackCount < 0)
			throw new IllegalArgumentException();
		this.availableAttackCount = availableAttackCount;
	}

	@Override
	public void setFlags(List<Flag> flags) {
		if (flags == null)
			throw new IllegalArgumentException();
		this.flags = flags;
	}

	@Override
	public void setDiceReserve(Map<Flag, Integer> diceReserve) {
		if (diceReserve == null)
			throw new IllegalArgumentException();
		this.diceReserve = diceReserve;
	}
	
	@Override
	public void setFullLands(Set<FullLand> fullLands) {
		if (fullLands == null) 
			throw new IllegalArgumentException();
		this.fullLands = fullLands;
	}
	
	@Override
	public Set<FullLand> getFullLands() {
		return fullLands;
	}
	
	@Override
	public Map<Flag, Integer> getDiceReserve() {
		return diceReserve;
	}
	
	@Override
	public void incDiceCountInReserve(Flag flag){
		Integer diceCount = diceReserve.get(flag);
		if (diceCount == null){
			diceReserve.put(flag, 1);
		}else{
			diceReserve.put(flag, diceCount + 1);
		}
	}
	
	@Override
	public void decDiceCountInReserve(Flag flag) {
		Integer diceCount = diceReserve.get(flag);
		if (diceCount == null || diceCount < 1) throw new IllegalStateException();
		diceReserve.put(flag, diceCount - 1);		
	}
	
	@Override
	public boolean hasInReserve(Flag flag) {
		Integer diceCount = diceReserve.get(flag);
		return diceCount != null && diceCount > 0;
	}
	
	@Override
	public int getMaxConnectedLandsByFlag(final Flag flag) {
		if (flag == null) throw new IllegalArgumentException();
		int result = 0;
		final Set<Integer> marker = new HashSet<Integer>();
		for (Land land : this.getLands()) {
			if (land.getFlag().equals(flag)
					&& !marker.contains(land.getLandId())) {
				Queue<Land> queue = new LinkedList<Land>();
				queue.add(land);
				marker.add(land.getLandId());
				int r = 0;
				while (!queue.isEmpty()) {
					final Land l = queue.poll();
					r++;
					for (Land w : l.getNeighbouringLands())
						if (w.getFlag().equals(l.getFlag())
								&& !marker.contains(w.getLandId())) {
							queue.add(w);
							marker.add(w.getLandId());
						}
				}
				if (r > result)
					result = r;
			}
		}
		return result;
	}
	
	@Override
	public boolean isExistsLandByFlag(final Flag flag){
		for (Land land : this.getLands()){
			if (flag.equals(land.getFlag())){
				return true;
			}
		}
		return false;
	}
}
