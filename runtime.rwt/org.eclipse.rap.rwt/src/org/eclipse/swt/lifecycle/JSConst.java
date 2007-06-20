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

package org.eclipse.swt.lifecycle;


/**
 * TODO [rh] JavaDoc
 */
public final class JSConst {
  
  //////////////////////////
  // Request parameter names
  
  // SWT keys used to identify which kind of SWT-Event is requested
  public static final String EVENT_WIDGET_SELECTED 
    = "org.eclipse.swt.events.widgetSelected";
  public static final String EVENT_WIDGET_DEFAULT_SELECTED 
    = "org.eclipse.swt.events.widgetDefaultSelected";
  public static final String EVENT_WIDGET_RESIZED 
    = "org.eclipse.swt.events.widgetResized";
  public static final String EVENT_WIDGET_MOVED
    = "org.eclipse.swt.events.widgetMoved";
  public static final String EVENT_WIDGET_ACTIVATED 
    = "org.eclipse.swt.events.controlActivated";
  public static final String EVENT_SHELL_ACTIVATED 
    = "org.eclipse.swt.events.shellActivated";
  public static final String EVENT_TREE_EXPANDED 
    = "org.eclipse.swt.events.treeExpanded";
  public static final String EVENT_TREE_COLLAPSED 
    = "org.eclipse.swt.events.treeCollapsed";
  public static final String EVENT_MODIFY_TEXT
    = "org.eclipse.swt.events.modifyText";
  public static final String EVENT_FOCUS_GAINED
    = "org.eclipse.swt.events.focusGained";
  public static final String EVENT_FOCUS_LOST
    = "org.eclipse.swt.events.focusLost";
  public static final String EVENT_MENU_SHOWN
    = "org.eclipse.swt.events.menuShown";
  public static final String EVENT_MENU_HIDDEN
    = "org.eclipse.swt.events.menuHidden";
  public static final String EVENT_SET_DATA
    = "org.eclipse.swt.events.setData";
  
  // Parameter names that specify further event details 
  public static final String EVENT_WIDGET_SELECTED_DETAIL 
    = "org.eclipse.swt.events.widgetSelected.detail";
  public static final String EVENT_SET_DATA_INDEX
    = "org.eclipse.swt.events.setData.index";
  
  /** 
   * <p>Indicates that a shell was closed on the client side. The parameter 
   * value holds the id of the shell that was closed.</p> */
  public static final String EVENT_SHELL_CLOSED 
    = "org.eclipse.swt.widgets.Shell_close";
  
  // function pointers for client side event handling
  public static final String JS_WIDGET_SELECTED 
    = "org.eclipse.swt.EventUtil.widgetSelected";
  public static final String JS_WIDGET_RESIZED
    = "org.eclipse.swt.EventUtil.widgetResized";
  public static final String JS_WIDGET_MOVED
    = "org.eclipse.swt.EventUtil.widgetMoved";
  public static final String JS_SHELL_CLOSED
    = "org.eclipse.swt.EventUtil.shellClosed";
  public static final String JS_TREE_SELECTED 
    = "org.eclipse.swt.TreeUtil.widgetSelected";
  public static final String JS_CONTEXT_MENU 
    = "org.eclipse.swt.MenuUtil.contextMenu";
  
  // keys of the Qooxdoo listeners, used to register the client side 
  // eventhandlers 
  public static final String QX_EVENT_EXECUTE = "execute";
  public static final String QX_EVENT_BLUR = "blur";
  public static final String QX_EVENT_KEYDOWN = "keydown";
  public static final String QX_EVENT_KEY_UP = "keyup";
  public static final String QX_EVENT_CHANGE_LOCATION_X = "changeLeft";
  public static final String QX_EVENT_CHANGE_LOCATION_Y = "changeTop";
  public static final String QX_EVENT_CHANGE_WIDTH = "changeWidth";
  public static final String QX_EVENT_CHANGE_HEIGHT = "changeHeight";
  public static final String QX_EVENT_CHANGE_VISIBILITY = "changeVisibility";
  public static final String QX_EVENT_CONTEXTMENU = "contextmenu";
  public static final String QX_EVENT_CHANGE_SELECTED = "changeSelected";
  public static final String QX_EVENT_CHANGE_CHECKED = "changeChecked";
  
  // field names
  public static final String QX_FIELD_LABEL = "label";
  public static final String QX_FIELD_ICON = "icon";
  public static final String QX_FIELD_SELECTION = "selection";
  public static final String QX_FIELD_FONT = "font";
  public static final String QX_FIELD_COLOR = "textColor";
  public static final String QX_FIELD_BG_COLOR = "backgroundColor";
  public static final String QX_FIELD_ORIENTATION = "orientation";
  public static final String QX_FIELD_CAPTION = "caption";
  public static final String QX_FIELD_ENABLED = "enabled";
  public static final String QX_FIELD_VISIBLE = "visibility";
  public static final String QX_FIELD_CHECKED = "checked";
  public static final String QX_FIELD_APPEARANCE = "appearance";
  public static final String QX_FIELD_Z_INDEX = "zIndex";
  public static final String QX_FIELD_TAB_INDEX = "tabIndex";
  
  // constants
  public static final JSVar QX_CONST_VERTICAL_ORIENTATION
    = new JSVar( "qx.constant.Layout.ORIENTATION_VERTICAL" );
  public static final JSVar QX_CONST_HORIZONTAL_ORIENTATION 
    = new JSVar( "qx.constant.Layout.ORIENTATION_HORIZONTAL" );
  
  public static final JSVar QX_CONST_ALIGN_RIGHT 
    = new JSVar( "qx.constant.Layout.ALIGN_RIGHT" );
  public static final JSVar QX_CONST_ALIGN_CENTER 
    = new JSVar( "qx.constant.Layout.ALIGN_CENTER" );
  public static final JSVar QX_CONST_ALIGN_LEFT 
    = new JSVar( "qx.constant.Layout.ALIGN_LEFT" );
  
  private JSConst() {
    // prevent instantiation
  }
}
