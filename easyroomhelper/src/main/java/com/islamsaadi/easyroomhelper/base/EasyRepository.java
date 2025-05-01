package com.islamsaadi.easyroomhelper.base;

import androidx.room.RoomDatabase;
import androidx.sqlite.db.SimpleSQLiteQuery;
import androidx.sqlite.db.SupportSQLiteQuery;
import com.islamsaadi.easyroomhelper.result.Result;

import java.util.List;

/**
 * A generic repository providing common CRUD, query, and utility methods
 * for Room entities. Extend and implement getDao() to use.
 */
public abstract class EasyRepository<T> {

    private final Class<T> entityClass;
    private final boolean useLowercase;

    /**
     * Provide your Entity-specific EasyDao<T> implementation.
     */
    protected abstract EasyDao<T> getDao();

    /**
     * Default constructor: table name == entityClass simple name.
     */
    public EasyRepository(Class<T> entityClass) {
        this(entityClass, false);
    }

    /**
     * @param entityClass     your @Entity class
     * @param useLowercase    if true, table names are lowercased
     */
    public EasyRepository(Class<T> entityClass, boolean useLowercase) {
        this.entityClass  = entityClass;
        this.useLowercase = useLowercase;
    }

    private String tableName() {
        String name = entityClass.getSimpleName();
        return useLowercase ? name.toLowerCase() : name;
    }

    private SupportSQLiteQuery buildQuery(String sql, Object[] args) {
        return new SimpleSQLiteQuery(sql, args);
    }

    // ===== CRUD =====

    public Result<Void> insert(T entity) {
        try {
            getDao().insert(entity);
            return Result.success(null);
        } catch (Exception e) {
            return Result.error(e);
        }
    }

    public Result<Void> insertAll(List<T> list) {
        try {
            getDao().insertAll(list);
            return Result.success(null);
        } catch (Exception e) {
            return Result.error(e);
        }
    }

    public Result<Void> update(T entity) {
        try {
            getDao().update(entity);
            return Result.success(null);
        } catch (Exception e) {
            return Result.error(e);
        }
    }

    public Result<Void> delete(T entity) {
        try {
            getDao().delete(entity);
            return Result.success(null);
        } catch (Exception e) {
            return Result.error(e);
        }
    }

    public Result<Integer> deleteAll() {
        return customDelete("DELETE FROM " + tableName());
    }

    // ===== Basic Queries =====

    /**
     * Retrieve all rows from the table.
     */
    public Result<List<T>> findAll() {
        try {
            String sql = "SELECT * FROM " + tableName();
            SupportSQLiteQuery q = buildQuery(sql, new Object[]{});
            return Result.success(getDao().rawQuery(q));
        } catch (Exception e) {
            return Result.error(e);
        }
    }

    /**
     * Count total rows in the table.
     */
    public Result<Integer> count() {
        try {
            String sql = "SELECT COUNT(*) FROM " + tableName();
            SupportSQLiteQuery q = buildQuery(sql, new Object[]{});
            int cnt = getDao().rawCount(q);
            return Result.success(cnt);
        } catch (Exception e) {
            return Result.error(e);
        }
    }

    // ===== Dynamic Field Queries =====

    public Result<List<T>> findByField(String field, Object value) {
        return findByField(tableName(), field, value);
    }

    public Result<List<T>> findByField(String customTable, String field, Object value) {
        try {
            String sql = "SELECT * FROM " + customTable + " WHERE " + field + " = ?";
            SupportSQLiteQuery q = buildQuery(sql, new Object[]{value});
            return Result.success(getDao().rawQuery(q));
        } catch (Exception e) {
            return Result.error(e);
        }
    }

    public Result<Integer> deleteByField(String field, Object value) {
        return deleteByField(tableName(), field, value);
    }

    public Result<Integer> deleteByField(String customTable, String field, Object value) {
        try {
            String sql = "DELETE FROM " + customTable + " WHERE " + field + " = ?";
            SupportSQLiteQuery q = buildQuery(sql, new Object[]{value});
            return Result.success(getDao().rawDelete(q));
        } catch (Exception e) {
            return Result.error(e);
        }
    }

    public Result<Integer> updateField(
            String fieldToUpdate,
            Object newValue,
            String whereField,
            Object whereValue
    ) {
        return updateField(tableName(), fieldToUpdate, newValue, whereField, whereValue);
    }

    public Result<Integer> updateField(
            String customTable,
            String fieldToUpdate,
            Object newValue,
            String whereField,
            Object whereValue
    ) {
        try {
            String sql = "UPDATE " + customTable +
                    " SET " + fieldToUpdate + " = ? WHERE " + whereField + " = ?";
            SupportSQLiteQuery q = buildQuery(sql, new Object[]{newValue, whereValue});
            return Result.success(getDao().rawUpdate(q));
        } catch (Exception e) {
            return Result.error(e);
        }
    }

    // ===== Pagination & Sorting =====

    public Result<List<T>> findWithLimit(int limit) {
        try {
            String sql = "SELECT * FROM " + tableName() + " LIMIT ?";
            SupportSQLiteQuery q = buildQuery(sql, new Object[]{limit});
            return Result.success(getDao().rawQuery(q));
        } catch (Exception e) {
            return Result.error(e);
        }
    }

    public Result<List<T>> findWithLimitOffset(int limit, int offset) {
        try {
            String sql = "SELECT * FROM " + tableName() + " LIMIT ? OFFSET ?";
            SupportSQLiteQuery q = buildQuery(sql, new Object[]{limit, offset});
            return Result.success(getDao().rawQuery(q));
        } catch (Exception e) {
            return Result.error(e);
        }
    }

    public Result<List<T>> findAllSorted(String orderBy, boolean asc) {
        try {
            String sql = "SELECT * FROM " + tableName() +
                    " ORDER BY " + orderBy + (asc ? " ASC" : " DESC");
            SupportSQLiteQuery q = buildQuery(sql, new Object[]{});
            return Result.success(getDao().rawQuery(q));
        } catch (Exception e) {
            return Result.error(e);
        }
    }

    // ===== Custom Raw Queries =====

    public Result<List<T>> customQueryList(String sql, Object... args) {
        try {
            SupportSQLiteQuery q = buildQuery(sql, args);
            return Result.success(getDao().rawQuery(q));
        } catch (Exception e) {
            return Result.error(e);
        }
    }

    public Result<T> customQuerySingle(String sql, Object... args) {
        try {
            SupportSQLiteQuery q = buildQuery(sql, args);
            List<T> list = getDao().rawQuery(q);
            return Result.success(list.isEmpty() ? null : list.get(0));
        } catch (Exception e) {
            return Result.error(e);
        }
    }

    public Result<Integer> customUpdate(String sql, Object... args) {
        try {
            SupportSQLiteQuery q = buildQuery(sql, args);
            return Result.success(getDao().rawUpdate(q));
        } catch (Exception e) {
            return Result.error(e);
        }
    }

    public Result<Integer> customDelete(String sql, Object... args) {
        try {
            SupportSQLiteQuery q = buildQuery(sql, args);
            return Result.success(getDao().rawDelete(q));
        } catch (Exception e) {
            return Result.error(e);
        }
    }
}
