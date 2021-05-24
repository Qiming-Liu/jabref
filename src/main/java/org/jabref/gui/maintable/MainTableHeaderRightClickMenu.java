package org.jabref.gui.maintable;

import java.util.ArrayList;
import java.util.List;

import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.RadioMenuItem;
import javafx.scene.control.SeparatorMenuItem;
import javafx.scene.control.TableColumn;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.StackPane;

import org.jabref.gui.DialogService;
import org.jabref.gui.JabRefFrame;
import org.jabref.gui.maintable.columns.MainTableColumn;
import org.jabref.gui.preferences.PreferencesDialogView;
import org.jabref.gui.preferences.table.TableTab;
import org.jabref.gui.preferences.table.TableTabViewModel;
import org.jabref.logic.l10n.Localization;

public class MainTableHeaderRightClickMenu extends ContextMenu {

    public void show(MainTable mainTable, JabRefFrame jabRefFrame, DialogService dialogService) {

        PreferencesDialogView preferencesDialogView = new PreferencesDialogView(jabRefFrame);
        preferencesDialogView.getPreferenceTabList().getSelectionModel().select(3);
        TableTabViewModel tableTabViewModel = ((TableTab) preferencesDialogView.getViewModel().getPreferenceTabs().get(3)).getViewModel();

        mainTable.setOnContextMenuRequested(clickEvent -> {

            // Click on the tableColumns
            if (!(clickEvent.getTarget() instanceof StackPane)) {

                // Create radioMenuItemList from tableColumnList
                List<RadioMenuItem> radioMenuItems = new ArrayList<>();

                mainTable.getColumns().forEach(tableColumn -> radioMenuItems.add(createRadioMenuItem(tableColumn, tableTabViewModel)));

                SeparatorMenuItem line = new SeparatorMenuItem();

                // Show preferences Button
                MenuItem columnsPreferences = new MenuItem(Localization.lang("Show preferences"));
                columnsPreferences.setOnAction(event -> {
                    // Show Entry table
                    dialogService.showCustomDialog(preferencesDialogView);
                });

                // Clean items and add newItems
                this.getItems().clear();
                this.getItems().addAll(radioMenuItems);
                this.getItems().addAll(line, columnsPreferences);

                // Show ContextMenu
                this.show(mainTable, clickEvent.getScreenX(), clickEvent.getScreenY());
            }
            clickEvent.consume();
        });

        // Cancel ContextMenu
        mainTable.setOnMouseClicked(event -> {
            if (event.getButton() != MouseButton.SECONDARY && !event.isControlDown()) {
                this.hide();
            }
        });
    }

    private RadioMenuItem createRadioMenuItem(TableColumn<BibEntryTableViewModel, ?> tableColumn, TableTabViewModel tableTabViewModel) {

        // Get DisplayName
        RadioMenuItem radioMenuItem = new RadioMenuItem(((MainTableColumn<?>) tableColumn).getDisplayName());

        // Get VisibleStatus
        radioMenuItem.setSelected(true); // need to compare with default
        radioMenuItem.setOnAction(event -> {
            if(radioMenuItem.isSelected()){
                tableTabViewModel.removeColumn(((MainTableColumn<?>) tableColumn).getModel());
                tableTabViewModel.storeSettings();
            } else {
                //Todo setup new MainTableColumn here
//                tableTabViewModel.addColumnProperty() = new MainTableColumnModel(MainTableColumnModel.Type.GROUPS);
                tableTabViewModel.storeSettings();
            }
        });

        return radioMenuItem;
    }
}
