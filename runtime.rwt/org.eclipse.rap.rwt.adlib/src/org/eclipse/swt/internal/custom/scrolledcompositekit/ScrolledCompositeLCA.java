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

package org.eclipse.swt.internal.custom.scrolledcompositekit;

import java.io.IOException;

import org.eclipse.rwt.internal.lifecycle.JSConst;
import org.eclipse.rwt.lifecycle.*;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.internal.events.EventLCAUtil;
import org.eclipse.swt.internal.widgets.Props;
import org.eclipse.swt.widgets.*;


public final class ScrolledCompositeLCA extends AbstractWidgetLCA {

  private static final String QX_TYPE
    = "org.eclipse.swt.custom.ScrolledComposite";

  private static final Integer ZERO = new Integer( 0 );

  // Request parameter names
  private static final String PARAM_H_BAR_SELECTION
    = "horizontalBar.selection";
  private static final String PARAM_V_BAR_SELECTION
    = "verticalBar.selection";

  // Property names for preserve value mechanism
  static final String PROP_BOUNDS = "clientArea";
  static final String PROP_OVERFLOW = "overflow";
  private static final String PROP_H_BAR_SELECTION = "hBarSelection";
  private static final String PROP_V_BAR_SELECTION = "vBarSelection";
  private static final String PROP_SHOW_FOCUSED_CONTROL = "showFocusedControl";


  public void preserveValues( final Widget widget ) {
    ScrolledComposite composite = ( ScrolledComposite )widget;
    ControlLCAUtil.preserveValues( composite );
    IWidgetAdapter adapter = WidgetUtil.getAdapter( composite );
    adapter.preserve( PROP_BOUNDS, composite.getBounds() );
    adapter.preserve( PROP_OVERFLOW, getOverflow( composite ) );
    adapter.preserve( PROP_H_BAR_SELECTION,
                      getBarSelection( composite.getHorizontalBar() ) );
    adapter.preserve( PROP_V_BAR_SELECTION,
                      getBarSelection( composite.getVerticalBar() ) );
    adapter.preserve( Props.SELECTION_LISTENERS,
                      Boolean.valueOf( hasSelectionListener( composite ) ) );
    adapter.preserve( PROP_SHOW_FOCUSED_CONTROL,
                      Boolean.valueOf( composite.getShowFocusedControl() ) );
    WidgetLCAUtil.preserveCustomVariant( composite );
  }

  public void readData( final Widget widget ) {
    ScrolledComposite composite = ( ScrolledComposite )widget;
    Point origin = composite.getOrigin();
    String value
      = WidgetLCAUtil.readPropertyValue( widget, PARAM_H_BAR_SELECTION );
    ScrollBar hScroll = composite.getHorizontalBar();
    if( value != null && hScroll != null ) {
      origin.x = Integer.parseInt( value );
      processSelection( hScroll );
    }
    value = WidgetLCAUtil.readPropertyValue( widget, PARAM_V_BAR_SELECTION );
    ScrollBar vScroll = composite.getVerticalBar();
    if( value != null && vScroll != null ) {
      origin.y = Integer.parseInt( value );
      processSelection( vScroll );
    }
    composite.setOrigin( origin );
    ControlLCAUtil.processMouseEvents( composite );
    ControlLCAUtil.processKeyEvents( composite );
    ControlLCAUtil.processMenuDetect( composite );
    WidgetLCAUtil.processHelp( composite );
  }

  public void renderInitialization( final Widget widget ) throws IOException {
    ScrolledComposite scrolledComposite = ( ScrolledComposite )widget;
    JSWriter writer = JSWriter.getWriterFor( scrolledComposite );
    writer.newWidget( QX_TYPE );
    ControlLCAUtil.writeStyleFlags( scrolledComposite );
  }

  public void renderChanges( final Widget widget ) throws IOException {
    ScrolledComposite composite = ( ScrolledComposite )widget;
    ControlLCAUtil.writeChanges( composite );
    writeClipBounds( composite );
    // TODO [rh] initial positioning of the client-side scroll bar does not work
    writeBarSelection( composite );
    // [if] Order is important: writeScrollBars after writeBarSelection
    writeScrollBars( composite );
    writeSelectionListener( composite );
    writeShowFocusedControl( composite );
    WidgetLCAUtil.writeCustomVariant( composite );
  }

  public void renderDispose( final Widget widget ) throws IOException {
    ScrolledComposite composite = ( ScrolledComposite )widget;
    JSWriter writer = JSWriter.getWriterFor( composite );
    writer.dispose();
  }

  ///////////////////////////////////
  // Helping methods to write changes

  private static void writeScrollBars( final ScrolledComposite composite )
    throws IOException
  {
    String overflow = getOverflow( composite );
    JSWriter writer = JSWriter.getWriterFor( composite );
    writer.set( PROP_OVERFLOW, "overflow", overflow, null );
  }

  private static void writeBarSelection( final ScrolledComposite composite )
    throws IOException
  {
    JSWriter writer = JSWriter.getWriterFor( composite );
    Integer hBarSelection = getBarSelection( composite.getHorizontalBar() );
    if( hBarSelection != null ) {
      writer.set( PROP_H_BAR_SELECTION, "hBarSelection", hBarSelection, ZERO );
    }
    Integer vBarSelection = getBarSelection( composite.getVerticalBar() );
    if( vBarSelection != null ) {
      writer.set( PROP_V_BAR_SELECTION, "vBarSelection", vBarSelection, ZERO );
    }
  }

  private static void writeClipBounds( final ScrolledComposite composite )
    throws IOException
  {
    Rectangle bounds = composite.getBounds();
    if( WidgetLCAUtil.hasChanged( composite, PROP_BOUNDS, bounds, null ) ) {
      JSWriter writer = JSWriter.getWriterFor( composite );
      writer.set( "clipWidth", bounds.width );
      writer.set( "clipHeight", bounds.height );
    }
  }

  private static void writeSelectionListener( final ScrolledComposite composite )
    throws IOException
  {
    boolean hasListener = hasSelectionListener( composite );
    Boolean newValue = Boolean.valueOf( hasListener );
    String prop = Props.SELECTION_LISTENERS;
    if( WidgetLCAUtil.hasChanged( composite, prop, newValue, Boolean.FALSE ) ) {
      JSWriter writer = JSWriter.getWriterFor( composite );
      writer.set( "hasSelectionListener", newValue );
    }
  }

  private static void writeShowFocusedControl( final ScrolledComposite composite )
    throws IOException
  {
    Boolean newValue = Boolean.valueOf( composite.getShowFocusedControl() );
    String prop = PROP_SHOW_FOCUSED_CONTROL;
    if( WidgetLCAUtil.hasChanged( composite, prop, newValue, Boolean.FALSE ) ) {
      JSWriter writer = JSWriter.getWriterFor( composite );
      writer.set( "showFocusedControl", newValue );
    }
  }

  private static String getOverflow( final ScrolledComposite composite ) {
    String result;
    ScrollBar horizontalBar = composite.getHorizontalBar();
    boolean scrollX = horizontalBar != null && horizontalBar.getVisible();
    ScrollBar verticalBar = composite.getVerticalBar();
    boolean scrollY = verticalBar != null && verticalBar.getVisible();
    if( scrollX && scrollY ) {
      result = "scroll";
    } else if( scrollX ) {
      result = "scrollX";
    } else if( scrollY ) {
      result = "scrollY";
    } else {
      result = "hidden";
    }
    return result;
  }

  //////////////////////////////////////////////////
  // Helping methods to obtain scroll bar properties

  private static Integer getBarSelection( final ScrollBar scrollBar ) {
    Integer result;
    if( scrollBar != null ) {
      result = new Integer( scrollBar.getSelection() );
    } else {
      result = null;
    }
    return result;
  }

  private static boolean hasSelectionListener( final ScrolledComposite composite )
  {
    boolean result = false;
    ScrollBar horizontalBar = composite.getHorizontalBar();
    if( horizontalBar != null ) {
      result = result || SelectionEvent.hasListener( horizontalBar );
    }
    ScrollBar verticalBar = composite.getVerticalBar();
    if( verticalBar != null ) {
      result = result || SelectionEvent.hasListener( verticalBar );
    }
    return result;
  }

  private static void processSelection( final ScrollBar scrollBar ) {
    SelectionEvent evt
      = new SelectionEvent( scrollBar, null, SelectionEvent.WIDGET_SELECTED );
    evt.stateMask
      = EventLCAUtil.readStateMask( JSConst.EVENT_WIDGET_SELECTED_MODIFIER );
    evt.processEvent();
  }
}
