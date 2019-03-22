package com.jaspercloud.plus.resolver;

import com.jaspercloud.plus.JasperMybatisConfiguration;
import com.jaspercloud.plus.MapperUtil;
import com.jaspercloud.plus.TableInfo;
import org.apache.ibatis.builder.MapperBuilderAssistant;
import org.apache.ibatis.executor.keygen.KeyGenerator;
import org.apache.ibatis.mapping.ResultSetType;
import org.apache.ibatis.mapping.SqlCommandType;
import org.apache.ibatis.mapping.SqlSource;
import org.apache.ibatis.mapping.StatementType;
import org.apache.ibatis.scripting.LanguageDriver;

import java.lang.reflect.Method;
import java.util.List;

public class InsertTemplateMethodResolver implements TemplateMethodResolver {

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
        SqlCommandType sqlCommandType = SqlCommandType.INSERT;
        Integer fetchSize = null;
        Integer timeout = null;
        String parameterMap = null;
        Class<?> parameterType = MapperUtil.getParameterType(method);
        String resultMap = null;
        Class<?> resultType = method.getReturnType();
        ResultSetType resultSetType = ResultSetType.FORWARD_ONLY;
        boolean flushCache = true;
        boolean useCache = false;
        boolean resultOrdered = false;
        KeyGenerator keyGenerator = MapperUtil.handleSelectKeyAnnotation(config, assistant, mappedStatementId, tableInfo, modelClass, lang);
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
        List<TableInfo.TableColumn> columns = tableInfo.getColumns();
        StringBuilder builder = new StringBuilder();
        builder.append("<script>\n");
        builder.append("insert into ").append(tableName).append("\n");
        builder.append("<trim prefix=\"(\" suffix=\")\" suffixOverrides=\",\">\n");
        for (TableInfo.TableColumn column : columns) {
            builder.append(String.format("<if test=\"null!=%s\">%s,</if>\n", column.getPropertyName(), column.getColumnName()));
        }
        builder.append("</trim>\n");
        builder.append("VALUES\n");
        builder.append("<trim prefix=\"(\" suffix=\")\" suffixOverrides=\",\">\n");
        for (TableInfo.TableColumn column : columns) {
            builder.append(String.format("<if test=\"null!=%s\">#{%s},</if>\n", column.getPropertyName(), column.getPropertyName()));
        }
        builder.append("</trim>\n");
        builder.append("</script>\n");
        String sql = builder.toString();
        return sql;
    }
}
