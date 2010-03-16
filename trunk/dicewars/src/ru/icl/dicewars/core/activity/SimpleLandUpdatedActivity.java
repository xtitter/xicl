package ru.icl.dicewars.core.activity;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import ru.icl.dicewars.client.Flag;
import ru.icl.dicewars.client.Land;
import ru.icl.dicewars.core.DiceStack;
import ru.icl.dicewars.core.FullLand;
import ru.icl.dicewars.core.Point;

public class SimpleLandUpdatedActivity implements LandUpdatedActivity{
	private FullLand fullLand;
	
	public SimpleLandUpdatedActivity(final FullLand fullLand) {
		if (fullLand == null) throw new IllegalArgumentException();
		
		final int landId = fullLand.getLandId();
		final Flag flag = fullLand.getFlag();
		final int diceCount = fullLand.getDiceCount();
		final Set<Point> points = Collections.unmodifiableSet(new HashSet<Point>(fullLand.getPoints()));
		this.fullLand = new FullLand() {
			private static final long serialVersionUID = 1L;

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
	}
	
	@Override
	public FullLand getFullLand() {
		return fullLand;
	}
}

