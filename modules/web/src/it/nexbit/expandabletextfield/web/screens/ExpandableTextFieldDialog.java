package it.nexbit.expandabletextfield.web.screens;

import com.haulmont.cuba.gui.components.RichTextArea;
import com.haulmont.cuba.gui.components.TextInputField;
import com.haulmont.cuba.gui.screen.DialogMode;
import com.haulmont.cuba.gui.screen.UiController;
import com.haulmont.cuba.gui.screen.UiDescriptor;
import it.nexbit.expandabletextfield.web.components.ExpandableTextField;

import javax.inject.Inject;

@UiController(ExpandableTextField.Dialog.NAME)
@UiDescriptor("expandable-textfield-dialog.xml")
@DialogMode(forceDialog = true)
public class ExpandableTextFieldDialog extends ExpandableTextField.Dialog {
    @Inject
    protected RichTextArea richTextArea;

    @Override
    public TextInputField<String> getExpandedField() {
        return richTextArea;
    }
}