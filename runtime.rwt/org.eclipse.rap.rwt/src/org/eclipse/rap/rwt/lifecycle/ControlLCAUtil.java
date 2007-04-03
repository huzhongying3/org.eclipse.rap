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

package org.eclipse.rap.rwt.lifecycle;

import java.io.IOException;
import org.eclipse.rap.rwt.RWT;
import org.eclipse.rap.rwt.custom.SashForm;
import org.eclipse.rap.rwt.events.*;
import org.eclipse.rap.rwt.graphics.*;
import org.eclipse.rap.rwt.internal.widgets.Props;
import org.eclipse.rap.rwt.widgets.*;

/**
 * Utility class that provides methods needed by LCAs for various Controls.
 * TODO [rh] JavaDoc
 * <p></p>
 */
public class ControlLCAUtil {

  public static final int MAX_STATIC_ZORDER = 300;
  
  private static final JSListenerInfo FOCUS_GAINED_LISTENER_INFO 
    = new JSListenerInfo( "focusin", 
                          "org.eclipse.rap.rwt.EventUtil.focusGained", 
                          JSListenerType.ACTION );
  private static final JSListenerInfo FOCUS_LOST_LISTENER_INFO 
    = new JSListenerInfo( "focusout", 
                          "org.eclipse.rap.rwt.EventUtil.focusLost", 
                          JSListenerType.ACTION );
  
  // Property names to preserve widget property values 
  private static final String PROP_BACKGROUND = "background";
  private static final String PROP_FOREGROUND = "foreground";
  private static final String PROP_ACTIVATE_LISTENER = "activateListener";
  private static final String PROP_FOCUS_LISTENER = "focusListener";
  private static final String PROP_TAB_INDEX = "tabIndex";

  private ControlLCAUtil() {
    // prevent instance creation
  }
  
  public static void preserveValues( final Control control ) {
    IWidgetAdapter adapter = WidgetUtil.getAdapter( control );
    adapter.preserve( Props.BOUNDS, control.getBounds() );
    // TODO [rh] revise this (see also writeZIndex)
    if( !( control instanceof Shell ) ) {
      adapter.preserve( Props.Z_INDEX, new Integer( getZIndex( control ) ) );
    }
    adapter.preserve( PROP_TAB_INDEX, new Integer( getTabIndex( control ) ) );
    WidgetLCAUtil.preserveToolTipText( control, control.getToolTipText() );
    adapter.preserve( Props.MENU, control.getMenu() );
    adapter.preserve( Props.VISIBLE, Boolean.valueOf( control.getVisible() ) );
    WidgetLCAUtil.preserveEnabled( control, control.isEnabled() );
    adapter.preserve( PROP_FOREGROUND, control.getForeground() );
    adapter.preserve( PROP_BACKGROUND, control.getBackground() );
    WidgetLCAUtil.preserveFont( control, control.getFont() );
    adapter.preserve( Props.CONTROL_LISTENERS, 
                      Boolean.valueOf( ControlEvent.hasListener( control ) ) );
    adapter.preserve( PROP_ACTIVATE_LISTENER, 
                      Boolean.valueOf( ActivateEvent.hasListener( control ) ) );
    if( ( control.getStyle() & RWT.NO_FOCUS ) == 0 ) {
      adapter.preserve( PROP_FOCUS_LISTENER, 
                        Boolean.valueOf( FocusEvent.hasListener( control ) ) );
    }
  }
  
  public static void readBounds( final Control control ) {
    Rectangle current = control.getBounds();
    Rectangle newBounds = WidgetLCAUtil.readBounds( control, current );
    control.setBounds( newBounds );
  }
  
  public static void writeBounds( final Control control ) throws IOException {
    Composite parent = control.getParent();
    WidgetLCAUtil.writeBounds( control, parent, control.getBounds(), false );
  }
  
  public static void writeZIndex( final Control control ) throws IOException {
    // TODO [rst] remove if statement as soon as z-order on shells is
    //      implemented completely
    if( !( control instanceof Shell ) ) {
      JSWriter writer = JSWriter.getWriterFor( control );
      Integer newValue = new Integer( getZIndex( control ) );
      writer.set( Props.Z_INDEX, JSConst.QX_FIELD_Z_INDEX, newValue, null );
    }
  }
  
  // TODO [rh] there seems to be a qooxdoo problem when trying to change the
  //      visibility of a newly created widget (no flushGlobalQueues was called)
  //      MSG: Modification of property "visibility" failed with exception: 
  //           Error - Element must be created previously!
  public static void writeVisible( final Control control ) 
    throws IOException
  {
    // we only need getVisible here (not isVisible), as qooxdoo also hides/shows
    // contained controls
    Boolean newValue = Boolean.valueOf( control.getVisible() );
    Boolean defValue = control instanceof Shell ? Boolean.FALSE : Boolean.TRUE;
    JSWriter writer = JSWriter.getWriterFor( control );
    writer.set( Props.VISIBLE, JSConst.QX_FIELD_VISIBLE, newValue, defValue );
  }

  public static void writeEnabled( final Control control )
    throws IOException
  {
    WidgetLCAUtil.writeEnabled( control, control.isEnabled() );
  }

  public static void writeChanges( final Control control ) throws IOException {
    writeBounds( control );
    writeZIndex( control );
    writeTabIndex( control );
    writeVisible( control );
    writeEnabled( control );
    writeForeground( control );
    writeBackground( control );
    writeFont( control );
    writeToolTip( control );
    writeMenu( control );
    writeActivateListener( control );
    writeFocusListener( control );
  }
  
  public static void writeResizeNotificator( final Widget widget )
    throws IOException
  {
    JSWriter writer = JSWriter.getWriterFor( widget );
    writer.addListener( JSConst.QX_EVENT_CHANGE_WIDTH,
                        JSConst.JS_WIDGET_RESIZED );
    writer.addListener( JSConst.QX_EVENT_CHANGE_HEIGHT,
                        JSConst.JS_WIDGET_RESIZED );
  }
  
  public static void writeMoveNotificator( final Widget widget )
    throws IOException
  {
    JSWriter writer = JSWriter.getWriterFor( widget );
    writer.addListener( JSConst.QX_EVENT_CHANGE_LOCATION_X,
                        JSConst.JS_WIDGET_MOVED );
    writer.addListener( JSConst.QX_EVENT_CHANGE_LOCATION_Y,
                        JSConst.JS_WIDGET_MOVED );
  }
  
  public static void writeMenu( final Control control ) throws IOException {
    WidgetLCAUtil.writeMenu( control, control.getMenu() );
  }
  
  public static void writeToolTip( final Control control ) 
    throws IOException 
  {
    WidgetLCAUtil.writeToolTip( control, control.getToolTipText() );
  }
  
  // TODO [rh] move this to WidgetLCAUtil, move test case along, change LCA's
  //      to use this instead of manually setting images
  public static void writeImage( final Widget widget, final Image image ) 
    throws IOException 
  {
    if( WidgetLCAUtil.hasChanged( widget, Props.IMAGE, image, null ) ) {
      JSWriter writer = JSWriter.getWriterFor( widget );
      // work around qooxdoo, that interprets 'null' as an image path 
      String path = image == null ? "" : Image.getPath( image );
      writer.set( JSConst.QX_FIELD_ICON, path );
    }
  }

  public static void writeForeground( final Control control ) throws IOException 
  {
    JSWriter writer = JSWriter.getWriterFor( control );
    Color color = control.getForeground();
    if( WidgetLCAUtil.hasChanged( control, PROP_FOREGROUND, color, null ) ) {
      Object[] args = new Object[] { control, color };
      writer.call( JSWriter.WIDGET_MANAGER_REF, "setForeground", args );
    }
  }

  public static void writeBackground( final Control control ) throws IOException 
  {
    JSWriter writer = JSWriter.getWriterFor( control );
    Color newValue = control.getBackground();
    writer.set( PROP_BACKGROUND, JSConst.QX_FIELD_BG_COLOR, newValue, null );
  }
  
  /**
   * Writes RWT style flags that must be handled on the client side (e.g.
   * <code>RWT.BORDER</code>). Flags are transmitted as qooxdoo <q>states</q>
   * that will be respected by the appearance that renders the widget.
   * 
   * @param widget
   * @throws IOException
   */
  public static void writeStyleFlags( final Widget widget ) throws IOException {
    JSWriter writer = JSWriter.getWriterFor( widget );
    if( ( widget.getStyle() & RWT.BORDER ) != 0 ) {
      writer.call( "addState", new Object[]{ "rwt_BORDER" } );
    }
    if( ( widget.getStyle() & RWT.FLAT ) != 0 ) {
      writer.call( "addState", new Object[]{ "rwt_FLAT" } );
    }
  }

  public static void writeFont( final Control control ) throws IOException {
    WidgetLCAUtil.writeFont( control, control.getFont() );
  }
  
  public static void writeActivateListener( final Control control ) 
    throws IOException
  {
    Boolean newValue = Boolean.valueOf( ActivateEvent.hasListener( control ) );
    Boolean defValue = Boolean.FALSE;
    String prop = PROP_ACTIVATE_LISTENER;
    if( WidgetLCAUtil.hasChanged( control, prop, newValue, defValue ) ) {
      String function = newValue.booleanValue()
                      ? "addActivateListenerWidget"
                      : "removeActivateListenerWidget";
      JSWriter writer = JSWriter.getWriterFor( control );
      Object[] args = new Object[] { control };
      writer.call( control.getShell(), function, args );
    }
  }
  
  /**
   * Note that there is no corresponding readData metod to fire the focus events
   * that are send by the JavaScript event listeners that are registered below.
   * FocusEvents are thrown when the focus is changed programmatically and when
   * it is change by the user.
   * Therefore the methods in Display that maintain the current focusControl
   * also fire FocusEvents. The current client-side focusControl is read in
   * DisplayLCA#readData.
   */
  private static void writeFocusListener( final Control control ) 
    throws IOException 
  {
    if( ( control.getStyle() & RWT.NO_FOCUS ) == 0 ) {
      JSWriter writer = JSWriter.getWriterFor( control );
      boolean hasListener = FocusEvent.hasListener( control );
      writer.updateListener( FOCUS_GAINED_LISTENER_INFO, 
                             PROP_FOCUS_LISTENER, 
                             hasListener );
      writer.updateListener( FOCUS_LOST_LISTENER_INFO, 
                             PROP_FOCUS_LISTENER, 
                             hasListener );
    }
  }

  //////////
  // Z-INDEX
  
  /**
   * Determines the z-index to render for a given control.
   */
  public static int getZIndex( final Control control ) {
    Object adapter = control.getAdapter( IControlAdapter.class );
    IControlAdapter controlAdapter = ( IControlAdapter )adapter;
    int max = MAX_STATIC_ZORDER;
    if( control.getParent() != null ) {
      max = Math.max( control.getParent().getChildrenCount(), max );
    }
    return max - controlAdapter.getZIndex();
  }
  
  ////////////
  // TAB INDEX

  public static void writeTabIndex( final Control control ) throws IOException {
    if( control instanceof Shell ) {
      computeTabIndices( ( Shell )control );
    }
    JSWriter writer = JSWriter.getWriterFor( control );
    Object newValue = new Integer( getTabIndex( control ) );
    // there is no reliable default value for all controls
    writer.set( PROP_TAB_INDEX, "tabIndex", newValue );
  }
  
  /**
   * Invokes computation of tab indices for all controls within a shell.
   */
  private static void computeTabIndices( final Shell comp ) {
    computeTabIndices( comp, 0 );
  }

  /**
   * Recursively computes the tab indices for all child controls of a given
   * composite and stores the resulting values in the control adapters.
   */
  private static int computeTabIndices( final Composite comp, final int index )
  {
    Control[] children = comp.getChildren();
    for( int i = 0; i < children.length; i++ ) {
      Control control = children[ i ];
      Object adapter = control.getAdapter( IControlAdapter.class );
      IControlAdapter controlAdapter = ( IControlAdapter )adapter;
      controlAdapter.setTabIndex( -1 );
    }
    Control[] tabList = comp.getTabList();
    int nextIndex = index;
    for( int i = 0; i < tabList.length; i++ ) {
      Control control = tabList[ i ];
      Object adapter = control.getAdapter( IControlAdapter.class );
      IControlAdapter controlAdapter = ( IControlAdapter )adapter;
      controlAdapter.setTabIndex( nextIndex );
      // for Links, leave a range out to be assigned to hrefs on the client
      if( control instanceof Link ) {
        nextIndex += 300;
      } else {
        nextIndex += 1;
      }
      if( control instanceof Composite ) {
        nextIndex = computeTabIndices( ( Composite )control, nextIndex );
      }
    }
    return nextIndex;
  }

  /**
   * Determines the tab index to write for a given control.
   */
  private static int getTabIndex( final Control control ) {
    int result = -1;
    if( takesFocus( control ) ) {
      Object adapter = control.getAdapter( IControlAdapter.class );
      IControlAdapter controlAdapter = ( IControlAdapter )adapter;
      result = controlAdapter.getTabIndex();
    }
    return result;
  }

  // TODO [rst] Refactor: should this method be part of Control?
  private static boolean takesFocus( final Control control ) {
    boolean result = true;
    result &= ( control.getStyle() & RWT.NO_FOCUS ) == 0;
    result &= control.getClass() != Composite.class;
    result &= control.getClass() != ToolBar.class;
    result &= control.getClass() != SashForm.class;
    return result;
  }
  
  /////////////////////
  // SELECTION LISTENER

  public static void processSelection( final Widget widget, 
                                       final Item item, 
                                       final boolean readBounds )
  {
    String eventId = JSConst.EVENT_WIDGET_SELECTED;
    if( WidgetLCAUtil.wasEventSent( widget, eventId ) ) {
      SelectionEvent event;
      event = createSelectionEvent( widget,
                                    item,
                                    readBounds,
                                    SelectionEvent.WIDGET_SELECTED );
      event.processEvent();
    }
    eventId = JSConst.EVENT_WIDGET_DEFAULT_SELECTED;
    if( WidgetLCAUtil.wasEventSent( widget, eventId ) ) {
      SelectionEvent event;
      event = createSelectionEvent( widget,
                                    item,
                                    readBounds,
                                    SelectionEvent.WIDGET_DEFAULT_SELECTED );
      event.processEvent();
    }
  }
  
  private static SelectionEvent createSelectionEvent( final Widget widget,
                                                      final Item item,
                                                      final boolean readBounds,
                                                      final int type )
  {
    Rectangle bounds;
    if( widget instanceof Control && readBounds ) {
      Control control = ( Control )widget;
      bounds = WidgetLCAUtil.readBounds( control, control.getBounds() ); 
    } else {
      bounds = new Rectangle( 0, 0, 0, 0 );
    }
    return new SelectionEvent( widget, item, type, bounds, null, true, RWT.NONE );
  }
}
