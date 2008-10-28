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

package org.eclipse.swt.widgets;

import java.io.IOException;

import junit.framework.TestCase;

import org.eclipse.rwt.Fixture;
import org.eclipse.rwt.graphics.Graphics;
import org.eclipse.rwt.internal.lifecycle.*;
import org.eclipse.rwt.internal.service.ContextProvider;
import org.eclipse.rwt.internal.service.ServiceContext;
import org.eclipse.rwt.lifecycle.*;
import org.eclipse.swt.RWTFixture;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.*;
import org.eclipse.swt.layout.FillLayout;

public class Display_Test extends TestCase {

  public static final class EnsureIdEntryPoint implements IEntryPoint {
    public int createUI() {
      Display display = new Display();
      Shell shell = new Shell( display );
      WidgetUtil.getId( shell );
      return 0;
    }
  }
  
  public void testSingleDisplayPerSession() {
    Device display = new Display();
    assertEquals( Display.getCurrent(), display );
    try {
      new Display();
      fail( "Only one display allowed per session" );
    } catch( IllegalStateException e ) {
      // expected
    }
  }

  public void testGetThread() throws InterruptedException {
    Display first = new Display();
    assertSame( Thread.currentThread(), first.getThread() );
    first.dispose();

    final ServiceContext context = ContextProvider.getContext();
    final Display[] display = { null };
    Runnable runnable = new Runnable() {
      public void run() {
        ContextProvider.setContext( context );
        Fixture.fakeResponseWriter();
        display[ 0 ] = new Display();
      }
    };
    Thread thread = new Thread( runnable );
    thread.start();
    thread.join();
    assertSame( thread, display[ 0 ].getThread() );
    
    
//    final Display[] display = { new Display() };
//    final Thread[] thread = new Thread[ 1 ];
//    final ServiceContext context[] = { ContextProvider.getContext() };
//    final RWTLifeCycle lifeCycle = new RWTLifeCycle();
//    lifeCycle.addPhaseListener( new PhaseListener() {
//      private static final long serialVersionUID = 1L;
//      public void afterPhase( PhaseEvent event ) {
//      }
//      public void beforePhase( PhaseEvent event ) {
//        thread[ 0 ] = display[ 0 ].getThread();
//      }
//      public PhaseId getPhaseId() {
//        return PhaseId.PREPARE_UI_ROOT;
//      }
//    } );
//    
//    Runnable runnable = new Runnable() {
//      public void run() {
//        ContextProvider.setContext( context[ 0 ] );
//        Fixture.fakeResponseWriter();
//        String id = "org.eclipse.swt.display";
//        ContextProvider.getSession().setAttribute( id, display[ 0 ] );
//        String displayId = DisplayUtil.getAdapter( display[ 0 ] ).getId();
//        Fixture.fakeRequestParam( RequestParams.UIROOT, displayId );
//        try {
//          lifeCycle.execute();
//        } catch( IOException e ) {
//          // TODO Auto-generated catch block
//          e.printStackTrace();
//          fail();
//        }
//      }
//    };
//    Thread thread1 = new Thread( runnable );
//    thread1.start();
//    thread1.join();
//    assertSame( thread1, thread[ 0 ] );
//    
//    Thread thread2 = new Thread( runnable );
//    thread2.start();
//    thread2.join();
//    assertSame( thread2, thread[ 0 ] );
//    RWTFixture.tearDown();
//    assertNull( display[ 0 ].getThread() );
//    RWTFixture.setUp();
  }
  
  public void testGetShells() {
    Display display = new Display();
    assertEquals( 0, display.getShells().length );
    Composite shell1 = new Shell( display , SWT.NONE );
    assertSame( shell1, display.getShells()[ 0 ] );
    Composite shell2 = new Shell( display , SWT.NONE );
    Composite[] shells = display.getShells();
    assertTrue( shell2 == shells[ 0 ] || shell2 == display.getShells()[ 1 ] );
  }
  
  public void testProperties() {
    Display display = new Display();
    assertEquals( 0, display.getShells().length );
    Rectangle bounds = display.getBounds();
    assertNotNull( bounds );
    bounds.x += 1;
    assertTrue( bounds.x != display.getBounds().x );
  }

  
  public void testMap() {
    Display display = new Display();
    Shell shell = new Shell( display, SWT.NONE );
    Rectangle shellBounds = new Rectangle( 10, 10, 400, 400 );
    shell.setBounds( shellBounds );
    
    Rectangle actual = display.map( shell, shell, 1, 2, 3, 4 );
    Rectangle expected = new Rectangle( 1, 2, 3, 4 );
    assertEquals( expected, actual );
    
    actual = display.map( shell, null, 5, 6, 7, 8 );
    expected = new Rectangle( shellBounds.x + 5,
                              shellBounds.y + 6,
                              7,
                              8 );
    assertEquals( expected, actual );
    
    shell.setLayout( new FillLayout() );
    TabFolder folder = new TabFolder( shell, SWT.NONE );
    shell.layout();
    actual = display.map( folder, shell, 6, 7, 8, 9 );
    expected = new Rectangle( folder.getBounds().x + 6,
                              folder.getBounds().y + 7,
                              8,
                              9 );
    assertEquals( expected, actual );
    
    actual = display.map( null, folder, 1, 2, 3, 4 );
    expected = new Rectangle( 1 - shell.getBounds().x - folder.getBounds().x,
                              2 - shell.getBounds().y - folder.getBounds().y,
                              3,
                              4 );
    assertEquals( expected, actual );
  }
  
  public void testMapWithChildShell() {
    Display display = new Display();
    Shell shell = new Shell( display, SWT.NONE );
    shell.setBounds( 100, 100, 800, 600 );
    Shell childShell1 = new Shell( shell, SWT.NONE );
    childShell1.setBounds( 200, 200, 800, 600 );
    Point expected = new Point( 200, 200 );
    Point actual = display.map( childShell1, null, 0, 0 );
    assertEquals( expected, actual );
    expected = new Point( 100, 100 );
    actual = display.map( childShell1, shell, 0, 0 );
    assertEquals( expected, actual );
    
    Shell childShell2 = new Shell( shell, SWT.NONE );
    childShell2.setBounds( 200, 200, 800, 600 );
    expected = new Point( 14, 17 );
    actual = display.map( childShell1, childShell2, 14, 17 );
    assertEquals( expected, actual );
  }
  
  public void testActiveShell() {
    // TODO [rh] This test needs to be reworked when Shell.open() is implemented
    //      since it assumes opened shells.
    Display display = new Display();
    assertNull( display.getActiveShell() );
    Shell shell1 = new Shell( display, SWT.NONE );
    assertNull( display.getActiveShell() );
    shell1.open();
    assertSame( shell1, display.getActiveShell() );
    Shell shell2 = new Shell( display, SWT.NONE );
    shell2.open();
    assertSame( shell2, display.getActiveShell() );
    shell2.dispose();
    assertSame( shell1, display.getActiveShell() );
    
    // Test disposing of inactive shell
    Shell inactiveShell = new Shell( display, SWT.NONE );
    Shell activeShell = new Shell( display, SWT.NONE );
    inactiveShell.open();
    activeShell.open();
    assertSame( activeShell, display.getActiveShell() );
    inactiveShell.dispose();
    assertSame( activeShell, display.getActiveShell() );
    
    // Test explicitly setting the active shell
    Shell shell3 = new Shell( display, SWT.NONE );
    Shell shell4 = new Shell( display, SWT.NONE );
    shell3.open();
    shell4.open();
    assertSame( shell4, display.getActiveShell() );
    shell3.setActive();
    assertSame( shell3, display.getActiveShell() );
  }
  
  public void testSystemFont() {
    Device display = new Display();
    Font systemFont = display.getSystemFont();
    assertNotNull( systemFont );
  }
  
  public void testSystemColor() {
    Display display = new Display();
    Color systemColor = display.getSystemColor( SWT.COLOR_WIDGET_BACKGROUND );
    assertNotNull( systemColor );
    Color systemRed = display.getSystemColor( SWT.COLOR_RED );
    assertEquals( 255, systemRed.getRed() );
    assertEquals( 0, systemRed.getGreen() );
    assertEquals( 0, systemRed.getBlue() );
    Color red = Graphics.getColor( 255, 0, 0 );
    assertEquals( red, systemRed );
    assertSame( red, systemRed );
  }
  
  public void testAddAndRemoveFilter() {
    RWTFixture.fakePhase( PhaseId.PROCESS_ACTION );
    final int CLOSE_CALLBACK = 0;
    final int DISPOSE_CALLBACK = 1;
    final boolean[] callbackReceived = new boolean[]{ false, false };
    Listener listener = new Listener() {
      public void handleEvent( Event e ) {
        if( e.type == SWT.Close ) {
          callbackReceived[ CLOSE_CALLBACK ] = true;
        } else if( e.type == SWT.Dispose ) {
          callbackReceived[ DISPOSE_CALLBACK ] = true;
        }
      }
    };
    
    // addFilter
    Display display = new Display();
    try {
      display.addFilter( SWT.Dispose, null );
      fail( "No exception thrown for addFilter with null argument" );
    } catch( IllegalArgumentException e ) {
      // expected
    }
    display.addFilter( SWT.Close, listener );
    Shell shell = new Shell( display );
    shell.close();
    assertTrue( callbackReceived[ CLOSE_CALLBACK ] );
    assertFalse( callbackReceived[ DISPOSE_CALLBACK ] );
  
    // removeFilter
    callbackReceived[ CLOSE_CALLBACK ] = false;
    callbackReceived[ DISPOSE_CALLBACK ] = false;
    try {
      display.removeFilter( SWT.Dispose, null );
      fail( "No exception thrown for removeFilter with null argument" );
    } catch( IllegalArgumentException e ) {
      // expected
    }
    display.removeFilter( SWT.Close, listener );
    shell = new Shell( display );
    shell.close();
    assertFalse( callbackReceived[ CLOSE_CALLBACK ] );
    assertFalse( callbackReceived[ DISPOSE_CALLBACK ] );
    
    // remove filter for an event that was not added before -> do nothing
    display.removeFilter( SWT.FocusIn, listener );
  }

  public void testEnsureIdIsW1() throws IOException {
    Class entryPointClass = EnsureIdEntryPoint.class;
    EntryPointManager.register( EntryPointManager.DEFAULT, entryPointClass );
    RWTFixture.fakeNewRequest();
    RWTLifeCycle lifeCycle = ( RWTLifeCycle )LifeCycleFactory.getLifeCycle();
    lifeCycle.execute();
    assertEquals( "w1", DisplayUtil.getId( Display.getCurrent() ) );
    EntryPointManager.deregister( EntryPointManager.DEFAULT );
  }
  
  protected void setUp() throws Exception {
    RWTFixture.setUp();
  }

  protected void tearDown() throws Exception {
    RWTFixture.tearDown();
  }
}
