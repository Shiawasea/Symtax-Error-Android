package com.example.vary.Models;

import com.example.vary.UI.PenaltyType;
import com.example.vary.UI.GameMode;

import java.util.List;

public class CurrentGameModel {
    List<CardModel> mCardModelList;
    List<TeamModel> mCommands;
    boolean mSteal;
    PenaltyType mPenalty;
    int mRoundDuration;
    GameMode mCurMode = GameMode.explain_mode;
    int currentCard = 0;
    int startRoundCard = 0;
    int currentRoundPoints = 0;
    int roundTimeLeft = 0;

    public CurrentGameModel(boolean steal, PenaltyType penalty, int roundDuration) {
        mSteal = steal;
        mPenalty = penalty;
        mRoundDuration = roundDuration;
    }

    public void setCardModelList(List<CardModel> cardModelList) {
        mCardModelList = cardModelList;
    }

    public void setCommands(List<TeamModel> teams) {
        mCommands = teams;
    }

    public int getRoundDuration() {
        return mRoundDuration;
    }

    public boolean nextGameMode() {
        if (mCurMode == GameMode.explain_mode) {
            mCurMode = GameMode.gesture_mode;
        }
        else if (mCurMode == GameMode.gesture_mode) {
            mCurMode = GameMode.one_word_mode;
        }
        else {
            return false;
        }
        return true;
    }

    public PenaltyType getPenalty() {
        return mPenalty;
    }

    public boolean getSteal() {
        return mSteal;
    }

    public GameMode getGameMode() {
        return mCurMode;
    }

    public void setCurrentAndStartCard(int curCard, int startCard) {
        currentCard = curCard;
        startRoundCard = startCard;
    }

    public int getCurrentCard() {
        return currentCard;
    }

    public int getStartRoundCard() {
        return startRoundCard;
    }

    public void setCurrentRoundPoints(int currentPoints) {
        currentRoundPoints = currentPoints;
    }

    public int getCurrentRoundPoints() {
        return currentRoundPoints;
    }

    public void setRoundTimeLeft(int time) {
        roundTimeLeft = time;
    }

    public int getRoundTimeLeft() {
        return roundTimeLeft;
    }


}
