// Created on 30.09.2007
package org.eclipse.rap.rms.ui.internal.datamodel;

import java.util.Calendar;
import java.util.Date;

import org.eclipse.core.databinding.conversion.IConverter;
import org.eclipse.jface.viewers.IColorProvider;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.rap.custom.viewers.AutoColoProvider;
import org.eclipse.rap.custom.viewers.GanttViewer;
import org.eclipse.rap.custom.viewers.IGanttItemLabelProvider;
import org.eclipse.rap.custom.viewers.IGanttLabelProvider;
import org.eclipse.rap.rms.ui.internal.Activator;
import org.eclipse.rap.rms.ui.internal.RMSMessages;
import org.eclipse.rap.rms.ui.internal.datamodel.PageUtil.Container;
import org.eclipse.rap.rwt.custom.Gantt;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.editor.FormEditor;
import org.eclipse.ui.forms.editor.FormPage;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.ScrolledForm;


public class ProjectGanttPage extends FormPage {
  private static final String GANTT = "Gantt";
  private final ProjectCopy project;
  private Text txtDate;
  private Composite body;
  private Composite timeSelection;
  private GanttViewer viewer;
  
  private final class TaskLabelProvider 
    extends LabelProvider 
    implements IGanttLabelProvider, 
      IGanttItemLabelProvider, 
      IColorProvider
  {
    private final AutoColoProvider colorProvider;
    private final Calendar selection;
    
    
    public TaskLabelProvider( final Display display, final Date date ) {
      Calendar calendar = Activator.createCalendar();
      calendar.setTime( date );
      this.selection = calendar;
      colorProvider = new AutoColoProvider();
      colorProvider.addSystemColors( display );
    }
    
    public Color getBackground( final Object element ) {
      return colorProvider.getBackground( element );
    }
    
    public Color getForeground( final Object element ) {
      return null;
    }

    public String getText( final Object element ) {
      TaskCopy task = ( TaskCopy )element;
      return task.getName();
    }
    
    public int getStart( final Object element ) {
      TaskCopy task = ( TaskCopy )element;
      int result = selection.getActualMaximum( Calendar.DAY_OF_MONTH );
      Date startDate = task.getStartDate();
      if( startDate != null ) {
        Calendar calendar = Activator.createCalendar();
        calendar.setTime( startDate );
        int selectedMonth = selection.get( Calendar.MONTH );
        int month = calendar.get( Calendar.MONTH );
        if( month == selectedMonth ) {
          result = calendar.get( Calendar.DAY_OF_MONTH );
        } else if( month < selectedMonth ){
          result = 0;
        }
      }
      return result;
    }

    public int getLength( final Object element ) {
      TaskCopy task = ( TaskCopy )element;
      int result = 0;
      Date endDate = task.getEndDate();
      if( endDate != null ) {
        Calendar calendar = Activator.createCalendar();
        calendar.setTime( endDate );
        int selectedMonth = selection.get( Calendar.MONTH );
        int month = calendar.get( Calendar.MONTH );
        int start = getStart( element );
        if( month == selectedMonth ) {
          int end = calendar.get( Calendar.DAY_OF_MONTH );
          if( end - start > 0 ) {
            result = end - start + 1;
          }
        } else if( month > selectedMonth ) {
          int maxLenght = selection.getActualMaximum( Calendar.DAY_OF_MONTH );
          result = maxLenght - start;
        }
      }
      return result;
    }

    public String[] getLegendText( final Object rootElement ) {
      int daysPerMonth = selection.getActualMaximum( Calendar.DAY_OF_MONTH );
      String[] result = new String[ daysPerMonth ];
      for( int i = 0; i < daysPerMonth; i++ ) {
        result[ i ] = String.valueOf( i + 1 );
      }
      return result;            
    }

    public int getMark( final Object rootElement ) {
      return selection.get( Calendar.DAY_OF_MONTH );
    }
  }

  private final class TaskContentProvider
    implements IStructuredContentProvider
  {
    public Object[] getElements( final Object inputElement ) {
      return project.getTasks().toArray();
    }

    public void inputChanged( final Viewer viewer, 
                              final Object oldInput, 
                              final Object newInput )
    {
    }

    public void dispose() {
    }
  }

  public ProjectGanttPage( final FormEditor editor,
                           final ProjectCopy project )
  {
    super( editor, GANTT, RMSMessages.get().ProjectGanttPage_GanttPage );
    this.project = project;
  }


  public void init( final IEditorSite site, final IEditorInput input ) {
    super.init( site, input );
    setTitleToolTip( RMSMessages.get().ProjectGanttPage_ToolTip );
  }
  
  protected void createFormContent( final IManagedForm managedForm ) {
    ScrolledForm scrolledForm = managedForm.getForm();
    FormToolkit toolkit = managedForm.getToolkit();
    body = PageUtil.createBody( scrolledForm, Activator.IMG_FORM_HEAD_GANTT );
    body.setLayout( new FormLayout() );
    
    timeSelection = new Composite( body, SWT.NONE );
    FormData fdTimeSelection = new FormData();
    timeSelection.setLayoutData( fdTimeSelection );
    fdTimeSelection.top = new FormAttachment( 0, 0 );
    fdTimeSelection.left = new FormAttachment( 0, 0 );
    fdTimeSelection.right = new FormAttachment( 100, 0 );

    timeSelection.setLayout( new GridLayout( 3, false ) );
    IConverter dateToString = PageUtil.DATE_TO_STRING_CONVERTER;
    String date = ( String )dateToString.convert( new Date() );
    Container container = new Container( toolkit, timeSelection );
    txtDate = PageUtil.createDatePicker( container, RMSMessages.get().ProjectGanttPage_ChangeDate, date );
    IConverter stringToDate = PageUtil.STRING_TO_DATE_CONVERTER;
    Date now = ( Date )stringToDate.convert( date );
    viewer = createViewer( body, timeSelection, now );
    txtDate.addModifyListener( new ModifyListener() {
      public void modifyText( final ModifyEvent event ) {
        createViewer();
      }
    } );
  }
  
  @Override
  public void setActive( final boolean active ) {
    super.setActive( active );
    if( isActive() ) {
      createViewer();
    }
  }

  private GanttViewer createViewer( final Composite body,
                                    final Composite timeSelection,
                                    final Date selection )
  {
    GanttViewer result = new GanttViewer( body );
    Gantt gantt = ( Gantt )result.getControl();
    gantt.setBackground( body.getBackground() );
    FormData fdGantt = new FormData();
    gantt.setLayoutData( fdGantt );
    fdGantt.top = new FormAttachment( timeSelection, 5 );
    fdGantt.left = new FormAttachment( 0, 0 );
    fdGantt.right = new FormAttachment( 100, 0 );
    fdGantt.bottom = new FormAttachment( 100, 0 );
    
    result.setContentProvider( new TaskContentProvider() );
    final Display display = body.getDisplay();
    result.setLabelProvider( new TaskLabelProvider( display, selection ) );
    result.setInput( project );
    return result;
  }

  private void createViewer() {
    viewer.getControl().dispose();
    viewer = null;
    IConverter stringToDate = PageUtil.STRING_TO_DATE_CONVERTER;
    Date selected = ( Date )stringToDate.convert( txtDate.getText() );
    viewer = createViewer( body, timeSelection, selected );
    body.layout();
  }
}
