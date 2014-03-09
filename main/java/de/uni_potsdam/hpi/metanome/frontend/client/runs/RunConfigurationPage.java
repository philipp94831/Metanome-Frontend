/*
 * Copyright 2014 by the Metanome project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package de.uni_potsdam.hpi.metanome.frontend.client.runs;

import java.util.List;

import com.google.gwt.core.shared.GWT;
import com.google.gwt.user.client.ui.DockPanel;
import com.google.gwt.user.client.ui.Label;

import de.uni_potsdam.hpi.metanome.frontend.client.BasePage;
import de.uni_potsdam.hpi.metanome.frontend.client.parameter.InputParameter;
import de.uni_potsdam.hpi.metanome.frontend.client.parameter.InputParameterDataSource;
import de.uni_potsdam.hpi.metanome.frontend.client.parameter.ParameterTable;
import de.uni_potsdam.hpi.metanome.frontend.client.services.ExecutionService;
import de.uni_potsdam.hpi.metanome.frontend.client.services.ExecutionServiceAsync;

/**
 * The Run Configuration page allow to specify all parameters for an algorithm execution:
 * The algorithm itself is chosen through a JarChooser widget, and the ParameterTable allows
 * to specify algorithm specific parameters.
 * The page can be referenced (and switched to) by other pages with pre-set values. Executing an 
 * algorithm navigates to the corresponding Results page. 
 */
public class RunConfigurationPage extends DockPanel{
	protected BasePage basePage;
	protected ParameterTable parameterTable;
	protected JarChooser jarChooser;
	protected Label primaryDataSourceLabel;
	public InputParameterDataSource primaryDataSource;
	
	protected ExecutionServiceAsync executionService;
	
	
	/**
	 * Constructor. Initializes ExecutoinService and registers given algorithms.
	 * However, more algorithms can be registered whenever they become available,
	 * through <link>addAlgorithms(String... algorithmNames)</link>
	 * 
	 * @param algorithmNames 
	 */
	public RunConfigurationPage(BasePage basePage, String... algorithmNames){
		this.setWidth("100%");
		this.basePage = basePage;
		
		this.primaryDataSourceLabel = new Label();
		this.add(this.primaryDataSourceLabel, DockPanel.NORTH);
		this.jarChooser = new JarChooser(algorithmNames);
		this.add(this.jarChooser, DockPanel.NORTH);
		
		this.executionService = GWT.create(ExecutionService.class);
	}
	
		
	/**
	 * Adds a widget for user's parameter input to the tab. The content of the tab 
	 * depends on the requested parameters.
	 *
	 * @param paramList	list of required parameters
	 */
	public void addParameterTable(List<InputParameter> paramList){
		removeParameterTable();
		parameterTable = new ParameterTable(paramList, primaryDataSource);
		this.add(parameterTable, DockPanel.SOUTH);
	}

	/**
	 * Method to add more algorithms after construction.
	 * 
	 * @param algorithmNames
	 */
	public void addAlgorithms(String... algorithmNames){
		this.jarChooser.addAlgorithms(algorithmNames);
	}
	
	/**
	 * Select the given algorithm on the underlying JarChooser.
	 * 
	 * @param algorithmName	the value to select
	 */
	public void selectAlgorithm(String algorithmName) {
		this.jarChooser.setSelectedAlgorithm(algorithmName);
		this.jarChooser.submit();
	}

	/**
	 * 
	 * @return	the name of the algorithm that is currently selected on this page's JarChooser
	 */
	public String getCurrentlySelectedAlgorithm(){
		return this.jarChooser.getSelectedAlgorithm();
	}
	
	/**
	 * Sets the data source that should be profiled ("Primary Data Source"),
	 * displays its value in a label, and triggers the jarChooser to filter for applicable algorithms.
	 * 
	 * @param dataSource
	 */
	public void setPrimaryDataSource(InputParameterDataSource dataSource) {
		this.primaryDataSource = dataSource;
		this.primaryDataSourceLabel.setText("This should filter for algorithms applicable on " + dataSource.getValueAsString());
		removeParameterTable();	
		this.jarChooser.filterForPrimaryDataSource(dataSource);
	}

	/**
	 * Remove the parameterTable from UI if it was present
	 */
	private void removeParameterTable() {
		if (parameterTable != null)
			this.remove(parameterTable);
	}

	/**
	 * 
	 * @return	the name of the data source that is currently selected in the 
	 * 			first data source field in the configuration interface
	 */
	public List<InputParameterDataSource> getCurrentlySelectedDataSources() {
		return this.parameterTable.getInputParameterDataSourcesWithValues();
	}
	
	/**
	 * Execute the currently selected algorithm and switch to results page.
	 * 
	 * @param parameters	parameters to use for the algorithm execution
	 * @param dataSources 
	 */
	public void callExecutionService(List<InputParameter> parameters, List<InputParameterDataSource> dataSources) {
		final String algorithmName = getCurrentlySelectedAlgorithm();
		basePage.startExecutionAndResultPolling(executionService, algorithmName, parameters, dataSources);
	}

	// Getters & Setters
	
	public JarChooser getJarChooser() {
		return jarChooser;
	}

}
