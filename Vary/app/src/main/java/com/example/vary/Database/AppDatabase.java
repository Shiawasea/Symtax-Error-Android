package com.example.vary.Database;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.example.vary.Models.CardModel;
import com.example.vary.Models.CategoryModel;

@Database(entities = {CardModel.class, CategoryModel.class}, version = 1)
public abstract class AppDatabase extends RoomDatabase {

    private static AppDatabase instance;

    static synchronized AppDatabase getInstance(Context context) {
        if (instance == null) {
            instance = create(context);
        }
        return instance;
    }


    public abstract CardModelDao getCardDao();

    public abstract CategoryModelDao getCategoryDao();


    private static AppDatabase create(final Context context) {
        return Room.databaseBuilder(
                context,
                AppDatabase.class,
                "card_repo.db")
                .build();
    }
}