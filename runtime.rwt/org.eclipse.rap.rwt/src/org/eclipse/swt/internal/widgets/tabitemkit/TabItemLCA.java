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

package org.eclipse.swt.internal.widgets.tabitemkit;

import java.io.IOException;

import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.internal.widgets.ItemLCAUtil;
import org.eclipse.swt.internal.widgets.Props;
import org.eclipse.swt.lifecycle.*;
import org.eclipse.swt.widgets.*;


public class TabItemLCA extends AbstractWidgetLCA {

  private static final String PROP_CHECKED = "checked";

  private final static JSListenerInfo JS_LISTENER_INFO
    = new JSListenerInfo( "changeChecked",
                          "org.eclipse.swt.TabUtil.tabSelected",
                          JSListenerType.STATE_AND_ACTION );


  public void preserveValues( final Widget widget ) {
    TabItem item = ( TabItem )widget;
    ItemLCAUtil.preserve( item );
    IWidgetAdapter adapter = WidgetUtil.getAdapter( widget );
    adapter.preserve( PROP_CHECKED, Boolean.valueOf( isChecked( item ) ) );
    // preserve the listener state of the parent tabfolder here, since the
    // javascript handling is added to the clientside tab buttons and therefore
    // the jswriter will check the preserved state of the tabitem...
    TabFolder folder = item.getParent();
    boolean hasListeners = SelectionEvent.hasListener( folder );
    adapter.preserve( Props.SELECTION_LISTENERS,
                      Boolean.valueOf( hasListeners ) );
  }
  
  public void readData( final Widget widget ) {
    String value = WidgetLCAUtil.readPropertyValue( widget, "checked" );
    if( Boolean.valueOf( value ).booleanValue() ) {
      final TabItem item = ( TabItem )widget;
      final TabFolder folder = item.getParent();
      // TODO [rh] same hack as in CTabFolderLCA#readData
      // Read selected item and process selection event
      ProcessActionRunner.add( new Runnable() {
        public void run() {
          folder.setSelection( item );
          ControlLCAUtil.processSelection( folder, item, false );
        }
      } );
    }
  }
  
  public void renderInitialization( final Widget widget ) throws IOException {
    TabItem tabItem = ( TabItem )widget;
    JSWriter writer = JSWriter.getWriterFor( widget );
    Object[] args = new Object[] { 
      WidgetUtil.getId( tabItem ), 
      WidgetUtil.getId( tabItem.getParent() )
    };
    writer.callStatic( "org.eclipse.swt.TabUtil.createTabItem", args );
  }

  public void renderChanges( final Widget widget ) throws IOException {
    TabItem tabItem = ( TabItem )widget;
    setJSParent( tabItem );
    JSWriter writer = JSWriter.getWriterFor( tabItem );
    ItemLCAUtil.writeChanges( tabItem );
    writeSelection( tabItem );
    writer.updateListener( JS_LISTENER_INFO, 
                           Props.SELECTION_LISTENERS, 
                           SelectionEvent.hasListener( tabItem.getParent() ) );
  }
  
  public void renderDispose( final Widget widget ) throws IOException {
    // TODO [rh] preliminary: find out how to properly dispose of a TabItem
    JSWriter writer = JSWriter.getWriterFor( widget );
    writer.dispose();
  }
  
  //////////////////
  // helping methods
  
  private void writeSelection( final TabItem item ) throws IOException {
    JSWriter writer = JSWriter.getWriterFor( item );
    Boolean newValue = Boolean.valueOf( isChecked( item ) );
    writer.set( PROP_CHECKED, "checked", newValue, Boolean.FALSE );
  }
  
  private boolean isChecked( final TabItem tabItem ) {
    TabFolder parent = tabItem.getParent();
    int selectionIndex = parent.getSelectionIndex();
    return selectionIndex != -1 && parent.getItem( selectionIndex ) == tabItem;
  }
  
  private static void setJSParent( final TabItem tabItem ) {
    Control control = tabItem.getControl();
    if( control != null ) {
      IWidgetAdapter itemAdapter = WidgetUtil.getAdapter( tabItem );
      StringBuffer replacementId = new StringBuffer();
      replacementId.append( itemAdapter.getId() );
      replacementId.append( "pg" );
      IWidgetAdapter controlAdapter = WidgetUtil.getAdapter( control );
      controlAdapter.setJSParent( replacementId.toString() );
    }
  }
}
