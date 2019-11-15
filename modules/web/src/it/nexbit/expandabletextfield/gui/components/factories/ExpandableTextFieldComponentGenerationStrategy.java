package it.nexbit.expandabletextfield.gui.components.factories;

import com.haulmont.chile.core.datatypes.Datatype;
import com.haulmont.chile.core.datatypes.impl.StringDatatype;
import com.haulmont.chile.core.model.MetaPropertyPath;
import com.haulmont.chile.core.model.Range;
import com.haulmont.cuba.core.app.dynamicattributes.DynamicAttributesTools;
import com.haulmont.cuba.core.global.Messages;
import com.haulmont.cuba.core.global.MetadataTools;
import com.haulmont.cuba.gui.UiComponents;
import com.haulmont.cuba.gui.components.Component;
import com.haulmont.cuba.gui.components.ComponentGenerationContext;
import com.haulmont.cuba.gui.components.data.ValueSource;
import com.haulmont.cuba.gui.components.factories.AbstractComponentGenerationStrategy;
import it.nexbit.expandabletextfield.web.components.ExpandableTextField;
import org.springframework.core.Ordered;

import javax.annotation.Nullable;
import javax.inject.Inject;

@org.springframework.stereotype.Component(ExpandableTextFieldComponentGenerationStrategy.NAME)
public class ExpandableTextFieldComponentGenerationStrategy extends AbstractComponentGenerationStrategy implements Ordered {
    public static final String NAME = "nxexptxt_ExpandableTextFieldComponentGenerationStrategy";

    @Inject
    protected UiComponents uiComponents;

    @Inject
    protected MetadataTools metadataTools;

    @Inject
    public ExpandableTextFieldComponentGenerationStrategy(Messages messages, DynamicAttributesTools dynamicAttributesTools) {
        super(messages, dynamicAttributesTools);
    }

    @Nullable
    @Override
    public Component createComponent(ComponentGenerationContext context) {
        String property = context.getProperty();
        MetaPropertyPath mpp = resolveMetaPropertyPath(context.getMetaClass(), property);

        if (mpp != null) {
            Range mppRange = mpp.getRange();
            if (mppRange.isDatatype()
                    && metadataTools.isLob(mpp.getMetaProperty())
                    && ((Datatype) mppRange.asDatatype()) instanceof StringDatatype) {

                ExpandableTextField expandableTextField = uiComponents.create(ExpandableTextField.class);

                ValueSource valueSource = context.getValueSource();
                if (valueSource != null) {
                    //noinspection unchecked
                    expandableTextField.setValueSource(valueSource);
                }

                return expandableTextField;
            }
        }

        return null;
    }

    @Override
    public int getOrder() {
        return HIGHEST_PLATFORM_PRECEDENCE + 20;
    }
}
