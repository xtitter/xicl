package ru.icl.dicewars.client;

import java.io.Serializable;

public interface Attack extends Serializable{
	int getFromLandId();

	int getToLandId();
}
