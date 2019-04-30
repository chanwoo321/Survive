package com.example.pathingthroughcs;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import org.w3c.dom.Text;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
public class ActualGameActivity extends AppCompatActivity {


    private ProgressBar fProgressBar;
    private ProgressBar wProgressBar;
    private ProgressBar hProgressBar;
    private ProgressBar pProgressBar;

    private static int food = 100;
    private static int water = 100;
    private static int health = 100;
    private static int location = 0;
    private static int endLocation = 14;
    private static int numFood = 3;
    private static int numWater = 3;

    private static String text;

    /**
     * Some older devices needs a small delay between UI widget updates
     * and a change of the status and navigation bar.
     */
    private static final int UI_ANIMATION_DELAY = 300;


    private final Handler mHideHandler = new Handler();
    private View mContentView;

    private final Runnable mHidePart2Runnable = new Runnable() {
        @SuppressLint("InlinedApi")
        @Override
        public void run() {
            // Delayed removal of status and navigation bar
            mContentView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE
                    | View.SYSTEM_UI_FLAG_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                    | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
        }
    };
    private View mControlsView;

    private final Runnable mShowPart2Runnable = new Runnable() {
        @Override
        public void run() {
            // Delayed display of UI elements
            ActionBar actionBar = getSupportActionBar();
            if (actionBar != null) {
                actionBar.show();
            }
            mControlsView.setVisibility(View.GONE);
            System.out.print("got to mshowpart2runnable");
        }
    };

    private final Runnable mHideRunnable = new Runnable() {
        @Override
        public void run() {
            hide();
        }
    };
    /**
     * Touch listener to use for in-layout UI controls to delay hiding the
     * system UI. This is to prevent the jarring behavior of controls going away
     * while interacting with activity UI.
     */
    /**
    private final View.OnTouchListener mDelayHideTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            if (AUTO_HIDE) {
                delayedHide(AUTO_HIDE_DELAY_MILLIS);
            }
            return false;
        }
    };
     **/

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.actual_game_activity);
        this.setTitle("");

        endLocation = (int) (12 + Math.random() * 5);

        final TextView hunger = findViewById(R.id.hunger);
        final TextView thirst = findViewById((R.id.thirst));
        final TextView healthText = findViewById(R.id.health);
        final TextView event = findViewById(R.id.event);
        text = ("The game begins. You find yourself lost in the woods. With limited supplies," +
                " you set off to try to find your way out.");
        final TextView waterBox = findViewById(R.id.numWater);
        final TextView foodBox = findViewById(R.id.numFood);

        fProgressBar = findViewById(R.id.food);
        wProgressBar = findViewById(R.id.water);
        hProgressBar = findViewById(R.id.healthBar);
        pProgressBar = findViewById(R.id.progress);

        // setup the eating button
        Button eat = findViewById(R.id.eat);
        eat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                eat();
                event.setText(text);
            }
        });

        // setup the drinking button
        Button drink = findViewById(R.id.drink);
        drink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                drink();
                event.setText(text);
            }
        });

        // setup the move button
        Button move = findViewById(R.id.forward);
        move.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                move();
                event.setText(text);
            }
        });

        // Deals with hunger
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (food > 0 && water > 0) {
                    if (food > 100) { food = 100; }
                    if (water > 100) { water = 100; }
                    //food--;//has hunger increase over time automatically
                    //water--;//water decreasing
                    android.os.SystemClock.sleep(400); //makes it wait before ticking
                    mHideHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            fProgressBar.setProgress(food);
                            pProgressBar.setProgress((int) (100 * ((double) location / endLocation)));
                            //put code here to do the number showing for food
                            hunger.setText("Hunger: " + food + "%");
                            wProgressBar.setProgress(water);
                            thirst.setText("Thirst: " + water + "%");
                            hProgressBar.setProgress(health);
                            healthText.setText("Health: " + health + "%");
                            event.setText(text);
                            foodBox.setText("Food: " + numFood);
                            waterBox.setText("Water: " + numWater);
                        }
                    });
                }
                startActivity(new Intent(ActualGameActivity.this, GameOver.class));
                mHideHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        //foodText.setVisibility(View.VISIBLE);
                        //death due to starvation here?
                    }
                });
            }
        }).start();

        //mContentView = findViewById(R.id.fullscreen_content);
        //mControlsView = findViewById(R.id.game_fullscreen);
        //mControlsView.setVisibility(View.GONE);

        //setContentView(R.layout.actual_game_activity);
        //runOnUiThread(mHidePart2Runnable);
    }
/**
    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

        // Trigger the initial hide() shortly after the activity has been
        // created, to briefly hint to the user that UI controls
        // are available.
        delayedHide(100);
    }
**/

    private void eat() {
        if (numFood == 0) {
            text = "Since you do not have food, you can not eat.";
            return;
        }
        food += 30;
        water -= 2 + Math.random() * 8;
        numFood--;
        Stats.foodEaten++;
        changeHealth(-30);
        text = "You eat some of the food in your supplies. It fills you up but leaves you a little" +
                " bit more thirsty.";
    }

    private void drink() {
        if (numWater == 0) {
            text = "Since you do not have water, you can not drink.";
            return;
        }
        water += 30;
        food -= 2 + Math.random() * 8;
        numWater--;
        Stats.waterDrank++;
        changeHealth(-10);
        text = "You drink some water. It quenches your thirst but leaves you " +
                "a little bit more hungry.";
    }

    private void move() {
        location++;
        if (location >= endLocation) {
            startActivity(new Intent(ActualGameActivity.this, WinScreen.class));
            return;
        }
        food -= (int) 7 + (6 * Math.random());
        water -= (int) 7 + (6 * Math.random());
        if (Math.random() < .4) {
            Stats.encounters++;
            startActivity(new Intent(ActualGameActivity.this, Encounter.class));
            text = "AN ENEMY APPEARS!";
        } else if (Math.random() < .8) {
            randomEvent();
        } else {
            text = "You move forward.";
        }
        if (endLocation - location == 2) {
            text += " You get a strange feeling you are close to the end.";
        }
    }

    private void randomEvent() {
        double prob = Math.random();
        Stats.random++;
        if (prob < .2) {
            text = "A tree breaks and falls on you. You take damage.";
            int damage = (int) (getHealth() * .3);
            if (damage == getHealth()) {
                changeHealth(damage - 1);
            } else {
                changeHealth(damage);
            }
        } else if (prob < .25) {
            text = "You come across a river. You drink up and quench your thirst.";
            water = 100;
        } else if (prob < .3) {
            text = "You find a bunch of random food. You eat until you are full.";
            food = 100;
        } else if (prob < .35) {
            text = "You find some health supplies randomly scattered. You heal up.";
            health = 100;
        } else if (prob < .45) {
            text = "You find a sharper weapon. You can now do more damage in fights.";
            Encounter.damageBoost += 5;
        } else if (prob < .5) {
            text = "You find a food pack. You bring it with you.";
            numFood++;
        } else if (prob < .55) {
            text = "You find a bottle of water. You bring it with you.";
            numWater++;
        } else if (prob < .65) {
            text = "The ground is very flat. You move extra far";
            location++;
        } else if (prob < .75){
            text = "After moving, you realized you accidentally backtracked. No progress was made.";
            location--;
        } else {
            text = "You make your way forward.";
            Stats.random--;
        }
    }

    public static int getFood() {
        return food;
    }

    public static int getNumFood() {
        return numFood;
    }

    public static int getWater() {
        return water;
    }

    public static int getNumWater() { return numWater; }

    public static int getLocation() {
        return location;
    }

    public static int getEndLocation() {
        return endLocation;
    }

    public static int getHealth() { return health; }

    public static void addFood(int numFoodAdded) {
        numFood += numFoodAdded;
    }

    public static void addWater(int numWaterAdded) {
        numWater += numWaterAdded;
    }

    public static void setEvent(String theEvent) {
        text = theEvent;
    }

    public static void changeHealth(int healthLost) {
        int newHealth = health - healthLost;
        if (newHealth < 0) {
            newHealth = 0;
        } else if (newHealth > 100) {
            newHealth = 100;
        }
        health = newHealth;
    }

    //resets the game for repeat plays
    public static void reset() {
        food = 100;
        water = 100;
        health = 100;
        location = 0;
        numWater = 3;
        numFood = 3;
    }

    private void hide() {
        // Hide UI first
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }
        mControlsView.setVisibility(View.GONE);

        // Schedule a runnable to remove the status and navigation bar after a delay
        mHideHandler.removeCallbacks(mShowPart2Runnable);
        mHideHandler.postDelayed(mHidePart2Runnable, UI_ANIMATION_DELAY);
    }

    /**
    @SuppressLint("InlinedApi")
    private void show() {
        // Show the system bar
        mContentView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION);
        mVisible = true;

        // Schedule a runnable to display UI elements after a delay
        mHideHandler.removeCallbacks(mHidePart2Runnable);
        mHideHandler.postDelayed(mShowPart2Runnable, UI_ANIMATION_DELAY);
    }
    **/

    /**
     * Schedules a call to hide() in delay milliseconds, canceling any
     * previously scheduled calls.
     */


    private void delayedHide(int delayMillis) {
        mHideHandler.removeCallbacks(mHideRunnable);
        mHideHandler.postDelayed(mHideRunnable, delayMillis);
    }

    public void onBackPressed(){ }
}

