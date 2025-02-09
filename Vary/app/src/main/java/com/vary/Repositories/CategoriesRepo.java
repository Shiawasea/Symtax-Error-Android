package com.vary.Repositories;

import android.content.Context;
import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.vary.Database.DbManager;
import com.vary.UI.CardCallback;
import com.vary.UI.LoadDataCallback;
import com.vary.Models.CardModel;
import com.vary.Models.CategoryModel;
import com.vary.Network.CategoriesNetworkService;
import com.vary.R;
import com.vary.UI.PenaltyType;
import com.vary.UI.SetDataCallback;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class CategoriesRepo implements SetDataCallback {
    private static CategoriesNetworkService categoriesNetworkService;
    private final static CardsRepo mCardsRepo = CardsRepo.getInstance();
    private static CategoriesRepo sInstance = null;
    private final static MutableLiveData<List<CategoryModel>> mCategories = new MutableLiveData<>();
    private DbManager dbManager = null;
    private static int version;

    LoadDataCallback mCallback;

    private final DbManager.CategoryRepositoryListener categoryRepositoryListener = categoryModels -> {
        if (categoryModels.size() > 0) {
            mCategories.postValue(categoryModels);
            updateVersion(categoryModels);
        }
        else {
            version = -1;
        }
    };

    public void getVersion() {
        categoriesNetworkService.getVersion(this);
    }

    public void setLoadCallback(LoadDataCallback callback) {
        mCallback = callback;
    }

//    public CategoryModel getCategory(int index) {
//        if (index >= mCategories.getValue().size()) {
//            loadCategoriesFromDatabase(index);
//        }
//        return mCategories.getValue().get(index);
//    }

    public ArrayList<String> getCategoriesNames() {
        ArrayList<String> names = new ArrayList<>();

        for (int i = 0; i < getCategoriesSize(); i++) {
            names.add(Objects.requireNonNull(mCategories
                    .getValue())
                    .get(i)
                    .getName());
        }

        return names;
    }

    public void setDbManager(Context context) {
        dbManager = DbManager.getInstance(context);
        dbManager.setCategoryRepositoryListener(categoryRepositoryListener);
        mCardsRepo.setDbManager(dbManager);
        loadCategoriesFromDatabase();
    }

    public void setNetworkService(Context context) {
        categoriesNetworkService = CategoriesNetworkService.getInstance(context.getResources().getString(R.string.base_url));
//        getNewCategories();
    }

    public LiveData<List<CategoryModel>> getCategories() {
        return mCategories;
    }

    public void fillCards(int index, int amount) {
        CategoryModel category = Objects.requireNonNull(mCategories.getValue()).get(index);
        mCardsRepo.fillCards(category.getName(), amount);
    }

    public LiveData<List<CardModel>> getCards() {
        return mCardsRepo.getCards();
    }

    public void mixCards() {
        mCardsRepo.mixCards();
    }

    public void setCards(List<CardModel> cards) {
        mCardsRepo.setCards(cards);
    }

    public void declineCard() {
        mCardsRepo.declineCard();
    }

    public void answerCard(int team) {
        mCardsRepo.answerCard(team);
    }

    public void changeAnswerState(int pos) {
        mCardsRepo.changeAnswerState(pos);
    }

    public void setAnsweredTeam(int team) {
        mCardsRepo.setAnsweredTeam(team);
    }

    public boolean getAnswerState(int pos) {
        return mCardsRepo.getAnswerState(pos);
    }

    public String getUsedCardByPosition(int pos) {
        return mCardsRepo.getUsedCardByPosition(pos);
    }

    public int getAmountOfUsedCards() {
        return mCardsRepo.getAmountOfUsedCards();
    }

    public int getAnsweredTeam(int pos) {
        return mCardsRepo.getAnsweredTeam(pos);
    }

    public void newRoundMix() {
        mCardsRepo.newRoundMix();
    }

    public String getCard() {
        return mCardsRepo.getCard();
    }

    public boolean newRoundRequired() {
        return mCardsRepo.newRoundRequiredAndSort();
    }

    public int getCardsLeft() {
        return mCardsRepo.getCardsLeft();
    }

    public int getCurrentPosition() {
        return mCardsRepo.getCurrentPosition();
    }

    public void setCurrentPosition(int position) {
        mCardsRepo.setCurrentPosition(position);
    }

    public int getStartRoundPosition() {
        return mCardsRepo.getStartRoundPosition();
    }

    public void setStartRoundPosition(int position) {
        mCardsRepo.setStartRoundPosition(position);
    }

    public static synchronized CategoriesRepo getInstance() {
        if (sInstance == null) {
            Log.d("GetInstance", "created ");
            sInstance = new CategoriesRepo();
        }
        boolean voidCat = mCategories.getValue() == null;
        Log.d("GetInstance", "Categories are void inside " + voidCat);
        return sInstance;
    }

    public void onLoaded(List<CategoryModel> categories) { //TODO вернуть из getNewCategories
        if (categories != null) {
            Log.d("network", "add " + categories.size());
            loadCategoriesToDatabase(categories);
            addCategories(categories);
            mCallback.onLoad(null, false);
        } else {
            Log.d("Sad", "Wrong data back");
        }
    }

    public void onLoaded(Integer sVersion) {
        Log.d("Network", "We've got network ver: " + sVersion);
        if (version < sVersion) {
            getNewCategories();
        }
        mCallback.onLoad(null, false);
    }

    public void onLoaded(Throwable t) {
        Log.d("Network", "Version = " + version);
        if (version == -1) {
            mCallback.onLoad(t, true);
        }
        else
            mCallback.onLoad(null, false);
    }

    private void addCategories(List<CategoryModel> categories) {
        List<CategoryModel> categoriesCapture = mCategories.getValue();
        if (categoriesCapture == null) {
            categoriesCapture = new ArrayList<>();
        }
        for (CategoryModel category : categories) {
            CategoryModel oldCategory = null;
            if (categoriesCapture.size() > 0) {
                for (CategoryModel existingCategoryIter : categoriesCapture) {
                    if (category.getName().equals(existingCategoryIter.getName())) {
                        oldCategory = existingCategoryIter;
                    }
                }
            }
            if (oldCategory == null)
                categoriesCapture.add(category);
        }
        mCategories.postValue(categoriesCapture);
        updateVersion();
    }

    private void updateVersion() {
        List<CategoryModel> categoriesValue = mCategories.getValue();
        if (categoriesValue != null)
            for (CategoryModel category : categoriesValue) {
                int categoryVersion = category.getVersion();
                if (categoryVersion > version)
                    version = categoryVersion;
            }
        else
            version = -1;
    }

    private void updateVersion(List<CategoryModel> newCategories) {
        for (CategoryModel category : newCategories) {
            int categoryVersion = category.getVersion();
            if (categoryVersion > version)
                version = categoryVersion;
        }
    }

    private void loadCategoriesFromDatabase() {
        dbManager.getCategoriesNoCards(); //TODO если их нет - грузить с нетворка + обыграть на UI загрузку
        Log.d("Network", "Version ???? " + version);
    }

// --Commented out by Inspection START (04.06.2021, 14:25):
//    public void getCount(DbManager.CountListener listener) {
//        dbManager.getCount(listener);
//    }
// --Commented out by Inspection STOP (04.06.2021, 14:25)

    public void getNewCategories() { //TODO переделать
        //откуда брать версию? отсюда же
//        loadCategoriesFromDatabase();
        categoriesNetworkService.getNewCategories(version, this);
    }

    public int countPoints(PenaltyType penalty) {
        return mCardsRepo.countPoints(penalty);
    }

// --Commented out by Inspection START (04.06.2021, 14:25):
//    public int getAmountOfCards() {
//        return mCardsRepo.getAmountOfCards();
//    }
// --Commented out by Inspection STOP (04.06.2021, 14:25)

    private void loadCategoriesToDatabase(List<CategoryModel> categoryModelList) {
        dbManager.update(categoryModelList);
    }

    public void setCardsCallback(CardCallback callback){
        mCardsRepo.setCardCallback(callback);
    }


    public int getCategoriesSize() {
        if (mCategories.getValue() != null) {
            return mCategories
                    .getValue()
                    .size();
        }
        return 0;
    }

}
