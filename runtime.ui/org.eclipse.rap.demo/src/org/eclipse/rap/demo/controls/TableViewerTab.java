/*******************************************************************************
 * Copyright (c) 2002-2006 Innoopract Informationssysteme GmbH. All rights
 * reserved. This program and the accompanying materials are made available
 * under the terms of the Eclipse Public License v1.0 which accompanies this
 * distribution, and is available at http://www.eclipse.org/legal/epl-v10.html
 * Contributors: Innoopract Informationssysteme GmbH - initial API and
 * implementation
 ******************************************************************************/

package org.eclipse.rap.demo.controls;

import java.util.ArrayList;

import org.eclipse.jface.viewers.*;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.*;

public class TableViewerTab extends ExampleTab {

  private static final class Person {
    String firstName;
    String lastName;
    int age;
    
    public Person( final String firstName, final String lastName, final int age ) {
      super();
      this.firstName = firstName;
      this.lastName = lastName;
      this.age = age;
    }
    
    public String toString() {
      return firstName + " " + lastName + " " + age;
    }
  }
  
  private static class PersonContentProvider 
    implements IStructuredContentProvider 
  {
    public Object[] getElements( final Object inputElement ) {
      java.util.List personList = ( java.util.List )inputElement;
      return personList.toArray();
    }
    public void dispose() {
      // do nothing
    }
    public void inputChanged( Viewer viewer, Object oldInput, Object newInput ) 
    {
      // do nothing
    }
  }
  
  private static final class PersonLabelProvider 
    extends LabelProvider 
    implements ITableLabelProvider
  {
    public Image getColumnImage( final Object element, final int columnIndex ) {
      return null;
    }
    public String getColumnText( final Object element, final int columnIndex ) {
      Person person = ( Person )element;
      String result;
      switch( columnIndex ) {
        case 0:
          result = person.firstName;
        break;
        case 1:
          result = person.lastName;
        break;
        case 2:
          result = String.valueOf( person.age );
          break;
        default:
          result = "";
        break;
      }
      return result;
    }
  }
  
  
  protected static final int ADD_ITEMS = 300;
  
  private TableViewer viewer;
  private final java.util.List persons = new ArrayList();

  private Label lblSelection;

  public TableViewerTab( final CTabFolder topFolder ) {
    super( topFolder, "TableViewer" );
    initPersons();
  }

  private void initPersons() {
    persons.add( new Person( "R�gn\"�y&", "H�vl&lt;, the char tester", 1 ) );
    persons.add( new Person( "Paul", "Panther", 1 ) );
    persons.add( new Person( "Carl", "Marx", 2 ) );
    persons.add( new Person( "Sofia", "Loren", 3 ) );
    persons.add( new Person( "King", "Cool", 4 ) );
    persons.add( new Person( "Albert", "Einstein", 5 ) );
    persons.add( new Person( "Donald", "Duck", 6 ) );
    persons.add( new Person( "Mickey", "Mouse", 7 ) );
    persons.add( new Person( "Asterix", "", 8 ) );
    persons.add( new Person( "Nero", "", 9 ) );
    persons.add( new Person( "Elvis", "Presley", 10 ) );
  }

  protected void createStyleControls() {
    createAddItemsButton();
    createSelectYoungestPersonButton();
    lblSelection = new Label( styleComp, SWT.NONE );
  }

  protected void createExampleControls( final Composite top ) {
    top.setLayout( new FillLayout() );
    viewer = new TableViewer( top, getStyle() );
    viewer.setContentProvider( new PersonContentProvider() );
    viewer.setLabelProvider( new PersonLabelProvider() );
    viewer.setColumnProperties( initColumnProperties( viewer.getTable() ) );
    viewer.setInput( persons );
    viewer.addSelectionChangedListener( new ISelectionChangedListener() {
      public void selectionChanged( SelectionChangedEvent event ) {
        lblSelection.setText( "Selection: " + event.getSelection() );
      }
    } );
    viewer.getTable().setHeaderVisible( true );
    registerControl( viewer.getControl() );
  }

  private String[] initColumnProperties( final Table table ) {
    TableColumn firstNameColumn = new TableColumn( table, SWT.NONE );
    firstNameColumn.setText( "First Name" );
    firstNameColumn.setWidth( 170 );
    TableColumn lastNameColumn = new TableColumn( table, SWT.NONE );
    lastNameColumn.setText( "Last Name" );
    lastNameColumn.setWidth( 100 );    
    TableColumn ageColumn = new TableColumn( table, SWT.NONE );
    ageColumn.setText( "Age" );
    ageColumn.setWidth( 80 );
    return new String[] {
      firstNameColumn.getText(), lastNameColumn.getText(), ageColumn.getText()
    };
  }
  
  private void addPerson() {
    int maxAge = 0;
    for( int i = 0; i < persons.size(); i++ ) {
      Person person = ( Person )persons.get( i );
      if( person.age > maxAge ) {
        maxAge = person.age;
      }
    }
    persons.add( new Person( "new", "person", maxAge + 1 ) );
    viewer.refresh();
  }

  private void createAddItemsButton() {
    Button button = new Button( styleComp, SWT.PUSH );
    button.setText( "Add " + ADD_ITEMS + " Items" );
    button.addSelectionListener( new SelectionAdapter() {
      public void widgetSelected( final SelectionEvent event ) {
        for( int i = 0; i < ADD_ITEMS; i++ ) {
          addPerson();
        }
      }
    } );
  }
  
  private void createSelectYoungestPersonButton() {
    Button button = new Button( styleComp, SWT.PUSH );
    button.setText( "Select youngest Person" );
    button.addSelectionListener( new SelectionAdapter() {
      public void widgetSelected( final SelectionEvent event ) {
        Person youngestPerson = null;
        int minAge = Integer.MAX_VALUE;
        for( int i = 0; i < persons.size(); i++ ) {
          Person person = ( Person )persons.get( i );
          if( person.age < minAge ) {
            minAge = person.age;
            youngestPerson = person;
          }
        }
        viewer.setSelection( new StructuredSelection( youngestPerson ) );
      }
    } );
  }
}
