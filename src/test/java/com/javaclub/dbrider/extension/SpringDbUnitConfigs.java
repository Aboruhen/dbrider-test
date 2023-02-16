package com.javaclub.dbrider.extension;

import com.github.database.rider.core.api.configuration.DBUnit;
import com.github.database.rider.core.configuration.DBUnitConfig;
import java.util.Optional;
import org.apache.commons.lang3.ArrayUtils;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.springframework.test.context.junit.jupiter.SpringExtension;

public class SpringDbUnitConfigs {

    private final ExtensionContext extensionContext;

    public SpringDbUnitConfigs(ExtensionContext extensionContext) {
        this.extensionContext = extensionContext;
    }


    public DBUnitConfig dbUnitConfig(Optional<DBUnit> dbUnitAnnotation) {
        return dbUnitAnnotation
            .map(dbUnit -> getFrom(dbUnit, activeProfiles()))
            .orElse(DBUnitConfig.fromGlobalConfig());
    }

    private String[] activeProfiles() {
        return SpringExtension.getApplicationContext(extensionContext).getEnvironment()
            .getActiveProfiles();
    }

    @NotNull
    private DBUnitConfig getFrom(DBUnit dbUnit, String[] activeProfiles) {
        DBUnitConfig dbUnitConfig = DBUnitConfig.from(dbUnit);
        if (ArrayUtils.contains(activeProfiles, "mssql")) {
            dbUnitConfig.addDBUnitProperty("escapePattern", "\"?\"");
        }
        if (ArrayUtils.contains(activeProfiles, "oracle")) {
            dbUnitConfig.addDBUnitProperty("escapePattern", "\"");
        }
        return dbUnitConfig;
    }

}
