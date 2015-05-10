package net.bodemann.threegames;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.widget.EditText;

import net.bodemann.threegames.fragments.BoatFragment;
import net.bodemann.threegames.fragments.FlowerFragment;
import net.bodemann.threegames.fragments.GameFragment;
import net.bodemann.threegames.fragments.GameWonFragment;
import net.bodemann.threegames.fragments.MemoryFragment;
import net.bodemann.threegames.fragments.SelectionFragment;
import net.bodemann.threegames.listener.GameListener;
import net.bodemann.threegames.models.Medal;

import java.util.Map;

import static net.bodemann.threegames.constants.ConstantPreferences.Keys.BOATS_LEVEL_KEY;
import static net.bodemann.threegames.constants.ConstantPreferences.Keys.FLOWER_LEVEL_KEY;
import static net.bodemann.threegames.constants.ConstantPreferences.Keys.MEMORY_LEVEL_KEY;
import static net.bodemann.threegames.constants.ConstantPreferences.NAME;
import static net.bodemann.threegames.fragments.SelectionFragment.SelectionResultListener;


public class MainActivity extends Activity {

    private SelectionResultListener mSelectionListener = new SelectionResultListener() {
        @Override
        public void onFlowerGameButtonClicked() {
            GameFragment fragment = new FlowerFragment();
            fragment.setListener(new GameListener() {
                @Override
                public void onGameWon(int moves, int expected, Medal medal) {
                    final int level = mPreferences.getInt(FLOWER_LEVEL_KEY.toString(), 0);
                    mPreferences.edit().putInt(FLOWER_LEVEL_KEY.toString(), level + 1).apply();

                    final int medalCount = mPreferences.getInt(medal.name(), 0);
                    mPreferences.edit().putInt(medal.name(), medalCount + 1).apply();

                    showWonScreen(moves, expected, medal, new Runnable() {
                                @Override
                                public void run() {
                                    getFragmentManager().popBackStack();
                                    onFlowerGameButtonClicked();
                                }
                            }
                    );
                }
            });

            getFragmentManager().beginTransaction()
                    .replace(R.id.main_fragment_container, fragment)
                    .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                    .addToBackStack(fragment.getBackStackHint())
                    .commit();
        }

        @Override
        public void onMemoryGameButtonClicked() {
            GameFragment fragment = new MemoryFragment();
            fragment.setListener(new GameListener() {
                @Override
                public void onGameWon(int moves, int expected, Medal medal) {
                    final int level = mPreferences.getInt(MEMORY_LEVEL_KEY.toString(), 0);
                    mPreferences.edit().putInt(MEMORY_LEVEL_KEY.toString(), level + 1).apply();

                    final int medalCount = mPreferences.getInt(medal.name(), 0);
                    mPreferences.edit().putInt(medal.name(), medalCount + 1).apply();

                    showWonScreen(moves, expected, medal, new Runnable() {
                                @Override
                                public void run() {
                                    getFragmentManager().popBackStack();
                                    onMemoryGameButtonClicked();
                                }
                            }
                    );
                }
            });

            getFragmentManager().beginTransaction()
                    .replace(R.id.main_fragment_container, fragment)
                    .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                    .addToBackStack(fragment.getBackStackHint())
                    .commit();
        }

        @Override
        public void onBoatGameButtonClicked() {
            GameFragment fragment = new BoatFragment();
            fragment.setListener(new GameListener() {
                @Override
                public void onGameWon(int moves, int expected, Medal medal) {
                    final int level = mPreferences.getInt(BOATS_LEVEL_KEY.toString(), 0);
                    mPreferences.edit().putInt(BOATS_LEVEL_KEY.toString(), level + 1).apply();

                    final int medalCount = mPreferences.getInt(medal.name(), 0);
                    mPreferences.edit().putInt(medal.name(), medalCount + 1).apply();

                    showWonScreen(moves, expected, medal, new Runnable() {
                                @Override
                                public void run() {
                                    getFragmentManager().popBackStack();
                                    onBoatGameButtonClicked();
                                }
                            }
                    );
                }
            });

            getFragmentManager().beginTransaction()
                    .replace(R.id.main_fragment_container, fragment)
                    .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                    .addToBackStack(fragment.getBackStackHint())
                    .commit();
        }

        @Override
        public void onCheatMenuRequested() {
            displayCheatingAlert();
        }

        @Override
        public void onInfoClicked() {
            showInfoDialog();
        }
    };

    private SharedPreferences mPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mPreferences = MainActivity.this.getSharedPreferences(NAME, Context.MODE_PRIVATE);

        createFragments();
    }

    private void createFragments() {
        if (findViewById(R.id.main_fragment_container) != null) {
            SelectionFragment fragment = new SelectionFragment();
            fragment.setListener(mSelectionListener);

            getFragmentManager().beginTransaction()
                    .replace(R.id.main_fragment_container, fragment)
                    .commit();
        }
    }

    private void displayCheatingAlert() {
        final SharedPreferences preferences = getSharedPreferences(NAME, Context.MODE_PRIVATE);

        final Map<String, ?> all = preferences.getAll();
        final String[] items = new String[all.size()];
        final String template = getString(R.string.configuration_template);
        int i = 0;
        for (String key : all.keySet()) {
            final Object untypedValue = all.get(key);
            final String value;
            if (untypedValue instanceof String) {
                value = preferences.getString(key, "");
            } else if (untypedValue instanceof Integer) {
                value = "" + preferences.getInt(key, -1);
            } else {
                value = "unknown";
            }

            items[i++] = String.format(template, key.toLowerCase().replace('_', ' '), value);
        }

        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.configuration_title);
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                final AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                builder.setTitle(R.string.configuration_title);
                final EditText editText = new EditText(MainActivity.this);
                final String[] split = items[which].split(":");

                if (split.length == 2) {
                    editText.setText(split[1].trim());
                    builder.setView(editText);
                    builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            final String key = split[0].toUpperCase().replace(' ', '_');
                            final String value = editText.getText().toString();

                            final Object untypedValue = preferences.getAll().get(key);
                            if (untypedValue instanceof String) {
                                preferences.edit().putString(key, value).apply();
                            } else if (untypedValue instanceof Integer) {
                                preferences.edit().putInt(key, Integer.parseInt(value)).apply();
                            }
                        }
                    });
                }
                builder.show();
            }
        });

        builder.show();
    }

    private void showWonScreen(int moves, int expected, Medal medal, final Runnable onDoneRunnable) {
        GameWonFragment fragment = new GameWonFragment();
        fragment.setScore(moves, expected, medal);
        fragment.setListener(new GameWonFragment.Listener() {
            @Override
            public void onDone() {
                onDoneRunnable.run();
            }
        });
        fragment.show(getFragmentManager(), GameWonFragment.FRAGMENT_TAG);
    }

    private void showInfoDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setTitle(getString(R.string.info_title, getString(R.string.app_name)))
                .setItems(R.array.info_credits, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        uriForInfoItemRequested(which);
                    }
                })
                .setIcon(R.drawable.ic_launcher)
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                })
                .show();
    }

    private void uriForInfoItemRequested(int which) {
        final String selectedUri = getResources().getStringArray(R.array.info_uri)[which];
        final Uri uri = Uri.parse(selectedUri);
        startActivity(new Intent(Intent.ACTION_VIEW, uri));
    }

}
