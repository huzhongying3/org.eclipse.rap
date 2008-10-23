/*******************************************************************************
 * Copyright (c) 2008 Innoopract Informationssysteme GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Innoopract Informationssysteme GmbH - initial API and implementation
 ******************************************************************************/
package org.eclipse.rwt.internal.theme;

import java.io.IOException;
import java.io.InputStream;

import org.eclipse.rwt.internal.theme.css.*;
import org.w3c.css.sac.CSSException;


/**
 * Provides static utility methods for theming tests.
 */
public final class ThemeTestUtil {

  private ThemeTestUtil() {
    // prevent instantiation
  }

  public static ResourceLoader createResourceLoader( final Class clazz ) {
    final ClassLoader classLoader = clazz.getClassLoader();
    ResourceLoader resLoader = new ResourceLoader() {
      public InputStream getResourceAsStream( final String resourceName )
        throws IOException
      {
        return classLoader.getResourceAsStream( resourceName );
      }
    };
    return resLoader;
  }

  public static StyleSheet getStyleSheet( final String fileName )
    throws CSSException, IOException
  {
    StyleSheet result = null;
    ClassLoader classLoader = ThemeTestUtil.class.getClassLoader();
    InputStream inStream = classLoader.getResourceAsStream( "resources/theme/"
                                                            + fileName );
    if( inStream != null ) {
      try {
        CssFileReader reader = new CssFileReader();
        result = reader.parse( inStream, fileName, null );
      } finally {
        inStream.close();
      }
    }
    return result;
  }
}
