package com.fiap.br.repositories;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import com.fiap.br.models.enums.CRUDOperation;
import com.fiap.br.services.QueryExecutor;
import com.fiap.br.util.annotations.CollumnName;
import com.fiap.br.util.annotations.JoinTable;
import com.fiap.br.util.annotations.JoinedTableFk;
import com.fiap.br.util.annotations.TableName;
import com.fiap.br.util.interfaces.Loggable;

import jakarta.validation.constraints.NotNull;

public abstract class Repository<T> implements Loggable<String> {

    protected final QueryExecutor queryExecutor;

    public Repository(QueryExecutor queryExecutor) {
        this.queryExecutor = queryExecutor;
    }

    // ======================================
    // = CRUD =
    // ======================================
    public T findOne(Class<T> entityClass, int id) {
        String tableName = getTableName(entityClass);
        String sql = buildFindOneSQL(entityClass, tableName);
        return executeQuery(entityClass, sql, id, CRUDOperation.READ).stream().findFirst().orElse(null);
    }

    public List<T> findAll(Class<T> entityClass) {
        String tableName = getTableName(entityClass);
        String sql = buildFindAllSQL(entityClass, tableName);
        return executeQuery(entityClass, sql, null, CRUDOperation.READ);
    }

    public int save(T entity) {
        String tableName = getTableName(entity.getClass());
        String sql = buildSaveSQL(entity.getClass(), tableName);
        List<Object> params = buildParamsList(entity);
        return executeInsert(entity.getClass(), sql, params, CRUDOperation.CREATE);
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

    // ======================================
    // = QUERIES =
    // ======================================
    private String buildFindOneSQL(Class<?> entityClass, String tableName) {
        String idColumn = getIdColumn(entityClass);
        String joinClause = hasJoinAnnotation(entityClass) ? buildJoinClause(entityClass) : "";
        return String.format("SELECT * FROM %s %s WHERE %s.%s = ?", tableName, joinClause, tableName, idColumn);
    }

    private String buildFindAllSQL(Class<?> entityClass, String tableName) {
        String joinClause = hasJoinAnnotation(entityClass) ? buildJoinClause(entityClass) : "";
        return String.format("SELECT * FROM %s %s", tableName, joinClause);
    }

    protected String buildSaveSQL(Class<?> entityClass, String tableName) {
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

    // ======================================
    // = JOIN =
    // ======================================
    private String buildJoinClause(Class<?> entityClass) {
        StringBuilder joinClause = new StringBuilder();
        String entityTable = getTableName(entityClass);

        for (Field field : entityClass.getDeclaredFields()) {
            if (field.isAnnotationPresent(JoinTable.class)) {
                Class<?> joinClass = field.getAnnotation(JoinTable.class).value();
                String joinTable = getTableName(joinClass);
                String joinColumn = getIdColumn(joinClass);
                String foreignKeyColumn = getForeignKeyColumn(joinClass);

                joinClause.append(String.format(
                        "JOIN %s ON %s.%s = %s.%s ",
                        joinTable,
                        entityTable,
                        foreignKeyColumn,
                        joinTable,
                        joinColumn));
            }
        }
        return joinClause.toString();
    }

    private String getForeignKeyColumn(Class<?> joinClass) {
        for (Field field : joinClass.getDeclaredFields()) {
            if (field.isAnnotationPresent(JoinedTableFk.class)) {
                return field.getAnnotation(CollumnName.class).value();
            }
        }
        return null;
    }

    // ======================================
    // = EXECUTE =
    // ======================================
    private List<T> executeQuery(Class<T> entityClass, String sql, Integer id, CRUDOperation operation) {
        try {
            Optional<Integer> idOptional = Optional.ofNullable(id);
            return queryExecutor.execute(entityClass, sql, null, operation, idOptional);
        } catch (Exception e) {
            logError("Erro ao executar consulta para " + getTableName(entityClass) + ": " + e.getMessage());
            return new ArrayList<>();
        }
    }

    protected int executeInsert(Class<?> entityClass, String sql, List<Object> params, CRUDOperation operation) {
        try {
            return queryExecutor.executeInsert(entityClass, sql, params.toArray(), operation);
        } catch (Exception e) {
            logError("Erro ao executar inserção para " + getTableName(entityClass) + ": " + e.getMessage());
            return -1;
        }
    }

    private void executeUpdate(Class<?> entityClass, String sql, List<Object> params, CRUDOperation operation) {
        try {
            queryExecutor.execute(entityClass, sql, params.toArray(), operation, Optional.empty());
        } catch (Exception e) {
            logError("Erro ao executar atualização para " + getTableName(entityClass) + ": " + e.getMessage());
        }
    }

    // ======================================
    // = PARAMS =
    // ======================================
    private List<Object> buildParamsList(Object entity) {
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

    // ======================================
    // = OUTROS =
    // ======================================
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

    private boolean hasJoinAnnotation(Class<?> entityClass) {
        for (Field field : entityClass.getDeclaredFields()) {
            if (field.isAnnotationPresent(JoinTable.class)) {
                return true;
            }
        }
        return false;
    }
}
