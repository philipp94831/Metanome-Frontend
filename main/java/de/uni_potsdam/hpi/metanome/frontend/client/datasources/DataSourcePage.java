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

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Label;

import de.uni_potsdam.hpi.metanome.algorithm_integration.configuration.ConfigurationSettingCsvFile;
import de.uni_potsdam.hpi.metanome.algorithm_integration.configuration.ConfigurationSettingDataSource;
import de.uni_potsdam.hpi.metanome.algorithm_integration.configuration.ConfigurationSettingSqlIterator;
import de.uni_potsdam.hpi.metanome.algorithm_integration.configuration.DbSystem;
import de.uni_potsdam.hpi.metanome.frontend.client.BasePage;
import de.uni_potsdam.hpi.metanome.frontend.client.TabContent;
import de.uni_potsdam.hpi.metanome.frontend.client.TabWrapper;
import de.uni_potsdam.hpi.metanome.frontend.client.helpers.InputValidationException;
import de.uni_potsdam.hpi.metanome.frontend.client.services.DatabaseConnectionService;
import de.uni_potsdam.hpi.metanome.frontend.client.services.DatabaseConnectionServiceAsync;
import de.uni_potsdam.hpi.metanome.frontend.client.services.FileInputService;
import de.uni_potsdam.hpi.metanome.frontend.client.services.FileInputServiceAsync;
import de.uni_potsdam.hpi.metanome.frontend.client.services.TableInputService;
import de.uni_potsdam.hpi.metanome.frontend.client.services.TableInputServiceAsync;
import de.uni_potsdam.hpi.metanome.results_db.DatabaseConnection;
import de.uni_potsdam.hpi.metanome.results_db.FileInput;
import de.uni_potsdam.hpi.metanome.results_db.TableInput;

import java.util.List;

/**
 * Page to configure data inputs.
 */
public class DataSourcePage extends FlowPanel implements TabContent {

  private static final String DATABASE_CONNECTION = "Database Connection";
  private static final String FILE_INPUT = "File Input";
  private static final String TABLE_INPUT = "Table Input";

  private DatabaseConnectionServiceAsync databaseConnectionService;
  private TableInputServiceAsync tableInputService;
  private FileInputServiceAsync fileInputService;

  private TabWrapper messageReceiver;
  private BasePage basePage;

  protected FlowPanel content;
  protected FlowPanel editForm;
  protected DatabaseConnectionEditForm databaseConnectionEditForm;
  protected FileInputEditForm fileInputEditForm;
  protected TableInputEditForm tableInputEditForm;

  protected Boolean databaseConnectionSelected;
  protected Boolean tableInputSelected;
  protected Boolean fileInputSelected;

  protected FlowPanel saveButtonPanel;

  protected FlexTable databaseConnectionTable;
  protected FlexTable fileInputTable;
  protected FlexTable tableInputTable;

  /**
   * Constructor
   * @param basePage the parent page
   */
  public DataSourcePage(BasePage basePage) {
    this.basePage = basePage;

    databaseConnectionService = GWT.create(DatabaseConnectionService.class);
    tableInputService = GWT.create(TableInputService.class);
    fileInputService = GWT.create(FileInputService.class);

    databaseConnectionTable = new FlexTable();
    fileInputTable = new FlexTable();
    tableInputTable = new FlexTable();

    // Initialize all input fields
    this.databaseConnectionEditForm = new DatabaseConnectionEditForm();
    this.fileInputEditForm = new FileInputEditForm();
    this.tableInputEditForm = new TableInputEditForm();

    // Initialize all buttons
    Button dbButton = new Button(DATABASE_CONNECTION);
    dbButton.addClickHandler(getClickHandlerForDbButton());
    Button fileButton = new Button(FILE_INPUT);
    fileButton.addClickHandler(getClickHandlerForFileButton());
    Button tableButton = new Button(TABLE_INPUT);
    tableButton.addClickHandler(getClickHandlerForTableButton());

    this.saveButtonPanel = new FlowPanel();
    Button saveButton = new Button("Save", new ClickHandler() {
      @Override
      public void onClick(ClickEvent clickEvent) {
        saveObject();
      }
    });
    this.saveButtonPanel.add(saveButton);

    // initialize all panels
    FlowPanel wrapper = new FlowPanel();
    FlowPanel buttonWrapper = new FlowPanel();
    FlowPanel contentWrapper = new FlowPanel();
    this.editForm = new FlowPanel();

    // add buttons
    buttonWrapper.add(dbButton);
    buttonWrapper.add(tableButton);
    buttonWrapper.add(fileButton);

    // set up content
    this.content = new FlowPanel();
    contentWrapper.add(this.content);
    fillContent(DATABASE_CONNECTION);

    // add wrapper to page
    wrapper.add(buttonWrapper);
    wrapper.add(contentWrapper);
    this.add(wrapper);

    // set selected field to database connection
    this.databaseConnectionSelected = true;
    this.tableInputSelected = false;
    this.fileInputSelected = false;
  }

  /**
   * Creates the click handles for the file input button.
   * If the button is clicked the content panel should display a list with all file inputs and
   * a file input edit form.
   * @return the click handler
   */
  private ClickHandler getClickHandlerForFileButton() {
    return new ClickHandler() {
      public void onClick(ClickEvent event) {
        messageReceiver.clearErrors();
        messageReceiver.clearInfos();

        content.clear();
        fillContent(FILE_INPUT);

        databaseConnectionSelected = false;
        tableInputSelected = false;
        fileInputSelected = true;
      }
    };
  }

  /**
   * Creates the click handles for the database connection button.
   * If the button is clicked the content panel should display a list with all database connections and
   * a database connection edit form.
   * @return the click handler
   */
  private ClickHandler getClickHandlerForDbButton() {
    return new ClickHandler() {
      public void onClick(ClickEvent event) {
        messageReceiver.clearErrors();
        messageReceiver.clearInfos();

        content.clear();
        fillContent(DATABASE_CONNECTION);

        databaseConnectionSelected = true;
        tableInputSelected = false;
        fileInputSelected = false;
      }
    };
  }

  /**
   * Creates the click handles for the table input button.
   * If the button is clicked the content panel should display a list with all table inputs and
   * a table input edit form.
   * @return the click handler
   */
  private ClickHandler getClickHandlerForTableButton() {
    return new ClickHandler() {
      public void onClick(ClickEvent event) {
        messageReceiver.clearErrors();
        messageReceiver.clearInfos();

        tableInputEditForm.updateDatabaseConnectionListBox();
        fillContent(TABLE_INPUT);

        databaseConnectionSelected = false;
        tableInputSelected = true;
        fileInputSelected = false;
      }
    };
  }

  /**
   * Gets and stores the current selected input into the database.
   */
  protected void saveObject() {
    this.messageReceiver.clearErrors();
    this.messageReceiver.clearInfos();

    if (databaseConnectionSelected) {
      saveDatabaseConnection();
    } else if (tableInputSelected) {
      saveTableInput();
    } else if (fileInputSelected) {
      saveFileInput();
    }
  }

  /**
   * Stores the current database connection in the database.
   */
  private void saveDatabaseConnection() {
    DatabaseConnectionEditForm w = (DatabaseConnectionEditForm) editForm.getWidget(0);
    try {
      databaseConnectionService.storeDatabaseConnection(w.getValue(), new AsyncCallback<Void>() {
        @Override
        public void onFailure(Throwable throwable) {
          messageReceiver.addError("Database Connection could not be stored!");
        }

        @Override
        public void onSuccess(Void aVoid) {
          databaseConnectionEditForm.reset();
          fillContent(DATABASE_CONNECTION);
          messageReceiver.addInfo("Database Connection successfully saved.");
          basePage.updateDataSourcesOnRunConfiguration();
        }
      });
    } catch (InputValidationException e) {
      messageReceiver.addError("Database Connection could not be stored: " + e.getMessage());
    }
  }

  /**
   * Stores the current file input in the database.
   */
  private void saveFileInput() {
    FileInputEditForm w = (FileInputEditForm) editForm.getWidget(0);
    try {
      fileInputService.storeFileInput(w.getValue(), new AsyncCallback<Void>() {
        @Override
        public void onFailure(Throwable throwable) {
          messageReceiver.addError("File Input could not be stored!");
        }

        @Override
        public void onSuccess(Void aVoid) {
          fileInputEditForm.reset();
          fillContent(FILE_INPUT);
          messageReceiver.addInfo("File Input successfully saved.");
          basePage.updateDataSourcesOnRunConfiguration();
        }
      });
    } catch (InputValidationException e) {
      messageReceiver.addError("File Input could not be stored: " + e.getMessage());
    }
  }

  /**
   * Stores the current table input in the database.
   */
  private void saveTableInput() {
    TableInputEditForm w = (TableInputEditForm) editForm.getWidget(0);
    try {
      final TableInput input = w.getValue();
      tableInputService.storeTableInput(input, new AsyncCallback<Void>() {
        @Override
        public void onFailure(Throwable throwable) {
          messageReceiver.addError("Table Input could not be stored!");
        }

        @Override
        public void onSuccess(Void aVoid) {
          tableInputEditForm.reset();
          fillContent(TABLE_INPUT);
          messageReceiver.addInfo("Table Input successfully saved.");
          basePage.updateDataSourcesOnRunConfiguration();
        }
      });
    } catch (InputValidationException e) {
      messageReceiver.addError("Table Input could not be stored: " + e.getMessage());
    }
  }

  /**
   * Fills the content widget according to the selected input type.
   * If there already exists some inputs of the type, the inputs are listed.
   * Additionally, a form to add a new input of that type is added.
   * @param type the selected input type
   */
  private void fillContent(String type) {
    this.content.clear();
    switch (type) {
      case DATABASE_CONNECTION:
        databaseConnectionService.listDatabaseConnections(
          new AsyncCallback<List<DatabaseConnection>>() {
            @Override
            public void onFailure(Throwable throwable) {
              content.add(new Label("There are no Database Connections yet."));
              addEditForm(DATABASE_CONNECTION);
            }

            @Override
            public void onSuccess(List<DatabaseConnection> connections) {
              listDatabaseConnections(connections);
              addEditForm(DATABASE_CONNECTION);
            }
          });
        // disable all delete button of database connection which are referenced by a table input
        tableInputService.listTableInputs(new AsyncCallback<List<TableInput>>() {
          @Override
          public void onFailure(Throwable throwable) { }
          @Override
          public void onSuccess(List<TableInput> tableInputs) {
            for (TableInput input : tableInputs)
              setEnableOfDeleteButton(input.getDatabaseConnection(), false);
          }
        });

        break;
      case TABLE_INPUT:
        tableInputService.listTableInputs(new AsyncCallback<List<TableInput>>() {
          @Override
          public void onFailure(Throwable throwable) {
            content.add(new Label("There are no Table Inputs yet."));
            addEditForm(TABLE_INPUT);
          }

          @Override
          public void onSuccess(List<TableInput> tableInputs) {
            listTableInputs(tableInputs);
            addEditForm(TABLE_INPUT);
          }
        });
        break;
      case FILE_INPUT:
        fileInputService.listFileInputs(
          new AsyncCallback<List<FileInput>>() {
            @Override
            public void onFailure(Throwable throwable) {
              content.add(new Label("There are no File Inputs yet."));
              addEditForm(FILE_INPUT);
            }

            @Override
            public void onSuccess(List<FileInput> fileInputs) {
              listFileInputs(fileInputs);
              addEditForm(FILE_INPUT);
            }
          });
        break;
      default:
        this.messageReceiver.addError("Type not supported!");
        break;
    }
  }

  /**
   * Adds a edit form to add a new input of the given type.
   * @param type the input type
   */
  private void addEditForm(String type) {
    switch (type) {
      case DATABASE_CONNECTION:
        this.content.add(new HTML("<hr>"));
        this.content.add(new HTML("<h3>Add a new Database Connection</h3>"));
        this.content.add(saveButtonPanel);
        this.editForm.clear();
        this.editForm.add(databaseConnectionEditForm);
        this.content.add(this.editForm);
        break;
      case FILE_INPUT:
        this.content.add(new HTML("<hr>"));
        this.content.add(new HTML("<h3>Add a new File Input</h3>"));
        this.content.add(saveButtonPanel);
        this.editForm.clear();
        this.editForm.add(fileInputEditForm);
        this.content.add(this.editForm);
        break;
      case TABLE_INPUT:
        this.content.add(new HTML("<hr>"));
        this.content.add(new HTML("<h3>Add a new Table Input</h3>"));
        this.content.add(saveButtonPanel);
        this.editForm.clear();
        this.editForm.add(tableInputEditForm);
        this.content.add(this.editForm);
        break;
      default:
        this.messageReceiver.addError("Type not supported!");
        break;
    }

  }

  /**
   * Lists all given table inputs in a table and adds the table to the content.
   * @param inputs the table inputs
   */
  private void listTableInputs(List<TableInput> inputs) {
    if (inputs.isEmpty()) {
      content.add(new HTML("<h4>There are no Table Inputs yet.</h4>"));
      return;
    }

    tableInputTable.clear();
    int row = 1;

    tableInputTable.setHTML(0, 0, "<b>Database Connection</b>");
    tableInputTable.setHTML(0, 1, "<b>Table Name</b>");

    for (final TableInput input : inputs) {
      Button deleteButton = new Button("Delete");
      final int finalRow = row;
      deleteButton.addClickHandler(new ClickHandler() {
        @Override
        public void onClick(ClickEvent clickEvent) {
          tableInputService.deleteTableInput(input, getDeleteCallback(tableInputTable,
                                                                      finalRow,
                                                                      TABLE_INPUT));
        }
      });

      Button runButton = new Button("Run");
      runButton.setTitle(String.valueOf(input.getId()));
      runButton.addClickHandler(new ClickHandler() {
        @Override
        public void onClick(ClickEvent event) {
          callRunConfiguration(convertTableInputToDataSource(input));
        }
      });

      tableInputTable.setText(row, 0, input.getDatabaseConnection().getUrl());
      tableInputTable.setText(row, 1, input.getTableName());
      tableInputTable.setWidget(row, 2, runButton);
      tableInputTable.setWidget(row, 3, deleteButton);
      row++;
    }

    this.content.add(new HTML("<h3>List of all Table Inputs</h3>"));
    this.content.add(tableInputTable);
  }

  /**
   * Lists all given file inputs in a table and adds the table to the content.
   * @param inputs the file inputs
   */
  private void listFileInputs(List<FileInput> inputs) {
    if (inputs.isEmpty()) {
      content.add(new HTML("<h4>There are no File Inputs yet.</h4>"));
      return;
    }

    fileInputTable.clear();
    int row = 1;

    fileInputTable.setHTML(0, 0, "<b>File Name</b>");

    for (final FileInput input : inputs) {
      Button deleteButton = new Button("Delete");
      final int finalRow = row;
      deleteButton.addClickHandler(new ClickHandler() {
        @Override
        public void onClick(ClickEvent clickEvent) {
          fileInputService.deleteFileInput(input, getDeleteCallback(fileInputTable,
                                                                    finalRow, FILE_INPUT));
        }
      });

      Button runButton = new Button("Run");
      runButton.setTitle(String.valueOf(input.getId()));
      runButton.addClickHandler(new ClickHandler() {
        @Override
        public void onClick(ClickEvent event) {
          callRunConfiguration(convertFileInputToDataSource(input));
        }
      });

      fileInputTable.setText(row, 0, input.getFileName());
      fileInputTable.setWidget(row, 1, runButton);
      fileInputTable.setWidget(row, 2, deleteButton);
      row++;
    }

    this.content.add(new HTML("<h3>List of all File Inputs</h3>"));
    this.content.add(fileInputTable);
  }

  /**
   * Lists all given database connections in a table and adds the table to the content.
   * @param inputs a list of database connection which should be added to a table
   */
  private void listDatabaseConnections(List<DatabaseConnection> inputs) {
    if (inputs.isEmpty()) {
      content.add(new HTML("<h4>There are no Database Connections yet.</h4>"));
      return;
    }

    databaseConnectionTable.clear();
    int row = 1;

    databaseConnectionTable.setHTML(0, 0, "<b>Url</b>");
    databaseConnectionTable.setHTML(0, 1, "<b>Username</b>");
    databaseConnectionTable.setHTML(0, 2, "<b>Password</b>");

    for (final DatabaseConnection input : inputs) {
      final Button deleteButton = new Button("Delete");
      final int finalRow = row;
      deleteButton.addClickHandler(new ClickHandler() {
        @Override
        public void onClick(ClickEvent clickEvent) {
          databaseConnectionService.deleteDatabaseConnection(input, getDeleteCallback(databaseConnectionTable,
                                                                                      finalRow,
                                                                                      DATABASE_CONNECTION));
        }
      });

      Button runButton = new Button("Run");
      runButton.setTitle(String.valueOf(input.getId()));
      runButton.addClickHandler(new ClickHandler() {
        @Override
        public void onClick(ClickEvent event) {
          callRunConfiguration(convertDatabaseConnectionToDataSource(input));
        }
      });

      databaseConnectionTable.setWidget(row, 0, new HTML(input.getUrl()));
      databaseConnectionTable.setText(row, 1, input.getUsername());
      databaseConnectionTable.setText(row, 2, input.getPassword());
      databaseConnectionTable.setWidget(row, 3, runButton);
      databaseConnectionTable.setWidget(row, 4, deleteButton);
      row++;
    }

    this.content.add(new HTML("<h3>List of all Database Connections</h3>"));
    this.content.add(databaseConnectionTable);
  }

  /**
   * Creates the callback for the delete call.
   * @param table The table from which the input should be removed.
   * @param row The row, which contains the input.
   * @param type The type of the input.
   * @return The callback
   */
  protected AsyncCallback<Void> getDeleteCallback(final FlexTable table, final int row, final String type) {
    return new AsyncCallback<Void>() {
      @Override
      public void onFailure(Throwable throwable) {
        messageReceiver.addError("Could not delete the " + type + ": " + throwable.getMessage());
      }

      @Override
      public void onSuccess(Void aVoid) {
        table.removeRow(row);
      }
    };
  }

  /**
   * Converts a table input into a ConfigurationSettingDataSource
   * @param input the table input
   * @return      the ConfigurationSettingDataSource from the given table input
   */
  private ConfigurationSettingDataSource convertTableInputToDataSource(TableInput input) {
    // TODO configuration setting is missing
    return null;
  }

  /**
   * Converts a file input into a ConfigurationSettingDataSource
   * @param input the file input
   * @return      the ConfigurationSettingDataSource from the given file input
   */
  private ConfigurationSettingDataSource convertFileInputToDataSource(FileInput input) {
    ConfigurationSettingCsvFile setting = new ConfigurationSettingCsvFile();

    setting.setFileName(input.getFileName());
    setting.setEscapeChar(input.getEscapechar());
    setting.setHeader(input.isHasHeader());
    setting.setIgnoreLeadingWhiteSpace(input.isIgnoreLeadingWhiteSpace());
    setting.setQuoteChar(input.getQuotechar());
    setting.setSeparatorChar(input.getSeparator());
    setting.setSkipDifferingLines(input.isSkipDifferingLines());
    setting.setSkipLines(input.getSkipLines());
    setting.setStrictQuotes(input.isStrictQuotes());

    return setting;
  }

  /**
   * Converts a database connection into a ConfigurationSettingDataSource
   * @param input the database connection
   * @return      the ConfigurationSettingDataSource from the given database connection
   */
  private ConfigurationSettingDataSource convertDatabaseConnectionToDataSource(DatabaseConnection input) {
    return new ConfigurationSettingSqlIterator(input.getUrl(), input.getUsername(), input.getPassword(),
                                               DbSystem.DB2);
  }

  /**
   * Switch to the run configuration page.
   * The selected data source should be preselected.
   * @param dataSource the preselected data source
   */
  private void callRunConfiguration(ConfigurationSettingDataSource dataSource) {
    this.basePage.switchToRunConfiguration(null, dataSource);
  }

  /**
   * If a table input has a reference to a database connection, the delete button of the database
   * connection should be disabled.
   * To delete the database connection, first the related table input has to be deleted.
   * If the table input is deleted, the button should be enabled again.
   * @param connection the database connection, which delete button should be enabled/disabled
   * @param enabled true, if the button should be enabled, false otherwise
   */
  protected void setEnableOfDeleteButton(DatabaseConnection connection, boolean enabled) {
    int row = 0;
    while (row < this.databaseConnectionTable.getRowCount()) {
      HTML textWidget = (HTML) this.databaseConnectionTable.getWidget(row, 0);
      if (textWidget != null && connection.getUrl().equals(textWidget.getText())) {
        Button deleteButton = (Button) this.databaseConnectionTable.getWidget(row, 4);
        deleteButton.setEnabled(enabled);
        return;
      }
      row++;
    }
  }

  @Override
  public void setMessageReceiver(TabWrapper tab) {
    this.messageReceiver = tab;
    this.tableInputEditForm.setMessageReceiver(tab);
  }

}
