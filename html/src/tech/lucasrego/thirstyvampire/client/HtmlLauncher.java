package tech.lucasrego.thirstyvampire.client;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.backends.gwt.GwtApplication;
import com.badlogic.gdx.backends.gwt.GwtApplicationConfiguration;
import tech.lucasrego.thirstyvampire.ThirstyVampireGame;

public class HtmlLauncher extends GwtApplication {

        @Override
        public GwtApplicationConfiguration getConfig () {
                // Resizable application, uses available space in browser
//                return new GwtApplicationConfiguration(true);
                // Fixed size application:
                return new GwtApplicationConfiguration(800, 430);
        }

        @Override
        public ApplicationListener createApplicationListener () {
                return new ThirstyVampireGame();
        }
}