/**
 * 
 */
package com.tpps.test.ui;

import static org.junit.Assert.*;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.CoreMatchers.is;

import org.junit.Before;
import org.junit.Test;
import javax.swing.JFrame;

import com.tpps.ui.MainFrame;

/**
 * @author ladler - Lukas
 *
 */
public class MainMenuTest {

	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
	}

	@Test
	public void test() {

		MainFrame m = new MainFrame();
		assertThat(m, is(notNullValue()));

		m.setVisible(true);
		assertTrue(m.isVisible());

		assertThat(m.getDefaultCloseOperation(), is(JFrame.EXIT_ON_CLOSE));
	}

}
