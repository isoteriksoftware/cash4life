package com.isoterik.cash4life.cashpuzzles.components.managers;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.Json;
import com.isoterik.cash4life.GlobalConstants;
import com.isoterik.cash4life.cashpuzzles.utils.Storage;
import io.github.isoteriktech.xgdx.Component;

import java.io.File;

public class StorageManager extends Component {
    private Storage storage;

    @Override
    public void start() {
        storage = new Json().fromJson(Storage.class, getJsonFile());
    }

    private FileHandle getJsonFile() {
        String currentPath = Gdx.files.internal(GlobalConstants.CASH_PUZZLES_ASSETS_HOME).path();
        String fileDirectory = currentPath + File.separatorChar + "json";
        return Gdx.files.local(fileDirectory + File.separatorChar + "storage.json");
    }

    public Storage getStorage() {
        return storage;
    }

    public void save(Storage s) {
        new Json().toJson(s, getJsonFile());
    }

    public void reset() {
        Storage defaultState = new Storage(
                false,
                null,
                0,
                0
        );
        new Json().toJson(defaultState, getJsonFile());
    }
}
