/*******************************************************************************
 * Copyright (c) 2007, 2009 Innoopract Informationssysteme GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Innoopract Informationssysteme GmbH - initial API and implementation
 *     EclipseSource - ongoing development
 ******************************************************************************/
package org.eclipse.rwt.internal.theme;

import junit.framework.TestCase;

import org.eclipse.rwt.Fixture;
import org.eclipse.rwt.theme.IControlThemeAdapter;
import org.eclipse.swt.internal.widgets.buttonkit.ButtonThemeAdapter;
import org.eclipse.swt.internal.widgets.shellkit.ShellThemeAdapter;
import org.eclipse.swt.widgets.*;


public class ThemeManager_Test extends TestCase {

  private static final ResourceLoader LOADER
    = ThemeTestUtil.createResourceLoader( ThemeManager_Test.class );

  public void testReset() {
    ThemeManager manager = ThemeManager.getInstance();
    manager.initialize();
    String id = manager.getDefaultThemeId();
    assertEquals( "org.eclipse.swt.theme.Default", id );
    Theme theme = manager.getTheme( id );
    assertNotNull( theme );
    assertEquals( "RAP Default Theme", theme.getName() );
    manager.reset();
    try {
      manager.hasTheme( "foo" );
      fail( "Theme manager de-initialized, should throw IllegalStateException" );
    } catch( IllegalStateException e ) {
      // expected
    }
  }

  public void testRegisterResources() {
    ThemeManager manager = ThemeManager.getInstance();
    manager.initialize();
    manager.registerResources();
    String[] themeIds = manager.getRegisteredThemeIds();
    assertNotNull( themeIds );
    assertTrue( themeIds.length > 0 );
  }

  public void testRegisterThemeNull() throws Exception {
    ThemeManager themeManager = ThemeManager.getInstance();
    themeManager.initialize();
    try {
      themeManager.registerTheme( null, "foo", null, null );
      fail( "Null id must throw NullPointerException" );
    } catch( NullPointerException e ) {
      // expected
    }
    try {
      themeManager.registerTheme( "", "foo", null, null );
      fail( "Empty id must throw IlleaglArgumentException" );
    } catch( IllegalArgumentException e ) {
      // expected
    }
  }

  public void testRegisterPropertyFile() throws Exception {
    ThemeManager manager = ThemeManager.getInstance();
    manager.initialize();
    String themeId = "test.valid.theme";
    String themeName = "Valid Test Theme";
    String themeFile = "resources/theme/theme-valid.properties";
    try {
      manager.registerTheme( themeId, themeName, themeFile, LOADER );
    } catch( ThemeManagerException e ) {
      // expected
      String expectedMessage = "Failed parsing CSS file";
      assertTrue( e.getMessage().indexOf( expectedMessage ) != -1 );
    }
  }

  public void testRegisterCssThemeFile() throws Exception {
    ThemeManager manager = ThemeManager.getInstance();
    manager.initialize();
    String themeId = "TestExample";
    String themeName = "Test Example Theme";
    String themeFile = "resources/theme/TestExample.css";
    manager.registerTheme( themeId, themeName, themeFile, LOADER );
    String[] themeIds = manager.getRegisteredThemeIds();
    assertNotNull( themeIds );
    assertEquals( 2, themeIds.length );
    Theme theme = manager.getTheme( themeId );
    assertNotNull( theme );
    assertEquals( themeName, theme.getName() );
  }

  public void testGetThemeableWidget() {
    ThemeManager manager = ThemeManager.getInstance();
    manager.initialize();
    ThemeableWidget themeableWidget = manager.getThemeableWidget( Button.class );
    assertNotNull( themeableWidget );
    assertNotNull( themeableWidget.loader );
    assertEquals( Button.class, themeableWidget.widget );
    assertNotNull( themeableWidget.elements );
    assertTrue( themeableWidget.elements.length > 0 );
  }

  public void testValuesMap() throws Exception {
    ThemeManager manager = ThemeManager.getInstance();
    manager.initialize();
    Theme defTheme = manager.getTheme( manager.getDefaultThemeId() );
    ThemeCssValuesMap valuesMap = defTheme.getValuesMap();
    assertNotNull( valuesMap );

    String themeId = "custom";
    String themeFile = "resources/theme/TestExample.css";
    manager.registerTheme( themeId, "Custom Theme", themeFile, LOADER );
    Theme customTheme = manager.getTheme( themeId );
    ThemeCssValuesMap customValuesMap = customTheme.getValuesMap();
    assertNotNull( customValuesMap );
  }

  protected void setUp() throws Exception {
    Fixture.setUp();
    Fixture.fakeNewRequest();
  }

  protected void tearDown() throws Exception {
    ThemeManager.getInstance().reset();
    Fixture.tearDown();
  }
}
