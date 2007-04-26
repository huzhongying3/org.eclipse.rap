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

package org.eclipse.swt.events;

import junit.framework.TestCase;
import org.eclipse.swt.RWTFixture;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import com.w4t.engine.lifecycle.PhaseId;


public class ShellEvent_Test extends TestCase {
  
  private static final String SHELL_CLOSED = "shellClosed|";
  
  private String log = "";
  
  protected void setUp() throws Exception {
    RWTFixture.setUp();
    RWTFixture.fakePhase( PhaseId.PROCESS_ACTION );
  }
  
  protected void tearDown() throws Exception {
    RWTFixture.tearDown();
  }
  
  public void testAddRemoveClosedListener() {
    ShellListener listener = new ShellAdapter() {
      public void shellClosed( final ShellEvent event ) {
        log += SHELL_CLOSED;
      }
    };
    Display display = new Display();
    Shell shell = new Shell( display , SWT.NONE );
    shell.addShellListener( listener );
    
    shell.close();
    assertEquals( SHELL_CLOSED, log );
    
    log = "";
    shell = new Shell( display , SWT.NONE );
    shell.addShellListener( listener );
    shell.removeShellListener( listener );
    shell.close();
    assertEquals( "", log );
  }
}
