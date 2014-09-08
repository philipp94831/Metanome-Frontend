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

import com.google.gwt.dom.client.Style;
import com.google.gwt.user.client.ui.TabLayoutPanel;

import de.uni_potsdam.hpi.metanome.algorithm_integration.configuration.ConfigurationSettingDataSource;
import de.uni_potsdam.hpi.metanome.frontend.client.BasePage;
import de.uni_potsdam.hpi.metanome.frontend.client.TabContent;
import de.uni_potsdam.hpi.metanome.frontend.client.TabWrapper;
import de.uni_potsdam.hpi.metanome.results_db.DatabaseConnection;

/**
 * Page to configure data inputs.
 */
public class DataSourcePage extends TabLayoutPanel implements TabContent {

  private static final String DATABASE_CONNECTION = "Database Connection";
  private static final String FILE_INPUT = "File Input";
  private static final String TABLE_INPUT = "Table Input";

  private TabWrapper messageReceiver;
  private BasePage basePage;

  protected TableInputTab tableInputTab;
  protected FileInputTab fileInputTab;
  protected DatabaseConnectionTab databaseConnectionTab;

  /**
   * Constructor
   * @param basePage the parent page
   */
  public DataSourcePage(BasePage basePage) {
    super(1, Style.Unit.CM);

    this.basePage = basePage;

    this.tableInputTab = new TableInputTab(this);
    this.fileInputTab = new FileInputTab(this);
    this.databaseConnectionTab = new DatabaseConnectionTab(this);

    this.add(fileInputTab, FILE_INPUT);
    this.add(databaseConnectionTab, DATABASE_CONNECTION);
    this.add(tableInputTab, TABLE_INPUT);
  }

  /**
   * Switch to the run configuration page.
   * The selected data source should be preselected.
   * @param dataSource the preselected data source
   */
  public void callRunConfiguration(ConfigurationSettingDataSource dataSource) {
    this.basePage.switchToRunConfiguration(null, dataSource);
  }

  /**
   * Updates the table input tab. Adds a new database connection to the list of available
   * database connections.
   * @param connection the connection which is new and should be added
   */
  public void updateTableInputTab(DatabaseConnection connection) {
    this.tableInputTab.addDatabaseConnection(connection);
  }

  @Override
  public void setMessageReceiver(TabWrapper tab) {
    this.messageReceiver = tab;
    this.tableInputTab.setMessageReceiver(tab);
    this.fileInputTab.setMessageReceiver(tab);
    this.databaseConnectionTab.setMessageReceiver(tab);
  }

}
