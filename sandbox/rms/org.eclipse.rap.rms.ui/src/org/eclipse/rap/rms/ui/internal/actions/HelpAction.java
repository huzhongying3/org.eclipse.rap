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

package org.eclipse.rap.rms.ui.internal.actions;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.rap.rms.ui.internal.RMSMessages;
import org.eclipse.rap.rms.ui.internal.dialogs.HelpDialog;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.plugin.AbstractUIPlugin;

public class HelpAction extends Action {
  
  private final Shell shell;
  
  public HelpAction( final Shell shell ) {
    this.shell = shell;
    setId( HelpAction.class.getName() );
    setText( RMSMessages.get().HelpAction_HelpContent );
    ImageDescriptor image 
      = AbstractUIPlugin.imageDescriptorFromPlugin( "org.eclipse.rap.rms.ui",  //$NON-NLS-1$
                                                    "icons/help.gif" ); //$NON-NLS-1$
    setImageDescriptor( image );
  }
  
  public void run() {
    HelpDialog helpDlg = new HelpDialog( shell );
    helpDlg.open();
  }
}
