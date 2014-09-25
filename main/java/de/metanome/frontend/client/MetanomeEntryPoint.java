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

package de.metanome.frontend.client;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.LayoutPanel;
import com.google.gwt.user.client.ui.RootLayoutPanel;

public class MetanomeEntryPoint implements EntryPoint {

  @Override
  public void onModuleLoad() {
    System.out.println(GWT.getModuleBaseURL());

    LayoutPanel bodyPanel = new LayoutPanel();
    bodyPanel.addStyleName("body");

    FlowPanel heading = new FlowPanel();
    heading.add(new HTML("<h1>Metanome</h1>"));
    heading.addStyleName("heading");

    DockLayoutPanel p = new DockLayoutPanel(Style.Unit.EM);
    p.addNorth(heading, 4);
    p.add(bodyPanel);

    RootLayoutPanel root = RootLayoutPanel.get();
    root.addStyleName("root");
    root.add(p);

    bodyPanel.add(new BasePage());
  }

}
