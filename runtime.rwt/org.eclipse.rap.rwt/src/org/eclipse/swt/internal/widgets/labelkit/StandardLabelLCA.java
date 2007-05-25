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

package org.eclipse.swt.internal.widgets.labelkit;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.internal.widgets.Props;
import org.eclipse.swt.lifecycle.*;
import org.eclipse.swt.widgets.Label;

public class StandardLabelLCA extends AbstractLabelLCADelegate {

  private static final Pattern LINE_BREAK_PATTERN = Pattern.compile( "\n" );

  private static final String PROP_TEXT = "text";
  private static final String PROP_ALIGNMENT = "alignment";
  private static final String PROP_IMAGE = "image";

  private static final Integer DEFAULT_ALIGNMENT = new Integer( SWT.LEFT );

  void preserveValues( final Label label ) {
    ControlLCAUtil.preserveValues( label );
    IWidgetAdapter adapter = WidgetUtil.getAdapter( label );
    adapter.preserve( PROP_TEXT, label.getText() );
    adapter.preserve( PROP_IMAGE, label.getImage() );
    adapter.preserve( PROP_ALIGNMENT, new Integer( label.getAlignment() ) );
  }

  void readData( final Label label ) {
  }

  void renderInitialization( final Label label ) throws IOException {
    JSWriter writer = JSWriter.getWriterFor( label );
    writer.newWidget( "qx.ui.basic.Atom" );
    ControlLCAUtil.writeStyleFlags( label );
    Boolean wrap = Boolean.valueOf( ( label.getStyle() & SWT.WRAP ) != 0 );
    Object[] args = { label, wrap };
    writer.callStatic( "org.eclipse.swt.LabelUtil.initialize", args  );
  }
  
  void renderChanges( final Label label ) throws IOException {
    ControlLCAUtil.writeChanges( label );
    writeText( label );
    writeImage( label );
    writeAlignment( label );
  }

  void renderDispose( final Label label ) throws IOException {
    JSWriter writer = JSWriter.getWriterFor( label );
    writer.dispose();
  }

  //////////////////////////////////////
  // Helping methods to write JavaScript

  private static void writeText( final Label label ) throws IOException {
    if( WidgetLCAUtil.hasChanged( label, PROP_TEXT, label.getText(), "" ) ) {
      // Order is important here: escapeText, replace line breaks
      String text = WidgetLCAUtil.escapeText( label.getText(), true );
      Matcher matcher = LINE_BREAK_PATTERN.matcher( text );
      text = matcher.replaceAll( "<br/>" );
      JSWriter writer = JSWriter.getWriterFor( label );
      Object[] args = new Object[]{ label, text };
      writer.callStatic( "org.eclipse.swt.LabelUtil.setText", args );
    }
  }

  private static void writeImage( final Label label ) throws IOException {
    Image image = label.getImage();
    if( WidgetLCAUtil.hasChanged( label, Props.IMAGE, image, null ) )
    {
      String imagePath;
      if( image == null ) {
        imagePath = null;
      } else {
        // TODO passing image bounds to qooxdoo can speed up rendering
        imagePath = Image.getPath( image );
      }
      JSWriter writer = JSWriter.getWriterFor( label );
      Object[] args = new Object[]{ label, imagePath };
      writer.callStatic( "org.eclipse.swt.LabelUtil.setImage", args );
    }
  }

  private static void writeAlignment( final Label label ) throws IOException {
    Integer alignment = new Integer( label.getAlignment() );
    Integer defValue = DEFAULT_ALIGNMENT;
    if( WidgetLCAUtil.hasChanged( label, PROP_ALIGNMENT, alignment, defValue ) ) 
    {
      JSWriter writer = JSWriter.getWriterFor( label );
      Object[] args = new Object[]{
        label, getAlignment( label.getAlignment() )
      };
      writer.callStatic( "org.eclipse.swt.LabelUtil.setAlignment", args );
    }
  }

  private static String getAlignment( final int alignment ) {
    String result;
    if( ( alignment & SWT.LEFT ) != 0 ) {
      result = "left";
    } else if( ( alignment & SWT.CENTER ) != 0 ) {
      result = "center";
    } else if( ( alignment & SWT.RIGHT ) != 0 ) {
      result = "right";
    } else {
      result = "left";
    }
    return result;
  }
}
