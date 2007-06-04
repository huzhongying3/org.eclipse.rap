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

package org.eclipse.swt.internal.theme;

import java.io.InputStream;
import java.util.*;

import junit.framework.TestCase;

import org.eclipse.swt.internal.theme.ThemeDefinitionReader.ThemeDef;
import org.eclipse.swt.internal.theme.ThemeDefinitionReader.ThemeDefHandler;
import org.eclipse.swt.widgets.Widget;

public class ThemeDefinitionReader_Test extends TestCase {

  private static final String WIDGET_THEME_FILE
    = "org/eclipse/swt/internal/widgets/widgetkit/Widget.theme.xml";

  public void testRead() throws Exception {
    ClassLoader loader = Widget.class.getClassLoader();
    InputStream is = loader.getResourceAsStream( WIDGET_THEME_FILE );
    ThemeDefinitionReader reader = new ThemeDefinitionReader( is );
    final Map result = new HashMap();
    try {
      reader.read( new ThemeDefHandler() {
        public void readThemeDef( final ThemeDef def ) {
          result.put( def.name, def.value );
        }
      } );
    } finally {
      is.close();
    }
    Set keys = result.keySet();
    assertTrue( keys.size() > 0 );
    assertTrue( result.get( "widget.background" ) instanceof QxColor );
  }
}
