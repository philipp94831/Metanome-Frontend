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

package de.uni_potsdam.hpi.metanome.frontend.client.datasources;

import com.google.gwt.core.shared.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.VerticalPanel;

import de.uni_potsdam.hpi.metanome.frontend.client.BasePage;
import de.uni_potsdam.hpi.metanome.frontend.client.parameter.InputParameterCsvFile;
import de.uni_potsdam.hpi.metanome.frontend.client.parameter.InputParameterDataSource;
import de.uni_potsdam.hpi.metanome.frontend.client.services.InputDataService;
import de.uni_potsdam.hpi.metanome.frontend.client.services.InputDataServiceAsync;

/**
 * Data Sources page is the Tab that lists all previously defined data sources (CSV files, DB connections) 
 * and links to actions on them. It also allows to define new data sources.
 * 
 * @author Claudia
 */
public class DataSourcesPage extends VerticalPanel {

	private FlexTable dbConnectionsList;
	private FlexTable csvFilesList;
	
	private InputDataServiceAsync inputDataService;
	
	protected final BasePage basePage;

	public DataSourcesPage(BasePage parent) {		
		this.inputDataService = GWT.create(InputDataService.class);
		
		this.basePage = parent;
		
		createDataSourcesList();
		
		Label temporaryContent = new Label();
		temporaryContent.setText("As data sources, you can configure any database connection in the Run Configurations,"
				+ "or choose from any CSV-files in the designated folder.");
		this.add(temporaryContent);
	}

	/**
	 * This method triggers retrieval of all available data sources and returns a widget with a list of them.
	 * @return
	 */
	private void createDataSourcesList() {
		this.add(new HTML("<b>CSV Files</b>"));
		csvFilesList = new FlexTable();
		this.add(csvFilesList);
		listCsvFiles();
		
		this.add(new HTML("<hr><b>Database Connections</b>"));
		dbConnectionsList = new FlexTable();
		this.add(dbConnectionsList);
		listDbConnections();

		this.add(new HTML("<hr>"));
	}

	private void listCsvFiles() {
		inputDataService.listCsvInputFiles(new AsyncCallback<String[]>() {
			
			@Override
			public void onFailure(Throwable caught) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void onSuccess(String[] result) {
				InputParameterCsvFile[] dataSources = new InputParameterCsvFile[result.length];
				for (int i=0; i<result.length; i++){
					dataSources[i] = new InputParameterCsvFile();
					dataSources[i].setFileNameValue(result[i]);
				}
				addDataSourcesToList(dataSources, csvFilesList);
			}
		});		
	}

	private void listDbConnections() {
		// TODO Auto-generated method stub - need to be able to save connections first
		
	}
	
	protected void addDataSourcesToList(InputParameterDataSource[] dataSources, FlexTable list) {
		int row = 0;
		for(final InputParameterDataSource dataSource : dataSources) {
			Button runButton = new Button("Run Algorithm");
			Button showButton = new Button("Show Profile");
			
			runButton.addClickHandler(new ClickHandler() {
				
				@Override
				public void onClick(ClickEvent event) {
					// TODO Auto-generated method stub - should open Run Configuration with pre-configured data source
					jumpToRunConfiguration(dataSource);
				}
			});
			showButton.addClickHandler(new ClickHandler() {
				
				@Override
				public void onClick(ClickEvent event) {
					// TODO Auto-generated method stub - should show results from previously run algorithm
					
				}
			});
			
			list.setText(row, 0, dataSource.getValueAsString());
			list.setWidget(row, 1, runButton);			
			list.setWidget(row, 2, showButton);			
			row++;
		}
	}
	
	protected void jumpToRunConfiguration(InputParameterDataSource dataSource) {
		basePage.jumpToRunConfiguration(null, dataSource);
	}
}
