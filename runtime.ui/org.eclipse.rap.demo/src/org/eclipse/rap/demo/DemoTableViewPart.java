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

package org.eclipse.rap.demo;

import java.util.ArrayList;
import java.util.List;
import org.eclipse.rap.jface.viewers.*;
import org.eclipse.rap.rwt.RWT;
import org.eclipse.rap.rwt.graphics.Image;
import org.eclipse.rap.rwt.layout.*;
import org.eclipse.rap.rwt.widgets.*;
import org.eclipse.rap.ui.part.ViewPart;


public class DemoTableViewPart extends ViewPart {

  private static final int ROWS = 40;
  private static final int COLUMNS = 10;

  private class ViewContentProvider implements IStructuredContentProvider {
    public Object[] getElements( Object inputElement ) {
      List buffer = new ArrayList();
      for( int i = 0; i < ROWS; i++ ) {
        String[] row = new String[ COLUMNS ];
        for( int j = 0; j < COLUMNS; j++ ) {
          row[ j ] = "Item" + i + "-" + j ;
        }
        buffer.add( row );
      }
      Object[] result = new Object[ buffer.size() ];
      buffer.toArray( result );
      return result;
    }
    public void dispose() {
    }
    public void inputChanged( Viewer viewer, Object oldInput, Object newInput ) {
    }
  }
  
  private class ViewLabelProvider 
    extends LabelProvider
    implements ITableLabelProvider
  {
    public Image getColumnImage( Object element, int columnIndex ) {
      return null;
    }
    public String getColumnText( Object element, int columnIndex ) {
      String[] row = ( String[] )element;
      String result = row[ columnIndex ];
      return result;
    }
  }
  
  public void createPartControl( final Composite parent ) {
    parent.setLayout( new FormLayout() );
    TableViewer viewer = new TableViewer( parent, RWT.NONE );
    viewer.setContentProvider( new ViewContentProvider() );
    viewer.setLabelProvider( new ViewLabelProvider() );
    final Table table = viewer.getTable();
    viewer.setColumnProperties( initColumnProperties( table ) );
    viewer.setInput( this );
    FormData formData = new FormData();
    table.setLayoutData( formData );
    formData.top = new FormAttachment( 0, 5 );
    formData.left = new FormAttachment( 0, 5 );
    formData.right = new FormAttachment( 100, -5 );
    formData.bottom = new FormAttachment( 100, -5 );
    getSite().setSelectionProvider( viewer );
  }

  private String[] initColumnProperties( final Table table ) {
    String[] result = new String[ COLUMNS ];
    for( int i = 0; i < COLUMNS; i++ ) {
      TableColumn tableColumn = new TableColumn( table, RWT.NONE );
      result[ i ] = "Column" + i ;
      tableColumn.setText( result[ i ] );
      if( i == 2 ) {
        tableColumn.setWidth( 190 );
      } else {
        tableColumn.setWidth( 70 );
      }
    }
    return result;
  }
}
