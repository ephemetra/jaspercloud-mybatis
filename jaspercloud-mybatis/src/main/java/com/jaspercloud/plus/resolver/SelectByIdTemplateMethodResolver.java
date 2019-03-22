package com.jaspercloud.plus.resolver;

import com.jaspercloud.plus.JasperMybatisConfiguration;
import com.jaspercloud.plus.MapperUtil;
import com.jaspercloud.plus.TableInfo;
import org.apache.ibatis.builder.MapperBuilderAssistant;
import org.apache.ibatis.executor.keygen.KeyGenerator;
import org.apache.ibatis.executor.keygen.NoKeyGenerator;
import org.apache.ibatis.mapping.ResultSetType;
import org.apache.ibatis.mapping.SqlCommandType;
import org.apache.ibatis.mapping.SqlSource;
import org.apache.ibatis.mapping.StatementType;
import org.apache.ibatis.scripting.LanguageDriver;

import java.lang.reflect.Method;

public class SelectByIdTemplateMethodResolver implements TemplateMethodResolver {

    @Override
    public void resolver(JasperMybatisConfiguration config, Class<?> type, Class<?> modelClass, Method method) {
        TableInfo tableInfo = config.getTableInfo(modelClass);
        if (null == tableInfo) {
            tableInfo = MapperUtil.parseTableInfo(modelClass);
            config.addTableInfo(modelClass, tableInfo);
        }

        String resource = type.getName().replace('.', '/') + ".java (best guess)";
        MapperBuilderAssistant assistant = new MapperBuilderAssistant(config, resource);
        assistant.setCurrentNamespace(type.getName());

        String mappedStatementId = type.getName() + "." + method.getName();
        String sql = genSqlScript(tableInfo);
        LanguageDriver lang = MapperUtil.getLanguageDriver(assistant, method);
        SqlSource sqlSource = lang.createSqlSource(config, sql, modelClass);
        StatementType statementType = StatementType.PREPARED;
        SqlCommandType sqlCommandType = SqlCommandType.SELECT;
        Integer fetchSize = null;
        Integer timeout = null;
        String parameterMap = null;
        Class<?> parameterType = MapperUtil.getParameterType(method);
        String resultMap = MapperUtil.genResultMapName(assistant, type, method, tableInfo, modelClass);
        Class<?> resultType = modelClass;
        ResultSetType resultSetType = ResultSetType.FORWARD_ONLY;
        boolean flushCache = true;
        boolean useCache = false;
        boolean resultOrdered = false;
        KeyGenerator keyGenerator = NoKeyGenerator.INSTANCE;
        String keyProperty = null;
        String keyColumn = null;
        String databaseId = null;
        String resultSets = null;
        assistant.addMappedStatement(
                mappedStatementId,
                sqlSource,
                statementType,
                sqlCommandType,
                fetchSize,
                timeout,
                parameterMap,
                parameterType,
                resultMap,
                resultType,
                resultSetType,
                flushCache,
                useCache,
                resultOrdered,
                keyGenerator,
                keyProperty,
                keyColumn,
                databaseId,
                lang,
                resultSets
        );
    }

    private String genSqlScript(TableInfo tableInfo) {
        String tableName = tableInfo.getTableName();
        TableInfo.TableColumn keyColumn = tableInfo.getKeyColumn();
        StringBuilder builder = new StringBuilder();
        builder.append("<script>\n");
        builder.append("select * from ").append(tableName).append("\n");
        builder.append(" where ");
        builder.append(keyColumn.getColumnName()).append("=").append("#{").append(keyColumn.getPropertyName()).append("}");
        builder.append("\n");
        builder.append("</script>\n");
        String sql = builder.toString();
        return sql;
    }
}
