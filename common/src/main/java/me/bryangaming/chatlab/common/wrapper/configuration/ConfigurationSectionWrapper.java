package me.bryangaming.chatlab.common.wrapper.configuration;

import java.util.Set;

public interface ConfigurationSectionWrapper {

    Set<String> getKeys(boolean deep);
}
