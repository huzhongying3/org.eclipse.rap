/*******************************************************************************
 * Copyright (c) 2002-2006 Innoopract Informationssysteme GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Innoopract Informationssysteme GmbH - initial API and implementation
 ******************************************************************************/

package org.eclipse.rap.rwt.internal.widgets;

/**
 * TODO [rh] JavaDoc
 * <p></p>
 */
// TODO: [fappel] don't know whether it is a good idea to have a global
//                constant class for properties of different widgets...
public final class Props {

  // Control properties
  public static final String BOUNDS = "bounds";
  public static final String TOOL_TIP_TEXT = "toolTip";
  public static final String MENU = "menu";
  public static final String CONTROL_LISTENERS = "hasControlListeners";
  
  // Scrollable
  public static final String CLIENT_AREA = "clientArea";

  // Button properties
  public static final String SELECTION_LISTENERS = "hasSelectionListeners";
  
  // Text properties
  public static final String TEXT = "text";
  
  public static final String IMAGE = "image";
  
  // TabFolder and TabItem properties
  public static final String SELECTION_INDEX = "selectionIndex";
  public static final String CHECKED = "checked";
  
  // Table, TableItem and TableColumn properties
  public static final String SELECTION_INDICES = "selection";
  
  
  private Props() {
    // prevent instantiation
  }
}

