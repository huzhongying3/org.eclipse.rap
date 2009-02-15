/*******************************************************************************
 * Copyright (c) 2008, 2009 Innoopract Informationssysteme GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Innoopract Informationssysteme GmbH - initial API and implementation
 ******************************************************************************/
package org.eclipse.rwt.internal.theme.css;

import junit.framework.TestCase;

import org.eclipse.rwt.internal.theme.*;
import org.eclipse.rwt.internal.theme.WidgetMatcher.Constraint;
import org.eclipse.rwt.lifecycle.WidgetUtil;
import org.eclipse.swt.RWTFixture;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.*;


public class WidgetMatcher_Test extends TestCase {

  public void testWidgetMatcher() {
    // Create matcher for a certain widget instance
    WidgetMatcher matcher = new WidgetMatcher();
    matcher.addStyle( "BORDER", SWT.BORDER );
    matcher.addStyle( "PUSH", SWT.PUSH );
    matcher.addState( "enabled", WidgetMatcher.CONTROL_ENABLED );
    matcher.addState( "disabled", new Constraint() {

      public boolean matches( final Widget widget ) {
        return !( ( Control )widget ).isEnabled();
      }
    } );

    // Get set of conditional results
    ConditionalValue value1 = new ConditionalValue(
      new String[] { "[BORDER", "[PUSH", ":enabled" },
      QxBorder.create( 2, "solid", "red" ) );
    ConditionalValue value2 = new ConditionalValue(
      new String[] { "[BORDER", "[PUSH" },
      QxBorder.create( 2, "dotted", "blue" ) );
    ConditionalValue value3 = new ConditionalValue(
      new String[] { ".special" },
      QxBorder.create( 1, "solid", "green" ) );
    ConditionalValue[] values
      = new ConditionalValue[] { value1, value2, value3 };

    // Test matcher with example widgets
    Display display = new Display();
    Shell shell = new Shell( display );
    Widget button1 = new Button( shell, SWT.PUSH );

    QxType result = matcher.select( values, button1 );
    assertNull( result );

    Button button2 = new Button( shell, SWT.PUSH | SWT.BORDER );
    result = matcher.select( values, button2 );
    assertNotNull( result );
    assertEquals( value1.value, result );

    button2.setEnabled( false );
    button2.setData( WidgetUtil.CUSTOM_VARIANT, "special" );
    result = matcher.select( values, button2 );
    assertNotNull( result );
    assertEquals( value2.value, result );

    Button button3 = new Button( shell, SWT.PUSH );
    button3.setData( WidgetUtil.CUSTOM_VARIANT, "special" );
    result = matcher.select( values, button3 );
    assertNotNull( result );
    assertEquals( value3.value, result );

    button3.setData( WidgetUtil.CUSTOM_VARIANT, "other" );
    result = matcher.select( values, button3 );
    assertNull( result );
  }

  protected void setUp() throws Exception {
    RWTFixture.setUp();
  }

  protected void tearDown() throws Exception {
    RWTFixture.tearDown();
  }
}
