package com.lxmusicmobile.lyric;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.util.Log;

import com.facebook.react.bridge.ReactApplicationContext;

import java.util.HashMap;
import java.util.List;

public class Lyric extends LyricPlayer {
  LyricView lyricView = null;
  LyricEvent lyricEvent = null;
  ReactApplicationContext reactAppContext;

  boolean isShowLyric = false;
  boolean isLock = false;
  String themeColor = "#07c556";
  String lastText = "LX Music ^-^";
  int lyricViewX = 0;
  int lyricViewY = 0;
  String textX = "LEFT";
  String textY = "TOP";

  Lyric(ReactApplicationContext reactContext) {
    this.reactAppContext = reactContext;
    registerScreenBroadcastReceiver();
  }

  private void registerScreenBroadcastReceiver() {
    final IntentFilter theFilter = new IntentFilter();
    /** System Defined Broadcast */
    theFilter.addAction(Intent.ACTION_SCREEN_ON);
    theFilter.addAction(Intent.ACTION_SCREEN_OFF);

    BroadcastReceiver screenOnOffReceiver = new BroadcastReceiver() {
      @Override
      public void onReceive(Context context, Intent intent) {
        String strAction = intent.getAction();

        switch (strAction) {
          case Intent.ACTION_SCREEN_OFF:
            Log.d("Lyric", "ACTION_SCREEN_OFF");
            handleScreenOff();
            break;
          case Intent.ACTION_SCREEN_ON:
            Log.d("Lyric", "ACTION_SCREEN_ON");
            handleScreenOn();
            break;
        }
      }
    };

    reactAppContext.registerReceiver(screenOnOffReceiver, theFilter);
  }

  private void handleScreenOff() {
    if (!isShowLyric) return;
    setTempPause(true);

    if (lyricView != null) {
      lyricView.runOnUiThread(new Runnable() {
        @Override
        public void run() {
          lyricView.destroy();
        }
      });
    }
  }

  private void handleScreenOn() {
    if (!isShowLyric) return;
    if (lyricView == null) lyricView = new LyricView(reactAppContext, lyricEvent);
    lyricView.runOnUiThread(new Runnable() {
      @Override
      public void run() {
        lyricView.showLyricView(isLock, themeColor, lyricViewX, lyricViewY, textX, textY);
        lyricView.setLyric(lastText);
        setTempPause(false);
      }
    });
  }

  public void showLyric(boolean isLock, String themeColor, int lyricViewX, int lyricViewY, String textX, String textY) {
    if (lyricEvent == null) lyricEvent = new LyricEvent(reactAppContext);
    if (lyricView == null) lyricView = new LyricView(reactAppContext, lyricEvent);
    this.isLock = isLock;
    this.themeColor = themeColor;
    this.lyricViewX = lyricViewX;
    this.lyricViewY = lyricViewY;
    this.textX = textX;
    this.textY = textY;
    lyricView.showLyricView(isLock, themeColor, lyricViewX, lyricViewY, textX, textY);
    isShowLyric = true;
  }

  public void hideLyric() {
    this.pause();
    if (lyricView != null) {
      lyricView.destroy();
    }
    isShowLyric = false;
  }

  @Override
  public void setLyric(String lyric, String translationLyric) {
    if (lyricView != null) super.setLyric(lyric, translationLyric);
  }

  @Override
  public void onSetLyric(List lines) {
    for (int i = 0; i < lines.size(); i++) {
      HashMap line = (HashMap) lines.get(i);
      // Log.d("Lyric", (String) line.get("text") + " " + (String) line.get("translation"));
    }
  }

  @Override
  public void onPlay(int lineNum, String text) {
    // HashMap line = (HashMap) lines.get(lineNum);
    lastText = text;
    if (lyricView == null) return;
    lyricView.setLyric(text);
    // Log.d("Lyric", lineNum + " " + text + " " + (String) line.get("translation"));
  }

  public void lockLyric() {
    if (lyricView == null) return;
    this.isLock = true;
    lyricView.lockView();
  }

  public void unlockLyric() {
    if (lyricView == null) return;
    this.isLock = false;
    lyricView.unlockView();
  }

  public void toggleTranslation(boolean isShowTranslation) {

  }

  public void setColor(String color) {
    lyricView.setColor(color);
    this.themeColor = color;
  }

  public void setLyricTextPosition(String positionX, String positionY) {
    this.textX = positionX;
    this.textY = positionY;
    lyricView.setLyricTextPosition(positionX, positionY);
  }
}
