/*******************************************************************************
 * Copyright (c) 2002, 2009 Innoopract Informationssysteme GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Innoopract Informationssysteme GmbH - initial API and implementation
 *     EclipseSource - ongoing development
 ******************************************************************************/
package org.eclipse.swt.internal.widgets.treekit;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;

import org.eclipse.rwt.internal.lifecycle.JSConst;
import org.eclipse.rwt.internal.service.ContextProvider;
import org.eclipse.rwt.lifecycle.*;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.*;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.internal.events.EventLCAUtil;
import org.eclipse.swt.internal.widgets.ITreeAdapter;
import org.eclipse.swt.widgets.*;

public final class TreeLCA extends AbstractWidgetLCA {

  // Property names used by preserve mechanism
  private static final String PROP_SELECTION_LISTENERS = "selectionListeners";
  static final String PROP_HEADER_HEIGHT = "headerHeight";
  static final String PROP_HEADER_VISIBLE = "headerVisible";
  static final String PROP_COLUMN_ORDER = "columnOrder";
  static final String PROP_SCROLL_LEFT = "scrollLeft";
  static final String PROP_HAS_H_SCROLL_BAR = "hasHScrollBar";
  static final String PROP_HAS_V_SCROLL_BAR = "hasVScrollBar";

  private static final Integer DEFAULT_SCROLL_LEFT = new Integer( 0 );

  public void preserveValues( final Widget widget ) {
    Tree tree = ( Tree )widget;
    ControlLCAUtil.preserveValues( ( Control )widget );
    IWidgetAdapter adapter = WidgetUtil.getAdapter( tree );
    adapter.preserve( PROP_SELECTION_LISTENERS,
                      Boolean.valueOf( SelectionEvent.hasListener( tree ) ) );
    adapter.preserve( PROP_HEADER_HEIGHT,
                      new Integer( tree.getHeaderHeight() ) );
    adapter.preserve( PROP_HEADER_VISIBLE,
                      Boolean.valueOf( tree.getHeaderVisible() ) );
    int[] values = tree.getColumnOrder();
    Integer[] columnOrder = new Integer[ values.length ];
    for( int i = 0; i < values.length; i++ ) {
      columnOrder[ i ] = new Integer( values[ i ] );
    }
    adapter.preserve( PROP_COLUMN_ORDER, columnOrder );
    adapter.preserve( PROP_SCROLL_LEFT, getScrollLeft( tree ) );
    adapter.preserve( PROP_HAS_H_SCROLL_BAR, hasHScrollBar( tree ) );
    adapter.preserve( PROP_HAS_V_SCROLL_BAR, hasVScrollBar( tree ) );
    WidgetLCAUtil.preserveCustomVariant( tree );
  }

  public void readData( final Widget widget ) {
    Tree tree = ( Tree )widget;
    readSelection( tree );
    readScrollPosition( tree );
    processWidgetSelectedEvent( tree );
    processWidgetDefaultSelectedEvent( tree );
    ControlLCAUtil.processMouseEvents( tree );
    ControlLCAUtil.processKeyEvents( tree );
    ControlLCAUtil.processMenuDetect( tree );
    WidgetLCAUtil.processHelp( tree );
  }

  public void renderInitialization( final Widget widget ) throws IOException {
    Tree tree = ( Tree )widget;
    JSWriter writer = JSWriter.getWriterFor( tree );
    StringBuffer style = new StringBuffer();
    if( ( tree.getStyle() & SWT.MULTI ) != 0 ) {
      style.append( "multi|" );
    }
    if( ( tree.getStyle() & SWT.CHECK ) != 0 ) {
      style.append( "check|" );
    }
    if( ( tree.getStyle() & SWT.VIRTUAL ) != 0 ) {
      style.append( "virtual|" );
    }
    writer.newWidget( "org.eclipse.swt.widgets.Tree", new Object[]{
      style.toString()
    } );
    ControlLCAUtil.writeStyleFlags( tree );
  }

  public void renderChanges( final Widget widget ) throws IOException {
    Tree tree = ( Tree )widget;
    ControlLCAUtil.writeChanges( tree );
    updateSelectionListener( tree );
    writeHeaderHeight( tree );
    writeHeaderVisible( tree );
    writeColumnOrder( tree );
    writeScrollLeft( tree );
    writeOverflow( tree );
    WidgetLCAUtil.writeCustomVariant( tree );
  }

  public void renderDispose( final Widget widget ) throws IOException {
    JSWriter writer = JSWriter.getWriterFor( widget );
    writer.dispose();
  }

  public void doRedrawFake( final Control control ) {
    int evtId = ControlEvent.CONTROL_RESIZED;
    ControlEvent evt = new ControlEvent( control, evtId );
    evt.processEvent();
  }

  private static void processWidgetSelectedEvent( final Tree tree ) {
    HttpServletRequest request = ContextProvider.getRequest();
    String eventName = JSConst.EVENT_WIDGET_SELECTED;
    if( WidgetLCAUtil.wasEventSent( tree, eventName ) ) {
      Rectangle bounds = new Rectangle( 0, 0, 0, 0 );
      String itemId = request.getParameter( eventName + ".item" );
      Item treeItem = ( Item )WidgetUtil.find( tree, itemId );
      String detailStr = request.getParameter( eventName + ".detail" );
      int detail = "check".equals( detailStr )
                                              ? SWT.CHECK
                                              : SWT.NONE;
      int eventType = SelectionEvent.WIDGET_SELECTED;
      int stateMask
        = EventLCAUtil.readStateMask( JSConst.EVENT_WIDGET_SELECTED_MODIFIER );
      SelectionEvent event = new SelectionEvent( tree,
                                                 treeItem,
                                                 eventType,
                                                 bounds,
                                                 stateMask,
                                                 null,
                                                 true,
                                                 detail );
      event.processEvent();
    }
  }

  private static void processWidgetDefaultSelectedEvent( final Tree tree ) {
    HttpServletRequest request = ContextProvider.getRequest();
    String eventName = JSConst.EVENT_WIDGET_DEFAULT_SELECTED;
    if( WidgetLCAUtil.wasEventSent( tree, eventName ) ) {
      String itemId = request.getParameter( eventName + ".item" );
      Item treeItem = ( Item )WidgetUtil.find( tree, itemId );
      int eventType = SelectionEvent.WIDGET_DEFAULT_SELECTED;
      SelectionEvent event = new SelectionEvent( tree, treeItem, eventType );
      event.stateMask
        = EventLCAUtil.readStateMask( JSConst.EVENT_WIDGET_SELECTED_MODIFIER );
      event.processEvent();
    }
  }

  /////////////////////////////////////////////
  // Helping methods to read client-side state

  private static void readSelection( final Tree tree ) {
    String value = WidgetLCAUtil.readPropertyValue( tree, "selection" );
    if( value != null ) {
      String[] values = value.split( "," );
      TreeItem[] selectedItems = new TreeItem[ values.length ];
      boolean validItemFound = false;
      for( int i = 0; i < values.length; i++ ) {
        selectedItems[ i ] = ( TreeItem )WidgetUtil.find( tree, values[ i ] );
        if( selectedItems[ i ] != null ) {
          validItemFound = true;
        }
      }
      if( !validItemFound ) {
        selectedItems = new TreeItem[ 0 ];
      }
      tree.setSelection( selectedItems );
    }
  }

  private static void readScrollPosition( final Tree tree ) {
    String left = WidgetLCAUtil.readPropertyValue( tree, "scrollLeft" );
    String top = WidgetLCAUtil.readPropertyValue( tree, "scrollTop" );
    if( left != null && top != null ) {
      Object adapter = tree.getAdapter( ITreeAdapter.class );
      final ITreeAdapter treeAdapter = ( ITreeAdapter )adapter;
      final int newScrollLeft = parsePosition( left );
      final int newScrollTop = parsePosition( top );
      final int oldScrollTop = treeAdapter.getScrollTop();
      treeAdapter.setScrollLeft( newScrollLeft );
      treeAdapter.setScrollTop( newScrollTop );
      if( oldScrollTop != newScrollTop ) {
        ProcessActionRunner.add( new Runnable() {
          public void run() {
            treeAdapter.checkAllData( tree );
          }
        } );
      }
    }
  }

  private static int parsePosition( final String position ) {
    int result = 0;
    try {
      result = Integer.valueOf( position ).intValue();
    } catch( NumberFormatException e ) {
      // ignore and use default value
    }
    return result;
  }

  //////////////////////////////////////////////////////////////
  // Helping methods to write JavaScript for changed properties

  private static void writeHeaderHeight( final Tree tree ) throws IOException {
    JSWriter writer = JSWriter.getWriterFor( tree );
    Integer newValue = new Integer( tree.getHeaderHeight() );
    writer.set( PROP_HEADER_HEIGHT, "headerHeight", newValue, null );
  }

  private void writeColumnOrder( final Tree tree ) throws IOException {
    JSWriter writer = JSWriter.getWriterFor( tree );
    int[] values = tree.getColumnOrder();
    if( values.length > 0 ) {
      Integer[] newValue = new Integer[ values.length ];
      for( int i = 0; i < values.length; i++ ) {
        newValue[ i ] = new Integer( values[ i ] );
      }
      if( WidgetLCAUtil.hasChanged( tree,
                                    PROP_COLUMN_ORDER,
                                    newValue,
                                    new Integer[]{} ) )
      {
        writer.set( PROP_COLUMN_ORDER, "columnOrder", newValue, null );
      }
    }
  }

  private static void writeHeaderVisible( final Tree tree ) throws IOException {
    JSWriter writer = JSWriter.getWriterFor( tree );
    Boolean newValue = Boolean.valueOf( tree.getHeaderVisible() );
    writer.set( PROP_HEADER_VISIBLE, "headerVisible", newValue, Boolean.FALSE );
  }

  private void writeScrollLeft( final Tree tree ) throws IOException {
    JSWriter writer = JSWriter.getWriterFor( tree );
    Integer newValue = getScrollLeft( tree );
    writer.set( PROP_SCROLL_LEFT, "scrollLeft", newValue, DEFAULT_SCROLL_LEFT );
  }

  private static void writeOverflow( final Tree tree ) throws IOException {
    boolean hasHChanged = WidgetLCAUtil.hasChanged( tree,
                                                    PROP_HAS_H_SCROLL_BAR,
                                                    hasHScrollBar( tree ),
                                                    Boolean.TRUE );
    boolean hasVChanged = WidgetLCAUtil.hasChanged( tree,
                                                    PROP_HAS_V_SCROLL_BAR,
                                                    hasVScrollBar( tree ),
                                                    Boolean.TRUE );
    if( hasHChanged || hasVChanged ) {
      boolean scrollX = hasHScrollBar( tree ).booleanValue();
      boolean scrollY = hasVScrollBar( tree ).booleanValue();
      String overflow;
      if( scrollX && scrollY ) {
        overflow = "scroll";
      } else if( scrollX ) {
        overflow = "scrollX";
      } else if( scrollY ) {
        overflow = "scrollY";
      } else {
        overflow = "hidden";
      }
      JSWriter writer = JSWriter.getWriterFor( tree );
      writer.set( "treeOverflow", overflow );
    }
  }

  private static void updateSelectionListener( final Tree tree )
    throws IOException
  {
    Boolean newValue = Boolean.valueOf( SelectionEvent.hasListener( tree ) );
    String prop = PROP_SELECTION_LISTENERS;
    if( WidgetLCAUtil.hasChanged( tree, prop, newValue, Boolean.FALSE ) ) {
      JSWriter writer = JSWriter.getWriterFor( tree );
      writer.set( "selectionListeners", newValue );
    }
  }

  //////////////////
  // Helping methods

  private static Integer getScrollLeft( final Tree tree ) {
    Object adapter = tree.getAdapter( ITreeAdapter.class );
    ITreeAdapter treeAdapter = ( ITreeAdapter )adapter;
    return new Integer( treeAdapter.getScrollLeft() );
  }

  private static Boolean hasHScrollBar( final Tree tree ) {
    Object adapter = tree.getAdapter( ITreeAdapter.class );
    ITreeAdapter treeAdapter = ( ITreeAdapter )adapter;
    return Boolean.valueOf( treeAdapter.hasHScrollBar() );
  }

  private static Boolean hasVScrollBar( final Tree tree ) {
    Object adapter = tree.getAdapter( ITreeAdapter.class );
    ITreeAdapter treeAdapter = ( ITreeAdapter )adapter;
    return Boolean.valueOf( treeAdapter.hasVScrollBar() );
  }
}
