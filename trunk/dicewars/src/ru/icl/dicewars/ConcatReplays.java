package ru.icl.dicewars;

import java.io.File;
import java.util.UUID;

import ru.icl.dicewars.core.ActivityQueueStorage;
import ru.icl.dicewars.core.activity.DiceWarsActivity;

public class ConcatReplays {
	public static void main(String[] args) {
		ActivityQueueStorage totalActivityQueueStorage = new ActivityQueueStorage(new File("dicewars" + UUID.randomUUID().toString() + ".rep"));
		for (String fileName : args){
			File file = new File(fileName);
			ActivityQueueStorage activityQueueStorage = new ActivityQueueStorage(file);
			activityQueueStorage.load();
			while (activityQueueStorage.hasNext()){
				DiceWarsActivity diceWarsActivity = activityQueueStorage.pollFromActivityQueue();
				totalActivityQueueStorage.add(diceWarsActivity);
			}
		}
		totalActivityQueueStorage.store();
	}
}
