package com.tpps.technicalServices.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;

public class AutoCreatingProperties extends Properties {
	private static final long serialVersionUID = -8882987098297632096L;
	private File f;

	public AutoCreatingProperties() {
		super();
	}

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