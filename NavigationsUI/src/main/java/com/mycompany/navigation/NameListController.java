package com.mycompany.navigation;

import org.robovm.apple.foundation.NSIndexPath;
import org.robovm.apple.foundation.NSObject;
import org.robovm.apple.uikit.*;
import org.robovm.objc.annotation.CustomClass;
import org.robovm.objc.annotation.IBAction;

import java.util.Arrays;
import java.util.List;

@CustomClass("NameListController")
public class NameListController extends UITableViewController {
    private List<String> names; // [:1:]

    public void setNames(List<String> names) { // [:2:]
        this.names = names;
    }

    /*@Override
    public void viewDidLoad() { // [:3:]
        super.viewDidLoad();
        names = Arrays.asList("Richard Feynman", "Albert Einstein");
    }*/

    @Override
    public long getNumberOfSections(UITableView tableView) { // [:4:]
        return 1;
    }

    @Override
    public long getNumberOfRowsInSection(UITableView tableView, long section) { // [:5:]
        return names.size();
    }

    @Override
    public UITableViewCell getCellForRow(UITableView tableView, NSIndexPath indexPath) { // [:6:]
        int row = (int)indexPath.getRow();
        UITableViewCell cell = tableView.dequeueReusableCell("NameListCell"); // [:7:]
        if(cell == null) {
            cell = new UITableViewCell(UITableViewCellStyle.Default, "NameListCell"); // [:8:]
        }
        cell.getTextLabel().setText(names.get(row)); // [:9:]
        return cell;
    }
    @Override
    public void prepareForSegue(UIStoryboardSegue segue, NSObject sender) {
        super.prepareForSegue(segue, sender);
        if(segue.getIdentifier().equals("EditName")) { // [:1:]
            int selectedRow = (int)getTableView().getIndexPathForSelectedRow().getRow(); // [:2:]
            String name = names.get(selectedRow); // [:3:]
            UINavigationController navController = (UINavigationController)segue.getDestinationViewController(); // [:4:]
            EditingController editingController = (EditingController)navController.getVisibleViewController(); // [:5:]
            editingController.setNameToEdit(name, selectedRow); // [:6:]
        }
    }
    @IBAction
    public void unwindToNameList(UIStoryboardSegue segue) {
        if(segue.getIdentifier().equals("EditSave")) { // [:1:]
            EditingController editingController = (EditingController)segue.getSourceViewController(); // [:2:]
            String name = editingController.getEditedName().trim(); // [:3:]
            if(!name.isEmpty()) { // [:4:]
                names.set(editingController.getIndex(), name);  // [:5:]
                getTableView().reloadData(); // [:6:]
            }
        }
    }
}
