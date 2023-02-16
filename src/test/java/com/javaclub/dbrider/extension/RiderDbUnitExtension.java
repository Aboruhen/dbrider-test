package com.javaclub.dbrider.extension;

import static com.github.database.rider.junit5.jdbc.ConnectionManager.getConfiguredDataSourceBeanName;
import static com.github.database.rider.junit5.jdbc.ConnectionManager.getTestConnection;
import static com.github.database.rider.junit5.util.Constants.EMPTY_STRING;
import static com.github.database.rider.junit5.util.Constants.JUNIT5_EXECUTOR;
import static com.github.database.rider.junit5.util.Constants.NAMESPACE;

import com.github.database.rider.core.RiderRunner;
import com.github.database.rider.core.RiderTestContext;
import com.github.database.rider.core.api.configuration.DBUnit;
import com.github.database.rider.core.api.connection.ConnectionHolder;
import com.github.database.rider.core.api.dataset.DataSet;
import com.github.database.rider.core.api.dataset.DataSetExecutor;
import com.github.database.rider.core.api.leak.LeakHunter;
import com.github.database.rider.core.configuration.ConnectionConfig;
import com.github.database.rider.core.configuration.DBUnitConfig;
import com.github.database.rider.core.dataset.DataSetExecutorImpl;
import com.github.database.rider.core.leak.LeakHunterFactory;
import com.github.database.rider.junit5.DBUnitExtension;
import com.github.database.rider.junit5.DBUnitTestContext;
import com.github.database.rider.junit5.JUnit5RiderTestContext;
import com.github.database.rider.junit5.util.EntityManagerProvider;
import java.lang.reflect.Method;
import java.sql.SQLException;
import java.util.Optional;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.ExtensionContext.Store;
import org.junit.platform.commons.util.AnnotationUtils;
import org.junit.platform.commons.util.StringUtils;

/**
 * This is a copy of exist {@see DBUnitExtension}.
 * The only fix is under {@see SpringDbUnitConfigs}.
 * This does fix DB escape configs by spring active profiles
 */
public class RiderDbUnitExtension extends DBUnitExtension {

    @Override
    public void beforeTestExecution(ExtensionContext extensionContext) throws Exception {
        EntityManagerProvider.clear();
        DBUnitTestContext dbUnitTestContext = this.getTestContext(extensionContext);
        DataSetExecutor dataSetExecutor = dbUnitTestContext.getExecutor();
        DBUnitConfig dbUnitConfig = this.resolveDbUnitConfig(extensionContext);
        dataSetExecutor.setDBUnitConfig(dbUnitConfig);
        if (dbUnitConfig.isLeakHunter()) {
            try {
                LeakHunter leakHunter = LeakHunterFactory.from(dataSetExecutor.getRiderDataSource(),
                    extensionContext.getRequiredTestMethod().getName(), dbUnitConfig.isCacheConnection());
                leakHunter.measureConnectionsBeforeExecution();
                dbUnitTestContext.setLeakHunter(leakHunter);
            } catch (SQLException var7) {
//                log.warn(String.format("Could not create leak hunter for test %s",
//                    extensionContext.getRequiredTestMethod().getName()), var7);
            }
        }

        RiderTestContext riderTestContext = new JUnit5RiderTestContext(dbUnitTestContext.getExecutor(),
            extensionContext);
        RiderRunner riderRunner = new RiderRunner();
        setup(riderTestContext, extensionContext);
        riderRunner.runBeforeTest(riderTestContext);
    }

    public void setup(RiderTestContext riderTestContext, ExtensionContext extensionContext) throws SQLException {
        DBUnitConfig dbUnitConfig = resolveDBUnitConfig(riderTestContext, extensionContext);
        DataSetExecutor executor = riderTestContext.getDataSetExecutor();
        executor.setDBUnitConfig(dbUnitConfig);
        if (executor.getRiderDataSource().getDBUnitConnection().getConnection() == null) {
            ConnectionConfig connectionConfig = executor.getDBUnitConfig().getConnectionConfig();
            executor.initConnectionFromConfig(connectionConfig);
        }
    }

    private DBUnitConfig resolveDBUnitConfig(RiderTestContext riderTestContext, ExtensionContext extensionContext) {
        return new SpringDbUnitConfigs(extensionContext).dbUnitConfig(
            Optional.ofNullable(riderTestContext.getAnnotation(DBUnit.class)));
    }

    private DBUnitConfig resolveDbUnitConfig(ExtensionContext extensionContext) {
        Optional<Method> method = extensionContext.getTestMethod();
        Optional<DBUnit> dbUnitAnnotation = AnnotationUtils.findAnnotation(method, DBUnit.class);
        if (!dbUnitAnnotation.isPresent()) {
            Class testClass = extensionContext.getRequiredTestClass();
            dbUnitAnnotation = AnnotationUtils.findAnnotation(testClass, DBUnit.class);
            if (!dbUnitAnnotation.isPresent() && testClass.getSuperclass() != null) {
                dbUnitAnnotation = AnnotationUtils.findAnnotation(testClass.getSuperclass(), DBUnit.class);
            }
        }
        return new SpringDbUnitConfigs(extensionContext).dbUnitConfig(dbUnitAnnotation);
    }

    private DBUnitTestContext getTestContext(ExtensionContext context) {
        Class<?> testClass = context.getRequiredTestClass();
        Store store = context.getStore(NAMESPACE);
        return store.getOrComputeIfAbsent(testClass, (tc) -> createDbUnitTestContext(context), DBUnitTestContext.class);
    }

    private DBUnitTestContext createDbUnitTestContext(ExtensionContext extensionContext) {
        final String executorId = getExecutorId(extensionContext);
        final ConnectionHolder connectionHolder = getTestConnection(extensionContext, executorId);
        final DataSetExecutor dataSetExecutor = DataSetExecutorImpl.instance(executorId, connectionHolder);
        return new DBUnitTestContext(dataSetExecutor);
    }

    private String getExecutorId(final ExtensionContext extensionContext) {
        Optional<DataSet> annDataSet = findDataSetAnnotation(extensionContext);
        String dataSourceBeanName = getConfiguredDataSourceBeanName(extensionContext);
        String executionIdSuffix = dataSourceBeanName.isEmpty() ? EMPTY_STRING : "-" + dataSourceBeanName;
        return annDataSet
            .map(DataSet::executorId)
            .filter(StringUtils::isNotBlank)
            .map(id -> id + executionIdSuffix)
            .orElseGet(() -> JUNIT5_EXECUTOR + executionIdSuffix);
    }

    private Optional<DataSet> findDataSetAnnotation(ExtensionContext extensionContext) {
        return extensionContext.getTestMethod()
            .flatMap(tm -> AnnotationUtils.findAnnotation(tm, DataSet.class)
                .or(() -> AnnotationUtils.findAnnotation(extensionContext.getRequiredTestClass(), DataSet.class)));
    }

}
