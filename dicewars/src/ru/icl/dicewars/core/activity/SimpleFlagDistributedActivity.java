package ru.icl.dicewars.core.activity;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import ru.icl.dicewars.client.Flag;

public class SimpleFlagDistributedActivity implements FlagDistributedActivity{
	private Map<Flag, String> mapFlagToName;
	
	public SimpleFlagDistributedActivity(Map<Flag, String> mapFlagToName) {
		for (Flag flag : mapFlagToName.keySet()){
			if (mapFlagToName.get(flag) == null) throw new IllegalArgumentException();			
		}
		
		this.mapFlagToName = Collections.unmodifiableMap(new HashMap<Flag,String>(mapFlagToName));
	}
	
	@Override
	public Set<Flag> getFlags(){
		return Collections.unmodifiableSet(mapFlagToName.keySet());
	}
	
	@Override
	public String getNameByFlag(Flag flag){
		if (!mapFlagToName.keySet().contains(flag)) throw new IllegalArgumentException();
		return mapFlagToName.get(flag);
	}
}
