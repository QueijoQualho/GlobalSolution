package com.fiap.br.repositories;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import com.fiap.br.models.enums.CRUDOperation;
import com.fiap.br.services.QueryExecutor;
import com.fiap.br.util.annotations.CollumnName;
import com.fiap.br.util.annotations.Required;
import com.fiap.br.util.annotations.TableName;
import com.fiap.br.util.config.Loggable;

public class Repository<T> implements Loggable<String> {

    protected final QueryExecutor queryExecutor;

    public Repository(QueryExecutor queryExecutor) {
        this.queryExecutor = queryExecutor;
    }

    /* METODOS CRUD */
    public T findOne(Class<T> entityClass, int id) {
        String tableName = getTableName(entityClass);
        Optional<Integer> idOptional = Optional.of(id);

        try {
            String sql = buildFindOneSQL(entityClass, tableName);
            List<T> result = queryExecutor.execute(entityClass, sql, null, CRUDOperation.READ, idOptional);
            if (!result.isEmpty()) {
                return result.get(0);
            }
        } catch (Exception e) {
            logError("Erro ao pegar " + tableName + ": " + e.getMessage());
        }
        return null;
    }

    public List<T> findAll(Class<T> entityClass) {
        String tableName = getTableName(entityClass);
        String sql = buildFindAllSQL(tableName);
        try {
            return queryExecutor.execute(entityClass, sql, null, CRUDOperation.READ, Optional.empty());
        } catch (Exception e) {
            logError("Erro ao pegar " + tableName + ": " + e.getMessage());
        }
        return null;
    }

    public void save(T entity) {
        Class<?> entityClass = entity.getClass();
        String tableName = getTableName(entityClass);

        try {
            String sql = buildSaveSQL(entityClass, tableName);
            List<Object> params = buildParamsList(entity);
            queryExecutor.execute(entityClass, sql, params.toArray(), CRUDOperation.CREATE, Optional.empty());
        } catch (Exception e) {
            logError("Erro ao salvar a entidade: " + tableName + " " + e.getMessage());
        }
    }

    public void update(T entity, int id) {
        Class<?> entityClass = entity.getClass();
        String tableName = getTableName(entityClass);
        Optional<Integer> idOptional = Optional.of(id);

        try {
            String sql = buildUpdateSQL(entityClass, tableName);
            List<Object> params = buildParamsList(entity);
            queryExecutor.execute(entityClass, sql, params.toArray(), CRUDOperation.UPDATE, idOptional);
        } catch (Exception e) {
            logError("Erro ao atualizar a entidade: " + tableName + " " + e.getMessage());
        }
    }

    public void delete(Class<T> entityClass, int id) {
        String tableName = getTableName(entityClass);
        Optional<Integer> idOptional = Optional.of(id);

        try {
            String sql = buildDeleteSQL(entityClass, tableName);
            queryExecutor.execute(entityClass, sql, null, CRUDOperation.DELETE, idOptional);
        } catch (Exception e) {
            logError("Erro ao deletar a entidade: " + tableName + " " + e.getMessage());
        }
    }

    /* METODOS PRA MONTAR SQL */
    private String buildFindOneSQL(Class<?> entityClass, String tableName) throws NoSuchFieldException {
        Field idField = entityClass.getDeclaredField("id");
        idField.setAccessible(true);
        CollumnName idName = idField.getAnnotation(CollumnName.class);
        return "SELECT * FROM " + tableName + " WHERE " + idName.value() + " = ?";
    }

    private String buildFindAllSQL(String tableName) {
        return "SELECT * FROM " + tableName;
    }

    private String buildSaveSQL(Class<?> entityClass, String tableName) {
        Map<String, String> columnNames = queryExecutor.getRequiredColumnNames(entityClass);
        StringBuilder sqlQuery = new StringBuilder("INSERT INTO ").append(tableName).append(" (");
        StringBuilder valuesBuilder = new StringBuilder(" VALUES (");

        columnNames.remove("id");

        for (String columnName : columnNames.values()) {
            sqlQuery.append(columnName).append(", ");
            valuesBuilder.append("?, ");
        }

        sqlQuery.deleteCharAt(sqlQuery.length() - 2).append(")");
        valuesBuilder.deleteCharAt(valuesBuilder.length() - 2).append(")");
        sqlQuery.append(valuesBuilder);

        return sqlQuery.toString();
    }

    private String buildUpdateSQL(Class<?> entityClass, String tableName) throws NoSuchFieldException {
        Map<String, String> columnNames = queryExecutor.getRequiredColumnNames(entityClass);
        StringBuilder sqlQuery = new StringBuilder("UPDATE ").append(tableName).append(" SET ");

        for (String columnName : columnNames.values()) {
            sqlQuery.append(columnName).append(" = ?, ");
        }

        sqlQuery.deleteCharAt(sqlQuery.length() - 2);

        Field idField = entityClass.getDeclaredField("id");
        idField.setAccessible(true);
        CollumnName idName = idField.getAnnotation(CollumnName.class);
        sqlQuery.append(" WHERE ").append(idName.value()).append(" = ?");

        return sqlQuery.toString();
    }

    private String buildDeleteSQL(Class<?> entityClass, String tableName) throws NoSuchFieldException {
        Field idField = entityClass.getDeclaredField("id");
        idField.setAccessible(true);
        CollumnName idName = idField.getAnnotation(CollumnName.class);
        return "DELETE FROM " + tableName + " WHERE " + idName.value() + " = ?";
    }

    /* OUTROS */
    private List<Object> buildParamsList(T entity) throws IllegalAccessException {
        List<Object> params = new ArrayList<>();
        for (Field field : entity.getClass().getDeclaredFields()) {
            field.setAccessible(true);
            if (field.isAnnotationPresent(CollumnName.class) && field.isAnnotationPresent(Required.class)) {
                Object value = field.get(entity);
                params.add(value);
            }
        }
        return params;
    }

    private String getTableName(Class<?> entityClass) {
        if (entityClass.isAnnotationPresent(TableName.class)) {
            TableName tableNameAnnotation = entityClass.getAnnotation(TableName.class);
            return tableNameAnnotation.value();
        } else {
            return entityClass.getSimpleName().toLowerCase();
        }
    }
}
