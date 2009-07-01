/*******************************************************************************
 * Copyright (c) 2002, 2008 Innoopract Informationssysteme GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Innoopract Informationssysteme GmbH - initial API and implementation
 ******************************************************************************/

package org.eclipse.swt.internal.custom.ctabfolderkit;

import java.io.IOException;

import org.eclipse.rwt.lifecycle.*;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.*;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.internal.custom.ICTabFolderAdapter;
import org.eclipse.swt.internal.widgets.Props;
import org.eclipse.swt.widgets.Widget;


public final class CTabFolderLCA extends AbstractWidgetLCA {

  // Request parameter that denotes the id of the selected tab item
  public static final String PARAM_SELECTED_ITEM_ID = "selectedItemId";
  // Request parameters for min/max state
  public static final String PARAM_MAXIMIZED = "maximized";
  public static final String PARAM_MINIMIZED = "minimized";

  // Request parameters that denote CTabFolderEvents
  public static final String EVENT_FOLDER_MINIMIZED
    = "org.eclipse.swt.events.ctabFolderMinimized";
  public static final String EVENT_FOLDER_MAXIMIZED
    = "org.eclipse.swt.events.ctabFolderMaximized";
  public static final String EVENT_FOLDER_RESTORED
    = "org.eclipse.swt.events.ctabFolderRestored";
  public static final String EVENT_SHOW_LIST
    = "org.eclipse.swt.events.ctabFolderShowList";

  // Property names for preserveValues
  public static final String PROP_SELECTION_INDEX = "selectionIndex";
  public static final String PROP_MAXIMIZED = "maximized";
  public static final String PROP_MINIMIZED = "minimized";
  public static final String PROP_MINIMIZE_VISIBLE = "minimizeVisible";
  public static final String PROP_MAXIMIZE_VISIBLE = "maximizeVisible";
  public static final String PROP_MINIMIZE_RECT = "minimizeRect";
  public static final String PROP_MAXIMIZE_RECT = "maximizeRect";
  public static final String PROP_FOLDER_LISTENERS = "folderListeners";
  public static final String PROP_TAB_HEIGHT = "tabHeight";
  public static final String PROP_WIDTH = "width";
  public static final String PROP_CHEVRON_VISIBLE = "chevronVisible";
  public static final String PROP_CHEVRON_RECT = "chevronRect";
  public static final String PROP_SELECTION_FG = "selectionFg";
  public static final String PROP_SELECTION_BG = "selectionBg";
  public static final String PROP_TAB_POSITION = "tabPosition";
  private static final String PROP_BORDER_VISIBLE = "borderVisible";

  // Keep in sync with value in CTabFolder.js
  private static final Integer DEFAULT_TAB_HEIGHT = new Integer( 20 );
  private static final Integer DEFAULT_TAB_POSITION = new Integer( SWT.TOP );

  private static final Rectangle ZERO_RECTANGLE = new Rectangle( 0, 0, 0, 0 );

  public void preserveValues( final Widget widget ) {
    CTabFolder tabFolder = ( CTabFolder )widget;
    IWidgetAdapter adapter = WidgetUtil.getAdapter( widget );
    ICTabFolderAdapter tabFolderAdapter = getCTabFolderAdapter( tabFolder );
    ControlLCAUtil.preserveValues( tabFolder );
    boolean hasListeners = SelectionEvent.hasListener( tabFolder );
    adapter.preserve( Props.SELECTION_LISTENERS,
                      Boolean.valueOf( hasListeners ) );
    hasListeners = CTabFolderEvent.hasListener( tabFolder );
    adapter.preserve( PROP_FOLDER_LISTENERS,
                      Boolean.valueOf( hasListeners ) );
    adapter.preserve( PROP_SELECTION_INDEX,
                      new Integer( tabFolder.getSelectionIndex() ) );
    adapter.preserve( PROP_WIDTH, new Integer( tabFolder.getBounds().width ) );
    adapter.preserve( PROP_MINIMIZE_VISIBLE,
                      Boolean.valueOf( tabFolder.getMinimizeVisible() ) );
    adapter.preserve( PROP_MAXIMIZE_VISIBLE,
                      Boolean.valueOf( tabFolder.getMaximizeVisible() ) );
    adapter.preserve( PROP_MINIMIZE_RECT,
                      tabFolderAdapter.getMinimizeRect() );
    adapter.preserve( PROP_MAXIMIZE_RECT,
                      tabFolderAdapter.getMaximizeRect() );
    adapter.preserve( PROP_MINIMIZED,
                      Boolean.valueOf( tabFolder.getMinimized() ) );
    adapter.preserve( PROP_MAXIMIZED,
                      Boolean.valueOf( tabFolder.getMaximized() ) );
    adapter.preserve( PROP_TAB_HEIGHT,
                      new Integer( tabFolder.getTabHeight() ) );
    adapter.preserve( PROP_TAB_POSITION,
                      new Integer( tabFolder.getTabPosition() ) );
    adapter.preserve( PROP_SELECTION_BG,
                      tabFolderAdapter.getUserSelectionBackground() );
    adapter.preserve( PROP_SELECTION_FG,
                      tabFolderAdapter.getUserSelectionForeground() );
    adapter.preserve( PROP_CHEVRON_VISIBLE,
                      Boolean.valueOf( tabFolderAdapter.getChevronVisible() ) );
    adapter.preserve( PROP_CHEVRON_RECT, tabFolderAdapter.getChevronRect() );
    adapter.preserve( PROP_BORDER_VISIBLE,
                      Boolean.valueOf( tabFolder.getBorderVisible() ) );
    WidgetLCAUtil.preserveCustomVariant( tabFolder );
  }

  public void readData( final Widget widget ) {
    final CTabFolder tabFolder = ( CTabFolder )widget;
    // Standard control events and properties
    String value
      = WidgetLCAUtil.readPropertyValue( tabFolder, PARAM_MINIMIZED );
    if( value != null ) {
      tabFolder.setMinimized( Boolean.valueOf( value ).booleanValue() );
    }
    // Read maximized state
    value = WidgetLCAUtil.readPropertyValue( tabFolder, PARAM_MAXIMIZED );
    if( value != null ) {
      tabFolder.setMaximized( Boolean.valueOf( value ).booleanValue() );
    }
    // Minimized event
    if( WidgetLCAUtil.wasEventSent( tabFolder, EVENT_FOLDER_MINIMIZED ) ) {
      CTabFolderEvent event = CTabFolderLCA.minimize( tabFolder );
      event.processEvent();
    }
    // Maximized event
    if( WidgetLCAUtil.wasEventSent( tabFolder, EVENT_FOLDER_MAXIMIZED ) ) {
      CTabFolderEvent event = CTabFolderLCA.maximize( tabFolder );
      event.processEvent();
    }
    // Restore event
    if( WidgetLCAUtil.wasEventSent( tabFolder, EVENT_FOLDER_RESTORED ) ) {
      CTabFolderEvent event = CTabFolderLCA.restore( tabFolder );
      event.processEvent();
    }
    // TODO [rh] it's a hack: necessary because folder.setSelection changes
    //      the visibility of tabItem.control; but preserveValues stores
    //      the already changed visibility and thus no JavaScript is rendered
    // Read selected item and process selection event
    final String selectedItemId
      = WidgetLCAUtil.readPropertyValue( tabFolder, PARAM_SELECTED_ITEM_ID );
    if( selectedItemId != null ) {
      ProcessActionRunner.add( new Runnable() {
        public void run() {
          CTabItem tabItem
            = ( CTabItem )WidgetUtil.find( tabFolder, selectedItemId );
          tabFolder.setSelection( tabItem );
          ControlLCAUtil.processSelection( tabFolder, tabItem, false );
        }
      } );
    }
    // ShowList event
    if( WidgetLCAUtil.wasEventSent( tabFolder, EVENT_SHOW_LIST ) ) {
      ProcessActionRunner.add( new Runnable() {
        public void run() {
          CTabFolderEvent event = CTabFolderLCA.showList( tabFolder );
          event.processEvent();
          if( event.doit ) {
            ICTabFolderAdapter adapter = getCTabFolderAdapter( tabFolder );
            adapter.showListMenu();
          }
        }
      } );
    }
    // Mouse events
    ControlLCAUtil.processMouseEvents( tabFolder );
    // Key events
    ControlLCAUtil.processKeyEvents( tabFolder );
    // Help events
    WidgetLCAUtil.processHelp( tabFolder );
  }

  public void renderInitialization( final Widget widget ) throws IOException {
    JSWriter writer = JSWriter.getWriterFor( widget );
    writer.newWidget( "org.eclipse.swt.custom.CTabFolder" );    
    CTabFolder tabFolder = ( CTabFolder )widget;
    ControlLCAUtil.writeStyleFlags( tabFolder );
    String[] args = new String[] {
      SWT.getMessage( "SWT_Minimize" ),
      SWT.getMessage( "SWT_Maximize" ),
      SWT.getMessage( "SWT_Restore" ),
      SWT.getMessage( "SWT_ShowList" ),
      SWT.getMessage( "SWT_Close" ),
    };
    writer.callStatic( "org.eclipse.swt.custom.CTabFolder.setToolTipTexts",
                       args );
  }

  public void renderChanges( final Widget widget ) throws IOException {
    CTabFolder tabFolder = ( CTabFolder )widget;
    ControlLCAUtil.writeChanges( tabFolder );
    writeTabPosition( tabFolder );
    writeTabHeight( tabFolder );
    writeMinMaxVisible( tabFolder );
    writeMinMaxState( tabFolder );
    writeListener( tabFolder );
    writeChevron( tabFolder );
    writeColors( tabFolder );
    writeBorderVisible( tabFolder );
    WidgetLCAUtil.writeCustomVariant( tabFolder );
  }

  public void renderDispose( final Widget widget ) throws IOException {
    JSWriter writer = JSWriter.getWriterFor( widget );
    writer.dispose();
  }

  public void createResetHandlerCalls( final String typePoolId ) throws IOException {
  }

  public String getTypePoolId( final Widget widget ) {
    return null;
  }

  public Rectangle adjustCoordinates( final Widget widget,
                                      final Rectangle bounds )
  {
    Rectangle result
      = new Rectangle( bounds.x, bounds.y, bounds.width, bounds.height );
    if( widget instanceof CTabItem ) {
      result.height += 1;
      CTabItem item = ( CTabItem )widget;
      CTabFolder parent = item.getParent();
      if( parent.getTabPosition() == SWT.BOTTOM ) {
        result.y -= 1;
      }
    }
    return result;
  }

//  public Rectangle adjustCoordinates( final Rectangle bounds ) {
//    int border = 1;
//    int hTabBar = 23;
//    return new Rectangle( bounds.x - border - 10,
//                          bounds.y - hTabBar - border -10,
//                          bounds.width,
//                          bounds.height );
//  }


  //////////////////////////////////////
  // Helping methods to write properties

  private static void writeTabPosition( final CTabFolder tabFolder )
    throws IOException
  {
    Integer newValue = new Integer( tabFolder.getTabPosition() );
    Integer defValue = DEFAULT_TAB_POSITION;
    String prop = PROP_TAB_POSITION;
    if( WidgetLCAUtil.hasChanged( tabFolder, prop, newValue, defValue ) ) {
      JSWriter writer = JSWriter.getWriterFor( tabFolder );
      String tabPosition = newValue.intValue() == SWT.TOP ? "top" : "bottom";
      writer.set( "tabPosition", tabPosition );
    }
  }

  private static void writeTabHeight( final CTabFolder tabFolder )
    throws IOException
  {
    Integer tabHeight = new Integer( tabFolder.getTabHeight() );
    JSWriter writer = JSWriter.getWriterFor( tabFolder );
    writer.set( PROP_TAB_HEIGHT, "tabHeight", tabHeight, DEFAULT_TAB_HEIGHT );
  }

  private static void writeMinMaxVisible( final CTabFolder tabFolder )
    throws IOException
  {
    JSWriter writer = JSWriter.getWriterFor( tabFolder );
    boolean minChanged = hasMinChanged( tabFolder );
    boolean maxChanged = hasMaxChanged( tabFolder );
    if( minChanged || maxChanged ) {
      ICTabFolderAdapter tabFolderAdapter = getCTabFolderAdapter( tabFolder );
      if( tabFolder.getMaximizeVisible() ) {
        Rectangle maximizeRect = tabFolderAdapter.getMaximizeRect();
        Object[] args = new Object[] {
          new Integer( maximizeRect.x ),
          new Integer( maximizeRect.y ),
          new Integer( maximizeRect.width ),
          new Integer( maximizeRect.height )
        };
        writer.call( "showMaxButton", args );
      } else {
        writer.call( "hideMaxButton", null );
      }
      if( tabFolder.getMinimizeVisible() ) {
        Rectangle minimizeRect = tabFolderAdapter.getMinimizeRect();
        Object[] args = new Object[] {
          new Integer( minimizeRect.x ),
          new Integer( minimizeRect.y ),
          new Integer( minimizeRect.width ),
          new Integer( minimizeRect.height )
        };
        writer.call( "showMinButton", args );
      } else {
        writer.call( "hideMinButton", null );
      }
    }
  }

  private static boolean hasMinChanged( final CTabFolder tabFolder ) {
    ICTabFolderAdapter tabFolderAdapter = getCTabFolderAdapter( tabFolder );
    Boolean minVisible = Boolean.valueOf( tabFolder.getMinimizeVisible() );
    boolean visibilityChanged;
    visibilityChanged = WidgetLCAUtil.hasChanged( tabFolder,
                                                  PROP_MINIMIZE_VISIBLE,
                                                  minVisible,
                                                  Boolean.FALSE );
    boolean boundsChanged = false;
    if( !visibilityChanged ) {
      Rectangle newBounds = tabFolderAdapter.getMinimizeRect();
      boundsChanged = WidgetLCAUtil.hasChanged( tabFolder,
                                                PROP_MINIMIZE_RECT,
                                                newBounds,
                                                ZERO_RECTANGLE );
    }
    return visibilityChanged || boundsChanged;
  }

  private static boolean hasMaxChanged( final CTabFolder tabFolder ) {
    ICTabFolderAdapter tabFolderAdapter = getCTabFolderAdapter( tabFolder );
    Boolean maxVisible = Boolean.valueOf( tabFolder.getMaximizeVisible() );
    boolean visibilityChanged;
    visibilityChanged = WidgetLCAUtil.hasChanged( tabFolder,
                                                  PROP_MAXIMIZE_VISIBLE,
                                                  maxVisible,
                                                  Boolean.FALSE );
    boolean boundsChanged = false;
    if( !visibilityChanged ) {
      Rectangle newBounds = tabFolderAdapter.getMaximizeRect();
      boundsChanged = WidgetLCAUtil.hasChanged( tabFolder,
                                                PROP_MAXIMIZE_RECT,
                                                newBounds,
                                                ZERO_RECTANGLE );
    }
    return visibilityChanged || boundsChanged;
  }

  private static void writeMinMaxState( final CTabFolder tabFolder )
    throws IOException
  {
    JSWriter writer = JSWriter.getWriterFor( tabFolder );
    String minProp = CTabFolderLCA.PARAM_MINIMIZED;
    String maxProp = CTabFolderLCA.PARAM_MAXIMIZED;
    Boolean minimized = Boolean.valueOf( tabFolder.getMinimized() );
    Boolean maximized = Boolean.valueOf( tabFolder.getMaximized() );
    Boolean defValue = Boolean.FALSE;
    if(    WidgetLCAUtil.hasChanged( tabFolder, minProp, minimized, defValue )
        || WidgetLCAUtil.hasChanged( tabFolder, maxProp, maximized, defValue ) )
    {
      String state;
      if( !tabFolder.getMinimized() && !tabFolder.getMaximized() ) {
        state = "normal";
      } else if( tabFolder.getMinimized() ){
        state = "min";
      } else {
        state = "max";
      }
      writer.set( "minMaxState", state );
    }
  }

  private static void writeListener( final CTabFolder tabFolder )
    throws IOException
  {
    boolean hasListener = CTabFolderEvent.hasListener( tabFolder );
    Boolean newValue = Boolean.valueOf( hasListener );
    if( WidgetLCAUtil.hasChanged( tabFolder, PROP_FOLDER_LISTENERS, newValue, Boolean.FALSE ) ) {
      JSWriter writer = JSWriter.getWriterFor( tabFolder );
      writer.set( "hasFolderListener", newValue );
    }
    hasListener = SelectionEvent.hasListener( tabFolder );
    newValue = Boolean.valueOf( hasListener );
    String prop = Props.SELECTION_LISTENERS;
    if( WidgetLCAUtil.hasChanged( tabFolder, prop, newValue, Boolean.FALSE ) ) {
      JSWriter writer = JSWriter.getWriterFor( tabFolder );
      writer.set( "hasSelectionListener", newValue );
    }
  }

  private static void writeChevron( final CTabFolder tabFolder )
    throws IOException
  {
    ICTabFolderAdapter tabFolderAdapter = getCTabFolderAdapter( tabFolder );
    Boolean visible = Boolean.valueOf( tabFolderAdapter.getChevronVisible() );
    Boolean defValue = Boolean.FALSE;
    String prop = PROP_CHEVRON_VISIBLE;
    boolean visibilityChanged
      = WidgetLCAUtil.hasChanged( tabFolder, prop, visible, defValue );
    prop = PROP_CHEVRON_RECT;
    Rectangle chevronRect = tabFolderAdapter.getChevronRect();
    boolean rectangleChanged
      = WidgetLCAUtil.hasChanged( tabFolder, prop, chevronRect, null );
    if( visibilityChanged || rectangleChanged ) {
      JSWriter writer = JSWriter.getWriterFor( tabFolder );
      if( visible.booleanValue() ) {
        Object[] args = new Object[] {
          new Integer( chevronRect.x ),
          new Integer( chevronRect.y ),
          new Integer( chevronRect.width ),
          new Integer( chevronRect.height )
        };
        writer.call( "showChevron", args );
      } else {
        writer.call( "hideChevron", null );
      }
    }
  }

  private static void writeColors( final CTabFolder tabFolder )
    throws IOException
  {
    JSWriter writer = JSWriter.getWriterFor( tabFolder );
    ICTabFolderAdapter adapter
      = ( ICTabFolderAdapter )tabFolder.getAdapter( ICTabFolderAdapter.class );
    Color selFg = adapter.getUserSelectionForeground();
    writer.set( PROP_SELECTION_FG, "selectionForeground", selFg, null );
    Color selBg = adapter.getUserSelectionBackground();
    writer.set( PROP_SELECTION_BG, "selectionBackground", selBg, null );
  }

  private static void writeBorderVisible( final CTabFolder tabFolder )
    throws IOException
  {
    JSWriter writer = JSWriter.getWriterFor( tabFolder );
    writer.set( PROP_BORDER_VISIBLE,
                "borderVisible",
                Boolean.valueOf( tabFolder.getBorderVisible() ),
                Boolean.valueOf( ( tabFolder.getStyle() & SWT.BORDER ) != 0 ) );
  }

  private static ICTabFolderAdapter getCTabFolderAdapter(
    final CTabFolder tabFolder )
  {
    Object adapter = tabFolder.getAdapter( ICTabFolderAdapter.class );
    return ( ICTabFolderAdapter )adapter;
  }

  ///////////////
  // Event helper

  private static CTabFolderEvent showList( final CTabFolder tabFolder ) {
    CTabFolderEvent result
      = new CTabFolderEvent( tabFolder, CTabFolderEvent.SHOW_LIST );
    Object adapter = tabFolder.getAdapter( ICTabFolderAdapter.class );
    ICTabFolderAdapter folderAdapter = ( ICTabFolderAdapter )adapter;
    Rectangle chevronRect = folderAdapter.getChevronRect();
    result.x = chevronRect.x;
    result.y = chevronRect.y;
    result.height = chevronRect.height;
    result.width = chevronRect.width;
    result.doit = true;
    return result;
  }

  private static CTabFolderEvent restore( final CTabFolder tabFolder ) {
    return new CTabFolderEvent( tabFolder, CTabFolderEvent.RESTORE );
  }

  private static CTabFolderEvent maximize( final CTabFolder tabFolder ) {
    return new CTabFolderEvent( tabFolder, CTabFolderEvent.MAXIMIZE );
  }

  private static CTabFolderEvent minimize( final CTabFolder tabFolder ) {
    return new CTabFolderEvent( tabFolder, CTabFolderEvent.MINIMIZE );
  }
}
