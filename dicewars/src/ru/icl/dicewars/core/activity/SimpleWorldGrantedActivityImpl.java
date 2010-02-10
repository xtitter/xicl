package ru.icl.dicewars.core.activity;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import ru.icl.dicewars.client.Flag;
import ru.icl.dicewars.client.Land;
import ru.icl.dicewars.core.DiceStack;
import ru.icl.dicewars.core.FullLand;
import ru.icl.dicewars.core.Point;

public class SimpleWorldGrantedActivityImpl implements WorldGrantedActivity{
	private Set<FullLand> lands;
	
	public SimpleWorldGrantedActivityImpl(final Set<FullLand> lands) {
		this.lands = new HashSet<FullLand>();
		for (FullLand fullLand : lands){
			final int landId = fullLand.getLandId();
			final Flag flag = fullLand.getFlag();
			final int diceCount = fullLand.getDiceCount();
			final Set<Point> points = Collections.unmodifiableSet(new HashSet<Point>(fullLand.getPoints()));
			FullLand newfullLand = new FullLand() {
				
				@Override
				public Set<Land> getNeighbouringLands() {
					throw new UnsupportedOperationException();
				}
				
				@Override
				public int getLandId() {
					return landId;
				}
				
				@Override
				public Flag getFlag() {
					return flag;
				}
				
				@Override
				public int getDiceCount() {
					return diceCount;
				}
				
				@Override
				public void setPoints(Set<Point> points) {
					throw new UnsupportedOperationException();
				}
				
				@Override
				public void setNeighbouringLands(Set<FullLand> neighbouringLands) {
					throw new UnsupportedOperationException();
				}
				
				@Override
				public void setLandId(int landId) {
					throw new UnsupportedOperationException();
				}
				
				@Override
				public void setFlag(Flag flag) {
					throw new UnsupportedOperationException();
				}
				
				@Override
				public void setDiceCount(DiceStack diceStack) {
					throw new UnsupportedOperationException();
				}
				
				@Override
				public void incDiceCount() {
					throw new UnsupportedOperationException();				
				}
				
				@Override
				public Set<Point> getPoints() {
					return points;
				}
				
				@Override
				public Set<FullLand> getNeighbouringFullLands() {
					throw new UnsupportedOperationException();
				}
			};
			this.lands.add(newfullLand);
		}
	}
	
	public Set<FullLand> getLands(){
		return lands;
	}
}
