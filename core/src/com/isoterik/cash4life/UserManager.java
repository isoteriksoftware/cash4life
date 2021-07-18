package com.isoterik.cash4life;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.Json;
import io.github.isoteriktech.xgdx.Component;

import java.io.File;
import java.io.IOException;

public class UserManager extends Component {
    private User user;

    @Override
    public void start() {
        user = new Json().fromJson(User.class, getJsonFile());
        if (user ==  null)
            user = new User();
    }

    private FileHandle getJsonFile() {
        FileHandle fileHandle = Gdx.files.local("user.json");
        System.out.println(fileHandle.path());
        if (!fileHandle.exists()) {
            try {
                fileHandle.file().createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return fileHandle;
    }

    public User getUser() {
        return user;
    }

    public void deposit(float amount) {
        user.deposit(amount);
        save();
    }

    public void withdraw(float amount) {
        user.withdraw(amount);
        save();
    }

    public void save() {
        new Json().toJson(user, getJsonFile());
    }

    public void reset() {
        User defaultState = new User(
                "",
                "",
                "",
                0
        );
        new Json().toJson(defaultState, getJsonFile());
    }
}
