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
package org.eclipse.swt.internal.widgets.datetimekit;

import java.io.IOException;
import java.text.MessageFormat;

import org.eclipse.rwt.lifecycle.AbstractWidgetLCA;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.*;

public class DateTimeLCA extends AbstractWidgetLCA {

  private static final AbstractDateTimeLCADelegate DATE_LCA = new DateTimeDateLCA();
  private static final AbstractDateTimeLCADelegate TIME_LCA = new DateTimeTimeLCA();
  private static final AbstractDateTimeLCADelegate CALENDAR_LCA = new DateTimeCalendarLCA();

  public void preserveValues( final Widget widget ) {
    getDelegate( widget ).preserveValues( ( DateTime )widget );
  }

  public void readData( final Widget widget ) {
    getDelegate( widget ).readData( ( DateTime )widget );
  }

  public void renderInitialization( final Widget widget ) throws IOException {
    getDelegate( widget ).renderInitialization( ( DateTime )widget );
  }

  public void renderChanges( final Widget widget ) throws IOException {
    getDelegate( widget ).renderChanges( ( DateTime )widget );
  }

  public void renderDispose( final Widget widget ) throws IOException {
    getDelegate( widget ).renderDispose( ( DateTime )widget );
  }

  public void createResetHandlerCalls( final String typePoolId )
    throws IOException
  {
    getDelegate( typePoolId ).createResetHandlerCalls( typePoolId );
  }

  public String getTypePoolId( final Widget widget ) {
    // TODO [rh] disabled pooling, see bugs prefixed with [pooling]
    // https://bugs.eclipse.org/bugs/show_bug.cgi?id=204107
    // https://bugs.eclipse.org/bugs/show_bug.cgi?id=199142
    // return getDelegate( widget ).getTypePoolId( ( DateTime )widget );
    return null;
  }

  private static AbstractDateTimeLCADelegate getDelegate( final String tpId ) {
    AbstractDateTimeLCADelegate result;
    if( tpId.startsWith( DateTimeDateLCA.TYPE_POOL_ID ) ) {
      result = DATE_LCA;
    } else if( tpId.startsWith( DateTimeTimeLCA.TYPE_POOL_ID ) ) {
      result = TIME_LCA;
    } else if( tpId.startsWith( DateTimeCalendarLCA.TYPE_POOL_ID ) ) {
      result = CALENDAR_LCA;
    } else {
      String txt = "The typePoolId ''{0}'' is not supported.";
      String msg = MessageFormat.format( txt, new Object[]{
        tpId
      } );
      throw new IllegalArgumentException( msg );
    }
    return result;
  }

  private static AbstractDateTimeLCADelegate getDelegate( final Widget widget )
  {
    AbstractDateTimeLCADelegate result;
    if( ( widget.getStyle() & SWT.DATE ) != 0 ) {
      result = DATE_LCA;
    } else if( ( widget.getStyle() & SWT.TIME ) != 0 ) {
      result = TIME_LCA;
    } else {
      result = CALENDAR_LCA;
    }
    return result;
  }
}
