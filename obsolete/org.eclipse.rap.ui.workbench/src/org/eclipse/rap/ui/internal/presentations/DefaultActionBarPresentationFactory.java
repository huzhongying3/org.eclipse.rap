/*******************************************************************************
 * Copyright (c) 2000, 2006 IBM Corporation and others. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html Contributors:
 * IBM Corporation - initial API and implementation
 ******************************************************************************/
package org.eclipse.rap.ui.internal.presentations;

import org.eclipse.rap.jface.action.IToolBarManager;
import org.eclipse.rap.jface.internal.provisional.action.*;
import org.eclipse.rap.rwt.RWT;
import org.eclipse.rap.ui.internal.provisional.presentations.IActionBarPresentationFactory;

/**
 * The intention of this class is to allow for replacing the implementation of
 * the cool bar and tool bars in the workbench.
 * <p>
 * <strong>EXPERIMENTAL</strong>. This class or interface has been added as
 * part of a work in progress. There is a guarantee neither that this API will
 * work nor that it will remain the same. Please do not use this API without
 * consulting with the Platform/UI team.
 * </p>
 * 
 * @since 3.2
 */
public class DefaultActionBarPresentationFactory
  implements IActionBarPresentationFactory
{

  /*
   * (non-Javadoc)
   * 
   * @see org.eclipse.ui.internal.presentations.IActionBarPresentationFactory#createCoolBarManager()
   */
  public ICoolBarManager2 createCoolBarManager() {
    return new CoolBarManager2( RWT.FLAT );
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.eclipse.ui.internal.presentations.IActionBarPresentationFactory#createToolBarManager()
   */
  public IToolBarManager2 createToolBarManager() {
    return new ToolBarManager2( RWT.FLAT | RWT.RIGHT );
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.eclipse.ui.internal.presentations.IActionBarPresentationFactory#createViewToolBarManager()
   */
  public IToolBarManager2 createViewToolBarManager() {
    return new ToolBarManager2( RWT.FLAT | RWT.RIGHT | RWT.WRAP );
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.eclipse.ui.internal.presentations.IActionBarPresentationFactory#createToolBarContributionItem(org.eclipse.jface.action.IToolBarManager,
   *      java.lang.String)
   */
  public IToolBarContributionItem createToolBarContributionItem( IToolBarManager toolBarManager,
                                                                 String id )
  {
    return new ToolBarContributionItem2( toolBarManager, id );
  }
}
