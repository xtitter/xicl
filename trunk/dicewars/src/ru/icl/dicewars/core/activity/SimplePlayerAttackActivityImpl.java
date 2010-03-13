package ru.icl.dicewars.core.activity;

import java.util.List;

import ru.icl.dicewars.client.Attack;
import ru.icl.dicewars.client.Flag;

public class SimplePlayerAttackActivityImpl implements PlayerAttackActivity{
	private Attack attack;
	private List<Integer> opponentDicesList;
	private List<Integer> playerDicesList;
	private Flag opponentFlag;
	private Flag playerFlag;
	
	public SimplePlayerAttackActivityImpl(Attack attack, Flag playerFlag, Flag opponentFlag, List<Integer> playerDicesList,  List<Integer> opponentDicesList) {
		if (attack == null || playerDicesList == null || opponentDicesList == null) throw new IllegalArgumentException();
		
		if (playerDicesList.size() < 1 || opponentDicesList.size() > 8 || opponentDicesList.size() < 1 || opponentDicesList.size() > 8)
			throw new IllegalArgumentException();
		
		for (Integer i : opponentDicesList){
			if (i < 1 || i > 6) throw new IllegalArgumentException();
		}

		for (Integer i : playerDicesList){
			if (i < 1 || i > 6) throw new IllegalArgumentException();
		}
		
		this.attack = attack;
		this.playerDicesList = playerDicesList;
		this.opponentDicesList = opponentDicesList;
		this.playerFlag = playerFlag;
		this.opponentFlag = opponentFlag;
	}
	
	@Override
	public int getFromLandId(){
		return attack.getFromLandId();
	}
	
	@Override
	public int getToLandId(){
		return attack.getToLandId();
	}
	
	@Override
	public List<Integer> getPlayerDices(){
		return playerDicesList;
	}
	
	@Override
	public List<Integer> getOpponentDices(){
		return opponentDicesList;
	}
	
	@Override
	public Flag getOpponentFlag() {
		return opponentFlag;
	}
	
	@Override
	public Flag getPlayerFlag() {
		return playerFlag;
	}

}
