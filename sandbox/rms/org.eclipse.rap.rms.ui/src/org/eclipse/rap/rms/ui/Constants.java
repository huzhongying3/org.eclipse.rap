// Created on 11.09.2007
package org.eclipse.rap.rms.ui;

import org.eclipse.rap.rms.ui.internal.Activator;
import org.eclipse.rap.rms.ui.internal.editors.EntityEditor;
import org.eclipse.rap.rms.ui.internal.startup.IntroPerspective;
import org.eclipse.rap.rms.ui.internal.views.Navigator;


public class Constants {
  
  public static final String NAVIGATOR_ID = Navigator.class.getName();
  public static final String PLUGIN_ID
    = Activator.class.getPackage().getName();
  public static final String PERSPECTIVE_ID 
    = IntroPerspective.class.getName();
  public static final String ENTITY_EDITOR_ID = EntityEditor.class.getName();
  public static final String PRE_SELECTION
    = Navigator.class.getName() + ".PreSelection"; //$NON-NLS-1$
  
  private Constants() {
    // prevent instance creation
  }
}
