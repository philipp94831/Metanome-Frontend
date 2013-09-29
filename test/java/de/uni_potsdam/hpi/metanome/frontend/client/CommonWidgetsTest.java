package de.uni_potsdam.hpi.metanome.frontend.client;

import org.junit.Test;

import com.google.gwt.junit.client.GWTTestCase;

import de.uni_potsdam.hpi.metanome.frontend.client.jarchooser.InclusionDependencyJarChooser;
import de.uni_potsdam.hpi.metanome.frontend.client.jarchooser.JarChooser;

public class CommonWidgetsTest extends GWTTestCase{

	@Test
	public void testJarChooser() {
		//Setup
		String[] filenames = {"filename1.jar", "filename2.jar"};
		
		//Execute
		JarChooser jarChooser = new InclusionDependencyJarChooser(filenames);
		
		//Test
		assertEquals(2, jarChooser.getWidgetCount());
		assertEquals(2, jarChooser.getListItemCount());
	}

	@Override
	public String getModuleName() {
		return "de.uni_potsdam.hpi.metanome.frontend.Hello";
	}

}
