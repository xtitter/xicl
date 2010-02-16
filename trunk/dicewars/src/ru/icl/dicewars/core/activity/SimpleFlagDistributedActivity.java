package ru.icl.dicewars.core.activity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
	public List<Flag> getFlags(){
		List<Flag> tmp = new ArrayList<Flag>();
		tmp.addAll(mapFlagToName.keySet());
		return Collections.unmodifiableList(tmp);
	}
	
	@Override
	public String getNameByFlag(Flag flag){
		if (!mapFlagToName.keySet().contains(flag)) throw new IllegalArgumentException();
		return mapFlagToName.get(flag);
	}
}
