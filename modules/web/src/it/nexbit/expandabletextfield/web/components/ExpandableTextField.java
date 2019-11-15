package it.nexbit.expandabletextfield.web.components;

import com.google.common.base.Strings;
import com.haulmont.bali.events.Subscription;
import com.haulmont.cuba.core.global.AppBeans;
import com.haulmont.cuba.core.global.Configuration;
import com.haulmont.cuba.gui.ComponentsHelper;
import com.haulmont.cuba.gui.Screens;
import com.haulmont.cuba.gui.components.*;
import com.haulmont.cuba.gui.components.data.ValueSource;
import com.haulmont.cuba.gui.screen.OpenMode;
import com.haulmont.cuba.gui.screen.Screen;
import com.haulmont.cuba.web.gui.components.*;
import com.haulmont.cuba.web.gui.components.util.ShortcutListenerDelegate;
import com.haulmont.cuba.web.widgets.CubaTextField;
import it.nexbit.expandabletextfield.config.ExpandableTextFieldConfig;

import javax.inject.Inject;
import java.util.Collection;
import java.util.function.Consumer;

@CompositeDescriptor("expandable-textfield.xml")
public class ExpandableTextField extends CompositeComponent<CssLayout> implements TextInputField<String>,
        Collapsable,
        CompositeWithCaption,
        CompositeWithHtmlCaption,
        CompositeWithHtmlDescription,
        CompositeWithIcon,
        CompositeWithContextHelp {

    public static final String NAME = "expandableTextField";

    protected TextField<String> valueField;
    protected Button expandBtn;

    private Screens screens;

    private boolean isCollapsable = true;
    private boolean editable = true;
    private boolean expanded;

    private Subscription enterPressSubscription;

    private Dialog dialog;

    public ExpandableTextField() {
        addCreateListener(this::onCreate);
    }

    private void onCreate(CreateEvent createEvent) {
        valueField = getInnerComponent("valueField");
        expandBtn = getInnerComponent("expandBtn");

        valueField.addValueChangeListener(event -> {
            if (!expanded) getEventHub().publish(ValueChangeEvent.class, event);
        });

        String expandShortcut = AppBeans.get(Configuration.class)
                .getConfig(ExpandableTextFieldConfig.class)
                .getExpandShortcut();
        if (!Strings.isNullOrEmpty(expandShortcut)) {
            KeyCombination keyCombination = KeyCombination.create(expandShortcut);
            CubaTextField cubaTextField = valueField.unwrap(CubaTextField.class);
            cubaTextField.addShortcutListener(new ShortcutListenerDelegate("",
                    keyCombination.getKey().getCode(), KeyCombination.Modifier.codes(keyCombination.getModifiers()))
                    .withHandler((sender, target) -> expand()));
        }

        expandBtn.addClickListener(clickEvent -> expand());
    }

    protected Screens getScreens() {
        if (screens == null) {
            screens = ComponentsHelper.getScreenContext(getFrame()).getScreens();
        }
        return screens;
    }

    protected Dialog getDialog() {
        return dialog;
    }

    public boolean isExpanded() {
        return expanded;
    }

    public void setExpanded(boolean expanded) {
        if (isCollapsable && this.expanded != expanded) {
            valueField.setEditable(editable && !expanded);
            this.expanded = expanded;
            publishExpandedStateChangeEvent(expanded);

            if (expanded) {
                dialog = (Dialog) getScreens().create(Dialog.NAME, OpenMode.DIALOG);
                dialog.getWindow().setCaption(getCaption());
                // mirror valueField properties into dialog's expandedField
                dialog.getExpandedField().setRequired(this.isRequired());
                dialog.getExpandedField().setRequiredMessage(this.getRequiredMessage());
                dialog.getExpandedField().setValueSource(this.getValueSource());
                dialog.getExpandedField().setBuffered(this.isBuffered());
                dialog.getExpandedField().setEditable(editable);
                for (Consumer<String> validator : this.getValidators()) {
                    dialog.getExpandedField().addValidator(validator);
                }

                dialog.getExpandedField().addValueChangeListener(event -> getEventHub().publish(ValueChangeEvent.class, event));
                dialog.addAfterCloseListener(afterCloseEvent -> {
                    setExpanded(false);
                    focus();
                });
                dialog.show();
            } else {
                if (dialog.isOpen()) {
                    dialog.closeWithDefaultAction();
                }
                dialog = null;
            }
        }
    }

    @Override
    public boolean isCollapsable() {
        return isCollapsable;
    }

    @Override
    public void setCollapsable(boolean collapsable) {
        isCollapsable = collapsable;
        expandBtn.setVisible(collapsable);
    }

    @Override
    public Subscription addExpandedStateChangeListener(Consumer<ExpandedStateChangeEvent> listener) {
        return getEventHub().subscribe(ExpandedStateChangeEvent.class, listener);
    }

    @Override
    public void removeExpandedStateChangeListener(Consumer<ExpandedStateChangeEvent> listener) {
        unsubscribe(ExpandedStateChangeEvent.class, listener);
    }

    public void expand() {
        setExpanded(true);
    }

    public void collapse() {
        setExpanded(false);
    }

    @Override
    public boolean isRequired() {
        return valueField.isRequired();
    }

    @Override
    public void setRequired(boolean required) {
        valueField.setRequired(required);
        if (expanded) dialog.getExpandedField().setRequired(required);
        getComposition().setRequiredIndicatorVisible(required);
    }

    @Override
    public String getRequiredMessage() {
        return valueField.getRequiredMessage();
    }

    @Override
    public void setRequiredMessage(String msg) {
        valueField.setRequiredMessage(msg);
        if (expanded) dialog.getExpandedField().setRequiredMessage(msg);
    }

    @Override
    public void addValidator(Consumer<? super String> validator) {
        valueField.addValidator(validator);
        if (expanded) dialog.getExpandedField().addValidator(validator);
    }

    @Override
    public void removeValidator(Consumer<String> validator) {
        valueField.removeValidator(validator);
        if (expanded) dialog.getExpandedField().removeValidator(validator);
    }

    @Override
    public Collection<Consumer<String>> getValidators() {
        return valueField.getValidators();
    }

    @Override
    public boolean isEditable() {
        return editable;
    }

    @Override
    public void setEditable(boolean editable) {
        if (this.editable == editable) return;

        this.editable = editable;
        valueField.setEditable(editable);
        if (expanded) dialog.getExpandedField().setEditable(editable);
        expandBtn.setEnabled(editable);
    }

    @Override
    public String getValue() {
        return valueField.getValue();
    }

    @Override
    public void setValue(String value) {
        valueField.setValue(value);
        if (expanded) dialog.getExpandedField().setValue(value);
    }

    @SuppressWarnings("unchecked")
    @Override
    public Subscription addValueChangeListener(Consumer<ValueChangeEvent<String>> listener) {
        return getEventHub().subscribe(ValueChangeEvent.class, (Consumer) listener);
    }

    @SuppressWarnings("unchecked")
    @Override
    public void removeValueChangeListener(Consumer<ValueChangeEvent<String>> listener) {
        getEventHub().unsubscribe(ValueChangeEvent.class, (Consumer) listener);
    }

    @Override
    public boolean isValid() {
        try {
            validate();
            return true;
        } catch (ValidationException e) {
            return false;
        }
    }

    @Override
    public void validate() throws ValidationException {
        if (expanded)
            dialog.getExpandedField().validate();
        else
            valueField.validate();
    }

    @Override
    public void setValueSource(ValueSource<String> valueSource) {
        valueField.setValueSource(valueSource);
        if (expanded) dialog.getExpandedField().setValueSource(valueSource);
        getComposition().setRequiredIndicatorVisible(valueField.isRequired());
    }

    @Override
    public ValueSource<String> getValueSource() {
        return valueField.getValueSource();
    }

    @Override
    public void commit() {
        if (expanded)
            dialog.getExpandedField().commit();
        else
            valueField.commit();
    }

    @Override
    public void discard() {
        if (expanded)
            dialog.getExpandedField().discard();
        else
            valueField.discard();
    }

    @Override
    public boolean isBuffered() {
        return valueField.isBuffered();
    }

    @Override
    public void setBuffered(boolean buffered) {
        valueField.setBuffered(buffered);
        if (expanded) dialog.getExpandedField().setBuffered(buffered);
    }

    @Override
    public boolean isModified() {
        return expanded ? dialog.getExpandedField().isModified() : valueField.isModified();
    }

    @Override
    public void focus() {
        if (expanded)
            dialog.getExpandedField().focus();
        else
            valueField.focus();
    }

    @Override
    public int getTabIndex() {
        return valueField.getTabIndex();
    }

    @Override
    public void setTabIndex(int tabIndex) {
        valueField.setTabIndex(tabIndex);
    }

    protected void publishExpandedStateChangeEvent(boolean expanded) {
        if (hasSubscriptions(ExpandedStateChangeEvent.class)) {
            publish(ExpandedStateChangeEvent.class, new ExpandedStateChangeEvent(this, expanded, true));
        }
    }

    public boolean isExpandWithEnter() {
        return enterPressSubscription != null;
    }

    public void setExpandWithEnter(boolean expandWithEnter) {
        if (enterPressSubscription == null && expandWithEnter) {
            enterPressSubscription = valueField.addEnterPressListener(enterPressEvent -> expand());
        } else if (enterPressSubscription != null) {
            enterPressSubscription.remove();
            enterPressSubscription = null;
        }
    }

    public static abstract class Dialog extends Screen {

        public static final String NAME = "nxexptxt_ExpandableTextFieldDialog";

        @Inject
        private Screens screens;

        boolean isOpen() {
            return screens.getOpenedScreens().getDialogScreens().contains(this);
        }

        public abstract TextInputField<String> getExpandedField();
    }
}
