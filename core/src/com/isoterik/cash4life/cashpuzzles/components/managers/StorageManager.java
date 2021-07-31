package com.isoterik.cash4life.cashpuzzles.components.managers;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.Json;
import com.isoterik.cash4life.GlobalConstants;
import com.isoterik.cash4life.cashpuzzles.utils.Storage;
import io.github.isoteriktech.xgdx.Component;

import java.io.File;
import java.io.IOException;

public class StorageManager extends Component {
    private Storage storage;

    @Override
    public void start() {
        storage = new Json().fromJson(Storage.class, getJsonFile());
        if (storage == null) // First time
            storage = new Storage();
    }

    private FileHandle getJsonFile() {
        FileHandle fileHandle = Gdx.files.local("storage.json");
        if (!fileHandle.exists()) {
            try {
                fileHandle.file().createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return fileHandle;
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
                0,
                5,
                3
        );
        new Json().toJson(defaultState, getJsonFile());
    }
}
