/*******************************************************************************
 * Copyright (c) 2002, 2009 Innoopract Informationssysteme GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Innoopract Informationssysteme GmbH - initial API and implementation
 ******************************************************************************/
package org.eclipse.swt.internal.widgets.spinnerkit;

import java.io.IOException;

import org.eclipse.rwt.internal.lifecycle.JSConst;
import org.eclipse.rwt.lifecycle.*;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.*;
import org.eclipse.swt.widgets.Spinner;
import org.eclipse.swt.widgets.Widget;


public final class SpinnerLCA extends AbstractWidgetLCA {

  private static final String QX_TYPE = "org.eclipse.swt.widgets.Spinner";
//  private static final String TYPE_POOL_ID = SpinnerLCA.class.getName();
  
  private static final String PROP_SELECTION = "selection";
  static final String PROP_MAXIMUM = "maximum";
  static final String PROP_MINIMUM = "minimum";
  static final String PROP_INCREMENT = "increment";
  static final String PROP_PAGE_INCREMENT = "pageIncrement";
  static final String PROP_MODIFY_LISTENER = "modifyListener";
  static final String PROP_SELECTION_LISTENER = "selectionListener";

  public void preserveValues( final Widget widget ) {
    Spinner spinner = ( Spinner )widget;
    ControlLCAUtil.preserveValues( spinner );
    IWidgetAdapter adapter = WidgetUtil.getAdapter( widget );
    adapter.preserve( PROP_SELECTION, new Integer( spinner.getSelection() ) );
    adapter.preserve( PROP_MINIMUM, new Integer( spinner.getMinimum() ) );
    adapter.preserve( PROP_MAXIMUM, new Integer( spinner.getMaximum() ) );
    adapter.preserve( PROP_INCREMENT, new Integer( spinner.getIncrement() ) );
    adapter.preserve( PROP_PAGE_INCREMENT,
                      new Integer( spinner.getPageIncrement() ) );
    adapter.preserve( PROP_MODIFY_LISTENER,
                      Boolean.valueOf( ModifyEvent.hasListener( spinner ) ) );
    adapter.preserve( PROP_SELECTION_LISTENER,
                      Boolean.valueOf( SelectionEvent.hasListener( spinner ) ) );
    WidgetLCAUtil.preserveCustomVariant( spinner );
  }

  /* (intentionally non-JavaDoc'ed)
   * readData does not explicitly handle modifyEvents. They are fired implicitly
   * by updating the selection property.
   */
  public void readData( final Widget widget ) {
    Spinner spinner = ( Spinner )widget;
    String value = WidgetLCAUtil.readPropertyValue( widget, "selection" );
    if( value != null ) {
      spinner.setSelection( Integer.parseInt( value ) );
    }
    ControlLCAUtil.processSelection( widget, null, false );
    ControlLCAUtil.processMouseEvents( spinner );
    ControlLCAUtil.processKeyEvents( spinner );
  }

  public void renderInitialization( final Widget widget ) throws IOException {
    Spinner spinner = ( Spinner )widget;
    JSWriter writer = JSWriter.getWriterFor( spinner );
    writer.newWidget( QX_TYPE );    
    ControlLCAUtil.writeStyleFlags( spinner );
    writeReadOnly( spinner );
    writeWrap( spinner );
  }

  public void renderChanges( final Widget widget ) throws IOException {
    Spinner spinner = ( Spinner )widget;
    ControlLCAUtil.writeChanges( spinner );
    writeValues( spinner );
    writeModifyListener( spinner );
    writeSelectionListener( spinner );
    WidgetLCAUtil.writeCustomVariant( spinner );
  }

  public void renderDispose( final Widget widget ) throws IOException {
    JSWriter writer = JSWriter.getWriterFor( widget );
    writer.dispose();
  }

  public void createResetHandlerCalls( final String typePoolId )
    throws IOException
  {
    ControlLCAUtil.resetStyleFlags();
    resetReadOnly();
    resetValues();
  }

  public String getTypePoolId( final Widget widget ) {
    // see https://bugs.eclipse.org/bugs/show_bug.cgi?id=226651
//  return TYPE_POOL_ID;
    return null;
  }

  //////////////////////////////////////
  // Helping methods to write JavaScript

  private static void writeValues( final Spinner spinner ) throws IOException {
    JSWriter writer = JSWriter.getWriterFor( spinner );
    writeSetInt( writer, PROP_MINIMUM, "min", spinner.getMinimum(), 0 );
    writeSetInt( writer, PROP_MAXIMUM, "max", spinner.getMaximum(), 100 );
    writeSetInt( writer,
                 PROP_INCREMENT,
                 "incrementAmount",
                 spinner.getIncrement(),
                 1 );
    writeSetInt( writer,
                 PROP_PAGE_INCREMENT,
                 "pageIncrementAmount",
                 spinner.getPageIncrement(),
                 10 );
    writeSetInt( writer, PROP_SELECTION, "value", spinner.getSelection(), 0 );
  }

  private static void resetValues() throws IOException {
    JSWriter writer = JSWriter.getWriterForResetHandler();
//    TODO [rst] Missing resetters in QX_TYPE, discuss with qooxdoo
//    writer.reset( "min" );
//    writer.reset( "max" );
    writer.reset( "incrementAmount" );
    writer.reset( "pageIncrementAmount" );
    writer.reset( "value" );
  }

  private static void writeSetInt( final JSWriter writer,
                                   final String javaProperty,
                                   final String jsProperty,
                                   final int newValue,
                                   final int defValue )
    throws IOException
  {
    writer.set( javaProperty,
                jsProperty,
                new Integer( newValue ),
                new Integer( defValue ) );
  }

  private static void writeReadOnly( final Spinner spinner ) throws IOException 
  {
    boolean readOnly = ( spinner.getStyle() & SWT.READ_ONLY ) != 0;
    JSWriter writer = JSWriter.getWriterFor( spinner );
    writer.set( JSConst.QX_FIELD_EDITABLE, !readOnly );
  }

  private static void resetReadOnly() throws IOException {
    JSWriter writer = JSWriter.getWriterForResetHandler();
    writer.reset( JSConst.QX_FIELD_EDITABLE );
  }

  private static void writeWrap( final Spinner spinner ) throws IOException {
    if( ( spinner.getStyle() & SWT.WRAP ) != 0 ) {
      JSWriter writer = JSWriter.getWriterFor( spinner );
      writer.set( "wrap", true );
    }
  }

  private static void writeModifyListener( final Spinner spinner )
    throws IOException
  {
    JSWriter writer = JSWriter.getWriterFor( spinner );
    String prop = PROP_MODIFY_LISTENER;
    Boolean newValue = Boolean.valueOf( ModifyEvent.hasListener( spinner ) );
    Boolean defValue = Boolean.FALSE;
    writer.set( prop, "hasModifyListener", newValue, defValue );
  }

  private static void writeSelectionListener( final Spinner spinner )
    throws IOException
  {
    JSWriter writer = JSWriter.getWriterFor( spinner );
    String prop = PROP_SELECTION_LISTENER;
    Boolean newValue = Boolean.valueOf( SelectionEvent.hasListener( spinner ) );
    Boolean defValue = Boolean.FALSE;
    writer.set( prop, "hasSelectionListener", newValue, defValue );
  }
}
