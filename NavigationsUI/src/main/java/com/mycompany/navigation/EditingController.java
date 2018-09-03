package com.mycompany.navigation;

import org.robovm.apple.uikit.UITextField;
import org.robovm.apple.uikit.UIViewController;
import org.robovm.objc.annotation.CustomClass;
import org.robovm.objc.annotation.IBOutlet;

@CustomClass("EditingController")
public class EditingController extends UIViewController { // [:1:]
    private UITextField textField; // [:2:]
    private String nameToEdit; // [:3:]
    private int index; // [:4:]

    public void setNameToEdit(String nameToEdit, int index) { // [:5:]
        this.nameToEdit = nameToEdit;
        this.index = index;
    }

    public String getEditedName() { // [:5:]
        return textField.getText();
    }

    public int getIndex() { // [:6:]
        return index;
    }

    @IBOutlet
    public void setTextField(UITextField textField) { // [:7:]
        this.textField = textField;
    }

    @Override
    public void viewDidLoad() { // [:8:]
        super.viewDidLoad();
        textField.setText(nameToEdit);
    }
}