package ru.icl.dicewars.core.util;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import ru.icl.dicewars.client.World;
import ru.icl.dicewars.core.FullWorld;

public class WorldSerializationUtil {

	private WorldSerializationUtil() {
	}

	public static void serializeWorld(final World world, final String name)
			throws IOException {
		ObjectOutputStream oos = null;
		try {
			oos = new ObjectOutputStream(new FileOutputStream(name));
			oos.writeObject(world);
		} catch (IOException e) {
			throw e;
		} finally {
			try {
				if (oos != null)
					oos.close();
			} catch (IOException e) {
			}
		}
	}

	public static World loadWorld(final String name) throws IOException,
			ClassNotFoundException {
		ObjectInputStream osi = null;
		try {
			osi = new ObjectInputStream(new FileInputStream(name));
			Object o = osi.readObject();
			if (o instanceof World) {
				FullWorld fullWorld = (FullWorld) o;
				return fullWorld;
			} else {
				return null;
			}
		} catch (IOException e) {
			throw e;
		} finally {
			try {
				if (osi != null)
					osi.close();
			} catch (IOException e) {
			}
		}
	}

}
