package ru.icl.dicewars.core;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import ru.icl.dicewars.core.activity.DiceWarsActivity;

public class ActivityQueueStorage {
	private ActivityQueue activityQueue = new ActivityQueueImpl();

	private File file;

	public ActivityQueueStorage(File file) {
		this.file = file;
	}

	public void add(DiceWarsActivity activity) {
		activityQueue.add(activity);
	}
	
	public DiceWarsActivity pollFromActivityQueue() {
		return activityQueue.poll();
	}
	

	public void clean() {
		activityQueue = new ActivityQueueImpl();
	}

	public void load() {
		ObjectInputStream ois = null;
		try {
			ois = new ObjectInputStream(new FileInputStream(file));
			activityQueue = (ActivityQueue) ois.readObject();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} finally {
			if (ois != null) {
				try {
					ois.close();
				} catch (IOException e) {
				}
			}
		}
	}

	public void store() {
		ObjectOutputStream oos = null;
		try {
			oos = new ObjectOutputStream(new FileOutputStream(file));
			oos.writeObject(activityQueue);
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (oos != null) {
				try {
					oos.close();
				} catch (IOException e) {
				}
			}
		}
	}

	public boolean hasNext() {
		return activityQueue.hasNext();
	}
}
