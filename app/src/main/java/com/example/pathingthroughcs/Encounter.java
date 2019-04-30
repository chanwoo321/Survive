package com.example.pathingthroughcs;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.os.Handler;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.animation.ObjectAnimator;
import android.widget.ProgressBar;
import android.widget.Toast;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
public class Encounter extends AppCompatActivity {

    private static int enemyHealth = 100;
    private static boolean canAttack = true;
    public static int damageBoost = 0;

    /**
     * Whether or not the system UI should be auto-hidden after
     * {@link #AUTO_HIDE_DELAY_MILLIS} milliseconds.
     */
    private static final boolean AUTO_HIDE = true;

    /**
     * If {@link #AUTO_HIDE} is set, the number of milliseconds to wait after
     * user interaction before hiding the system UI.
     */
    private static final int AUTO_HIDE_DELAY_MILLIS = 3000;

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

            // Note that some of these constants are new as of API 16 (Jelly Bean)
            // and API 19 (KitKat). It is safe to use them, as they are inlined
            // at compile-time and do nothing on earlier devices.
            //mContentView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE
                    //| View.SYSTEM_UI_FLAG_FULLSCREEN
                    //| View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    //| View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                    //| View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    //| View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
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
            mControlsView.setVisibility(View.VISIBLE);
        }
    };
    private boolean mVisible;
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
    private final View.OnTouchListener mDelayHideTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            if (AUTO_HIDE) {
                delayedHide(AUTO_HIDE_DELAY_MILLIS);
            }
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_encounter);

        mVisible = true;
        mControlsView = findViewById(R.id.fullscreen_content_controls);
        mContentView = findViewById(R.id.fullscreen_content);


        // Set up the user interaction to manually show or hide the system UI.
        /*mContentView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                toggle();
            }
        });*/ //DELETE???

        // Upon interacting with UI controls, delay any scheduled hide()
        // operations to prevent the jarring behavior of controls going away
        // while interacting with the UI.

        //new stuff from here
        //sets up imageviews
        final ImageView enemy = findViewById(R.id.enemy);
        final ImageView player = findViewById(R.id.player);

        double random = Math.random();
        if (random < .25) {
            enemy.setImageResource(R.drawable.bear);
        } else if (random < .5) {
            enemy.setImageResource(R.drawable.fox);
        } else if (random < .75) {
            enemy.setImageResource(R.drawable.tiger);
        } else {
            enemy.setImageResource(R.drawable.robber);
        }

        final ProgressBar healthBar = findViewById(R.id.playerHealthBar);
        healthBar.setProgress(ActualGameActivity.getHealth());
        final ProgressBar enemyHealthBar = findViewById(R.id.enemyHealthBar);
        enemyHealthBar.setProgress(enemyHealth);

        //Button for running away
        Button run = findViewById(R.id.run);
        run.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                if (!(canAttack)) {
                    return;
                }
                canAttack = false;
                Stats.runAttempts++;
                //add stuff here for gameplay elements, but should end fight
                AnimatorSet set = new AnimatorSet();
                final ObjectAnimator enemyAttack = ObjectAnimator.ofFloat(enemy, "translationY", 340f);
                enemyAttack.setDuration(300);
                ObjectAnimator enemyRetreat = ObjectAnimator.ofFloat(enemy, "translationY", 0f);
                enemyRetreat.setDuration(1000);
                ObjectAnimator wait = ObjectAnimator.ofFloat(player, "translationX", 0f);
                wait.setDuration(1000);
                ObjectAnimator playerRun = ObjectAnimator.ofFloat(player, "translationY", 950f);
                playerRun.setDuration(1000);
                ObjectAnimator wait2 = ObjectAnimator.ofFloat(player, "translationX", 0f);
                wait2.setDuration(1000);
                ObjectAnimator wait3 = ObjectAnimator.ofFloat(player, "translationX", 0f);
                wait3.setDuration(1000);

                if (Math.random() < .55) {
                    Toast.makeText(getApplicationContext(), "Run away attempt successful!",
                            + 2000).show();
                    set.play(playerRun);
                    set.play(wait).after(playerRun);
                    set.start();
                    wait.addListener(new Animator.AnimatorListener() {
                        public void onAnimationStart(Animator animation) { } //is useless
                        @Override
                        public void onAnimationEnd(Animator animation) { //stuff to do when animation ends
                            reset();
                            Stats.runSuccesses++;
                            ActualGameActivity.setEvent("You ran away successfully.");
                            finish();
                        }
                        public void onAnimationCancel(Animator animation) { }//is useless
                        public void onAnimationRepeat(Animator animation) { }//is useless
                    });
                } else { //the enemy Attacks!
                    Toast.makeText(getApplicationContext(), "Run away attempt failed!",
                            + 2000).show();
                    Stats.runFailures++;
                    final int healthChange = ((int) (10 + Math.random() * 20));
                    set.play(enemyAttack);
                    set.play(enemyRetreat).after(enemyAttack);
                    set.play(wait).after(enemyRetreat);
                    if (ActualGameActivity.getHealth() - healthChange <= 0) {
                        set.play(playerRun).after(wait);
                        set.play(wait3).after(playerRun);
                    }
                    set.start();

                    enemyAttack.addListener(new Animator.AnimatorListener() {
                        public void onAnimationStart(Animator animation) { } //is useless
                        @Override
                        public void onAnimationEnd(Animator animation) { //stuff to do when animation ends
                            ActualGameActivity.changeHealth(healthChange);
                            healthBar.setProgress(ActualGameActivity.getHealth());
                        }
                        public void onAnimationCancel(Animator animation) { }//is useless
                        public void onAnimationRepeat(Animator animation) { }//is useless
                    });

                    set.addListener(new Animator.AnimatorListener() {
                        public void onAnimationStart(Animator animation) { } //is useless
                        @Override
                        public void onAnimationEnd(Animator animation) { //stuff to do when animation ends
                            canAttack = true;
                            if (ActualGameActivity.getHealth() <= 0) {
                                reset();
                                startActivity(new Intent(Encounter.this, GameOver.class));
                            }
                        }
                        public void onAnimationCancel(Animator animation) { }//is useless
                        public void onAnimationRepeat(Animator animation) { }//is useless
                    });

                }
            }
            //done with run button
        });

        //button for blocking
        final Button bait = findViewById(R.id.bait);
        bait.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                if (!canAttack) {
                    return;
                }
                if (ActualGameActivity.getNumFood() <= 0) {
                    Toast.makeText(getApplicationContext(), "You do not have food to bait with.",
                            + 3000).show();
                    return;
                }
                canAttack = false;
                ActualGameActivity.addFood(-1);
                Toast.makeText(getApplicationContext(), "Bait successful!",
                        + 3000).show();
                Stats.numBaits++;
                AnimatorSet bouncer = new AnimatorSet();
                ObjectAnimator playerRun = ObjectAnimator.ofFloat(player, "translationY", 950f);
                playerRun.setDuration(1000);
                ObjectAnimator wait = ObjectAnimator.ofFloat(player, "translationX", 0f);
                wait.setDuration(1000);
                bouncer.play(playerRun);
                bouncer.play(wait).after(playerRun);
                bouncer.start();
                bouncer.addListener(new Animator.AnimatorListener() {
                    public void onAnimationStart(Animator animation) { } //is useless
                    @Override
                    public void onAnimationEnd(Animator animation) { //stuff to do when animation ends
                        canAttack = true;
                        reset();
                        String theEvent = "You ran away successfully using 1 food as bait.";
                        if (ActualGameActivity.getEndLocation() - ActualGameActivity.getLocation() == 2) {
                            theEvent += " You get a strange feeling you are close to the end.";
                        }
                        ActualGameActivity.setEvent(theEvent);
                        finish();
                    }
                    public void onAnimationCancel(Animator animation) { }//is useless
                    public void onAnimationRepeat(Animator animation) { }//is useless
                });
            }
        });

        //button for attacking
        Button attack = findViewById(R.id.attack);
        attack.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                if (!(canAttack)) {
                    return;
                }
                canAttack = false;
                Stats.attacksGiven++;
                //sets up bouncer (set of animations to play)
                AnimatorSet bouncer = new AnimatorSet();

                //sets up animations
                ObjectAnimator userAttack = ObjectAnimator.ofFloat(player, "translationY", -340f);
                userAttack.setDuration(300);
                final ObjectAnimator userRetreat = ObjectAnimator.ofFloat(player, "translationY", 0f);
                userRetreat.setDuration(1000);
                ObjectAnimator wait = ObjectAnimator.ofFloat(player, "translationX", 0f);
                wait.setDuration(1000);
                final ObjectAnimator enemyAttack = ObjectAnimator.ofFloat(enemy, "translationY", 340f);
                enemyAttack.setDuration(300);
                ObjectAnimator enemyRetreat = ObjectAnimator.ofFloat(enemy, "translationY", 0f);
                enemyRetreat.setDuration(1000);
                ObjectAnimator enemyDisappear = ObjectAnimator.ofFloat(enemy, "translationY", -600f);
                enemyDisappear.setDuration(1000);
                ObjectAnimator wait2 = ObjectAnimator.ofFloat(player, "translationX", 0f);
                wait2.setDuration(1000);
                ObjectAnimator playerRun = ObjectAnimator.ofFloat(player, "translationY", 950f);
                playerRun.setDuration(1000);
                ObjectAnimator wait3 = ObjectAnimator.ofFloat(player, "translationX", 0f);
                wait3.setDuration(1000);

                final int enemyHealthChange = (int) (15 + damageBoost + (Math.random() * 25));
                final int healthChange = ((int) (10 + Math.random() * 20));


                //assembles, then plays animations
                bouncer.play(userAttack);
                bouncer.play(userRetreat).after(userAttack);
                bouncer.play(wait).after(userRetreat);
                if (enemyHealth - enemyHealthChange > 0) {
                    bouncer.play(enemyAttack).after(wait);
                    bouncer.play(enemyRetreat).after(enemyAttack);
                    bouncer.play(wait2).after(enemyRetreat);
                    if (ActualGameActivity.getHealth() - healthChange <= 0) {
                        bouncer.play(playerRun).after(wait2);
                        bouncer.play(wait3).after(playerRun);
                    }
                } else {
                    bouncer.play(enemyDisappear).after(wait);
                    bouncer.play(wait2).after(enemyDisappear);
                }
                bouncer.start();

                userAttack.addListener(new Animator.AnimatorListener() {
                    public void onAnimationStart(Animator animation) { } //is useless
                    @Override
                    public void onAnimationEnd(Animator animation) { //stuff to do when animation ends
                        enemyHealth -= enemyHealthChange;
                        enemyHealthBar.setProgress(enemyHealth);
                    }
                    public void onAnimationCancel(Animator animation) { }//is useless
                    public void onAnimationRepeat(Animator animation) { }//is useless
                });

                enemyAttack.addListener(new Animator.AnimatorListener() {
                    public void onAnimationStart(Animator animation) { } //is useless
                    @Override
                    public void onAnimationEnd(Animator animation) { //stuff to do when animation ends
                        ActualGameActivity.changeHealth(healthChange);
                        healthBar.setProgress(ActualGameActivity.getHealth());
                    }
                    public void onAnimationCancel(Animator animation) { }//is useless
                    public void onAnimationRepeat(Animator animation) { }//is useless
                });

                bouncer.addListener(new Animator.AnimatorListener() {
                    public void onAnimationStart(Animator animation) { } //is useless
                    @Override
                    public void onAnimationEnd(Animator animation) { //stuff to do when animation ends
                        canAttack = true;
                        if (enemyHealth <= 0) {
                            String theEvent = "You won the fight. You also got some food and water.";
                            if (ActualGameActivity.getEndLocation() - ActualGameActivity.getLocation() == 2) {
                                theEvent += " You get a strange feeling you are close to the end.";
                            }
                            ActualGameActivity.setEvent(theEvent);
                            ActualGameActivity.addFood(2);
                            ActualGameActivity.addWater(2);
                            reset();
                            finish();
                        } else if (ActualGameActivity.getHealth() <= 0) {
                            reset();
                            startActivity(new Intent(Encounter.this, GameOver.class));
                        }
                    }
                    public void onAnimationCancel(Animator animation) { }//is useless
                    public void onAnimationRepeat(Animator animation) { }//is useless
                });

                //done
            }
        });


    }

    public static void reset() {
        enemyHealth = 100;
        canAttack = true;
        damageBoost = 0;
    }


    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

        // Trigger the initial hide() shortly after the activity has been
        // created, to briefly hint to the user that UI controls
        // are available.
        delayedHide(100);
    }

    private void toggle() {
        if (mVisible) {
            hide();
        } else {
            show();
        }
    }

    private void hide() {
        // Hide UI first
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }
        mVisible = false;

        // Schedule a runnable to remove the status and navigation bar after a delay
        mHideHandler.removeCallbacks(mShowPart2Runnable);
        mHideHandler.postDelayed(mHidePart2Runnable, UI_ANIMATION_DELAY);
    }

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

    /**
     * Schedules a call to hide() in delay milliseconds, canceling any
     * previously scheduled calls.
     */
    private void delayedHide(int delayMillis) {
        mHideHandler.removeCallbacks(mHideRunnable);
        mHideHandler.postDelayed(mHideRunnable, delayMillis);
    }

    @Override
    public void onBackPressed(){ }
}
