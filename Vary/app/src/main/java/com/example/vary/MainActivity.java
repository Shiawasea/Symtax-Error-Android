package com.example.vary;

// TODO: дохимичить с полноэкранным режимом

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.WindowManager;
import android.widget.Toast;

import java.util.Objects;

public class MainActivity extends AppCompatActivity {

    private final DbManager.CountListener countListener = new DbManager.CountListener() {
        @Override
        public void onGetCount (final int cardCount, final int catCount) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
    //                    showStringList(allItems);
                    String countText = "Loaded " + cardCount + " cards in " + catCount + " categories";
                    Toast.makeText(getApplicationContext(), countText, Toast.LENGTH_LONG).show();
                }
            });
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_main);
        StartFragment fragment = (StartFragment) getSupportFragmentManager().findFragmentById(R.id.container);
        if (fragment == null) {
            fragment = new StartFragment();
            fragment.setCallback(this::callback);
            fragment.setWidth(getWidth());
            getSupportFragmentManager()
                    .beginTransaction()
                    .add(R.id.container, fragment)
                    .commit();
        } else {
            fragment.checkContinueButtonVisibility();
        }
        final DbManager manager = DbManager.getInstance(this);
//
        manager.getCount(countListener);
    }


    public int getWidth() {
        DisplayMetrics displayMetrics = getApplicationContext()
                .getResources()
                .getDisplayMetrics();
        return (int) (displayMetrics.widthPixels / displayMetrics.density);
    }

    void callback(GameActions type) {
        switch (type) {
            case new_game_action:
                startNewGame();
                break;
            case open_settings:
                openSettings();
                break;
            case continue_game_action:
                continueGame();
                break;
            case open_rules:
                showRules();
                break;
            case open_game_settings:
                openGameSettings();
                break;
            case start_game_process:
                startGameProcess();
            default:
                break;
        }
    }

    void openGameSettings() {
        if (!Objects.requireNonNull(getSupportFragmentManager()
                .findFragmentById(R.id.container))
                .getClass()
                .equals(GameSettingsFragment.class)) {
            GameSettingsFragment fragment = new GameSettingsFragment();
            fragment.setCallback(this::callback);
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.container, fragment)
                    .addToBackStack(null)
                    .commit();
        }
    }

    void startGameProcess() {
        if (!Objects.requireNonNull(getSupportFragmentManager()
                .findFragmentById(R.id.container))
                .getClass()
                .equals(OnGameFragment.class)) {
            OnGameFragment fragment = new OnGameFragment();
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.container, fragment)
                    .addToBackStack(null)
                    .commit();
        }
    }

    void continueGame() {
        if (!Objects.requireNonNull(getSupportFragmentManager()
                .findFragmentById(R.id.container))
                .getClass()
                .equals(SetCommandsFragment.class)) {
            SetCommandsFragment fragment = new SetCommandsFragment();
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.container, fragment)
                    .addToBackStack(null)
                    .commit();
        }
    }

    void startNewGame() {
        if (CommandsSource
                .getInstance()
                .getRemoteData()
                .size() != 0) {
            CommandsSource
                    .getInstance()
                    .removeCommands();
        }
        if (!Objects.requireNonNull(getSupportFragmentManager()
                .findFragmentById(R.id.container))
                .getClass()
                .equals(SetCommandsFragment.class)) {
            SetCommandsFragment fragment = new SetCommandsFragment();
            fragment.setCallback(this::callback);
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.container, fragment)
                    .addToBackStack(null)
                    .commit();
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if (Objects.requireNonNull(getSupportFragmentManager()
                .findFragmentById(R.id.container))
                .getClass()
                .equals(StartFragment.class)) {
            StartFragment fragment = (StartFragment) getSupportFragmentManager()
                    .findFragmentById(R.id.container);
            fragment.checkContinueButtonVisibility();
        }
    }

    void showRules() {
        // Запуск фрагмента? всплывающего окна? с правилами игры
    }

    void openSettings() {
        if (!Objects.requireNonNull(getSupportFragmentManager()
                .findFragmentById(R.id.container))
                .getClass()
                .equals(SetCommandsFragment.class)) {
            SettingsFragment fragment = new SettingsFragment();
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.container, fragment)
                    .addToBackStack(null)
                    .commit();
        }
    }
}