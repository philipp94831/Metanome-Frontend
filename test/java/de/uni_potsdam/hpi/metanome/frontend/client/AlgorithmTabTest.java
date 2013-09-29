package de.uni_potsdam.hpi.metanome.frontend.client;

import java.util.ArrayList;

import org.junit.Test;

import com.google.gwt.junit.client.GWTTestCase;

import de.uni_potsdam.hpi.metanome.frontend.client.jarchooser.BasicStatisticsJarChooser;
import de.uni_potsdam.hpi.metanome.frontend.client.jarchooser.FunctionalDependencyJarChooser;
import de.uni_potsdam.hpi.metanome.frontend.client.parameter.InputParameter;
import de.uni_potsdam.hpi.metanome.frontend.client.tabs.AlgorithmTab;
import de.uni_potsdam.hpi.metanome.frontend.client.tabs.BasicStatisticsTab;
import de.uni_potsdam.hpi.metanome.frontend.client.tabs.FunctionalDependencyTab;

/**
 * Tests for the algorithm specific pages (tabs)
 */
public class AlgorithmTabTest extends GWTTestCase {

	@Test
	public void testAddJarChooserFD(){
		//Setup
		AlgorithmTab algoTab = new FunctionalDependencyTab();
		
		//Execute
		algoTab.addJarChooser("Algo1", "Algo2", "Algo3");
		
		//Check
		assertEquals(1, algoTab.getWidgetCount());
		assertTrue(algoTab.getJarChooser() instanceof FunctionalDependencyJarChooser);
	}
	
	@Test
	public void testAddParameterTableFD(){
		//Setup
		AlgorithmTab algoTab = new FunctionalDependencyTab();
		ArrayList<InputParameter> paramList = new ArrayList<InputParameter>();
		int widgetCount = algoTab.getWidgetCount();
				
		//Execute
		algoTab.addParameterTable(paramList);
				
		//Check
		assertEquals(widgetCount + 1, algoTab.getWidgetCount());
	}

	@Test
	public void testAddJarChooserBS(){
		//Setup
		AlgorithmTab algoTab = new BasicStatisticsTab();
		
		//Execute
		algoTab.addJarChooser("Algo1", "Algo2", "Algo3");
		
		//Check
		assertEquals(1, algoTab.getWidgetCount());
		assertTrue(algoTab.getJarChooser() instanceof BasicStatisticsJarChooser);
	}
	
	@Test
	public void testAddParameterTableBS(){
		//Setup
		AlgorithmTab algoTab = new BasicStatisticsTab();
		ArrayList<InputParameter> paramList = new ArrayList<InputParameter>();
		int widgetCount = algoTab.getWidgetCount();
				
		//Execute
		algoTab.addParameterTable(paramList);
				
		//Check
		assertEquals(widgetCount + 1, algoTab.getWidgetCount());
	}
	
	@Override
	public String getModuleName() {
		return "de.uni_potsdam.hpi.metanome.frontend.Hello";
	}

}
