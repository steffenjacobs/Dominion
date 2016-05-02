package com.tpps.technicalServices.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.util.Properties;

import sun.reflect.generics.reflectiveObjects.NotImplementedException;

/**
 * represents an optimized version of the normal java.util.Properties,
 * auto-saves everytime a porperty is set/get
 * 
 * @author Steffen Jacobs
 */
public class AutoCreatingProperties extends Properties {
	private static final long serialVersionUID = -8882987098297632096L;
	private File f;

	/** only calles the super-constructor */
	public AutoCreatingProperties() {
		super();
	}

	/**
	 * @param is
	 * @throws NotImplementedException
	 */
	public void load(InputStream is) {
		throw new NotImplementedException();
	}

	/**
	 * @param reader
	 * @throws NotImplementedException
	 */
	public void load(Reader reader) {
		throw new NotImplementedException();
	}

	/**
	 * loads the config from a file
	 * 
	 * @param file
	 */
	public void load(File file) {
		try {
			if (!file.exists()) {
				file.createNewFile();
			}
			super.load(new FileInputStream(file));
			this.f = file;

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * sets or creates a property and saves afterwards
	 * 
	 * @param key
	 * @param value
	 * @return parent
	 */
	public Object setProperty(String key, String value) {
		Object obj = super.setProperty(key, value);
		try {
			super.store(new FileOutputStream(f), null);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return obj;
	}

	/**
	 * @param name
	 * @param def
	 * @return the key to a property-name & saves afterwards
	 */
	public String getProperty(String name, String def) {
		if (super.containsKey(name)) {
			return super.getProperty(name);
		} else {
			super.setProperty(name, def);
			try {
				super.store(new FileOutputStream(f), null);
			} catch (IOException e) {
				e.printStackTrace();
			}
			return def;
		}
	}
}