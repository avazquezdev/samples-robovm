package com.mycompany.navigation;

import org.robovm.apple.coregraphics.CGRect;
import org.robovm.apple.foundation.NSObject;
import org.robovm.apple.uikit.*;
import org.robovm.objc.annotation.CustomClass;
import org.robovm.objc.annotation.IBAction;
import org.robovm.objc.annotation.IBOutlet;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@CustomClass("MainSceneController")

public class MyViewController extends UIViewController {
    private UITextField textField; // [:1:]
    private List<String> names; // [:2:]

    @Override
    public void viewDidLoad() { // [:3:]
        super.viewDidLoad();
        names = new ArrayList<String>();
        names = Arrays.asList("Richard Feynman", "Albert Einstein");
    }

    @IBOutlet
    public void setTextField(UITextField textField) { // [:4:]
        this.textField = textField;
    }

    @IBAction
    public void rememberName() { // [:5:]
        String name = textField.getText().trim();
        if(!name.isEmpty()) {
            names.add(name);
            this.textField.setText("");
        }
    }

    @Override
    public void prepareForSegue(UIStoryboardSegue uiStoryboardSegue, NSObject nsObject) {
        super.prepareForSegue(uiStoryboardSegue, nsObject);
        if(uiStoryboardSegue.getIdentifier().equals("ShowNames")) { // [:3:]
            NameListController nameListController = (NameListController)uiStoryboardSegue.getDestinationViewController(); // [:4:]
            nameListController.setNames(names); // [:5:]
        }
    }
}
