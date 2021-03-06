package com.airbus_cyber_security.graylog;

import org.graylog2.plugin.Plugin;
import org.graylog2.plugin.PluginMetaData;
import org.graylog2.plugin.PluginModule;

import java.util.Collection;
import java.util.Collections;

public class AggregationCountPlugin implements Plugin {
    @Override
    public PluginMetaData metadata() {
        return new AggregationCountMetaData();
    }

    @Override
    public Collection<PluginModule> modules () {
        return Collections.<PluginModule>singletonList(new AggregationCountModule());
    }
}
