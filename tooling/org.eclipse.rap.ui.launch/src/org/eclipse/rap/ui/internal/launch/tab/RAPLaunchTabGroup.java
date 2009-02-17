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
package org.eclipse.rap.ui.internal.launch.tab;

import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.debug.ui.ILaunchConfigurationDialog;
import org.eclipse.debug.ui.ILaunchConfigurationTab;
import org.eclipse.pde.ui.launcher.OSGiLauncherTabGroup;


// TODO [rh] Could be replace with org.eclipse.debug.ui.launchConfigurationTabs
//      extension point introduced in 3.3
public final class RAPLaunchTabGroup extends OSGiLauncherTabGroup
{

  public void createTabs( final ILaunchConfigurationDialog dialog, 
                          final String mode ) 
  {
    super.createTabs( dialog, mode );
    // Prepend existing tabs from OSGi launch with 'Main' tab
    setTabs( insertTab( getTabs(), 0, new MainTab() ) );
  }
  
  public void setDefaults( final ILaunchConfigurationWorkingCopy config ) 
  {
    super.setDefaults( config );
  }
  
  private static ILaunchConfigurationTab[] insertTab( 
    final ILaunchConfigurationTab[] tabs, 
    final int position, 
    final ILaunchConfigurationTab newTab )
  {
    ILaunchConfigurationTab[] result 
      = new ILaunchConfigurationTab[ tabs.length + 1 ];
    int offset = 0;
    for( int i = 0; i < result.length; i++ ) {
      if( i == position ) {
        result[ i ] = newTab;
        offset = -1;
      } else {
        result[ i ] = tabs[ i + offset ];
      }
    }
    return result;
  }
}
