package com.islamsaadi.easyroomhelper.base;

import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.RawQuery;
import androidx.room.Update;
import androidx.sqlite.db.SupportSQLiteQuery;

import java.util.List;

public interface EasyDao<T> {

    @Insert
    void insert(T entity);

    @Insert
    void insertAll(List<T> entities);

    @Update
    void update(T entity);

    @Delete
    void delete(T entity);

    @RawQuery
    List<T> rawQuery(SupportSQLiteQuery query);

    @RawQuery
    int rawUpdate(SupportSQLiteQuery query);

    @RawQuery
    int rawDelete(SupportSQLiteQuery query);

    @RawQuery
    int rawCount(SupportSQLiteQuery query);

}
