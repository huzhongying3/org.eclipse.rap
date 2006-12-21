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

import java.text.MessageFormat;
import org.eclipse.rap.rwt.internal.lifecycle.IDisplayLifeCycleAdapter;
import org.eclipse.rap.rwt.internal.widgets.IWidgetAdapter;
import org.eclipse.rap.rwt.widgets.Display;


public class DisplayUtil {
  
  private DisplayUtil() {
    // prevent instance creation
  }

  public static IDisplayLifeCycleAdapter getLCA( final Display display ) {
    Class clazz = ILifeCycleAdapter.class;
    IDisplayLifeCycleAdapter result;
    result = ( IDisplayLifeCycleAdapter )display.getAdapter( clazz );
    if( result == null ) {
      throwAdapterException( clazz );
    }
    return result;
  }

  public static IWidgetAdapter getAdapter( final Display display ) {
    Class clazz = IWidgetAdapter.class;
    IWidgetAdapter result;
    result = ( IWidgetAdapter )display.getAdapter( clazz );
    if( result == null ) {
      throwAdapterException( clazz );
    }
    return result;
  }
  
  public static String getId( final Display display ) {
    return getAdapter( display ).getId();
  }
  
  private static void throwAdapterException( final Class clazz ) {
    String text =   "Could not retrieve an instance of ''{0}''. Probably the "
                  + "AdapterFactory was not properly registered.";
    Object[] param = new Object[]{ clazz.getName() };
    String msg = MessageFormat.format( text, param );
    throw new IllegalStateException( msg );
  }
}
