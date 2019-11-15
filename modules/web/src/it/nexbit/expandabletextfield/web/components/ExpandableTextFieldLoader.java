package it.nexbit.expandabletextfield.web.components;

import com.google.common.base.Strings;
import com.haulmont.cuba.gui.xml.layout.loaders.AbstractTextFieldLoader;

public class ExpandableTextFieldLoader extends AbstractTextFieldLoader<ExpandableTextField> {

    /**
     * Creates result component by XML-element and loads its Id. Also creates all nested components.
     *
     * @see #getResultComponent()
     */
    @Override
    public void createComponent() {
        resultComponent = factory.create(ExpandableTextField.NAME);
        loadId(resultComponent, element);
    }

    @Override
    public void loadComponent() {
        super.loadComponent();
        String attributeValue = element.attributeValue("collapsable");
        if (!Strings.isNullOrEmpty(attributeValue)) {
            resultComponent.setCollapsable(Boolean.parseBoolean(attributeValue));
        }
        attributeValue = element.attributeValue("expandWithEnter");
        if (!Strings.isNullOrEmpty(attributeValue)) {
            resultComponent.setExpandWithEnter(Boolean.parseBoolean(attributeValue));
        }
    }
}
