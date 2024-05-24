package com.fiap.br.repositories;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import com.fiap.br.models.enums.CRUDOperation;
import com.fiap.br.services.QueryExecutor;
import com.fiap.br.util.annotations.CollumnName;
import com.fiap.br.util.annotations.TableName;
import com.fiap.br.util.interfaces.Loggable;

import jakarta.validation.constraints.NotNull;

public class Repository<T> implements Loggable<String> {

    private final QueryExecutor queryExecutor;

    public Repository(QueryExecutor queryExecutor) {
        this.queryExecutor = queryExecutor;
    }

    public T findOne(Class<T> entityClass, int id) {
        String tableName = getTableName(entityClass);
        String sql = buildFindOneSQL(entityClass, tableName);
        return executeQuery(entityClass, sql, id, CRUDOperation.READ).stream().findFirst().orElse(null);
    }

    public List<T> findAll(Class<T> entityClass) {
        String tableName = getTableName(entityClass);
        String sql = buildFindAllSQL(tableName);
        return executeQuery(entityClass, sql, null, CRUDOperation.READ);
    }

    public void save(T entity) {
        String tableName = getTableName(entity.getClass());
        String sql = buildSaveSQL(entity.getClass(), tableName);
        List<Object> params = buildParamsList(entity);
        executeUpdate(entity.getClass(), sql, params, CRUDOperation.CREATE);
    }

    public void update(T entity, int id) {
        String tableName = getTableName(entity.getClass());
        String sql = buildUpdateSQL(entity.getClass(), tableName);
        List<Object> params = buildParamsList(entity);
        params.add(id);
        executeUpdate(entity.getClass(), sql, params, CRUDOperation.UPDATE);
    }

    public void delete(Class<T> entityClass, int id) {
        String tableName = getTableName(entityClass);
        String sql = buildDeleteSQL(entityClass, tableName);
        executeUpdate(entityClass, sql, List.of(id), CRUDOperation.DELETE);
    }

    private String buildFindOneSQL(Class<?> entityClass, String tableName) {
        String idColumn = getIdColumn(entityClass);
        return String.format("SELECT * FROM %s WHERE %s = ?", tableName, idColumn);
    }

    private String buildFindAllSQL(String tableName) {
        return String.format("SELECT * FROM %s", tableName);
    }

    private String buildSaveSQL(Class<?> entityClass, String tableName) {
        Map<String, String> columnNames = queryExecutor.getRequiredColumnNames(entityClass);
        columnNames.remove("id");

        String columns = String.join(", ", columnNames.values());
        String placeholders = "?" + ", ?".repeat(columnNames.size() - 1);

        return String.format("INSERT INTO %s (%s) VALUES (%s)", tableName, columns, placeholders);
    }

    private String buildUpdateSQL(Class<?> entityClass, String tableName) {
        Map<String, String> columnNames = queryExecutor.getRequiredColumnNames(entityClass);
        String setClause = String.join(" = ?, ", columnNames.values()) + " = ?";
        String idColumn = getIdColumn(entityClass);

        return String.format("UPDATE %s SET %s WHERE %s = ?", tableName, setClause, idColumn);
    }

    private String buildDeleteSQL(Class<?> entityClass, String tableName) {
        String idColumn = getIdColumn(entityClass);
        return String.format("DELETE FROM %s WHERE %s = ?", tableName, idColumn);
    }

    private List<T> executeQuery(Class<T> entityClass, String sql, Integer id, CRUDOperation operation) {
        try {
            Optional<Integer> idOptional = Optional.ofNullable(id);
            return queryExecutor.execute(entityClass, sql, null, operation, idOptional);
        } catch (Exception e) {
            logError("Erro ao executar consulta para " + getTableName(entityClass) + ": " + e.getMessage());
            return new ArrayList<>();
        }
    }

    private void executeUpdate(Class<?> entityClass, String sql, List<Object> params, CRUDOperation operation) {
        try {
            queryExecutor.execute(entityClass, sql, params.toArray(), operation, Optional.empty());
        } catch (Exception e) {
            logError("Erro ao executar atualização para " + getTableName(entityClass) + ": " + e.getMessage());
        }
    }

    private List<Object> buildParamsList(T entity) {
        List<Object> params = new ArrayList<>();
        for (Field field : entity.getClass().getDeclaredFields()) {
            field.setAccessible(true);
            try {
                if (field.isAnnotationPresent(CollumnName.class) && field.isAnnotationPresent(NotNull.class)) {
                    params.add(field.get(entity));
                }
            } catch (IllegalAccessException e) {
                logError("Erro ao acessar valor do campo: " + e.getMessage());
            }
        }
        return params;
    }

    private String getTableName(Class<?> entityClass) {
        return Optional.ofNullable(entityClass.getAnnotation(TableName.class))
                .map(TableName::value)
                .orElseGet(() -> entityClass.getSimpleName().toLowerCase());
    }

    private String getIdColumn(Class<?> entityClass) {
        try {
            Field idField = entityClass.getDeclaredField("id");
            CollumnName idAnnotation = idField.getAnnotation(CollumnName.class);
            return idAnnotation.value();
        } catch (NoSuchFieldException e) {
            logError("Campo 'id' não encontrado na classe " + entityClass.getName() + ": " + e.getMessage());
            return "id";
        }
    }
}
