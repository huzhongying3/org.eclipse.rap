/*******************************************************************************
 * Copyright (c) 2002, 2007 Innoopract Informationssysteme GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Innoopract Informationssysteme GmbH - initial API and implementation
 ******************************************************************************/
package org.eclipse.rwt.internal.browser;



/** <p>the basic implementation for Safari browsers.</p>
*/
public class Safari2up extends Safari {

  public Safari2up( final boolean scriptEnabled ) {
    super( scriptEnabled );
  }

  public Safari2up( final boolean scriptEnabled, final boolean ajaxEnabled ) {
    super( scriptEnabled, ajaxEnabled );
  }

  public Safari2up( final Browser browser ) {
    super( browser );
  }
}
