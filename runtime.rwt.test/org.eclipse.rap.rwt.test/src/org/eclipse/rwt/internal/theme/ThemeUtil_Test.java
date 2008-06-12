/*******************************************************************************
 * Copyright (c) 2007, 2008 Innoopract Informationssysteme GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Innoopract Informationssysteme GmbH - initial API and implementation
 ******************************************************************************/

package org.eclipse.rwt.internal.theme;

import junit.framework.TestCase;

import org.eclipse.swt.RWTFixture;


public class ThemeUtil_Test extends TestCase {

  public void testSetCurrentThemeId() throws Exception {
    ThemeManager manager = ThemeManager.getInstance();
    manager.initialize();
    String validThemeId = "test.valid.theme";
    String themeName = "Valid Test Theme";
    String themeFile = "resources/theme/theme-valid.properties";
    ThemeManager_Test.loadThemeFile( manager, validThemeId, themeName, themeFile );
    ThemeUtil.setCurrentThemeId( validThemeId );
    assertEquals( validThemeId, ThemeUtil.getCurrentThemeId() );
    try {
      ThemeUtil.setCurrentThemeId( "woo.doo.schick.schnack" );
      fail( "should throw IAE for invalid theme ids" );
    } catch( IllegalArgumentException e ) {
      // expected
    }
  }

  protected void setUp() throws Exception {
    RWTFixture.setUp();
    RWTFixture.fakeNewRequest();
  }

  protected void tearDown() throws Exception {
    ThemeManager.getInstance().reset();
    RWTFixture.tearDown();
  }
}
