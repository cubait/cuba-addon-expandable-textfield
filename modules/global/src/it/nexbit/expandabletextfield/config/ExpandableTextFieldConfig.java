package it.nexbit.expandabletextfield.config;

import com.haulmont.cuba.core.config.Config;
import com.haulmont.cuba.core.config.Property;
import com.haulmont.cuba.core.config.Source;
import com.haulmont.cuba.core.config.SourceType;
import com.haulmont.cuba.core.config.defaults.DefaultString;

@Source(type = SourceType.DATABASE)
public interface ExpandableTextFieldConfig extends Config {

    @Property("nexbit.ui.expandShortcut")
    @DefaultString("SHIFT-F12")
    String getExpandShortcut();
}